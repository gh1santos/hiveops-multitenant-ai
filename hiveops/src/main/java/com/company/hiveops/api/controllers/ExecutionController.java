package com.company.hiveops.api.controllers;

import com.company.hiveops.domain.model.Execution;
import com.company.hiveops.domain.repositories.ExecutionRepository;
import com.company.hiveops.infrastructure.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/executions")
@RequiredArgsConstructor
public class ExecutionController {

    private final ExecutionRepository executionRepository;

    @GetMapping
    public List<Execution> listExecutions() {
        UUID companyId = TenantContext.getCurrentTenant();

        return executionRepository.findAllByCompanyIdOrderByCreatedAtDesc(companyId);
    }
}