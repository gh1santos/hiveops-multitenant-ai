package com.company.hiveops.domain.repositories;

import com.company.hiveops.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Filtros base de segurança Multi-tenant
    List<Task> findAllByCompanyId(UUID companyId);

    Optional<Task> findByIdAndCompanyId(UUID id, UUID companyId);

    // Buscas para a gestão das tarefas dentro de uma empresa
    List<Task> findAllByCompanyIdAndStatus(UUID companyId, Task.TaskStatus status);

    List<Task> findAllByCompanyIdAndAgentId(UUID companyId, UUID agentId);

    List<Task> findAllByCompanyIdAndAgentIdAndStatus(UUID companyId, UUID agentId, Task.TaskStatus status);

    // Métodos de uso interno (System Level) para o Heartbeat Scheduler
    // O Scheduler roda em background, fora do contexto de requisição do usuário,
    // então ele pode precisar buscar tarefas pendentes de forma global e agrupar por empresa/agente.
    List<Task> findAllByStatus(Task.TaskStatus status);

    List<Task> findAllByAgentIdAndStatus(UUID agentId, Task.TaskStatus status);
}
