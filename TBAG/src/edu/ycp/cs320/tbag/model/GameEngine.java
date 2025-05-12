package edu.ycp.cs320.tbag.model;

import edu.ycp.cs320.tbag.db.persist.*;

import edu.ycp.cs320.tbag.util.CSVLoader;
import edu.ycp.cs320.tbag.events.Damage;
import edu.ycp.cs320.tbag.events.Dialogue;
import edu.ycp.cs320.tbag.events.Event;
import edu.ycp.cs320.tbag.events.EventManager;
import edu.ycp.cs320.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.tbag.db.persist.IDatabase;
import edu.ycp.cs320.tbag.model.ShopManager;
import edu.ycp.cs320.tbag.model.ShopItem;
import edu.ycp.cs320.tbag.ending.EndingCondition;
import edu.ycp.cs320.tbag.ending.KickedOutEnding;
import edu.ycp.cs320.tbag.ending.LotteryEnding;
import edu.ycp.cs320.tbag.ending.MazonCEOEnding;
import edu.ycp.cs320.tbag.ending.MazonDriverEnding;
import edu.ycp.cs320.tbag.ending.McRonaldsEnding;
import edu.ycp.cs320.tbag.ending.RatKingEnding;
import edu.ycp.cs320.tbag.ending.WallMartEnding;
import edu.ycp.cs320.tbag.ending.YCPEnding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngine {
    private Room currentRoom;
    private Player player;
    private StringBuilder transcript;
    private EventManager eventManager;
    private Map<Integer, Room> roomMap;   // Maps roomID to Room objects
    private Map<Integer, Items> itemMap;   // Maps itemID to Item objects
    private boolean inCombat = false;
    private ShopManager shopManager;
    private boolean shopMode = false;
    private boolean dialogueMode = false;
    private int activeDialogueId = -1;
    private Enemy currentEnemy;
    private boolean pendingEndingPrompt = false;
    private EndingCondition pendingEnding = null;
    private List<EndingCondition> endings = new ArrayList<>();
    private String endingDescription;




    public GameEngine() {
        transcript = new StringBuilder();
        roomMap = new HashMap<>();
        itemMap = new HashMap<>();
        eventManager = new EventManager();
        DatabaseProvider.setInstance(new DerbyGameDatabase());
        
        endings = getAllEndingConditions();
        
        // Initialize the gas‐station shop
        this.shopManager = new ShopManager();
        
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
        
        for (Event event : db.loadAllEvents()) {
            int roomId = event.getRoomId();
            Room room = roomMap.get(roomId);
            if (room != null) {
                room.addEvent(event);
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
        	player.loadAchievements(db.getAchievementsForPlayer(1));
        	int roomId = db.getPlayerRoomId();
        	currentRoom = roomMap.get(roomId);
        	player.setCurrentRoom(currentRoom);
        }
        
        // Loads items from database
        itemMap = db.loadItemDefinitions();

        List<ItemLocation> itemLocations = db.loadAllItemLocations();
        for (ItemLocation loc : itemLocations) {
           Items item = itemMap.get(loc.getItemId());
            
            if (item != null) {
                if (loc.getLocationType().equals("room")) {
                    Room room = roomMap.get(loc.getLocationId());
                    if (room != null) {
                        for (int i = 0; i < loc.getQuantity(); i++) {
                            room.addItem(item);
                            System.out.println("Added item " + item.getName() + " (ID: " + item.getId() + ") to room: " + room.getName() + 
                                    " | Type: " + item.getType() + " | Weight: " + item.getWeight() + " | Value: " + item.getValue());
                        }
                    }
                } else if (loc.getLocationType().equals("player")) {
                    for (int i = 0; i < loc.getQuantity(); i++) {
                        player.pickUpItem(item);
                        System.out.println("Player picked up item " + item.getName() + " (ID: " + item.getId() + ") | Type: " + item.getType() + 
                                " | Weight: " + item.getWeight() + " | Value: " + item.getValue());
                    }
                }
            }
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
    
    public String useItem(Player player, Items item) {
        StringBuilder result = new StringBuilder();
        
        if (item.getType().equals("consumable")) {
        	Consumables consumableItem = (Consumables) item;

            // Assuming the consumable item has a 'getEffect' method for healing or other effects
            int healAmount = consumableItem.getEffect();  // You can replace this with any effect for the consumable
            player.addHealth(healAmount);  // Heal the player
            int actualHealed = player.addHealth(consumableItem.getEffect());
            
            if (actualHealed > 0) {
                result.append("You used ").append(item.getName()).append(" and restored ")
                      .append(actualHealed).append(" health.\n");
                player.getInventory().removeItem(consumableItem);
            } else {
                result.append("You used ").append(item.getName())
                      .append(", but your health is already full...you kept the item\n");
            }
            
            
            
        } else {
            result.append("This consumable item does not have the expected properties.");
        }
        return result.toString();
        
    }

    
    public String processCommand(String command) {
        String output = "";
        command = command.trim().toLowerCase();
        
     // ------------- DIALOGUE MODE HANDLING -------------
        if (dialogueMode) {
            transcript.append("> ").append(command).append("\n");

            try {
                int choice = Integer.parseInt(command.trim());

                if (choice >= 1 && choice <= 3) {
                    output = processDialogueChoice(activeDialogueId, choice);
                    transcript.append(output).append("\n");

                    // Check if dialogue continues or ends
                    if (output.contains("The conversation has ended.") || output.contains("Ending")) {
                        dialogueMode = false; // Exit dialogue mode
                        activeDialogueId = -1;
                    }
                } else {
                	output = "Invalid choice. Please enter 1, 2, or 3.";
                    transcript.append(output).append("\n");
                }
            } catch (NumberFormatException e) {
                output = "Invalid input. Please enter a number (1, 2, or 3).";
                transcript.append(output).append("\n");
            }

            return output;
        }
        
        // ------------- SHOP MODE HANDLING -------------
        if (shopMode) {
            // 1) Echo the command into the transcript
            transcript.append("> ").append(command).append("\n");
            
            StringBuilder out = new StringBuilder();
            // 2) Handle pending sale (yes/no prompt)
            if (shopManager.hasPendingSale()) {
                if (shopManager.handlePendingSale(command, player, out)) {
                    String result = out.toString();
                    transcript.append(result);
                    return result;
                }
                // fall through to invalid…
            }

            String verb  = command.trim();
            String lower = verb.toLowerCase();

         // HELP
            if (lower.equals("help")) {
                out.append(
                    "shop commands:\n" +
                    "  list        – show available items (price, name, description)\n" +
                    "  buy [item]  – purchase if you have enough money\n" +
                    "  money       – Check how much money you have.\n" +
                    "  sell [item] – offload items for half their value\n" +
                    "  exit        – leave the shop\n" +
                    "  help        – show this menu again\n"
                );
                String result = out.toString();
                transcript.append(result);
                return result;

            }
            // LIST
            else if (lower.equals("list")) {
                for (ShopItem item : shopManager.listItems()) {
                    out.append(String.format(
                        "$%d %s – %s\n",
                        item.getPrice(),
                        item.getName(),
                        item.getDescription()
                    ));
                }
                String result = out.toString();
                transcript.append(result);
                return result;
            }
            // BUY
         // BUY
            else if (lower.startsWith("buy ")) {
                String itemName = verb.substring(4).trim();

                // 1) invoke the buy logic (fills `out`)
                shopManager.initiateBuy(itemName, player, out);

                // 2) grab the text result
                String result = out.toString().trim();    // trim so ACCEPT_CODE matches exactly

                // 3) check for the lottery‐win code
                if (LotteryEnding.ACCEPT_CODE.equals(result)) {
                    // stash the ending description so the servlet can render it
                    this.endingDescription = new LotteryEnding().getEndingDescription();
                    return LotteryEnding.ACCEPT_CODE;
                }

                // 4) normal shop output
                transcript.append(result).append("\n");
                return result + "\n";
            }
            // SELL
            // NEW: initiate the sale (sets pendingSaleItem + prompt), confirmation comes next turn
            else if (lower.startsWith("sell ")) {
                String itemName = verb.substring(5).trim();
                shopManager.initiateSell(itemName, player, out);
                String result = out.toString();
                transcript.append(result);
                return result;
            }
            // EXIT
            else if (lower.equals("exit")) {
                shopMode = false;
                String roomDesc = currentRoom.getLongDescription() + "\n";
                transcript.append(roomDesc);
                return roomDesc;
            }
          //MONEY
            else if (lower.equals("money")) {
            	out.append("Balance: $" + player.getMoney()+ "\n");
            	String result = out.toString();
                transcript.append(result);
                return result;
            }
            // INVALID
            else {
                String result = "Invalid shop command. Type 'help' for a list of shop commands.\n";
                transcript.append(result);
                return result;
            }
        }
        // ------------- END SHOP MODE -------------

        if (pendingEndingPrompt) {
        	if (command.equals("yes")) {
        	    pendingEndingPrompt = false;
        	    player.unlockAchievement(pendingEnding.getClass().getSimpleName(), pendingEnding.getEndingDescription());
        	    this.endingDescription = pendingEnding.getEndingDescription();

        	    transcript.append("> ").append(command).append("\n");
        	    transcript.append(pendingEnding.getEndingDescription()).append("\n");
        	    return "__ENDING_ACCEPTED__";
        	} else if (command.equals("no")) {
                pendingEndingPrompt = false;
                String msg = "You declined the job. Your journey continues...";
                transcript.append("> ").append(command).append("\n").append(msg).append("\n");
                return msg;
            } else {
                String msg = "Do you accept the job offer? Please answer 'yes' or 'no'.";
                transcript.append("> ").append(command).append("\n").append(msg).append("\n");
                return msg;
            }
        }
        
        if (inCombat) {
            if (command.startsWith("attack")) {
                if (currentEnemy != null) {
                    int playerAttack = player.getAttack();
                    int damageDealt = Math.max(playerAttack, 10);
                    currentEnemy.takeDamage(damageDealt);
                    output = "You attacked " + currentEnemy.getName() + " for " + damageDealt + " damage.\n";
                    if (currentEnemy.getEnemyHealth() <= 0) {
                        output += currentEnemy.getName() + " has been defeated!";
                        currentRoom.removeEnemy(currentEnemy);
                        // Reset the Player object's multiplier
                        player.resetAttackMultiplier();

                        // Persist the reset so next HTTP request uses base attack
                        DatabaseProvider.getInstance()
                            .updatePlayerAttackMultiplier(player.getId(), 1.0);
                        
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
                   // Reset the Player object's multiplier
                    player.resetAttackMultiplier();

                    // Persist the reset so next HTTP request uses base attack
                    DatabaseProvider.getInstance()
                        .updatePlayerAttackMultiplier(player.getId(), 1.0);

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
                output =
                    "=== Combat Commands ===\n" +
                    "- attack : Strike your foe.\n" +
                    "- run    : Attempt to flee battle.\n" +
                    "- health : Check your current health.\n" +
                    "- use	  : use an item in battle from your inventory" +
                    "- help   : Show this help message.";
            } else if (command.equals("health")) {
                output = "Health: " + player.getHealth();
            } else if (command.startsWith("use ")) {
                String itemName = command.substring(4).trim();
                Items itemToUse = player.getInventory().getItemByName(itemName);

                if (itemToUse == null) {
                    output = "You don't have an item called '" + itemName + "'.";
                } else {
                    String effectResult = useItem(player, itemToUse);
                    output += effectResult;
                    output += "Health: " + player.getHealth();

                    // Optional: Enemy attacks after item use
                    if (currentEnemy != null && player.getHealth() > 0) {
                        int enemyAttack = currentEnemy.getEnemyAttack();
                        player.takeDamage(enemyAttack);
                        output += "\n" + currentEnemy.talk();
                        output += " " + currentEnemy.getName() + " attacks you for " + enemyAttack + " damage.\n";
                        output += " Your Health: " + player.getHealth();
                    }
                }} else {
                output = "You're in combat! You can only attack " + currentEnemy.getName() + ", run away, or check health.";
            }
        
            
        //<--Initial Game Commands-->    
        } else {
            if (command.startsWith("talk to ")) {
            	String npcName = command.substring(8).trim();
                NPC npc = currentRoom.getNPCByName(npcName);

                if (npc != null) {
                	 player.reduceTime(50);
                     if (player.outOfTime()) {
                         player.unlockAchievement("Out of time", "You ran out of time");
                     }
                     
                     IDatabase db = DatabaseProvider.getInstance();
                     db.updatePlayerTime(player.getId(), player.getTime());
                     
                     for (EndingCondition ending : endings) {
                         if (ending.isMet(player)) {
                             if (!player.hasAchievement(ending.getClass().getSimpleName())) {
                                 pendingEnding = ending;
                                 pendingEndingPrompt = true;
                                 if (player.outOfTime()) {
                                     pendingEndingPrompt = false;
                                     player.unlockAchievement(pendingEnding.getClass().getSimpleName(), pendingEnding.getEndingDescription());
                                     this.endingDescription = pendingEnding.getEndingDescription();

                                     transcript.append("> ").append(command).append("\n");
                                     transcript.append(pendingEnding.getEndingDescription()).append("\n");
                                     return "__ENDING_ACCEPTED__";
                                 }
                                 String prompt = "You have been offered a position!\n" + ending.getEndingDescription() + "\nDo you accept the job? (yes/no)";
                                 transcript.append("> ").append(command).append("\n");
                                 transcript.append(prompt).append("\n");
                                 return prompt;
                             }
                         }
                     }

                    if (!npc.hasAdvancedDialogue()) {
                        output = npc.talk();
                    } else {
                        output = startAdvancedDialogue(npc.getStartingDialogueId());
                        transcript.append("> ").append(command).append("\n").append(output).append("\n");

                        // Activate dialogue mode
                        dialogueMode = true;
                        activeDialogueId = npc.getStartingDialogueId();

                        transcript.append("Enter your dialogue choice (1, 2, or 3): ").append("\n");
                        return output;
                    }
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
                    player.reduceTime(100);
                    
                    if (player.outOfTime()) {
                        player.unlockAchievement("Out of time", "You ran out of time");
                    }

                    checkForEnemyEncounter(player, currentRoom);
                    
                    
                    IDatabase db = DatabaseProvider.getInstance();
                    db.updatePlayerLocation(currentRoom.getId());
                    db.updatePlayerTime(player.getId(), player.getTime());

                    StringBuilder sb = new StringBuilder();
                    sb.append(currentRoom.getLongDescription()).append("\n");
                    sb.append(currentRoom.getRoomItemsString()).append("\n");

                    if (!currentRoom.getEvents().isEmpty()) {
                        String eventOutput = eventManager.triggerEvents(currentRoom.getEvents(), player);
                        if (!eventOutput.isEmpty()) {
                            sb.append("\n\n").append(eventOutput);
                        }
                    }
                    
                    for (EndingCondition ending : endings) {
                        if (ending.isMet(player)) {
                            if (!player.hasAchievement(ending.getClass().getSimpleName())) {
                                pendingEnding = ending;
                                pendingEndingPrompt = true;
                                if (player.outOfTime()) {
                                    pendingEndingPrompt = false;
                                    player.unlockAchievement(pendingEnding.getClass().getSimpleName(), pendingEnding.getEndingDescription());
                                    this.endingDescription = pendingEnding.getEndingDescription();

                                    transcript.append("> ").append(command).append("\n");
                                    transcript.append(pendingEnding.getEndingDescription()).append("\n");
                                    return "__ENDING_ACCEPTED__";
                                }
                                String prompt = "You have been offered a position!\n" + ending.getEndingDescription() + "\nDo you accept the job? (yes/no)";
                                transcript.append("> ").append(command).append("\n");
                                transcript.append(prompt).append("\n");
                                return prompt;
                            }
                        }
                    }

                    
                    if (currentRoom.getName() == "Neighborhood") {
                    	player.unlockAchievement("Leave_house", "TESTING");
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
            }  else if (command.startsWith("drop ")) {
                String itemName = command.substring(5).trim();
                IDatabase db = DatabaseProvider.getInstance();

                Items droppedItem = null;
                for (Items item : player.getInventory().getItems()) {
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
            } else if (command.equals("money")) {
            	output = "Balance: $" + player.getMoney();
            } else if (command.equals("time")) {
            	output = "Time: " + player.getTime() + " remaining";
            } else if (command.equals("help")) {
                output =
                        "=== General Commands ===\n" +
                        "- go [direction] : Move north, south, east, west, etc.\n" +
                        "- look           : Re-examine your surroundings.\n" +
                        "- talk           : See who’s here to talk to.\n" +
                        "- talk to [name] : Converse with a specific NPC.\n" +
                        "- take [item]    : Pick up an item.\n" +
                        "- drop [item]    : Drop something from your inventory.\n" +
                        "- inventory      : List items you’re carrying.\n" +
                        "- money          : Check how much money you have.\n" +
                        "- health         : Check your current health.\n" +
                        "- time           : Check time remaining.\n" +
                        "- restart        : Restart the game (and reset the DB).\n" +
                        "- help           : Show this list again.\n\n" +
                        "When offered an ending you can also type:\n" +
                        "- yes            : Accept the job/ending.\n" +
                        "- no             : Decline and keep playing.";
            }else if (command.startsWith("take ")) {
                String itemName = command.substring(5).trim();
                IDatabase db = DatabaseProvider.getInstance();

                List<ItemLocation> roomItems = db.getItemsAtLocation("room", currentRoom.getId());
                ItemLocation found = null;
                for (ItemLocation loc : roomItems) {
                    Items item = itemMap.get(loc.getItemId());

                    if (item != null && item.getName().equalsIgnoreCase(itemName)) {
                        found = loc;
                        break;
                    }
                }

                if (found != null) {
                    Items targetItem = itemMap.get(found.getItemId());
                    if (player.pickUpItem(targetItem)) {
                        currentRoom.removeItem(targetItem);
                        db.transferItem(found.getInstanceId(), "room", currentRoom.getId(), "player", 1);
                        player.reduceTime(50);
                        if (player.outOfTime()) {
                            player.unlockAchievement("Out of time", "You ran out of time");
                        }
                     
                        db.updatePlayerTime(player.getId(), player.getTime());
                        
                        for (EndingCondition ending : endings) {
                            if (ending.isMet(player)) {
                                if (!player.hasAchievement(ending.getClass().getSimpleName())) {
                                    pendingEnding = ending;
                                    pendingEndingPrompt = true;
                                    if (player.outOfTime()) {
                                        pendingEndingPrompt = false;
                                        player.unlockAchievement(pendingEnding.getClass().getSimpleName(), pendingEnding.getEndingDescription());
                                        this.endingDescription = pendingEnding.getEndingDescription();

                                        transcript.append("> ").append(command).append("\n");
                                        transcript.append(pendingEnding.getEndingDescription()).append("\n");
                                        return "__ENDING_ACCEPTED__";
                                    }
                                    String prompt = "You have been offered a position!\n" + ending.getEndingDescription() + "\nDo you accept the job? (yes/no)";
                                    transcript.append("> ").append(command).append("\n");
                                    transcript.append(prompt).append("\n");
                                    return prompt;
                                }
                            }
                        }
                        output = "You picked up the " + targetItem.getName() + ".";
                    } else {
                        currentRoom.addItem(targetItem); // This line seems unnecessary; you already removed it conditionally.
                        output = "You can't carry the " + targetItem.getName() + ".";
                    }
                } else {
                    output = "That item isn't here.";
                }
            } else if (command.equals("inventory")) {
                output = player.getInventoryString();
            } else if (command.equals("restart")) {
                IDatabase db = DatabaseProvider.getInstance();
                db.resetGameData();
                initGame();
                output = "Game and database restarted.\n" +
                         currentRoom.getLongDescription() + "\n" +
                         currentRoom.getRoomItemsString();
            } else if (currentRoom.getName().equals("Gas Station")
                    && command.equalsIgnoreCase("enter gas station")) {
                shopMode = true;
                String welcome = "Welcome to Lou’s Gas & Goods!\nType 'help' for shop commands.\n";
                // record the fact that the player entered the shop
                transcript.append("> ").append(command).append("\n").append(welcome);
                return welcome;
            } else if (command.startsWith("equip ")) {
            	String itemName = command.substring(6).trim();

                // Try to get the item from the inventory
                Items item = player.getItemByName(itemName);
                

                if (item == null) {
                    output = "You don't have a " + itemName + " in your inventory.\n";
                } else if (!item.getType().equals("weapon")) {  // This checks if the type is not "weapon"
                    output = "You can't equip " + itemName + ". It's not a weapon.\n";
                } else {
                    Weapons weapon = (Weapons) item;  // Casting the item to a Weapons object
                    player.equipWeapon(weapon);  // Equip the weapon
                    output = "You equipped the " + weapon.getName() + ". Your attack is now " + player.getAttack() + ".\n";
                }
           } else if (command.startsWith("unequip")){
        	   if (player.getEquippedWeapon() == null) {
        	        output += "You have no weapon equipped.";
        	        return output;
        	    }

        	    String name = player.getEquippedWeapon().getName();
        	    player.unequipWeapon();
        	    output+= "You unequipped the " + name + ". Your attack is now " + player.getAttack() + ".";
           } else if (command.startsWith("use ")) {
               String itemName = command.substring(4).trim();
               Items itemToUse = player.getInventory().getItemByName(itemName);

               if (itemToUse == null) {
                   output = "You don't have an item called '" + itemName + "'.";
               } else {
                   String effectResult = useItem(player, itemToUse);
                   output += effectResult;
                   output += "Health: " + player.getHealth();
               }
           }else {
                output = "I don't understand that command.";
            } 
        } 

        transcript.append("> ").append(command).append("\n");
        transcript.append(output).append("\n");

        return output;
    }
        
    
    private String startAdvancedDialogue(int dialogueId) {
        IDatabase db = DatabaseProvider.getInstance();
        NPCDialogue dialogue = db.getDialogueById(dialogueId);

        if (dialogue == null) {
            return "This NPC has nothing to say.";
        }

        return dialogue.getDialogueText() + "\n1) " + dialogue.getResponse1() +
               "\n2) " + dialogue.getResponse2() + "\n3) " + dialogue.getResponse3();
    }

    public String processDialogueChoice(int dialogueId, int choice) {
        IDatabase db = DatabaseProvider.getInstance();
        NPCDialogue dialogue = db.getDialogueById(dialogueId);

        if (dialogue == null) {
            return "Invalid dialogue option.";
        }

        int nextDialogueId;
        switch (choice) {
            case 1: nextDialogueId = dialogue.getNext1(); break;
            case 2: nextDialogueId = dialogue.getNext2(); break;
            case 3: nextDialogueId = dialogue.getNext3(); break;
            default: return "Please enter a valid option (1, 2, or 3).";
        }
        
        System.out.println(nextDialogueId);

        if (nextDialogueId == 0) { // Assuming 0 means no further dialogue
            return "The conversation has ended.";
        }else if (nextDialogueId == -1) {
        	 player.unlockAchievement("Wall Mart Ending", "You completed the Wall Mart interview");
             String endingCheck = checkForEndingConditions();
             return endingCheck.isEmpty() ? "The conversation has ended." : endingCheck;

        }else if (nextDialogueId == -2) {
        	 player.unlockAchievement("Mazon Driver Ending", "You completed the Mazon driver interview");
             String endingCheck = checkForEndingConditions();
             return endingCheck.isEmpty() ? "The conversation has ended." : endingCheck;

        }else if (nextDialogueId == -3) {
        	 player.unlockAchievement("Mazon CEO Ending", "You completed the Mazon CEO interview");
             String endingCheck = checkForEndingConditions();
             return endingCheck.isEmpty() ? "The conversation has ended." : endingCheck;

        }

        activeDialogueId = nextDialogueId;
        return startAdvancedDialogue(nextDialogueId);
    }

    private String checkForEndingConditions() {
        for (EndingCondition ending : endings) {
            if (ending.isMet(player)) {
                if (!player.hasAchievement(ending.getClass().getSimpleName())) {
                    dialogueMode = false;
                    pendingEnding = ending;
                    pendingEndingPrompt = true;
                    return "You have been offered a position!\n" + ending.getEndingDescription() + 
                           "\nDo you accept the job? (yes/no)";
                }
            }
        }
        return ""; // No ending met
    }

    
	public String getTranscript() {
        return transcript.toString();
    }
    
    public Player getPlayer() {
        return player;
    }

    private List<EndingCondition> getAllEndingConditions() {
        List<EndingCondition> endings = new ArrayList<>();
        endings.add(new McRonaldsEnding());
        endings.add(new WallMartEnding());
        endings.add(new MazonDriverEnding());
        endings.add(new MazonCEOEnding());
        endings.add(new KickedOutEnding());
        endings.add(new LotteryEnding());
        endings.add(new YCPEnding());
        endings.add(new RatKingEnding());
        return endings;
    }
    
    public void setEndingDescription(String description) {
        this.endingDescription = description;
    }

    public String getEndingDescription() {
        return endingDescription;
    }

}
