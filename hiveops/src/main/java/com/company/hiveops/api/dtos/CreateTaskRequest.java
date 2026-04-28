package com.company.hiveops.api.dtos;

import com.company.hiveops.domain.model.Task.TaskPriority;
import java.util.UUID;

public record CreateTaskRequest(
        String title,
        String description,
        TaskPriority priority,
        UUID agentId
) {}
