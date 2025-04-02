package edu.ycp.cs320.tbag.events;

import edu.ycp.cs320.tbag.model.Player;

public class Dialogue extends Event {
    private String dialogue;
    
    public Dialogue(double probability, String dialogue) {
        super(probability);
        this.dialogue = dialogue;
    }
    
    @Override
    public String trigger(Player player) {
        return dialogue;
    }
}
