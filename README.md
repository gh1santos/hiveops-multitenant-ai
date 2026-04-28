# 🐝 HiveOps OS - Motor B2B de Orquestração de Agentes IA

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=flat-square&logo=spring)
![React](https://img.shields.io/badge/React-18-blue?style=flat-square&logo=react)
![TailwindCSS](https://img.shields.io/badge/Tailwind-v4-38B2AC?style=flat-square&logo=tailwind-css)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-336791?style=flat-square&logo=postgresql)

O **HiveOps** é uma plataforma SaaS B2B Multi-tenant desenvolvida para orquestrar e delegar tarefas assíncronas para Agentes de Inteligência Artificial autônomos. 

Este projeto foi desenhado com foco em **Alta Escalabilidade**, utilizando a nova API de *Virtual Threads* do Java 21 para processamento em background, garantindo que o motor de orquestração (Heartbeat) execute tarefas sem onerar a Thread Pool principal do servidor web.

---

## Principais Funcionalidades

* **Arquitetura Multi-Tenant:** Isolamento total de dados por empresa (`company_id`). O contexto de segurança extrai o tenant diretamente do Token JWT interceptado nas requisições.
* **Processamento Assíncrono (Heartbeat):** Um Scheduler interno varre o banco de dados periodicamente buscando tarefas `PENDING`. As tarefas são despachadas e processadas de forma isolada utilizando `VirtualThreads`.
* **Design Pattern "Adapter" (Agnóstico a LLM):** O sistema utiliza uma porta `AiAdapterPort` para comunicar com os agentes. 
  * Suporte nativo preparado para **OpenAI** e **Anthropic** via *Spring AI*.
  * Suporte a um motor **Mock (BASH)** para simulação local, ideal para testes de carga e demonstração de portfólio sem custos de API.
* **Dashboard em Tempo Real:** Interface web em modo Dark/Cyberpunk construída com React e Tailwind v4. O front-end utiliza uma estratégia de *Polling* para refletir as execuções e métricas do sistema automaticamente.
* **Sistema de Onboarding Público:** Fluxo de criação de empresas nativo na tela de login.

---

## 🛠️ Stack Tecnológica

### Backend (Motor de Orquestração)
* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.x (Web, Data JPA, Security)
* **Integração IA:** Spring AI
* **Concorrência:** Java Virtual Threads (`spring.threads.virtual.enabled=true`)
* **Migrações:** Flyway
* **Documentação de API:** Swagger / Springdoc OpenAPI

### Frontend (Interface do Usuário)
* **Ecossistema:** React + Vite
* **Estilização:** TailwindCSS v4 (Nativo, sem PostCSS) + Lucide Icons
* **Comunicação:** Axios (com interceptors para injeção automática de Bearer Token)

### Infraestrutura & Dados
* **Banco de Dados:** PostgreSQL 16 (com extensão `pgvector` nativa)
* **Containerização:** Docker & Docker Compose

---

## ⚙️ Como Executar Localmente

Para rodar este projeto na sua máquina, você precisará do **Docker**, **Java 21** e **Node.js** instalados.

### 1. Subindo o Banco de Dados
Na raiz do projeto, inicie o container do PostgreSQL:
```bash
docker-compose up -d
