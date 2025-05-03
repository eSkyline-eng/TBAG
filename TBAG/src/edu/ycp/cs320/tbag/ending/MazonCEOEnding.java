package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class MazonCEOEnding implements EndingCondition {
    @Override
    public boolean isMet(Player player) {
        return player.getCurrentRoom().getName().equalsIgnoreCase("Mazon")
            && player.checkInventory("resume")
            && player.hasAchievement("mazon_ceo_interview")
            && player.checkInventory("suit")
            && player.checkInventory("degree");
    }

    @Override
    public String getEndingDescription() {
        return "From packages to power — you're now the CEO of Mazon. Try not to union-bust.";
    }
}
