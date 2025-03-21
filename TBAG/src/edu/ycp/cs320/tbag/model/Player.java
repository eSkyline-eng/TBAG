package edu.ycp.cs320.tbag.model;

/**
 * Represents the player in the game.
 * The Player has a health value, a score, and a current room location.
 */
public class Player {
    private int health;
    private int score;
    private Room currentRoom;
    
    /**
     * Constructs a new Player with default health and score.
     */
    public Player() {
        this.health = 100;  // Default starting health
        this.score = 0;     // Default starting score
    }
    
    /**
     * Gets the current health of the player.
     * 
     * @return the player's health
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Sets the player's health.
     * 
     * @param health the new health value
     */
    public void setHealth(int health) {
        this.health = health;
    }
    
    /**
     * Gets the current score of the player.
     * 
     * @return the player's score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Adds points to the player's score.
     * 
     * @param points the points to add
     */
    public void addScore(int points) {
        score += points;
    }
    
    /**
     * Gets the room in which the player is currently located.
     * 
     * @return the current Room
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    /**
     * Sets the player's current room.
     * 
     * @param currentRoom the Room to set as the current location
     */
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }
}