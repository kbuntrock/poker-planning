package fr.bks.pokerPlanning.websocket;

import java.security.Principal;

public class WebSocketPrincipal implements Principal {
    private String name;

    private String displayName;

    public WebSocketPrincipal(String name) {
        if (name == null) {
            throw new NullPointerException("null name is illegal");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
