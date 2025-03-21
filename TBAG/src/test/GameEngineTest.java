package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import edu.ycp.cs320.tbag.model.GameEngine;

public class GameEngineTest {
	private GameEngine engine;
	
	@Before
	public void setUp() {
		engine = new GameEngine();
	}
	
	
	// Testing movement
	@Test
	public void testPlayerNorth() {
        assertEquals("Entrance", engine.getPlayer().getCurrentRoom().getName());
	}
	
	@Test
	public void testChainMovement() {
		engine.processCommand("go north");
		engine.processCommand("go east");
		assertEquals("Hallway", engine.getPlayer().getCurrentRoom().getName());
	}
	
	@Test
	public void testInvalidMovement() {
		String output = engine.processCommand("go east");
		assertEquals("You cannot go that way.", output);
	}
	
	
	
	@Test
	public void testLook() {
		engine.processCommand("go north");
		String output = engine.processCommand("look");
		assertEquals("You are in a spacious lobby filled with antique decorations.", output);
	}
	
	
	@Test
	public void testHelp() {
		String output = engine.processCommand("help");
		assertEquals("Available commands:\n" +
                "  go [direction] - move in a specified direction (e.g., 'go north')\n" +
                "  look           - look around the current room\n" +
                "  restart        - restart the game\n" +
                "  help           - display this help message", output);
	}
	
	@Test
	public void testRestartGame() {
		// Change player's room by moving from entrance to lobby
        engine.processCommand("go north");
        assertEquals("Lobby", engine.getPlayer().getCurrentRoom().getName());

        // Restart the game
        engine.processCommand("restart");
        
        // After restart, the game should be back at the entrance
        assertEquals("Entrance", engine.getPlayer().getCurrentRoom().getName());
		
	}
}
