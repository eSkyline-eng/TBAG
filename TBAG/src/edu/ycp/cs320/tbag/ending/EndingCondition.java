package edu.ycp.cs320.tbag.ending;

import edu.ycp.cs320.tbag.model.Player;

public interface EndingCondition {
    boolean isMet(Player player);
    String getEndingDescription();
}


