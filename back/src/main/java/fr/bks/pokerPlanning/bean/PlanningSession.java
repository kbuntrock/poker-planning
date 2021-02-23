package fr.bks.pokerPlanning.bean;

import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlanningSession {


    private final UUID planningUuid = UUID.randomUUID();

    // private final UUID planningAdminKey = UUID.randomUUID();

    private WebSocketPrincipal creator;

    private final Map<String, User> connectedUsers = new ConcurrentHashMap<>();

    private final Map<String, Integer> votes = new ConcurrentHashMap<>();

    //private final List<Story> stories = new ArrayList<>();

    private String storyLabel;

    private boolean voteInProgress;

    private Instant lastActivity = Instant.now(); // usefull for automatic cleaning

    public UUID getPlanningUuid() {
        return planningUuid;
    }


    /*
    public UUID getPlanningAdminKey() {
        return planningAdminKey;
    }
    */
    public Map<String, User> getConnectedUsers() {
        return connectedUsers;
    }


    // public List<Story> getStories() {
    //    return stories;
    // }
    public Instant getLastActivity() {
        return lastActivity;
    }

    public void updateActivity() {
        this.lastActivity = Instant.now();
    }

    public String getStoryLabel() {
        return storyLabel;
    }

    public void setStoryLabel(String storyLabel) {
        this.storyLabel = storyLabel;
    }


    public Map<String, Integer> getVotes() {
        return votes;
    }

    public boolean isVoteInProgress() {
        return voteInProgress;
    }

    public void setVoteInProgress(boolean voteInProgress) {
        this.voteInProgress = voteInProgress;
    }

    public WebSocketPrincipal getCreator() {
        return creator;
    }

    public void setCreator(WebSocketPrincipal creator) {
        this.creator = creator;
    }
}
