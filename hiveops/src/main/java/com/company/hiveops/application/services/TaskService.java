package com.company.hiveops.application.services;

import com.company.hiveops.domain.model.Agent;
import com.company.hiveops.domain.model.Company;
import com.company.hiveops.domain.model.Task;
import com.company.hiveops.domain.repositories.AgentRepository;
import com.company.hiveops.domain.repositories.CompanyRepository;
import com.company.hiveops.domain.repositories.TaskRepository;
import com.company.hiveops.infrastructure.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CompanyRepository companyRepository;
    private final AgentRepository agentRepository;

    @Transactional
    public Task createTask(String title, String description, Task.TaskPriority priority, UUID agentId) {
        UUID companyId = TenantContext.getCurrentTenant();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Agent agent = null;
        if (agentId != null) {
            agent = agentRepository.findByIdAndCompanyId(agentId, companyId)
                    .orElseThrow(() -> new RuntimeException("Agent not found or doesn't belong to company"));
        }

        Task task = Task.builder()
                .company(company)
                .agent(agent)
                .title(title)
                .description(description)
                .priority(priority)
                .status(Task.TaskStatus.PENDING)
                .build();

        log.info("Task created: {} for Agent: {}", title, agent != null ? agent.getName() : "Unassigned");
        return taskRepository.save(task);
    }

    public List<Task> listCompanyTasks() {
        UUID companyId = TenantContext.getCurrentTenant();
        return taskRepository.findAllByCompanyId(companyId);
    }
}
