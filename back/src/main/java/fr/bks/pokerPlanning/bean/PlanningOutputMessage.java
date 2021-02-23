package fr.bks.pokerPlanning.bean;

import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlanningOutputMessage {

    private String type;

    private WebSocketPrincipal creator;

    private List<User> connectedUsers;

    private String storyLabel;

    private Set<String> voted;

    private Map<String, Integer> votes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<User> getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(List<User> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

    public String getStoryLabel() {
        return storyLabel;
    }

    public void setStoryLabel(String storyLabel) {
        this.storyLabel = storyLabel;
    }

    public Set<String> getVoted() {
        return voted;
    }

    public void setVoted(Set<String> voted) {
        this.voted = voted;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    public void setVotes(Map<String, Integer> votes) {
        this.votes = votes;
    }

    public WebSocketPrincipal getCreator() {
        return creator;
    }

    public void setCreator(WebSocketPrincipal creator) {
        this.creator = creator;
    }
}
