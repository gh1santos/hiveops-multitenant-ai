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

    @Scheduled(fixedDelayString = "10000")
    @Transactional
    public void pulse() {
        log.debug("Heartbeat pulse triggered...");

        List<Task> pendingTasks = taskRepository.findAllByStatus(Task.TaskStatus.PENDING);

        if (pendingTasks.isEmpty()) {
            return;
        }

        log.info("Heartbeat found {} pending task(s). Waking up agents...", pendingTasks.size());

        for (Task task : pendingTasks) {
            if (task.getAgent() == null) {
                log.warn("Task {} has no agent assigned. Skipping.", task.getId());
                continue;
            }

            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            taskRepository.save(task);

            log.info("Assigning Task [{}] to Agent [{}]", task.getTitle(), task.getAgent().getName());
            agentExecutionUseCase.executeTaskAsync(task);
        }
    }
}