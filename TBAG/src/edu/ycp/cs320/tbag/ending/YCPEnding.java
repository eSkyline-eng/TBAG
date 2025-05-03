package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class YCPEnding implements EndingCondition {
    @Override
    public boolean isMet(Player player) {
        return player.getCurrentRoom().getName().equalsIgnoreCase("School")
            && player.hasAchievement("professor_trigger");
    }

    @Override
    public String getEndingDescription() {
        return "After some strange twist of fate, you became a YCP professor. Office hours: always ignored.";
    }
}
