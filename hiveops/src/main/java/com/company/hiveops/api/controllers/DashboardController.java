package com.company.hiveops.api.controllers;

import com.company.hiveops.api.dtos.DashboardSummaryResponse;
import com.company.hiveops.domain.model.Task;
import com.company.hiveops.domain.repositories.AgentRepository;
import com.company.hiveops.domain.repositories.ExecutionRepository;
import com.company.hiveops.domain.repositories.GoalRepository;
import com.company.hiveops.domain.repositories.TaskRepository;
import com.company.hiveops.infrastructure.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AgentRepository agentRepository;
    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        UUID companyId = TenantContext.getCurrentTenant();

        long totalAgents = agentRepository.findAllByCompanyId(companyId).size();
        long activeGoals = goalRepository.findAllByCompanyId(companyId).size();

        long pendingTasks = taskRepository.findAllByCompanyIdAndStatus(companyId, Task.TaskStatus.PENDING).size();
        long completedTasks = taskRepository.findAllByCompanyIdAndStatus(companyId, Task.TaskStatus.DONE).size();

        return new DashboardSummaryResponse(
                totalAgents,
                activeGoals,
                pendingTasks,
                completedTasks,
                0
        );
    }
}
