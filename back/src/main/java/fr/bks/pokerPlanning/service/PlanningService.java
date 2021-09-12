package fr.bks.pokerPlanning.service;

import fr.bks.pokerPlanning.bean.*;
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
    private SecurityService securityService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    // Map UUID session / planning
    private final Map<UUID, PlanningSession> sessions = new ConcurrentHashMap<>();

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

    public PlanningSession createSession(final String userId) {
        PlanningSession newSession = new PlanningSession();
        newSession.getAdminList().add(userId);

        sessions.put(newSession.getPlanningUuid(), newSession);

        return newSession;
    }


    public void register(UUID planningUuid) {
        securityService.checkNotAnonymous();

        WebSocketPrincipal principal = securityService.getPrincipal();

        PlanningSession session = getSession(planningUuid);
        String wsId = principal.getWsId();

        User user = session.getState().getConnectedUsers().get(principal.getName());
        if (user == null) {
            user = new User(principal, session);
            session.getState().getConnectedUsers().put(principal.getName(), user);
        } else {
            // security check, first to register fixes the secret key
            if (!user.getSecretKey().equals(principal.getSecretKey())) {
                throw new SecurityException("You seems changed to me...");
            }
        }
        boolean wasConnected = user.isConnected();

        securityService.registerUser(user);

        if (!wasConnected) {
            sendToPlanning(session, MessageType.STATE);
        }

        sendToWsSession(user.getName(), wsId, session, MessageType.FULL);

        session.updateActivity();
    }

    public void disconnectUser(String wsId) {
        User user = securityService.getUser();
        if (user != null) {
            securityService.unregisterUser(user);

            if (!user.isConnected()) {
                sendToPlanning(user.getSession(), MessageType.FULL);
            }
        }
    }

    public void vote(UUID planningUuid, Integer value) {
        securityService.checkNotAnonymous()
                .checkBelongToPlanning(planningUuid);

        PlanningSession session = getSession(planningUuid);

        if (!session.getState().isVoteInProgress()) {
            throw new IllegalStateException("No vote is in progress");
        }

        session.getState().getVotes().put(securityService.getUser().getName(), value);
        session.updateActivity();

        sendToPlanning(session, MessageType.VOTE);
    }

    public void newStory(UUID planningUuid, String label) {
        securityService.checkNotAnonymous()
                .checkBelongToPlanning(planningUuid)
                .checkIfAdmin();

        PlanningSession session = getSession(planningUuid);
        State sessionState = session.getState();

        if (sessionState.isVoteInProgress()) {
            throw new IllegalStateException("Vote is already in progress");
        }

        sessionState.setVoteInProgress(true);

        sessionState.getVotes().clear();
        sessionState.setStoryLabel(label);

        sendToPlanning(session, MessageType.STATE);
    }


    public void reveal(UUID planningUuid) {
        securityService.checkNotAnonymous()
                .checkBelongToPlanning(planningUuid)
                .checkIfAdmin();

        PlanningSession session = getSession(planningUuid);
        State sessionState = session.getState();

        if (!sessionState.isVoteInProgress()) {
            throw new IllegalStateException("No vote is in progress");
        }


        // TODO : après le reveal on peux laisser l'admin faire le choix de la valeur retenue ou lancer un revote, ensuite ne fois validé c'est la ou on le met dans la lsite archivée
        Story story = new Story(sessionState.getStoryLabel(), sessionState.getVotes());
        session.getStories().add(story);

        sessionState.setVoteInProgress(false);

        sendToPlanning(session, MessageType.FULL);
    }

    private void sendToPlanning(PlanningSession session, MessageType type) {
        PlanningOutputMessage output = getPlanningOutputMessage(session, type);

        messagingTemplate.convertAndSend("/topic/planning/" + session.getPlanningUuid().toString(), output);
    }

    private void sendToWsSession(String userName, String wsId, PlanningSession session, MessageType type) {
        PlanningOutputMessage output = getPlanningOutputMessage(session, type);

        output.setMyVote(session.getState().getVotes().get(securityService.getUser().getName()));

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(wsId);
        headerAccessor.setLeaveMutable(true);

        messagingTemplate.convertAndSendToUser(userName, "/topic/planning/" + session.getPlanningUuid().toString(), output, headerAccessor.getMessageHeaders());
    }

    private PlanningOutputMessage getPlanningOutputMessage(PlanningSession session, MessageType type) {
        PlanningOutputMessage output = new PlanningOutputMessage();
        output.setType(type.name());

        if (MessageType.FULL.equals(type)) {
            output.setStories(session.getStories());
        }

        if (MessageType.FULL.equals(type) || MessageType.STATE.equals(type)) {
            output.setAdminList(session.getAdminList());
            output.setConnectedUsers(new ArrayList<>(session.getState().getConnectedUsers().values()));
            output.setStoryLabel(session.getState().getStoryLabel());
        }

        if (session.getState().isVoteInProgress()) {
            output.setVoted(session.getState().getVotes().keySet());
        } else {
            output.setVotes(session.getState().getVotes());
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
