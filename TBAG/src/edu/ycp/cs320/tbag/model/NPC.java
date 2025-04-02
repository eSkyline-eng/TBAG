package edu.ycp.cs320.tbag.model;

public class NPC extends Character {
    private String name;
    private String dialogue; // What the NPC says when spoken to

    public NPC(String name, String dialogue) {
        super(); // Initialize inherited attributes (like health and inventory)
        this.name = name;
        this.dialogue = dialogue;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDialogue() {
        return dialogue;
    }

    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
    }

    // Method for player to interact with the NPC
    public String talk() {
        return dialogue; // Returns what the NPC says
    }
}


