package com.company.hiveops.application.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskNotificationListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @EventListener
    public void handleTaskCompleted(TaskCompletedEvent event) {
        log.info("Event Received: Task {} completed by {}", event.taskId(), event.agentName());

        String topic = "/topic/company/" + event.companyId();
        String message = String.format("SUCESSO: O agente %s finalizou a tarefa '%s'",
                event.agentName(), event.taskTitle());

        messagingTemplate.convertAndSend(topic, message);
    }

    @Async
    @EventListener
    public void handleGenericNotification(TaskNotificationEvent event) {
        String topic = "/topic/company/" + event.companyId();
        messagingTemplate.convertAndSend(topic, event.message());
    }
}