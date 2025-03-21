package edu.ycp.cs320.tbag.controller;

import edu.ycp.cs320.tbag.model.GameEngine;

/**
 * The GameController mediates between the web layer (servlets)
 * and the game logic (GameEngine). It processes commands from the user
 * and provides the updated transcript of game events.
 */
public class GameController {
    private GameEngine gameEngine;
    
    /**
     * Constructs a new GameController with the specified GameEngine.
     *
     * @param gameEngine the game engine to control
     */
    public GameController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    /**
     * Processes the user's command by delegating to the GameEngine.
     *
     * @param command the user command to process
     * @return the output/result after processing the command
     */
    public String processCommand(String command) {
        return gameEngine.processCommand(command);
    }
    
    /**
     * Retrieves the full transcript of the game, which includes
     * all the commands entered by the player and the game responses.
     *
     * @return the game transcript
     */
    public String getTranscript() {
        return gameEngine.getTranscript();
    }
}