package fr.bks.pokerPlanning.websocket;

import fr.bks.pokerPlanning.service.PlanningService;
import fr.bks.pokerPlanning.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.WebSocketAnnotationMethodMessageHandler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static String TOPIC_PLANNING_PREFIX = "/topic/planning/**";
    public static String USER_TOPIC_PLANNING_PREFIX = "/user" + TOPIC_PLANNING_PREFIX;
    public static String USER_TOPIC_ERROR_PREFIX = "/user/topic/error";
    public static String APP_DESTINATION_PREFIX = "/app";

    private static final String USER_ID_HEADER_NAME = "userId";
    private static final String USERKEY_HEADER_NAME = "userKey";
    private static final String USERNAME_HEADER_NAME = "username";

    @Autowired
    private PlanningService planningService;

    @Autowired
    private SecurityService securityService;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes(APP_DESTINATION_PREFIX);
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/api/websocket")
                .setAllowedOrigins("http://localhost:4200") // Autorisation des CORS pour le devmode
                .withSockJS()
                //.setWebSocketEnabled(false)
        ;
    }

    /**
     * https://stackoverflow.com/questions/45405332/websocket-authentication-and-authorization-in-spring
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ExecutorChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if(accessor != null && accessor.getCommand() != null) {
                    StompCommand command = accessor.getCommand();
                    if(StompCommand.CONNECT == command) {
                        String userId = accessor.getFirstNativeHeader(USER_ID_HEADER_NAME);
                        String userKey = accessor.getFirstNativeHeader(USERKEY_HEADER_NAME);
                        String encodedUserName = accessor.getFirstNativeHeader(USERNAME_HEADER_NAME);
                        String userName = URLDecoder.decode(encodedUserName, StandardCharsets.UTF_8);

                        WebSocketPrincipal principal = new WebSocketPrincipal(userId, userName, userKey, accessor.getSessionId());
                        accessor.setUser(new WebsocketAuthent(principal));
                    } else if(StompCommand.DISCONNECT == command){
                        securityService.registerPrincipal(WebSocketPrincipal.getFromHeader(accessor));
                        planningService.disconnectUser();
                    }
                }


                return message;
            }

            @Override
            public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
                if (handler instanceof WebSocketAnnotationMethodMessageHandler) {
                    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                    if(accessor.getCommand() != null) {
                        securityService.registerPrincipal((WebSocketPrincipal) ((WebsocketAuthent) accessor.getUser()).getPrincipal());
                    }
                }

                return message;
            }

            @Override
            public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler, Exception ex) {
                if (handler instanceof WebSocketAnnotationMethodMessageHandler) {
                    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                    if(accessor.getCommand() != null) {
                            securityService.unRegisterPrincipal((WebSocketPrincipal) ((WebsocketAuthent) accessor.getUser()).getPrincipal());
                    }
                }
            }
        });

    }

}
