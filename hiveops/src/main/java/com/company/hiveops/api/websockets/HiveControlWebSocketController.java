package com.company.hiveops.api.websockets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HiveControlWebSocketController {

    // O frontend enviaria uma mensagem para o destino "/app/agents/pause"
    @MessageMapping("/agents/pause")
    public void handleEmergencyPause(@Payload UUID agentId) {
        log.warn("WEBSOCKET COMMAND RECEIVED: Emergency pause requested for agent {}", agentId);
        // Aqui você chamaria um AgentService para mudar o status do agente para PAUSED no banco
    }
}
