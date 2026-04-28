-- Extensão opcional mas recomendada se formos usar busca semântica/RAG no futuro
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Companies (Multi-tenant root)
CREATE TABLE companies (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           name VARCHAR(255) NOT NULL,
                           status VARCHAR(50) DEFAULT 'ACTIVE',
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Agents (A força de trabalho)
CREATE TABLE agents (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                        manager_id UUID REFERENCES agents(id) ON DELETE SET NULL, -- Superior hierárquico
                        name VARCHAR(100) NOT NULL,
                        role VARCHAR(100) NOT NULL,
                        description TEXT,
                        adapter_type VARCHAR(50) NOT NULL, -- OPENAI, ANTHROPIC, BASH
                        budget_monthly NUMERIC(10, 2) NOT NULL DEFAULT 0.0,
                        status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, PAUSED, BLOCKED
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Índices essenciais para queries multi-tenant
CREATE INDEX idx_agents_company ON agents(company_id);

-- 3. Goals (Objetivos da Empresa e Projetos)
CREATE TABLE goals (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                       parent_goal_id UUID REFERENCES goals(id) ON DELETE CASCADE,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       status VARCHAR(50) DEFAULT 'IN_PROGRESS',
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_goals_company ON goals(company_id);

-- 4. Tasks (As tarefas a serem executadas)
CREATE TABLE tasks (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                       goal_id UUID REFERENCES goals(id) ON DELETE CASCADE,
                       agent_id UUID REFERENCES agents(id) ON DELETE SET NULL,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       priority VARCHAR(20) DEFAULT 'MEDIUM',
                       status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, REVIEW, DONE, FAILED
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tasks_company ON tasks(company_id);
CREATE INDEX idx_tasks_agent ON tasks(agent_id);
CREATE INDEX idx_tasks_status ON tasks(status);

-- 5. Executions (Resultados das chamadas dos Agentes)
CREATE TABLE executions (
                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                            task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
                            agent_id UUID NOT NULL REFERENCES agents(id),
                            prompt_used TEXT,
                            result_output TEXT,
                            tokens_consumed INTEGER DEFAULT 0,
                            estimated_cost NUMERIC(10, 6) DEFAULT 0.0,
                            status VARCHAR(50) NOT NULL, -- SUCCESS, FAILED
                            execution_time_ms BIGINT,
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_executions_company ON executions(company_id);
CREATE INDEX idx_executions_task ON executions(task_id);

-- 6. Audit Logs (Governança e Rastreabilidade)
CREATE TABLE audit_logs (
                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                            entity_type VARCHAR(50) NOT NULL, -- AGENT, TASK, EXECUTION, BUDGET
                            entity_id UUID NOT NULL,
                            action VARCHAR(100) NOT NULL,
                            details JSONB,
                            created_by VARCHAR(255), -- Pode ser 'SYSTEM' ou UUID do humano/agente
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_company ON audit_logs(company_id);