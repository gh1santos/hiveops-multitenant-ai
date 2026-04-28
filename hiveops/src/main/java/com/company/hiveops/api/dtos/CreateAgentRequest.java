package com.company.hiveops.api.dtos;

import com.company.hiveops.domain.model.Agent.AdapterType;
import java.math.BigDecimal;

public record CreateAgentRequest(
        String name,
        String role,
        String description,
        AdapterType adapterType,
        BigDecimal budgetMonthly
) {}