package fr.bks.pokerPlanning.bean;

import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlanningOutputMessage {

    private String type;

    private List<WebSocketPrincipal> connectedUsers;

    private String storyLabel;

    private Map<String, Integer> votes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WebSocketPrincipal> getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(List<WebSocketPrincipal> connectedUsers) {
        this.connectedUsers = connectedUsers;
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

    public void setVotes(Map<String, Integer> votes) {
        this.votes = votes;
    }
}
