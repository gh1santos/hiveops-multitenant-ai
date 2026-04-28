package com.company.hiveops.api.controllers;

import com.company.hiveops.api.dtos.CreateGoalRequest;
import com.company.hiveops.domain.model.Company;
import com.company.hiveops.domain.model.Goal;
import com.company.hiveops.domain.repositories.CompanyRepository;
import com.company.hiveops.domain.repositories.GoalRepository;
import com.company.hiveops.infrastructure.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalRepository goalRepository;
    private final CompanyRepository companyRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Goal createGoal(@RequestBody CreateGoalRequest request) {
        UUID companyId = TenantContext.getCurrentTenant();
        Company company = companyRepository.findById(companyId).orElseThrow();

        Goal parentGoal = null;
        if (request.parentGoalId() != null) {
            parentGoal = goalRepository.findById(request.parentGoalId()).orElse(null);
        }

        Goal goal = Goal.builder()
                .company(company)
                .title(request.title())
                .description(request.description())
                .parentGoal(parentGoal)
                .build();

        return goalRepository.save(goal);
    }

    @GetMapping
    public List<Goal> listGoals() {
        return goalRepository.findAllByCompanyId(TenantContext.getCurrentTenant());
    }
}