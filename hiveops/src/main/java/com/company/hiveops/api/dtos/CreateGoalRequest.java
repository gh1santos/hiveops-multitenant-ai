package com.company.hiveops.api.dtos;

import java.util.UUID;

public record CreateGoalRequest(
        String title,
        String description,
        UUID parentGoalId
) {}
