package com.company.hiveops.api.dtos;

public record DashboardSummaryResponse(
        long totalAgents,
        long activeGoals,
        long pendingTasks,
        long completedTasks,
        long failedExecutions
) {}
