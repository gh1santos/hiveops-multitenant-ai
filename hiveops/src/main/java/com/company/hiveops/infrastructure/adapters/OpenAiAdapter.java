package com.company.hiveops.infrastructure.adapters;

import com.company.hiveops.application.dtos.AiExecutionResult;
import com.company.hiveops.application.ports.AiAdapterPort;
import com.company.hiveops.domain.model.Agent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiAdapter implements AiAdapterPort {

    private final ChatModel chatModel;

    @Override
    public boolean supports(Agent.AdapterType adapterType) {
        return adapterType == Agent.AdapterType.OPENAI;
    }

/*    @Override
    public AiExecutionResult executePrompt(String systemPrompt, String userPrompt) {
        Prompt prompt = new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(userPrompt)));

        // Chamada real
        ChatResponse response = chatModel.call(prompt);

        String output = response.getResult().getOutput().getText();

        // Extrai o consumo de tokens da API da OpenAI usando a resposta nativa do Spring AI
        int tokens = 0;
        if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
            tokens = response.getMetadata().getUsage().getTotalTokens().intValue();
        }

        return new AiExecutionResult(output, tokens);
    }*/

    @Override
    public AiExecutionResult executePrompt(String systemPrompt, String userPrompt) {
        // COMENTE as linhas que chamam API real:
        // Prompt prompt = new Prompt(...);
        // ChatResponse response = chatModel.call(prompt);

        // ADICIONE este retorno fake para o portfólio:
        return new AiExecutionResult(
                "PROCESSO CONCLUÍDO (MODO DEMO):\nO código/resposta foi gerado com sucesso pelo motor de IA do HiveOps.",
                120
        );
    }
}
