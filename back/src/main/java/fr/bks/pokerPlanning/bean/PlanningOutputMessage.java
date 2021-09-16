package fr.bks.pokerPlanning.bean;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlanningOutputMessage {

    private String type;

    private List<Story> stories;
    private List<Integer> voteValues;

    private Set<String> adminList;
    private List<User> connectedUsers;
    private String storyLabel;
    private Set<String> voted;
    private Integer myVote;
    private Map<String, Integer> votes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Story> getStories() {
        return stories;
    }

    public List<Integer> getVoteValues() {
        return voteValues;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public void setVoteValues(List<Integer> voteValues) {
        this.voteValues = voteValues;
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

    public Integer getMyVote() {
        return myVote;
    }

    public void setMyVote(Integer myVote) {
        this.myVote = myVote;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    public void setVotes(Map<String, Integer> votes) {
        this.votes = votes;
    }

    public Set<String> getAdminList() {
        return adminList;
    }

    public void setAdminList(Set<String> adminList) {
        this.adminList = adminList;
    }
}
