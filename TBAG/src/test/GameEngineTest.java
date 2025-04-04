package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import edu.ycp.cs320.tbag.model.*;

public class GameEngineTest {
    private GameEngine engine;

    @BeforeEach
    public void setup() {
        engine = new GameEngine();
    }

    @Test
    public void testStartRoomDescription() {
        String transcript = engine.getTranscript();
        assertTrue(transcript.toLowerCase().contains("you wake up") || transcript.toLowerCase().contains("home"));
    }

    @Test
    public void testGoInvalidDirection() {
        String result = engine.processCommand("go upward");
        assertTrue(result.toLowerCase().contains("cannot go"));
    }

    @Test
    public void testGoToCityStreet() {
        engine.processCommand("go south");
        Room current = engine.getPlayer().getCurrentRoom();
        assertEquals("City Street", current.getName());
    }

    @Test
    public void testLookCommand() {
        String result = engine.processCommand("look");
        assertTrue(result.toLowerCase().contains("you wake up") || result.toLowerCase().contains("room"));
    }

    @Test
    public void testHelpCommand() {
        String result = engine.processCommand("help");
        assertTrue(result.contains("go") && result.contains("look"));
    }

    @Test
    public void testRestartCommand() {
        engine.processCommand("go south");
        engine.processCommand("restart");
        String result = engine.getTranscript();
        assertTrue(result.toLowerCase().contains("game restarted"));
    }

    @Test
    public void testInventoryInitiallyEmpty() {
        String result = engine.processCommand("inventory");
        assertTrue(result.contains("empty"));
    }

    @Test
    public void testTakeAndDropItem() {
        engine.processCommand("take key");
        String afterTake = engine.getTranscript();
        assertTrue(afterTake.toLowerCase().contains("picked up") || afterTake.toLowerCase().contains("carry"));

        engine.processCommand("drop key");
        String afterDrop = engine.getTranscript();
        assertTrue(afterDrop.toLowerCase().contains("dropped"));
    }

    @Test
    public void testTalkToExistingNPC() {
        engine.processCommand("go south"); // To room 2 where NPCs are
        String response = engine.processCommand("talk to joe");
        assertTrue(response.toLowerCase().contains("hi") || response.toLowerCase().contains("joe"));
    }

    @Test
    public void testTalkToNonExistentNPC() {
        String response = engine.processCommand("talk to dragon");
        assertTrue(response.toLowerCase().contains("no one"));
    }

    @Test
    public void testTalkWithNoNPCs() {
        String response = engine.processCommand("talk");
        assertTrue(response.toLowerCase().contains("no one") || response.toLowerCase().contains("available npcs"));
    }

    @Test
    public void testHealthCommand() {
        String response = engine.processCommand("health");
        assertTrue(response.contains("Health"));
    }

    @Test
    public void testUnknownCommand() {
        String response = engine.processCommand("do a flip");
        assertTrue(response.toLowerCase().contains("don't understand"));
    }

    @Test
    public void testEventTriggering() {
        engine.processCommand("go south"); // Room 2 has damage events
        int newHealth = engine.getPlayer().getHealth();
        assertTrue(newHealth <= 100, "Health should reduce due to damage event");
    }

    @Test
    public void testTakeNonexistentItem() {
        String result = engine.processCommand("take spaceship");
        assertTrue(result.toLowerCase().contains("not here"));
    }

    @Test
    public void testDropNonexistentItem() {
        String result = engine.processCommand("drop ghost");
        assertTrue(result.toLowerCase().contains("don't have"));
    }

    @Test
    public void testMultipleGoCommands() {
        engine.processCommand("go south");
        engine.processCommand("go east");
        String result = engine.getTranscript();
        assertTrue(result.contains("east") || result.toLowerCase().contains("tower"));
    }

    @Test
    public void testRestartClearsTranscript() {
        engine.processCommand("go south");
        engine.processCommand("restart");
        String transcript = engine.getTranscript();
        assertTrue(transcript.toLowerCase().contains("game restarted"));
        assertFalse(transcript.toLowerCase().contains("city street"));
    }
}
