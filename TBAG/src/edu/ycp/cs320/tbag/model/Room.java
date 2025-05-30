package edu.ycp.cs320.tbag.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ycp.cs320.tbag.events.Event;

/**
 * Represents a location (room) in the TBAG game.
 * Rooms have a unique ID, a name, a detailed narrative description,
 * directional connections to other rooms, and can contain items.
 */
public class Room {
    private int id;
    private String name;
    private String longDescription;
    private Map<String, Room> connections;  // e.g., "north" -> another Room
    private List<Items> items;              // Items present in this room
    private List<NPC> npcs;
    private List<Event> events; 				// Adds event to each room
    private List<Enemy> enemy;
    
    /**
     * Constructs a new Room with the given ID, name, and long description.
     *
     * @param id the unique room ID
     * @param name the room name
     * @param longDescription the narrative description of the room
     */
    public Room(int id, String name, String longDescription) {
        this.id = id;
        this.name = name;
        this.longDescription = longDescription;
        this.connections = new HashMap<>();
        this.items = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.events = new ArrayList<>();
        this.enemy = new ArrayList<>();
    }
    
    /**
     * Adds a connection from this room in the specified direction to another room.
     * The direction key is stored in lower-case to ensure case-insensitive lookups.
     *
     * @param direction the direction keyword (e.g., "north", "south", "east", "west")
     * @param room the room connected in that direction
     */
    public void addConnection(String direction, Room room) {
        connections.put(direction.toLowerCase(), room);
    }
    
    /**
     * Retrieves the room connected in the specified direction.
     *
     * @param direction the direction (e.g., "north", "south")
     * @return the connected Room, or null if no connection exists
     */
    public Room getConnection(String direction) {
        return connections.get(direction.toLowerCase());
    }
    
    /**
     * Returns a comma-separated string listing all available directions from this room.
     *
     * @return a string of available directions or "None" if there are no connections.
     */
    public String getAvailableDirections() {
        if (connections.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keys = connections.keySet();
        for (String dir : keys) {
            sb.append(dir).append(", ");
        }
        // Remove trailing comma and space if present.
        if (sb.length() >= 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
    
    /**
     * Returns the room's full narrative description.
     *
     * @return the long description
     */
    public String getLongDescription() {
        return longDescription;
    }
    
    /**
     * Adds an item to this room.
     *
     * @param item the item to add
     */
    public void addItem(Items item) {
        items.add(item);
    }
    
    /**
     * Removes the specified item from this room.
     *
     * @param item the item to remove
     * @return true if the item was removed; false otherwise
     */
    public boolean removeItem(Items item) {
        return items.remove(item);
    }
    
    /**
     * Removes and returns the first item matching the given name (case-insensitive).
     * Returns null if no matching item is found.
     *
     * @param itemName the name of the item to remove
     * @return the removed Item or null if not found
     */
    public Items removeItemByName(String itemName) {
        for (Items item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                items.remove(item);
                return item;
            }
        }
        return null;
    }
    
    /**
     * Returns the list of items currently in this room.
     *
     * @return a list of Items
     */
    public List<Items> getItems() {
        return items;
    }
    
    /**
     * Returns a formatted string listing the items present in this room.
     *
     * @return a string of items or a message if no items are present.
     */
    public String getRoomItemsString() {
        if (items.isEmpty()) {
            return "No items are present in this room.";
        }
        StringBuilder sb = new StringBuilder("Items available: ");
        for (Items item : items) {
            sb.append(item.getName()).append(", ");
        }
        // Remove trailing comma and space if present.
        if (sb.length() >= 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
    
    /**
     * Returns the unique ID of this room.
     *
     * @return the room ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Returns the name of this room.
     *
     * @return the room name
     */
    public String getName() {
        return name;
    }
    
    // Add event per room
    public void addEvent(Event event) {
    	events.add(event);
    }
    
    public List<Event> getEvents() {
    	return events;
    }
    
    /**
     * Adds a NPC to this room.
     *
     * @param npc the npc to add
     */
    public void addNPC(NPC npc) {
        npcs.add(npc);
    }

    // Get all NPCs in the room
    public List<NPC> getNPCs() {
        return npcs;
    }

    // Optional: Get a specific NPC by name
    public NPC getNPCByName(String name) {
        for (NPC npc : npcs) {
            if (npc.getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }
        return null;
    }
    
    /**
     * Adds a Enemy to this room.
     *
     * @param enemy the enemy to add
     */
    public void addEnemy(Enemy enemies) {
        enemy.add(enemies);
    }
    
    public boolean removeEnemy(Enemy enemies) {
        return enemy.remove(enemies);  // Removes the given Enemy from the room's Enemy list
    }
    
    public Enemy getEnemyByName(String enemyName) {
        for (Enemy enemy : enemy) {
            if (enemy.getName().trim().equalsIgnoreCase(enemyName.trim())) {
                return enemy;
            }
        }
        return null;
    }
    
    public List<Enemy> getEnemies() {
        return enemy;
    }
}


