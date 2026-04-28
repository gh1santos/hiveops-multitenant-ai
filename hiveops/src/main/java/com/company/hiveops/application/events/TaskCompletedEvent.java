package com.company.hiveops.application.events;

import java.util.UUID;

public record TaskCompletedEvent(
        UUID companyId,
        UUID taskId,
        String taskTitle,
        String agentName
) {}