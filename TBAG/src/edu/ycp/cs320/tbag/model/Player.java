package edu.ycp.cs320.tbag.model;

import java.util.List;

/**
 * Represents the player in the TBAG game.
 * The player has health, score, a current room, and an inventory for item management.
 */
public class Player {
    private int health;
    private int score;
    private Room currentRoom;
    private Inventory inventory;

    /**
     * Constructs a new Player with default health and score.
     * Initializes an empty inventory.
     */
    public Player() {
        this.health = 100;
        this.score = 0;
        this.inventory = new Inventory(); // If you have capacity logic, you can configure it here
    }

    // -----------------------
    // Health & Score Methods
    // -----------------------
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    public int getScore() {
        return score;
    }
    
    public void addScore(int points) {
        this.score += points;
    }

    // -----------------------
    // Room Methods
    // -----------------------
    
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    // -----------------------
    // Inventory Methods
    // -----------------------
    
    /**
     * Returns the player's Inventory object for direct manipulation.
     * If you want to enforce capacity or other rules, do so via pickUpItem/dropItem.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Attempts to add an item to the player's inventory.
     *
     * @param item the item to pick up
     * @return true if the item was successfully added; false if it was rejected (e.g., capacity exceeded)
     */
    public boolean pickUpItem(Item item) {
        return inventory.addItem(item);
    }

    /**
     * Attempts to remove an item from the player's inventory.
     *
     * @param item the item to drop
     * @return true if the item was successfully removed; false if the item was not found
     */
    public boolean dropItem(Item item) {
        return inventory.removeItem(item);
    }

    /**
     * Returns a string representation of the player's current inventory.
     * Example:
     *   "You are carrying:
     *    - Fast Food Uniform
     *    - Resume"
     *
     * If the inventory is empty, returns "Your inventory is empty."
     */
    public String getInventoryString() {
        List<Item> items = inventory.getItems();
        if (items.isEmpty()) {
            return "Your inventory is empty.";
        }
        
        StringBuilder sb = new StringBuilder("You are carrying:\n");
        for (Item i : items) {
            sb.append("- ")
              .append(i.getName())
              .append(" (")
              .append(i.getDescription())
              .append(")\n");
        }
        return sb.toString();
    }
}
