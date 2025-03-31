package edu.ycp.cs320.tbag.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private List<Item> items;              // Items present in this room
    
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
    public void addItem(Item item) {
        items.add(item);
    }
    
    /**
     * Removes the specified item from this room.
     *
     * @param item the item to remove
     * @return true if the item was removed; false otherwise
     */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }
    
    /**
     * Removes and returns the first item matching the given name (case-insensitive).
     * Returns null if no matching item is found.
     *
     * @param itemName the name of the item to remove
     * @return the removed Item or null if not found
     */
    public Item removeItemByName(String itemName) {
        for (Item item : items) {
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
    public List<Item> getItems() {
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
        for (Item item : items) {
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
}
