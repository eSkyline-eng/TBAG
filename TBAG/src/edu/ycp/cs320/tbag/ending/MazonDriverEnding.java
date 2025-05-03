package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class MazonDriverEnding implements EndingCondition {
    @Override
    public boolean isMet(Player player) {
        return player.getCurrentRoom().getName().equalsIgnoreCase("Mazon")
            && player.checkInventory("resume")
            && player.hasAchievement("mazon_interview");
    }

    @Override
    public String getEndingDescription() {
        return "You’ve become a Mazon driver. You now fear porch steps and wet driveways.";
    }
}
