package fr.bks.pokerPlanning.service;

import fr.bks.pokerPlanning.bean.PlanningOutputMessage;
import fr.bks.pokerPlanning.bean.PlanningSession;
import fr.bks.pokerPlanning.bean.User;
import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlanningService {

    private static Logger LOG = LoggerFactory.getLogger(PlanningService.class);

    public static final String CLEAN_JOB_FREQUENCY_EL = "#{${planner.planning.clean-frequency-second}*1000}";

    @Value("${planner.planning.max-age-hour}")
    public int planningMaxAgeHour;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    // Map UUID session / planning
    private final Map<UUID, PlanningSession> sessions = new ConcurrentHashMap<>();

    // Map websocket Id / User
    private final Map<String, User> wsIdToUser = new ConcurrentHashMap<>();

    private enum MessageType {
        FULL,
        STATE,
        VOTE;
    }

    private PlanningSession getSession(UUID sessionUuid) {
        PlanningSession planningSession = sessions.get(sessionUuid);
        if (planningSession == null) {
            throw new IllegalStateException("Session does not exists");
        }
        return planningSession;
    }

    public PlanningSession createSession(final String userId, final String username) {
        WebSocketPrincipal principal = new WebSocketPrincipal(userId);
        principal.setDisplayName(username);
        PlanningSession newSession = new PlanningSession();
        newSession.setCreator(principal);

        sessions.put(newSession.getPlanningUuid(), newSession);

        return newSession;
    }


    public void register(UUID planningUuid, WebSocketPrincipal principal, String wsId) {
        // todo vérifier pas déjà enregistré ?
        PlanningSession session = getSession(planningUuid);

        User user = session.getConnectedUsers().get(principal.getName());
        if (user == null) {
            user = new User(principal, session);
            session.getConnectedUsers().put(principal.getName(), user);
        }
        wsIdToUser.put(wsId, user);

        boolean wasConnected = user.isConnected();
        user.connectBy(wsId);
        if (!wasConnected) {
            sendToPlanning(session, MessageType.STATE);
        }

        sendToWsSession(user.getName(), wsId, session, MessageType.FULL);

        session.updateActivity();
    }

    public void disconnectUser(String wsId) {
        User user = wsIdToUser.get(wsId);
        user.disconnectBy(wsId);

        if (!user.isConnected()) {
            sendToPlanning(user.getSession(), MessageType.FULL);
        }
    }

    public void vote(UUID planningUuid, WebSocketPrincipal principal, Integer value) {
        PlanningSession session = getSession(planningUuid);

        if (!session.isVoteInProgress()) {
            throw new IllegalStateException("No vote is in progress");
        }

        String userUuid = principal.getName();

        if (session.getConnectedUsers().values().stream().noneMatch(u -> u.getName().equals(userUuid))) {
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
        PlanningOutputMessage output = getPlanningOutputMessage(session, type);

        messagingTemplate.convertAndSend("/topic/planning/" + session.getPlanningUuid().toString(), output);
    }

    private void sendToWsSession(String userName, String wsId, PlanningSession session, MessageType type) {
        PlanningOutputMessage output = getPlanningOutputMessage(session, type);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(wsId);
        headerAccessor.setLeaveMutable(true);

        messagingTemplate.convertAndSendToUser(userName, "/topic/planning/" + session.getPlanningUuid().toString(), output, headerAccessor.getMessageHeaders());
    }

    private PlanningOutputMessage getPlanningOutputMessage(PlanningSession session, MessageType type) {
        PlanningOutputMessage output = new PlanningOutputMessage();
        output.setType(type.name());

        if (MessageType.FULL.equals(type) || MessageType.STATE.equals(type)) {
            output.setCreator(session.getCreator());
            output.setConnectedUsers(new ArrayList<>(session.getConnectedUsers().values()));
            output.setStoryLabel(session.getStoryLabel());
        }

        if (session.isVoteInProgress()) {
            output.setVoted(session.getVotes().keySet());
        } else {
            output.setVotes(session.getVotes());
        }
        return output;
    }

    @Scheduled(fixedDelayString = CLEAN_JOB_FREQUENCY_EL, initialDelayString = CLEAN_JOB_FREQUENCY_EL)
    public void cleanOldPlannings() {
        LOG.info("Clean old plannings job");

        Instant timeLimit = Instant.now().minus(planningMaxAgeHour, ChronoUnit.HOURS);

        int prevSize = sessions.size();
        sessions.entrySet().removeIf(e -> e.getValue().getLastActivity().isBefore(timeLimit));

        LOG.info("  " + sessions.size() + " plannings remaining (" + (prevSize - sessions.size()) + " deleted)");
    }

}
