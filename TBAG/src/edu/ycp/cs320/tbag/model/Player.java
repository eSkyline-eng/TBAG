package edu.ycp.cs320.tbag.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ycp.cs320.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.tbag.db.persist.IDatabase;
import edu.ycp.cs320.tbag.ending.Achievement;

public class Player extends Character {
	private int attack = 10;
	private int health = 100;
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
        if (!achievements.containsKey(id)) {
            Achievement achievement = new Achievement(id, description, true);
            achievements.put(id, achievement);
           
            IDatabase db = DatabaseProvider.getInstance();
            int playerId = 1;
            db.addAchievement(playerId, achievement);
        }
    }

    public void removeAchievement(String id) {
        if (achievements.containsKey(id)) {           
            IDatabase db = DatabaseProvider.getInstance();
            db.removeAchievement(id);
            achievements.remove(id);
        }
    }
   
    public void loadAchievements(List<Achievement> achievementsList) {
        for (Achievement achievement : achievementsList) {
            this.achievements.put(achievement.getId(), achievement);
        }
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
    
    public String getFormattedAchievements() {
        if (achievements == null || achievements.isEmpty()) {
            return "None";
        }

        StringBuilder sb = new StringBuilder("<ul>");
        for (Achievement a : achievements.values()) {
            sb.append("<li>").append(a.getDescription());
            if (a.isCompleted()) {
                sb.append(" ✅");
            }
            sb.append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
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
    
    public boolean checkInventory(String item) {
        String inventory = getInventoryString();
        if (inventory.contains(item)) {
            return true;
        } else {
            return false;
        }
    }
    
    //player attacks
    public int getAttack() {
        return attack;
    }
    public void setAttack(int attack) {
        this.attack = attack;
    }
  
  
    public int getHealth() {
        return health;
    }
  
    public void setHealth(int health) {
    	this.health = health;
    }
  
  
    //Used to make the player take damage from enemies
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }
 }
