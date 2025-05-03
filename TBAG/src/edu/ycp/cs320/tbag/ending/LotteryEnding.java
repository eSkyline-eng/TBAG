package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public class LotteryEnding implements EndingCondition {
    @Override
    public boolean isMet(Player player) {
        return player.hasAchievement("lottery_win");
    }

    @Override
    public String getEndingDescription() {
        return "You hit the jackpot! Money rained down, and you never had to work again.";
    }
}
