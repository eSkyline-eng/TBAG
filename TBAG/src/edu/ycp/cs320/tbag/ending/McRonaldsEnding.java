package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class McRonaldsEnding implements EndingCondition {
    @Override
    public boolean isMet(Player player) {
        return player.getCurrentRoom().getName().equalsIgnoreCase("McRonalds")
        	&& player.checkInventory("resume");
    }

    @Override
    public String getEndingDescription() {
        return "You got a job at McRonald's! Smell that fry grease pride!";
    }
}

