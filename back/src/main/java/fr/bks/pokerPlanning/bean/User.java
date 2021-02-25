package fr.bks.pokerPlanning.bean;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;

import java.util.ArrayList;
import java.util.List;

/**
 * A user on a planning (he can be connected multiple times on a planning)
 */
public class User {
    private final String name;
    private String displayName;

    @JsonIgnore
    private final String secretKey;

    @JsonIgnore
    private final PlanningSession session;
    @JsonIgnore
    private List<String> wsIdList = new ArrayList<>();

    public User(final WebSocketPrincipal principal, final PlanningSession session) {
        this.name = principal.getName();
        this.displayName = principal.getDisplayName();
        this.session = session;
        this.secretKey = principal.getSecretKey();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonGetter
    public boolean isConnected() {
        return !wsIdList.isEmpty();
    }

    /**
     * Register new wsSession with user
     *
     * @param wsId session id of websocket connection
     */
    public void connectBy(String wsId) {
        wsIdList.add(wsId);
    }

    public void disconnectBy(String wsId) {
        wsIdList.remove(wsId);
    }

    public PlanningSession getSession() {
        return session;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
