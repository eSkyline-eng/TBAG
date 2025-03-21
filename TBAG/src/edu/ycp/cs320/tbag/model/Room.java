package edu.ycp.cs320.tbag.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a location (room) in the text-based adventure game.
 * Each room has an ID, name, a long description, and connections to other rooms.
 */
public class Room {
    private int id;
    private String name;
    private String longDescription;
    // Maps a direction (e.g., "north") to a connected Room
    private Map<String, Room> connections;

    /**
     * Constructs a Room with the specified id, name, and long description.
     *
     * @param id the unique identifier for the room
     * @param name the name of the room
     * @param longDescription the detailed description of the room
     */
    public Room(int id, String name, String longDescription) {
        this.id = id;
        this.name = name;
        this.longDescription = longDescription;
        this.connections = new HashMap<>();
    }
    
    /**
     * Adds a connection from this room to another room in a specific direction.
     *
     * @param direction the direction (e.g., "north", "south") of the connection
     * @param room the room that is connected in that direction
     */
    public void addConnection(String direction, Room room) {
        connections.put(direction.toLowerCase(), room);
    }
    
    /**
     * Retrieves the room connected in the given direction.
     *
     * @param direction the direction to move from this room
     * @return the connected Room if it exists, otherwise null
     */
    public Room getConnection(String direction) {
        return connections.get(direction.toLowerCase());
    }
    
    /**
     * Returns the long description of the room.
     *
     * @return the long description
     */
    public String getLongDescription() {
        return longDescription;
    }
    
    /**
     * Returns the room's unique identifier.
     *
     * @return the id of the room
     */
    public int getId() {
        return id;
    }
    
    /**
     * Returns the room's name.
     *
     * @return the name of the room
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns a map of all connections from this room.
     *
     * @return the connections map
     */
    public Map<String, Room> getConnections() {
        return connections;
    }
}