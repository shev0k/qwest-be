package com.qwest.backend.configuration.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final ConcurrentHashMap<String, Long> sessionActivity = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(handler -> new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
                sessionActivity.put(session.getId(), System.currentTimeMillis());
                sessions.put(session.getId(), session);
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
                sessionActivity.remove(session.getId());
                sessions.remove(session.getId());
                super.afterConnectionClosed(session, closeStatus);
            }

            @Override
            public void handleMessage(@NonNull WebSocketSession session, @NonNull org.springframework.web.socket.WebSocketMessage<?> message) throws Exception {
                sessionActivity.put(session.getId(), System.currentTimeMillis());
                super.handleMessage(session, message);
            }
        });
    }

    @Scheduled(fixedRate = 30000)
    public void checkInactiveSessions() {
        long currentTime = System.currentTimeMillis();
        sessionActivity.forEach((id, lastActiveTime) -> {
            if (currentTime - lastActiveTime > TimeUnit.MINUTES.toMillis(5)) {
                WebSocketSession session = sessions.remove(id);
                if (session != null) {
                    try {
                        session.close(CloseStatus.GOING_AWAY);
                    } catch (Exception e) {
                        logger.error("Error closing inactive session", e);
                    }
                }
            }
        });
    }
}
