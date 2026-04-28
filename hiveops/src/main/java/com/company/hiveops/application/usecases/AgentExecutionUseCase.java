package com.company.hiveops.application.usecases;

import com.company.hiveops.domain.model.Task;

public interface AgentExecutionUseCase {

    // O método que o Scheduler vai chamar de forma assíncrona
    void executeTaskAsync(Task task);
}