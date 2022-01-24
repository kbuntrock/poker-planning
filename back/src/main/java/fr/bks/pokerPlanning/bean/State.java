package fr.bks.pokerPlanning.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class State {

    private final Map<String, Integer> votes = new ConcurrentHashMap<>();

    private String storyLabel;

    private boolean voteInProgress;

    public Map<String, Integer> getVotes() {
        return votes;
    }

    public String getStoryLabel() {
        return storyLabel;
    }

    public void setStoryLabel(String storyLabel) {
        this.storyLabel = storyLabel;
    }

    public boolean isVoteInProgress() {
        return voteInProgress;
    }

    public void setVoteInProgress(boolean voteInProgress) {
        this.voteInProgress = voteInProgress;
    }
}
