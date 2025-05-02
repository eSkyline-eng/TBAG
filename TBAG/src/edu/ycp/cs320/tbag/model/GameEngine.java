package edu.ycp.cs320.tbag.model;

import edu.ycp.cs320.tbag.db.persist.*;

import edu.ycp.cs320.tbag.util.CSVLoader;
import edu.ycp.cs320.tbag.events.Damage;
import edu.ycp.cs320.tbag.events.Dialogue;
import edu.ycp.cs320.tbag.events.EventManager;
import edu.ycp.cs320.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.tbag.db.persist.IDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngine {
    private Room currentRoom;
    private Player player;
    private StringBuilder transcript;
    private EventManager eventManager;
    private Map<Integer, Room> roomMap;   // Maps roomID to Room objects
    private Map<Integer, Item> itemMap;   // Maps itemID to Item objects
    private boolean inCombat = false;
    private Enemy currentEnemy;


    public GameEngine() {
        transcript = new StringBuilder();
        roomMap = new HashMap<>();
        itemMap = new HashMap<>();
        eventManager = new EventManager();
        DatabaseProvider.setInstance(new DerbyGameDatabase());
        initGame();
    }
    
    /**
     * Initializes the game by loading rooms, connections, items, and assigning items to rooms from CSV files.
     */
    private void initGame() {
        transcript.setLength(0);
        
        IDatabase db = DatabaseProvider.getInstance();
        
        // Load rooms and connections from the database
        roomMap = db.loadRooms();
        
        db.loadConnections(roomMap);
        
        if (roomMap.isEmpty()) {
            throw new IllegalStateException("No rooms were loaded from the database.");
        }
        
        
        
     // Load NPCs
        for (NPC npc : db.loadAllNPCs()) {
            Room room = roomMap.get(npc.getRoomId());
            System.out.println(npc.getRoomId());
            if (room != null) {
                room.addNPC(npc);  //adds NPC to the room
                System.out.println("Added NPC " + npc.getName() + " to room: " + room.getName());
            }
        }

        for (Enemy enemy : db.loadAllEnemies()) {
            Room room = roomMap.get(enemy.getRoomId());
            System.out.println(enemy.getRoomId());
            if (room != null) {
                room.addEnemy(enemy);  // Adds the enemy to the room
                System.out.println("Added enemy " + enemy.getEnemyName() + " (ID: " + enemy.getEnemyID() + ") to room: " + room.getName());
            } else {
                System.out.println("No room found for enemy " + enemy.getEnemyName() + " (ID: " + enemy.getEnemyID() + ")");
            }
        }
        
        currentRoom = roomMap.get(1);
        
        // initialize the player and set their starting room.
        player = db.loadPlayerState();
        if (player == null) {
        	player = new Player();
        	player.setHealth(100);
        	player.setCurrentRoom(currentRoom);
        	db.savePlayerState(player.getHealth(), currentRoom.getId());
        } else { // if user already exists
        	int roomId = db.getPlayerRoomId();
        	currentRoom = roomMap.get(roomId);
        	player.setCurrentRoom(currentRoom);
        }
        
        // Loads items from database
        itemMap = db.loadItemDefinitions();

        List<ItemLocation> itemLocations = db.loadAllItemLocations();
        for (ItemLocation loc : itemLocations) {
            Item item = itemMap.get(loc.getItemId());
            if (item != null) {
                if (loc.getLocationType().equals("room")) {
                    Room room = roomMap.get(loc.getLocationId());
                    if (room != null) {
                        for (int i = 0; i < loc.getQuantity(); i++) {
                            room.addItem(item);
                        }
                    }
                } else if (loc.getLocationType().equals("player")) {
                    for (int i = 0; i < loc.getQuantity(); i++) {
                        player.pickUpItem(item);
                    }
                }
            }
        }
        
        try {
            // Load room events from CSV (events.csv format: roomID | eventType | description | probability | dialogue)
            List<String[]> eventRecords = CSVLoader.loadCSV("WebContent/CSV/events.csv", "\\|");
            for (String[] record : eventRecords) {
            	int roomId = Integer.parseInt(record[0].trim());
                String eventType = record[1].trim();
                String description = record[2].trim();
                double probability = Double.parseDouble(record[3].trim());
                String dialogue = record[4].trim();
                int dam = Integer.parseInt(record[5].trim());
                Room room = roomMap.get(roomId);
                if (room != null) {
                    if (eventType.equalsIgnoreCase("Dialogue")) {
                        room.addEvent(new Dialogue(probability, dialogue));
                    } else if (eventType.equalsIgnoreCase("Damage")) {
                    	room.addEvent(new Damage(probability, dialogue, dam));
                    }
                }
            }
            
        } catch (Exception e) {
            transcript.append("Error loading game data: ").append(e.getMessage()).append("\n");
        }
        
        
      	
        
      	

        
        // Append starting room details to the transcript.
        transcript.append(currentRoom.getLongDescription()).append("\n");
        transcript.append(currentRoom.getRoomItemsString()).append("\n");
    }
    
    
    public void checkForEnemyEncounter(Player player, Room currentRoom) {
        for (Enemy enemy : currentRoom.getEnemies()) {
        	System.out.println("Checking enemy encounter for: " + enemy.getEnemyName());
            if (Math.random() < enemy.getEncounter()) {
                currentEnemy = enemy;
                inCombat = true;
                System.out.println("You have encountered a " + enemy.getEnemyName() + "!");
                return;
            }
        }
    }

    
    public String processCommand(String command) {
        String output = "";
        command = command.trim().toLowerCase();

        if (inCombat) {
            if (command.startsWith("attack")) {
                if (currentEnemy != null) {
                    int playerAttack = player.getAttack();
                    int damageDealt = Math.max(playerAttack, 0);
                    currentEnemy.takeDamage(damageDealt);
                    output = "You attacked " + currentEnemy.getName() + " for " + damageDealt + " damage.\n";
                    if (currentEnemy.getEnemyHealth() <= 0) {
                        output += currentEnemy.getName() + " has been defeated!";
                        currentRoom.removeEnemy(currentEnemy);
                        inCombat = false; // Battle is over
                        currentEnemy = null;
                    } else {
                        int enemyAttack = currentEnemy.getEnemyAttack();
                        player.takeDamage(enemyAttack);
                        output += currentEnemy.talk();
                        output += " " + currentEnemy.getName() + " attacks you for " + enemyAttack + " damage.\n";
                        output += " Your Health: " + player.getHealth();
                        output += " Enemy Health: " + currentEnemy.getEnemyHealth();
                        if (player.getHealth() <= 0) {
                            output += "\nYou have been defeated!";
                        }
                    }
                }
            } else if (command.equals("run")) {
                double chance = Math.random();
                if (chance < currentEnemy.getRunAway()) {
                    output = "You successfully ran away!";
                    inCombat = false;
                    currentEnemy = null;
                } else {
                    output = "You failed to run away!";
                    int enemyAttack = currentEnemy.getEnemyAttack();
                    player.takeDamage(enemyAttack);
                    output += " " + currentEnemy.talk();
                    output += " " + currentEnemy.getName() + " attacks you for " + enemyAttack + " damage.\n";
                    output += " Your Health: " + player.getHealth();
                }
            } else if (command.equals("help")) {
                output = "Combat Commands: attack, run, health";
            } else if (command.equals("health")) {
                output = "Health: " + player.getHealth();
            } else {
                output = "You're in combat! You can only attack " + currentEnemy.getName() + ", run away, or check health.";
            }
        } else {
            if (command.startsWith("talk to ")) {
                String npcName = command.substring(8).trim();
                NPC npc = currentRoom.getNPCByName(npcName);
                if (npc != null) {
                    output = npc.talk();
                } else {
                    output = "There is no one by that name here.";
                }
            } else if (command.equals("talk")) {
                if (currentRoom.getNPCs().isEmpty()) {
                    output = "There is no one to talk to here.";
                } else {
                    output = "Who would you like to talk to? Available NPCs: ";
                    for (NPC npc : currentRoom.getNPCs()) {
                        output += npc.getName() + ", ";
                    }
                    output = output.substring(0, output.length() - 2); // Remove trailing comma
                }
            } else if (command.startsWith("go ")) {
                String direction = command.substring(3).trim();
                Room nextRoom = currentRoom.getConnection(direction);
                if (nextRoom != null) {
                    currentRoom = nextRoom;
                    player.setCurrentRoom(currentRoom);
                    checkForEnemyEncounter(player, currentRoom);
                    
                    
                    IDatabase db = DatabaseProvider.getInstance();
                    db.updatePlayerLocation(currentRoom.getId());

                    StringBuilder sb = new StringBuilder();
                    sb.append(currentRoom.getLongDescription()).append("\n");
                    sb.append(currentRoom.getRoomItemsString()).append("\n");
                    

                    if (!currentRoom.getEvents().isEmpty()) {
                        String eventOutput = eventManager.triggerEvents(currentRoom.getEvents(), player);
                        if (!eventOutput.isEmpty()) {
                            sb.append("\n\n").append(eventOutput);
                        }
                    }

                    output = sb.toString();
                    
                    if (inCombat) {
                    	output += "You have encountered a " + currentEnemy.getEnemyName() + "!";
                    }
                } else {
                    output = "You cannot go that way. Available directions: " + currentRoom.getAvailableDirections();
                }
            } else if (command.equals("look")) {
                output = currentRoom.getLongDescription() + "\n" + currentRoom.getRoomItemsString();
            } else if (command.equals("help")) {
                output = "Available commands: go [direction], look, help, restart, inventory, take [item], drop [item].";
            } else if (command.equals("restart")) {
                IDatabase db = DatabaseProvider.getInstance();
                db.resetGameData();
                initGame();
                output = "Game and database restarted.\n" +
                         currentRoom.getLongDescription() + "\n" +
                         currentRoom.getRoomItemsString();
            } else if (command.equals("inventory")) {
                output = player.getInventoryString();
            } else if (command.startsWith("take ")) {
                String itemName = command.substring(5).trim();
                IDatabase db = DatabaseProvider.getInstance();

                List<ItemLocation> roomItems = db.getItemsAtLocation("room", currentRoom.getId());
                ItemLocation found = null;
                for (ItemLocation loc : roomItems) {
                    Item item = itemMap.get(loc.getItemId());
                    if (item != null && item.getName().equalsIgnoreCase(itemName)) {
                        found = loc;
                        break;
                    }
                }

                if (found != null) {
                    Item targetItem = itemMap.get(found.getItemId());
                    if (player.pickUpItem(targetItem)) {
                        currentRoom.removeItem(targetItem);
                        db.transferItem(found.getInstanceId(), "room", currentRoom.getId(), "player", 1);
                        output = "You picked up the " + targetItem.getName() + ".";
                    } else {
                        currentRoom.addItem(targetItem); // This line seems unnecessary; you already removed it conditionally.
                        output = "You can't carry the " + targetItem.getName() + ".";
                    }
                } else {
                    output = "That item isn't here.";
                }
            } else if (command.startsWith("drop ")) {
                String itemName = command.substring(5).trim();
                IDatabase db = DatabaseProvider.getInstance();

                Item droppedItem = null;
                for (Item item : player.getInventory().getItems()) {
                    if (item.getName().equalsIgnoreCase(itemName)) {
                        droppedItem = item;
                        break;
                    }
                }

                if (droppedItem != null) {
                    List<ItemLocation> playerItems = db.getItemsAtLocation("player", 1);

                    ItemLocation instanceToDrop = null;
                    for (ItemLocation loc : playerItems) {
                        if (loc.getItemId() == droppedItem.getId()) {
                            instanceToDrop = loc;
                            break;
                        }
                    }

                    if (player.dropItem(droppedItem)) {
                        currentRoom.addItem(droppedItem);
                        db.transferItem(instanceToDrop.getInstanceId(), "player", 1, "room", currentRoom.getId());
                        output = "You dropped the " + droppedItem.getName() + ".";
                    } else {
                        output = "Couldn't drop the item.";
                    }
                } else {
                    output = "You don't have that item.";
                }
            } else if (command.equals("health")) {
                output = "Health: " + player.getHealth();
            } else {
                output = "I don't understand that command.";
            }
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
