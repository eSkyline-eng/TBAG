package edu.ycp.cs320.tbag.model;

public class Character {
    private int health;
    private Inventory inventory; // All characters have an inventory
    
    public Character() {
        this.health = 100; // Default health value
        this.inventory = new Inventory(); // Initialize inventory
    }

    // Getters and setters for health
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
    
    // Getters and setters for inventory
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}