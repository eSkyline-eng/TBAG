package edu.ycp.cs320.tbag.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The GameEngine is the heart of the text-based adventure game.
 * It initializes the game state, processes commands, and maintains a transcript of the game.
 */
public class GameEngine {
    private Room currentRoom;
    private Player player;
    private StringBuilder transcript;
    
    /**
     * Constructs a new GameEngine and initializes the game state.
     */
    public GameEngine() {
        transcript = new StringBuilder();
        initGame();
    }
    
    /**
     * Initializes the game state.
     * Creates sample rooms, establishes connections, sets the starting room and player, and records the initial transcript.
     */
    private void initGame() {
        // Create sample rooms.
        Room entrance = new Room(1, "Entrance", "You are at the entrance of a mysterious building.");
        Room lobby = new Room(2, "Lobby", "You are in a spacious lobby filled with antique decorations.");
        Room hallway = new Room(3, "Hallway", "A dimly lit hallway stretches before you.");
        
        // Define connections (node-based; not a simple grid)
        entrance.addConnection("north", lobby);
        lobby.addConnection("south", entrance);
        lobby.addConnection("east", hallway);
        hallway.addConnection("west", lobby);
        
        // Set the current room and initialize the player
        currentRoom = entrance;
        player = new Player();
        player.setCurrentRoom(currentRoom);
        
        // Record the starting transcript
        transcript.setLength(0); // clear previous transcript if any
        transcript.append(currentRoom.getLongDescription()).append("\n");
    }
    
    /**
     * Processes a player's command and updates the game state.
     * Supported commands: 
     *  - "go [direction]" moves the player if possible.
     *  - "look" displays the current room's description.
     *  - "help" provides a list of available commands.
     *  - "restart" reinitializes the game.
     * 
     * @param command the player's command as a String
     * @return the result/output of the command
     */
    public String processCommand(String command) {
        String output = "";
        command = command.trim().toLowerCase();
        
        if (command.startsWith("go ")) {
            String direction = command.substring(3);
            Room nextRoom = currentRoom.getConnection(direction);
            if (nextRoom != null) {
                currentRoom = nextRoom;
                player.setCurrentRoom(currentRoom);
                output = currentRoom.getLongDescription();
            } else {
                output = "You cannot go that way.";
            }
        } else if (command.equals("look")) {
            output = currentRoom.getLongDescription();
        } else if (command.equals("help")) {
            output = "Available commands:\n" +
                     "  go [direction] - move in a specified direction (e.g., 'go north')\n" +
                     "  look           - look around the current room\n" +
                     "  restart        - restart the game\n" +
                     "  help           - display this help message";
        } else if (command.equals("restart")) {
            initGame();
            output = "Game restarted.\n" + currentRoom.getLongDescription();
        } else {
            output = "I don't understand that command.";
        }
        
        // Append the command and output to the transcript.
        transcript.append("> ").append(command).append("\n");
        transcript.append(output).append("\n");
        return output;
    }
    
    /**
     * Retrieves the full transcript of the game.
     * The transcript includes player commands and game responses.
     *
     * @return the game transcript as a String
     */
    public String getTranscript() {
        return transcript.toString();
    }
    
    /**
     * Returns the current player.
     *
     * @return the Player object
     */
    public Player getPlayer() {
        return player;
    }
}