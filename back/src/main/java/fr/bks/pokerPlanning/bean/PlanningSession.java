package fr.bks.pokerPlanning.bean;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlanningSession {

    private final UUID planningUuid = UUID.randomUUID();

    // Le nom de cette session
    private String name;

    private final Map<String, User> connectedUsers = new ConcurrentHashMap<>();
    private Set<String> adminList = ConcurrentHashMap.newKeySet();

    // L'US en cours de vote
    private final State state = new State();
    // Les US déjà votées (historique)
    private final List<Story> stories = new ArrayList<>();
    private final List<Integer> voteValues = List.of(1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144);

    private Instant lastActivity = Instant.now(); // usefull for automatic cleaning

    public UUID getPlanningUuid() {
        return planningUuid;
    }

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

    public List<Integer> getVoteValues() {
        return voteValues;
    }

    public Map<String, User> getUsers() {
        return connectedUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
