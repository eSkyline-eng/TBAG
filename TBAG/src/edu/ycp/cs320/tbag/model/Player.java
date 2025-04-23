package edu.ycp.cs320.tbag.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.ycp.cs320.tbag.ending.Achievement;

public class Player extends Character {
    private int time; // Player-specific property
    private Room currentRoom; // The room the player is currently in
    private Map<String, Achievement> achievements = new HashMap<>();

    public Player() {
        super(); // Calls the Character constructor to initialize health and inventory
        this.time = 500; // Default score value
    }

    // Getters and setters for score
    public int getTime() {
        return time;
    }
    
    public void reduceTime(int points) {
        this.time -= points;
    }

    public boolean outOfTime() {
    	return time <= 0;
    }
    
    // Room management
    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }
    
    // Achievements
    public void unlockAchievement(String id, String description) {
        achievements.put(id, new Achievement(id, description, true));
    }

    public boolean hasAchievement(String id) {
        return achievements.containsKey(id) && achievements.get(id).isCompleted();
    }

    public Set<String> getAchievementIDs() {
        return achievements.keySet();
    }

    public Map<String, Achievement> getAchievements() {
        return achievements;
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