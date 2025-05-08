package edu.ycp.cs320.tbag.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NPC extends Character {
    private String name;
    private boolean hasAdvancedDialogue;
    private int startingDialogueId;
    private String dialogue1;
    private String dialogue2;
    private String dialogue3;
    private int roomId;
    private int currentIndex = 0;
    private List<String> dialogues = new ArrayList<>();

    public NPC(String name, String dialogue1, String dialogue2, String dialogue3, int roomId, boolean hasAdvancedDialogue, Integer startingDialogueId) {
        super(); // Initialize inherited attributes (like health and inventory)
        this.name = name;
        this.roomId = roomId;
        
        this.hasAdvancedDialogue = hasAdvancedDialogue;
        this.startingDialogueId = startingDialogueId;
        
        // Store basic dialogue if applicable
        if (!hasAdvancedDialogue) {
            this.dialogue1 = dialogue1;
            this.dialogue2 = dialogue2;
            this.dialogue3 = dialogue3;
            dialogues.add(dialogue1);
            dialogues.add(dialogue2);
            dialogues.add(dialogue3);
            Collections.shuffle(dialogues);
        }
    }

    public boolean hasAdvancedDialogue() {
        return hasAdvancedDialogue;
    }

    public int getStartingDialogueId() {
        return startingDialogueId;
    }

    
    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getRoomId() { return roomId; }
    
    public void setRoomId(int roomId) { this.roomId = roomId; }

    // Method for player to interact with the NPC
    public String talk() {
    	// Initialize the dialogue list if empty (first run)
        if (dialogues.isEmpty()) {
            dialogues.add(dialogue1);
            dialogues.add(dialogue2);
            dialogues.add(dialogue3);
            Collections.shuffle(dialogues);
            currentIndex = 0;
        }

        String dialogue = dialogues.get(currentIndex);
        currentIndex++;

        // Reset and shuffle after all options are shown once
        if (currentIndex >= dialogues.size()) {
            Collections.shuffle(dialogues);
            currentIndex = 0;
        }

        return dialogue;
    }

}


