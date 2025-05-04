package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

/**
 * Lottery ending: when the player has the 'lottery_win' achievement,
 * we signal the game-over forwarding by returning "__ENDING_ACCEPTED__".
 */
public class LotteryEnding implements EndingCondition {
    public static final String ACCEPT_CODE = "__ENDING_ACCEPTED__";

    @Override
    public boolean isMet(Player player) {
        return player.hasAchievement("lottery_win");
    }

    @Override
    public String getEndingDescription() {
        return "You hit the jackpot! Money rained down, and you never had to work again.";
    }

    /**
     * Called when the lottery is won. Returns ACCEPT_CODE to trigger the servlet forward.
     */
    public String apply(Player player) {
        // Mark ending in the engine (so getEndingDescription() is set)
        return ACCEPT_CODE;
    }
}
