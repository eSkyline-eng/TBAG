package edu.ycp.cs320.tbag.controller;

import edu.ycp.cs320.tbag.model.GameEngine;
import edu.ycp.cs320.tbag.model.Player;

public class GameController {
    private GameEngine gameEngine;
    
    public GameController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    public String processCommand(String command) {
        return gameEngine.processCommand(command);
    }
    
    public String getTranscript() {
        return gameEngine.getTranscript();
    }
    
    public GameEngine getGameEngine() {
        return gameEngine;
    }

    /**
     * Retrieves the current Player from the GameEngine.
     *
     * @return the Player object, or null if none is set
     */
    public Player getPlayer() {
        return gameEngine.getPlayer();
    }
    
    /**
     * Restarts the game by invoking the "restart" command.
     */
    public void restartGame() {
        processCommand("restart");
    }
}
