package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class KickedOutEnding implements EndingCondition {
    @Override
    public boolean isMet(Player player) {
    	return player.hasAchievement("Out of time");
    }

    @Override
    public String getEndingDescription() {
        return "Your time is up. You’ve been kicked out. Hope you enjoyed your last night indoors!";
    }
}
