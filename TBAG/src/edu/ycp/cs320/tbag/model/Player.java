package edu.ycp.cs320.tbag.model;

public class Player extends Character {
    private int score; // Player-specific property
    private Room currentRoom; // The room the player is currently in

    public Player() {
        super(); // Calls the Character constructor to initialize health and inventory
        this.score = 0; // Default score value
    }

    // Getters and setters for score
    public int getScore() {
        return score;
    }
    
    public void addScore(int points) {
        this.score += points;
    }

    // Room management
    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }
    
    // Inventory management (inherited from Character)
    public boolean pickUpItem(Item item) {
        return getInventory().addItem(item);
    }

    public boolean dropItem(Item item) {
        return getInventory().removeItem(item);
    }

    public String getInventoryString() {
        return getInventory().getInventoryString();
    }
}