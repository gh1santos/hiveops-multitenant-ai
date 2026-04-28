package com.company.hiveops.infrastructure.scheduler;

import com.company.hiveops.application.usecases.AgentExecutionUseCase;
import com.company.hiveops.domain.model.Task;
import com.company.hiveops.domain.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartbeatScheduler {

    private final TaskRepository taskRepository;
    private final AgentExecutionUseCase agentExecutionUseCase;

    // Roda a cada 10 segundos. Em produção, você pode parametrizar isso no application.yml
    @Scheduled(fixedDelayString = "10000")
    @Transactional
    public void pulse() {
        log.debug("Heartbeat pulse triggered...");

        // 1. Busca todas as tarefas que estão esperando para serem executadas
        List<Task> pendingTasks = taskRepository.findAllByStatus(Task.TaskStatus.PENDING);

        if (pendingTasks.isEmpty()) {
            return;
        }

        log.info("Heartbeat found {} pending task(s). Waking up agents...", pendingTasks.size());

        for (Task task : pendingTasks) {
            // Se a tarefa não tem um agente alocado, podemos aplicar uma lógica de roteamento aqui no futuro.
            // Por enquanto, só processamos as que já têm um funcionário IA designado.
            if (task.getAgent() == null) {
                log.warn("Task {} has no agent assigned. Skipping.", task.getId());
                continue;
            }

            // 2. Trava a tarefa mudando o status para IN_PROGRESS
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            taskRepository.save(task);

            // 3. Dispara a execução (Isso rodará em uma Virtual Thread graças ao @Async na implementação)
            log.info("Assigning Task [{}] to Agent [{}]", task.getTitle(), task.getAgent().getName());
            agentExecutionUseCase.executeTaskAsync(task);
        }
    }
}