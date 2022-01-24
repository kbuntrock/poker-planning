package fr.bks.pokerPlanning.websocket;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.security.Principal;

public class WebSocketPrincipal implements Principal {
    private final String id;

    private String displayName;
    private final String secretKey;

    private final String wsId;

    public WebSocketPrincipal(String id, String displayName, String secretKey, String wsId) {
        this.id = id;
        this.displayName = displayName;
        this.secretKey = secretKey;
        this.wsId = wsId;
    }

    @Override
    public String getName() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getWsId() {
        return wsId;
    }

    public static WebSocketPrincipal getFromHeader(StompHeaderAccessor accessor){
        return (WebSocketPrincipal) ((WebsocketAuthent) accessor.getUser()).getPrincipal();
    }

}
