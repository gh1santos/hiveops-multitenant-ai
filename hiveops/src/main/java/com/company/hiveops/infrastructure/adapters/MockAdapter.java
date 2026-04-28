package com.company.hiveops.infrastructure.adapters;

import com.company.hiveops.application.dtos.AiExecutionResult;
import com.company.hiveops.application.ports.AiAdapterPort;
import com.company.hiveops.domain.model.Agent;
import org.springframework.stereotype.Component;

@Component
public class MockAdapter implements AiAdapterPort {

    @Override
    public boolean supports(Agent.AdapterType adapterType) {
        return adapterType == Agent.AdapterType.BASH;
    }

    @Override
    public AiExecutionResult executePrompt(String systemPrompt, String userPrompt) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String fakeResponse = "Execução simulada com sucesso! \n\n" +
                "Este é um resultado gerado localmente pelo MockAdapter para demonstração de portfólio.\n" +
                "A tarefa foi processada e as métricas foram atualizadas.";

        return new AiExecutionResult(fakeResponse, 150);
    }
}