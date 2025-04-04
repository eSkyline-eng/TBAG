package edu.ycp.cs320.tbag.events;

import edu.ycp.cs320.tbag.model.Player;

public class Damage extends Event {
    private String dialogue;
    private int damage;

    public Damage(double probability, String dialogue, int damage) {
        super(probability);
        this.dialogue = dialogue;
        this.damage = damage;
    }

    @Override
    public String trigger(Player player) {
        player.setHealth((player.getHealth()-damage));
        return dialogue;
    }
}

