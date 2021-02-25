package fr.bks.pokerPlanning.bean;

import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlanningSession {

    private final UUID planningUuid = UUID.randomUUID();
    // private final UUID planningAdminKey = UUID.randomUUID();

    private Set<String> adminList = new HashSet<>();

    private final State state = new State();
    private final List<Story> stories = new ArrayList<>();

    private Instant lastActivity = Instant.now(); // usefull for automatic cleaning

    public UUID getPlanningUuid() {
        return planningUuid;
    }


    /*
    public UUID getPlanningAdminKey() {
        return planningAdminKey;
    }
    */

    public Instant getLastActivity() {
        return lastActivity;
    }

    public void updateActivity() {
        this.lastActivity = Instant.now();
    }

    public Set<String> getAdminList() {
        return adminList;
    }

    public void setAdminList(Set<String> adminList) {
        this.adminList = adminList;
    }

    public State getState() {
        return state;
    }

    public List<Story> getStories() {
        return stories;
    }
}
