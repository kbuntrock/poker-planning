package fr.bks.pokerPlanning.websocket;

import fr.bks.pokerPlanning.service.PlanningService;
import fr.bks.pokerPlanning.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String USER_ID_COOKIE_NAME = "userId";
    private static final String USERKEY_COOKIE_NAME = "userKey";
    private static final String USERNAME_COOKIE_NAME = "username";

    @Autowired
    private PlanningService planningService;

    @Autowired
    private SecurityService securityService;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/websocket")
                .setAllowedOrigins("http://localhost:4200") // Autorisation des CORS pour le devmode
                .withSockJS()
                .setInterceptors(httpSessionHandshakeInterceptor())
                //.setWebSocketEnabled(false)
        ;
    }

    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                if (request instanceof ServletServerHttpRequest) {
                    HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

                    forwardValue(USER_ID_COOKIE_NAME, servletRequest, attributes);
                    forwardValue(USERKEY_COOKIE_NAME, servletRequest, attributes);
                    forwardValue(USERNAME_COOKIE_NAME, servletRequest, attributes);
                }
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }

    private void forwardValue(String paramName, HttpServletRequest servletRequest, Map<String, Object> attributes) {
        Cookie token = WebUtils.getCookie(servletRequest, paramName);
        if (token != null) {
            attributes.put(paramName, token.getValue());
        }
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                switch (accessor.getCommand()) {
                    case CONNECT:
                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                        String userId = (String) sessionAttributes.get(USER_ID_COOKIE_NAME);
                        String userKey = (String) sessionAttributes.get(USERKEY_COOKIE_NAME);
                        String userName = (String) sessionAttributes.get(USERNAME_COOKIE_NAME);

                        WebSocketPrincipal principal = new WebSocketPrincipal(userId, userName, userKey, accessor.getSessionId());
                        accessor.setUser(principal);

                        break;
                    case DISCONNECT:
                        securityService.registerPrincipal((WebSocketPrincipal) accessor.getUser());
                        planningService.disconnectUser(accessor.getSessionId());
                        break;
                    default:
                        // nothing
                }

                return message;
            }
        });

    }

}
