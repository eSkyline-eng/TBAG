package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.tbag.model.NPC;

import java.util.HashSet;
import java.util.Set;

public class NPCTest {
    private NPC npc;

    @Before
    public void setUp() {
        npc = new NPC("Lou", "Hello!", "Need gas?", "Buy something!", 1, false, 1);
    }

    @Test
    public void testNPCInitialization() {
        assertEquals("Name should be set correctly", "Lou", npc.getName());
        assertEquals("Room ID should be set correctly", 1, npc.getRoomId());
        assertFalse("NPC should not have advanced dialogue", npc.hasAdvancedDialogue());
    }

    @Test
    public void testSettersAndGetters() {
        npc.setName("Bob");
        assertEquals("NPC name should update", "Bob", npc.getName());

        npc.setRoomId(5);
        assertEquals("Room ID should update", 5, npc.getRoomId());
    }

    @Test
    public void testDialogueRotation() {
        Set<String> dialoguesSeen = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            dialoguesSeen.add(npc.talk());
        }

        assertEquals("All three dialogues should appear", 3, dialoguesSeen.size());
    }

    @Test
    public void testDialogueShuffling() {
        String firstRun = npc.talk();
        String secondRun = npc.talk();
        npc.talk(); // Move past the third dialogue

        // Reset and shuffle dialogues
        String afterReset = npc.talk();

        assertNotEquals("Shuffled dialogues should not repeat the previous sequence", firstRun, secondRun);
    }
}
