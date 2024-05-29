package com.qwest.backend.configuration.websocket;

import com.qwest.backend.configuration.security.token.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String authToken = accessor.getFirstNativeHeader("Authorization");

        if (StringUtils.hasText(authToken) && authToken.startsWith("Bearer ")) {
            String token = authToken.substring(7);
            try {
                if (jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
                    Claims claims = jwtUtil.extractAllClaims(token);
                    accessor.setUser(new WebSocketPrincipal(claims.getSubject()));
                }
            } catch (Exception e) {
                // Handle token validation errors
                return null;
            }
        }
        return message;
    }
}
