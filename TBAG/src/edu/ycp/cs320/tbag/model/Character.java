package edu.ycp.cs320.tbag.model;

public class Character {
    private Inventory inventory; // All characters have an inventory
    
    public Character() {
        this.inventory = new Inventory(); // Initialize inventory
    }

    
    // Getters and setters for inventory
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}