package com.company.hiveops.application.ports;

import com.company.hiveops.application.dtos.AiExecutionResult;
import com.company.hiveops.domain.model.Agent;

public interface AiAdapterPort {

    // Agora retorna o texto e a quantidade de tokens gastos
    AiExecutionResult executePrompt(String systemPrompt, String userPrompt);

    boolean supports(Agent.AdapterType adapterType);
}