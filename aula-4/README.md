# Sua vez de arquitetar

Solução desenvolvida para o sistema de vendas e controle de estoque da empresa.

---

## 1. Diagrama de Contexto

Mostra quem usa o sistema e o que cada pessoa faz no dia a dia.

```mermaid
flowchart TD
    Vendedor["Vendedor<br>(Vende por telefone e presencial)"]
    Admin["Administrador<br>(Cadastra e remove produtos)"]
    Estoque["Equipe de Estoque<br>(Registra entradas e saídas)"]

    Sistema["Sistema de Vendas e Estoque"]

    Vendedor -->|Registra novos pedidos| Sistema
    Admin -->|Gerencia catálogo de produtos| Sistema
    Estoque -->|Atualiza quantidade em estoque| Sistema
```

---

## 2. Diagrama de Contêineres

Mostra as partes principais que fazem o sistema funcionar e como elas conversam entre si.

```mermaid
flowchart TD
    Vendedor["Vendedor"]
    Admin["Administrador"]
    Estoque["Equipe de Estoque"]

    subgraph Sistema["Sistema da Empresa"]
        Site["Sistema Web / Telas<br>(O que o usuário vê e usa)"]
        Servidor["Servidor / API<br>(Processa regras de negócio)"]
        Banco[("Banco de Dados<br>(Onde tudo fica salvo)")]

        Site -->|Envia dados| Servidor
        Servidor -->|Salva e busca dados| Banco
    end

    Vendedor --> Site
    Admin --> Site
    Estoque --> Site
```

---

## 3. Diagrama Anotado por Camadas

O mesmo sistema dividido nas três camadas clássicas de arquitetura: **Apresentação**, **Domínio** e **Dados**.

```mermaid
flowchart TD
    Vendedor["Vendedor"]
    Admin["Administrador"]
    Estoque["Equipe de Estoque"]

    subgraph Sistema["Sistema da Empresa"]
        subgraph Apresentacao["Camada de Apresentação"]
            Site["Telas / Interface Web<br>(Interação com os usuários)"]
        end

        subgraph Dominio["Camada de Domínio"]
            Servidor["Regras de Negócio / API<br>(Valida pedidos, produtos e estoque)"]
        end

        subgraph Dados["Camada de Dados"]
            Banco[("Banco de Dados<br>(Persistência de informações)")]
        end

        Site --> Servidor
        Servidor --> Banco
    end

    Vendedor --> Site
    Admin --> Site
    Estoque --> Site
```
