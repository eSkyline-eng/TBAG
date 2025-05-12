package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class DeathEnding implements EndingCondition {
	@Override
    public boolean isMet(Player player) {
        return player.isGameOver();  // Uses the boolean from your Player class
    }

    @Override
    public String getEndingDescription() {
        return "You collapsed from your wounds. The teachers weren't lying when they said you'd never make it in the real world.";
    }
}
