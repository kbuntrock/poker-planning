package fr.bks.pokerPlanning.service;

import fr.bks.pokerPlanning.bean.PlanningOutputMessage;
import fr.bks.pokerPlanning.bean.PlanningSession;
import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlanningService {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private final Map<UUID, PlanningSession> sessions = new ConcurrentHashMap<>();


    private enum MessageType {
        FULL,
        VOTE;
    }

    private PlanningSession getSession(UUID sessionUuid) {
        PlanningSession planningSession = sessions.get(sessionUuid);
        if (planningSession == null) {
            throw new IllegalStateException("Session does not exists");
        }
        return planningSession;
    }

    public PlanningSession createSession() {
        PlanningSession newSession = new PlanningSession();

        sessions.put(newSession.getPlanningUuid(), newSession);

        return newSession;
    }


    public void register(UUID planningUuid, WebSocketPrincipal principal, String name) {
        // todo vérifier pas déjà enregistré ?
        PlanningSession session = getSession(planningUuid);

        principal.setDisplayName(name);

        session.getConnectedUsers().add(principal);
        session.updateActivity();

        sendToPlanning(session, MessageType.FULL);
    }

    public void vote(UUID planningUuid, WebSocketPrincipal principal, Integer value) {
        PlanningSession session = getSession(planningUuid);

        if (!session.isVoteInProgress()) {
            throw new IllegalStateException("No vote is in progress");
        }

        String userUuid = principal.getName();

        if (session.getConnectedUsers().stream().noneMatch(u -> u.getName().equals(userUuid))) {
            // TODO erreur on est pas enregistré
        }

        session.getVotes().put(userUuid, value);
        session.updateActivity();

        sendToPlanning(session, MessageType.VOTE);
    }

    public void newStory(UUID planningUuid, String label) {
        PlanningSession session = getSession(planningUuid);

        if (session.isVoteInProgress()) {
            throw new IllegalStateException("Vote is already in progress");
        }

        session.setVoteInProgress(true);

        session.getVotes().clear();
        session.setStoryLabel(label);

        sendToPlanning(session, MessageType.FULL);
    }

    public void reveal(UUID planningUuid) {
        PlanningSession session = getSession(planningUuid);

        if (!session.isVoteInProgress()) {
            throw new IllegalStateException("No vote is in progress");
        }

        session.setVoteInProgress(false);

        sendToPlanning(session, MessageType.FULL);
    }

    private void sendToPlanning(PlanningSession session, MessageType type) {
        PlanningOutputMessage output = new PlanningOutputMessage();
        output.setType(type.name());

        if (MessageType.FULL.equals(type)) {
            output.setConnectedUsers(session.getConnectedUsers());
            output.setStoryLabel(session.getStoryLabel());
        }
        output.setVotes(session.getVotes());

        messagingTemplate.convertAndSend("/topic/planning/" + session.getPlanningUuid().toString(), output);
    }
}
