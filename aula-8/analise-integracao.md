# Análise de Integração: Vendas (MVC) x Hub de Faturamento (Arquitetura Hexagonal)

## Visão Geral da Integração Implementada

Na aplicação de vendas (`aula-8/mvc`), ao fechar um pedido (`POST /sales/{id}/close`), o `SaleService` realiza a baixa no estoque e aciona o `BillingHubClient`, que faz uma chamada REST síncrona (`POST /api/faturas`) ao Hub de Faturamento (`aula-7/hexagonal`), enviando `pedidoId` e `valor`. O hub executa a emissão do boleto (fila), emissão da NF-e (SOAP) e envio de e-mail (SMTP), persiste a fatura e retorna o `faturaId`, que é armazenado no pedido (`Sale.invoiceId`) com status `FECHADO`.

---

## Problema 1: Indisponibilidade ou Timeout do Hub de Faturamento

### O que acontece com o pedido
Como a comunicação entre a aplicação de vendas e o hub é **síncrona via REST**, se o Hub de Faturamento estiver fora do ar, reiniciando ou sofrendo lentidão (causando timeout na chamada HTTP), a requisição `POST /api/faturas` falha. A transação na aplicação de vendas é abortada, o pedido permanece com status `ABERTO` e sem `invoiceId` (e, caso a baixa de estoque não esteja na mesma transação distribuída ou ocorra em outro serviço, há risco de inconsistência entre estoque e pedido).

### O que o negócio percebe
- **Impossibilidade de concluir vendas:** vendedores e clientes não conseguem fechar pedidos enquanto o faturamento estiver instável.
- **Acoplamento temporal:** uma falha no subsistema de faturamento (ou em uma de suas dependências externas, como a SEFAZ/SOAP ou servidor SMTP) derruba efetivamente a operação de vendas da empresa, gerando perda de receita e má experiência.

### Solução indicada e onde atuaria
**Solução:** **Mensageria Assíncrona (Broker de Mensagens, ex.: RabbitMQ/Kafka) com Outbox Pattern**.

**Onde atuaria:**
- **Na aplicação de vendas (MVC):** na camada de serviço (`SaleService`) e persistência. Ao fechar o pedido, em vez de fazer uma chamada REST síncrona bloqueante, o pedido é salvo como `AGUARDANDO_FATURAMENTO` e um registro de evento (`PedidoFechado`) é gravado na mesma transação local (tabela Outbox), sendo publicado na fila por um *relay*.
- **No Hub de Faturamento (Arquitetura Hexagonal):** na camada de **adaptadores de entrada (`adapters/in`)**. Graças à arquitetura hexagonal, basta adicionar um *consumer* de fila como novo adaptador de entrada chamando a mesma porta `FaturaUseCase`, sem alterar o domínio. Após faturar, o hub publica um evento `FaturaEmitida` com o `faturaId` para que a aplicação de vendas finalize o pedido.

---

## Problema 2: Falha Parcial na Resposta e Faturamento Duplicado (Falta de Idempotência)

### O que acontece com o pedido
A aplicação de vendas envia `POST /api/faturas`. O Hub de Faturamento processa toda a requisição com sucesso: gera o código de barras do boleto, emite a NF-e via SOAP, envia o e-mail via SMTP e persiste a fatura no banco. Contudo, antes que a resposta HTTP `201 Created` retorne para a aplicação de vendas, ocorre uma queda de conexão ou *read timeout*.

Para a aplicação de vendas, a chamada falhou: o pedido continua `ABERTO` e **sem o `invoiceId` salvo**. Se o operador clicar novamente em "Fechar Pedido" (ou se houver *retry* automático), o Hub de Faturamento processará o mesmo `pedidoId` uma segunda vez, criando uma nova fatura, um novo boleto e uma nova nota fiscal.

### O que o negócio percebe
- **Faturamento e cobrança em duplicidade:** o cliente recebe dois boletos e dois e-mails de confirmação para um único pedido.
- **Problemas fiscais e operacionais:** duas NF-es emitidas para a mesma venda exigem cancelamento manual junto à SEFAZ, conciliação financeira e atendimento ao cliente para evitar pagamento duplo.

### Solução indicada e onde atuaria
**Solução:** **Idempotência (Chave de Idempotência / Controle de Unicidade por `pedidoId`)**.

**Onde atuaria:**
- **Na aplicação de vendas (`BillingHubClient`):** garantindo o envio consistente de uma chave única de idempotência (como o próprio `pedidoId` ou um header `Idempotency-Key` gerado para o pedido) em todas as tentativas.
- **No Hub de Faturamento (Camada de Aplicação e Persistência — `FaturaServiceImpl` e `FaturaRepository`):** antes de iniciar o faturamento (`boletoQueuePort`, `notaFiscalSoapPort`, `emailSmtpPort`), o caso de uso consulta o `FaturaRepository` (`buscarPorPedidoId(pedidoId)`). Caso já exista uma fatura persistida para aquele pedido, o hub **não reexecuta** os adaptadores externos e retorna imediatamente o `faturaId` já existente, garantindo que retentativas sejam seguras.
