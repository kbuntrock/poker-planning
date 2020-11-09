package fr.bks.pokerPlanning.bean;

import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlannerSession {

    private enum MessageType {
        FULL,
        VOTE
    }


    private final UUID planningUuid = UUID.randomUUID();

    private final List<WebSocketPrincipal> connectedUsers = Collections.synchronizedList(new ArrayList<>());

    private final Map<String, Integer> votes = new ConcurrentHashMap<>();

    //private final List<Story> stories = new ArrayList<>();

    private Instant lastActivity = Instant.now(); // usefull for automatic cleaning (TODO)

    public UUID getPlanningUuid() {
        return planningUuid;
    }

    public List<WebSocketPrincipal> getConnectedUsers() {
        return connectedUsers;
    }

   // public List<Story> getStories() {
   //    return stories;
   // }

    public Instant getLastActivity() {
        return lastActivity;
    }

    private void updateActivity() {
        this.lastActivity = Instant.now();
    }

    public PlanningOutputMessage register(WebSocketPrincipal user) {
        connectedUsers.add(user);
        updateActivity();

        return getState(MessageType.FULL);
    }

    public PlanningOutputMessage vote(String userUuid, Integer value) {
        if (connectedUsers.stream().noneMatch(u -> u.getName().equals(userUuid))) {
            // TODO erreur on est pas enregistré
        }
        votes.put(userUuid, value);
        updateActivity();

        return getState(MessageType.VOTE);
    }

    private PlanningOutputMessage getState(MessageType type) {
        PlanningOutputMessage output = new PlanningOutputMessage();
        output.setType(type.name());

        if (MessageType.FULL.equals(type)) {
            output.setConnectedUsers(connectedUsers);
        }
        output.setVotes(votes);

        return output;
    }
}
