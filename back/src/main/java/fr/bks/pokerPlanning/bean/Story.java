package fr.bks.pokerPlanning.bean;

import java.util.Map;

public class Story {
    private final String name;
    private final Map<String, Integer> votes;

    private Integer chosenValue;

    public Story(String name, Map<String, Integer> votes) {
        this.name = name;
        this.votes = Map.copyOf(votes);
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    public Integer getChosenValue() {
        return chosenValue;
    }

    public void setChosenValue(Integer chosenValue) {
        this.chosenValue = chosenValue;
    }

}
