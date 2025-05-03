package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class RatKingEnding implements EndingCondition {
    @Override
    public boolean isMet(Player player) {
        return player.checkInventory("crowbar")
            && player.hasAchievement("defeated_rat_king");
    }

    @Override
    public String getEndingDescription() {
        return "You slew the Rat King and claimed his sewer throne. May your rule be plague-free.";
    }
}
