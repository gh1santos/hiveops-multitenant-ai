package com.company.hiveops.api.controllers;

import com.company.hiveops.api.dtos.CreateAgentRequest;
import com.company.hiveops.domain.model.Agent;
import com.company.hiveops.domain.model.Company;
import com.company.hiveops.domain.repositories.AgentRepository;
import com.company.hiveops.domain.repositories.CompanyRepository;
import com.company.hiveops.infrastructure.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentRepository agentRepository;
    private final CompanyRepository companyRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agent createAgent(@RequestBody CreateAgentRequest request) {
        UUID companyId = TenantContext.getCurrentTenant();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found for current context"));

        Agent agent = Agent.builder()
                .company(company)
                .name(request.name())
                .role(request.role())
                .description(request.description())
                .adapterType(request.adapterType())
                .budgetMonthly(request.budgetMonthly())
                .build();

        return agentRepository.save(agent);
    }

    @GetMapping
    public List<Agent> listAgents() {
        UUID companyId = TenantContext.getCurrentTenant();
        return agentRepository.findAllByCompanyId(companyId);
    }
}