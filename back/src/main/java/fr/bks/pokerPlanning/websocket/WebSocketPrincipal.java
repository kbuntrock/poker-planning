package fr.bks.pokerPlanning.websocket;

import java.security.Principal;

public class WebSocketPrincipal implements Principal {
    private String id;

    private String displayName;

    public WebSocketPrincipal(String id) {
        if (id == null) {
            throw new NullPointerException("null id is illegal");
        }
        this.id = id;
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

}
