package com.company.hiveops.application.usecases;

import com.company.hiveops.domain.model.Task;

public interface AgentExecutionUseCase {

    void executeTaskAsync(Task task);
}