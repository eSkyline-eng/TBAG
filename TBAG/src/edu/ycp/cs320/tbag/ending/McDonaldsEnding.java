package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class McDonaldsEnding implements EndingCondition {
    @Override
    public boolean isMet(Player player) {
        return player.getCurrentRoom().getName().equalsIgnoreCase("McDonalds")
            && player.hasAchievement("has_resume")
            && player.hasAchievement("completed_interview");
    }

    @Override
    public String getEndingDescription() {
        return "You got a job at McDonald's! How exciting!";
    }
}

