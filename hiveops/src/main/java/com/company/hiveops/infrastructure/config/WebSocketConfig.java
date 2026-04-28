package com.company.hiveops.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita um broker em memória simples. O frontend vai escutar tópicos que começam com /topic
        config.enableSimpleBroker("/topic");
        // Prefixo para mensagens que o frontend envia para o backend (se necessário no futuro)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // O endpoint onde o cliente React vai se conectar: ws://localhost:8080/ws-hiveops
        registry.addEndpoint("/ws-hiveops")
                .setAllowedOriginPatterns("*") // Em produção, restrinja para o domínio do seu frontend
                .withSockJS(); // Fallback para navegadores antigos
    }
}
