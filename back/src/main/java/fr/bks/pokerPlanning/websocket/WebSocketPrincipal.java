package fr.bks.pokerPlanning.websocket;

import java.security.Principal;
import java.util.UUID;

public class WebSocketPrincipal implements Principal {
    private String id;

    private String displayName;
    private String secretKey;

    public WebSocketPrincipal(String id, String displayName, String secretKey) {
        this.id = id;
        this.displayName = displayName;
        this.secretKey = secretKey;
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

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
