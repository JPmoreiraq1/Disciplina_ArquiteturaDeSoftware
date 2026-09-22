1. Monolito
Conceito e definição:
Todo o sistema roda em uma unica base de codigo e processo unico, com comunicação ocorrendo diretamente na memoria.
Casos de uso comuns:
Indicado para MVPs e sistemas internos fechados. Exemplos reais: Stack Overflow e versao inicial do Shopify.
Principais vantagens:
Facilidade de implantação (deploy unico), desenvolvimento inicial simples e alto desempenho interno.
Principais desvantagens:
Alto acoplamento futuro, escalabilidade limitada (tudo ou nada) e risco de ponto unico de falha.

2. Publicador/Assinante
Conceito e definição:
Mensageria assíncrona orientada a eventos em que publicadores e assinantes comunicam-se através de um broker.
Casos de uso comuns:
Processamento em segundo plano e integração de serviços. Exemplos reais: Pos-compra de e-commerce e Uber.
Principais vantagens:
Alto desacoplamento entre serviços, escalabilidade independente e alta tolerancia a falhas.
Principais desvantagens:
Complexidade operacional para gerenciar o broker, depuração difícil e consistência eventual.
