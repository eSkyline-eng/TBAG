package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class WallMartEnding implements EndingCondition {
    @Override
    public boolean isMet(Player player) {
        return player.getCurrentRoom().getName().equalsIgnoreCase("Wall Mart")
            && player.checkInventory("resume");
    }

    @Override
    public String getEndingDescription() {
        return "Welcome to Wall Mart! You've officially joined the retail underworld.";
    }
}
