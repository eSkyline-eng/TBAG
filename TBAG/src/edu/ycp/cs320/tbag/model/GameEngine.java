package edu.ycp.cs320.tbag.model;

import edu.ycp.cs320.tbag.util.CSVLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngine {
    private Room currentRoom;
    private Player player;
    private StringBuilder transcript;
    private Map<Integer, Room> roomMap;   // Maps roomID to Room objects
    private Map<Integer, Item> itemMap;   // Maps itemID to Item objects

    public GameEngine() {
        transcript = new StringBuilder();
        roomMap = new HashMap<>();
        itemMap = new HashMap<>();
        initGame();
    }
    
    /**
     * Initializes the game by loading rooms, connections, items, and assigning items to rooms from CSV files.
     */
    private void initGame() {
        transcript.setLength(0);
        try {
            // Load rooms from CSV (rooms.csv format: roomID | roomName | longDescription)
            List<String[]> roomRecords = CSVLoader.loadCSV("WebContent/CSV/rooms.csv", "\\|");
            for (String[] record : roomRecords) {
                int id = Integer.parseInt(record[0].trim());
                String name = record[1].trim();
                String longDescription = record[2].trim();
                Room room = new Room(id, name, longDescription);
                roomMap.put(id, room);
            }
            
            // Load connections from CSV (connections.csv format: fromRoomID | direction | toRoomID)
            List<String[]> connectionRecords = CSVLoader.loadCSV("WebContent/CSV/connections.csv", "\\|");
            for (String[] record : connectionRecords) {
                int fromId = Integer.parseInt(record[0].trim());
                String direction = record[1].trim().toLowerCase();
                int toId = Integer.parseInt(record[2].trim());
                Room fromRoom = roomMap.get(fromId);
                Room toRoom = roomMap.get(toId);
                if (fromRoom != null && toRoom != null) {
                    fromRoom.addConnection(direction, toRoom);
                }
            }
            
            // Load items from CSV (items.csv format: itemID | itemName | description | weight | value)
            List<String[]> itemRecords = CSVLoader.loadCSV("WebContent/CSV/items.csv", "\\|");
            for (String[] record : itemRecords) {
                int itemId = Integer.parseInt(record[0].trim());
                String itemName = record[1].trim();
                String description = record[2].trim();
                double weight = Double.parseDouble(record[3].trim());
                double value = Double.parseDouble(record[4].trim());
                Item item = new Item(itemId, itemName, description, weight, value);
                itemMap.put(itemId, item);
            }
            
            // Assign items to rooms using room_items.csv (format: roomID | itemID)
            List<String[]> roomItemsRecords = CSVLoader.loadCSV("WebContent/CSV/room_items.csv", "\\|");
            for (String[] record : roomItemsRecords) {
                int roomId = Integer.parseInt(record[0].trim());
                int itemId = Integer.parseInt(record[1].trim());
                Room room = roomMap.get(roomId);
                Item item = itemMap.get(itemId);
                if (room != null && item != null) {
                    // Clone the item if needed (so multiple rooms can have similar items) 
                    // For now, we simply add the same item instance.
                    room.addItem(item);
                }
            }
            
            // Set the initial room (for example, room with ID 1 is the starting point)
            currentRoom = roomMap.get(1);
        } catch (Exception e) {
            transcript.append("Error loading game data: ").append(e.getMessage()).append("\n");
        }
        
        // Initialize the player and set their starting room.
        player = new Player();
        player.setCurrentRoom(currentRoom);
        
        // Append starting room details to the transcript.
        transcript.append(currentRoom.getLongDescription()).append("\n");
        transcript.append(currentRoom.getRoomItemsString()).append("\n");
    }
    
    public String processCommand(String command) {
        String output = "";
        command = command.trim().toLowerCase();
        
        if (command.startsWith("go ")) {
            String direction = command.substring(3).trim();
            Room nextRoom = currentRoom.getConnection(direction);
            if (nextRoom != null) {
                currentRoom = nextRoom;
                player.setCurrentRoom(currentRoom);
                output = currentRoom.getLongDescription() + "\n" + currentRoom.getRoomItemsString();
            } else {
                output = "You cannot go that way. Available directions: " + currentRoom.getAvailableDirections();
            }
        } else if (command.equals("look")) {
            output = currentRoom.getLongDescription() + "\n" + currentRoom.getRoomItemsString();
        } else if (command.equals("help")) {
            output = "Available commands: go [direction], look, help, restart, inventory, take [item], drop [item].";
        } else if (command.equals("restart")) {
            initGame();
            output = "Game restarted.\n" + currentRoom.getLongDescription() + "\n" + currentRoom.getRoomItemsString();
        } else if (command.equals("inventory")) {
            output = player.getInventoryString();
        } else if (command.startsWith("take ")) {
            String itemName = command.substring(5).trim();
            Item itemToTake = currentRoom.removeItemByName(itemName);
            if (itemToTake != null) {
                if (player.pickUpItem(itemToTake)) {
                    output = "You picked up the " + itemToTake.getName() + ".";
                } else {
                    currentRoom.addItem(itemToTake);
                    output = "You cannot carry the " + itemToTake.getName() + " (too heavy).";
                }
            } else {
                output = "That item is not here.";
            }
        } else if (command.startsWith("drop ")) {
            String itemName = command.substring(5).trim();
            Item droppedItem = null;
            for (Item item : player.getInventory().getItems()) {
                if (item.getName().equalsIgnoreCase(itemName)) {
                    droppedItem = item;
                    break;
                }
            }
            if (droppedItem != null) {
                if (player.dropItem(droppedItem)) {
                    currentRoom.addItem(droppedItem);
                    output = "You dropped the " + droppedItem.getName() + ".";
                } else {
                    output = "Couldn't drop the item.";
                }
            } else {
                output = "You don't have that item.";
            }
        } else {
            output = "I don't understand that command.";
        }
        
        transcript.append("> ").append(command).append("\n");
        transcript.append(output).append("\n");
        return output;
    }
    
    public String getTranscript() {
        return transcript.toString();
    }
    
    public Player getPlayer() {
        return player;
    }
}
