package com.company.hiveops.application.usecases.impl;

import com.company.hiveops.application.dtos.AiExecutionResult;
import com.company.hiveops.application.events.TaskNotificationEvent;
import com.company.hiveops.application.events.TaskCompletedEvent;
import com.company.hiveops.application.ports.AiAdapterPort;
import com.company.hiveops.application.usecases.AgentExecutionUseCase;
import com.company.hiveops.domain.model.Agent;
import com.company.hiveops.domain.model.AuditLog;
import com.company.hiveops.domain.model.Execution;
import com.company.hiveops.domain.model.Task;
import com.company.hiveops.domain.repositories.AgentRepository;
import com.company.hiveops.domain.repositories.AuditLogRepository;
import com.company.hiveops.domain.repositories.ExecutionRepository;
import com.company.hiveops.domain.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentExecutionServiceImpl implements AgentExecutionUseCase {

    private final List<AiAdapterPort> aiAdapters;
    private final TaskRepository taskRepository;
    private final ExecutionRepository executionRepository;
    private final AgentRepository agentRepository;
    private final AuditLogRepository auditLogRepository;

    // Injeção do Publicador de Eventos (Desacoplando WebSockets do Domínio)
    private final ApplicationEventPublisher eventPublisher;

    private static final BigDecimal COST_PER_1K_TOKENS = new BigDecimal("0.002");

    @Async
    @Override
    @Transactional
    public void executeTaskAsync(Task task) {
        long startTime = System.currentTimeMillis();
        Agent agent = task.getAgent();

        if (agent.getStatus() == Agent.AgentStatus.BLOCKED || agent.getBudgetMonthly().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Agent [{}] is blocked or out of budget. Aborting task.", agent.getName());
            task.setStatus(Task.TaskStatus.FAILED);
            taskRepository.save(task);
            return;
        }

        AiAdapterPort adapter = aiAdapters.stream()
                .filter(a -> a.supports(agent.getAdapterType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum adaptador encontrado"));

        String systemPrompt = String.format("Você é %s. Seu cargo é: %s. %s",
                agent.getName(), agent.getRole(), agent.getDescription() != null ? agent.getDescription() : "");
        String userPrompt = String.format("Tarefa: %s\nDescrição: %s", task.getTitle(), task.getDescription());

        Execution execution = Execution.builder()
                .companyId(task.getCompany().getId())
                .task(task)
                .agent(agent)
                .promptUsed(systemPrompt + "\n\n" + userPrompt)
                .build();

        // Publica evento genérico de notificação
        eventPublisher.publishEvent(new TaskNotificationEvent(
                task.getCompany().getId(),
                "Agent " + agent.getName() + " started task: " + task.getTitle()
        ));

        try {
            AiExecutionResult aiResult = adapter.executePrompt(systemPrompt, userPrompt);

            execution.setResultOutput(aiResult.output());
            execution.setTokensConsumed(aiResult.totalTokens());

            BigDecimal cost = BigDecimal.valueOf(aiResult.totalTokens())
                    .divide(new BigDecimal("1000"), 4, RoundingMode.HALF_UP)
                    .multiply(COST_PER_1K_TOKENS);

            execution.setEstimatedCost(cost);
            execution.setStatus(Execution.ExecutionStatus.SUCCESS);
            task.setStatus(Task.TaskStatus.DONE);

            agent.setBudgetMonthly(agent.getBudgetMonthly().subtract(cost));

            if (agent.getBudgetMonthly().compareTo(BigDecimal.ZERO) <= 0) {
                agent.setStatus(Agent.AgentStatus.BLOCKED);

                AuditLog blockLog = AuditLog.builder()
                        .companyId(agent.getCompany().getId())
                        .entityType("AGENT")
                        .entityId(agent.getId())
                        .action("BUDGET_EXCEEDED_BLOCKED")
                        .details(Map.of(
                                "cost", cost,
                                "remaining_budget", agent.getBudgetMonthly()
                        ))
                        .createdBy("SYSTEM")
                        .build();
                auditLogRepository.save(blockLog);

                eventPublisher.publishEvent(new TaskNotificationEvent(
                        task.getCompany().getId(),
                        "CRITICAL: Agent " + agent.getName() + " blocked due to budget limit!"
                ));
            }

            agentRepository.save(agent);

            // Publica o evento específico de conclusão
            eventPublisher.publishEvent(new TaskCompletedEvent(
                    task.getCompany().getId(), task.getId(), task.getTitle(), agent.getName()
            ));

        } catch (Exception e) {
            execution.setResultOutput("ERRO: " + e.getMessage());
            execution.setStatus(Execution.ExecutionStatus.FAILED);
            task.setStatus(Task.TaskStatus.FAILED);

            eventPublisher.publishEvent(new TaskNotificationEvent(
                    task.getCompany().getId(),
                    "Task FAILED by " + agent.getName() + " - Error: " + e.getMessage()
            ));
        } finally {
            execution.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            executionRepository.save(execution);
            taskRepository.save(task);

            AuditLog execLog = AuditLog.builder()
                    .companyId(task.getCompany().getId())
                    .entityType("EXECUTION")
                    .entityId(execution.getId())
                    .action("TASK_EXECUTION_" + execution.getStatus().name())
                    .createdBy("SYSTEM_HEARTBEAT")
                    .build();
            auditLogRepository.save(execLog);
        }
    }
}