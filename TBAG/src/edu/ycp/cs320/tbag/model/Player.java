package edu.ycp.cs320.tbag.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ycp.cs320.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.tbag.db.persist.IDatabase;
import edu.ycp.cs320.tbag.ending.Achievement;
import edu.ycp.cs320.tbag.model.Item;

/**
 * Represents the player, including health, attack, inventory, achievements, and shop-specific state.
 */
public class Player extends Character {
    private int id;                        // Database ID for persistence
    private int attack = 10;
    private int health = 100;
    private int time;                      // Player-specific timer
    private Room currentRoom;              // The room the player is currently in
    private Map<String, Achievement> achievements = new HashMap<>();

    // Shop-related fields
    private int money;
 // Multiplier applied to base attack for the next battle (stacking)
    private double attackMultiplier = 1.0;

    
    // at the top with your other fields
    private boolean gameOver = false;


    public Player() {
        super();                          // Initialize inventory
        this.time = 500;                  // Default time
        this.money = 50;                  // Starting money
    }

    /*** ID management ***/
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    /*** Money management ***/
    public int getMoney() {
        return money;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public void addMoney(int amount) {
        this.money += amount;
    }
    public void deductMoney(int amount) {
        this.money -= amount;
    }

    /*** Health management ***/
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public void addHealth(int amount) {
        this.health += amount;
    }

    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    /** 
     * Returns the attack value for the current battle,
     * applying any boosts, then rounded up.
     */
    public int getAttack() {
        // e.g. base=10, multiplier=1.5 → 15
        return (int) Math.ceil(this.attack * this.attackMultiplier);
    }
    /**
     * Multiply the attack multiplier by (1 + percent/100).
     * Stacks multiplicatively if called multiple times.
     */
    public void applyAttackBoost(int percent) {
        this.attackMultiplier *= (1.0 + percent / 100.0);
    }

    /** Reset the multiplier back to 1.0 after a battle ends. */
    public void resetAttackMultiplier() {
        this.attackMultiplier = 1.0;
    }

    public void setAttackMultiplier(Double attackMultiplier) {
        this.attackMultiplier = attackMultiplier;
    }
    
    public Double getAttackMultiplier() {
        return attackMultiplier;
    }

    /*** Timer ***/
    public int getTime() {
        return time;
    }
    public void reduceTime(int points) {
        this.time -= points;
    }
    public boolean outOfTime() {
        return time <= 0;
    }

    public void setTime(int time) {
    	this.time = time; 
    }
    
    /*** Room management ***/
    public Room getCurrentRoom() {
        return currentRoom;
    }
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    /*** Achievement management ***/
    public void unlockAchievement(String id, String description) {
        if (!achievements.containsKey(id)) {
            Achievement achievement = new Achievement(id, description, true);
            achievements.put(id, achievement);

            IDatabase db = DatabaseProvider.getInstance();
            db.addAchievement(this.id, achievement);
        }
    }

    public void removeAchievement(String id) {
        if (achievements.containsKey(id)) {
            IDatabase db = DatabaseProvider.getInstance();
            db.removeAchievement(id);
            achievements.remove(id);
        }
    }

    public void loadAchievements(List<Achievement> list) {
        for (Achievement a : list) {
            achievements.put(a.getId(), a);
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
        if (achievements.isEmpty()) {
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

    /*** Inventory management ***/

    public boolean pickUpItem(Item item) {
        return getInventory().addItem(item);
    }

    public boolean dropItem(Item item) {
        return getInventory().removeItem(item);
    }
    public String getInventoryString() {
        return getInventory().getInventoryString();
    }
    public boolean checkInventory(String name) {
        return getInventory().getInventoryString().contains(name);
    }

    /*** New helpers for shop ***/
    public boolean hasItem(String name) {
        return getInventory().getItems()
            .stream()
            .anyMatch(item -> item.getName().equalsIgnoreCase(name));
    }
    public boolean removeItem(String name) {
        Item removed = getInventory().removeItemByName(name);
        return (removed != null);
    }
    
    // getter+setter below your other accessors
    public boolean isGameOver() {
        return gameOver;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    
}
