package fr.bks.pokerPlanning.core;

import fr.bks.pokerPlanning.bean.PlannerSession;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("singleton")
public class PlannerSessionsHolder {

    private final Map<UUID, PlannerSession> sessions = new ConcurrentHashMap<>();

    public PlannerSession createSession() {
        PlannerSession newSession = new PlannerSession();

        sessions.put(newSession.getPlanningUuid(), newSession);

        return newSession;
    }

    public PlannerSession getSession(UUID sessionUuid) {
        return sessions.get(sessionUuid);
    }
}
