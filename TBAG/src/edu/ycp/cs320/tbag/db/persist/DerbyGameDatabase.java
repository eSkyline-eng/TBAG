package edu.ycp.cs320.tbag.db.persist;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ycp.cs320.tbag.model.RegularItem;
import edu.ycp.cs320.tbag.model.ItemLocation;
import edu.ycp.cs320.tbag.model.Player;
import edu.ycp.cs320.tbag.model.Room;
import edu.ycp.cs320.tbag.util.CSVLoader;
import edu.ycp.cs320.tbag.model.NPC;
import edu.ycp.cs320.tbag.model.NPCDialogue;
import edu.ycp.cs320.tbag.ending.Achievement;
import edu.ycp.cs320.tbag.events.Damage;
import edu.ycp.cs320.tbag.events.Dialogue;
import edu.ycp.cs320.tbag.events.Event;
import edu.ycp.cs320.tbag.model.Enemy;
import edu.ycp.cs320.tbag.model.Weapons;
import edu.ycp.cs320.tbag.model.Items;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;

public class DerbyGameDatabase implements IDatabase {
	private static final String DB_PATH;

	static {
		try {
			File configFile = new File("../config.properties"); // Add or remove ../ if needed
			if (!configFile.exists()) {
				throw new FileNotFoundException("Missing config.properties at " + configFile.getAbsolutePath());
			}

			Properties props = new Properties();
			props.load(new FileInputStream(configFile));

			DB_PATH = props.getProperty("db.path");

			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			throw new IllegalStateException("Could not load Derby driver", e);
		}
		try {
			DerbyGameDatabase db = new DerbyGameDatabase();

			// Create tables
			db.createTables();

			// Load item definitions from items.csv
			db.loadItemDefinitionsFromCSV("WebContent/CSV/items.csv");

			// Load room items from room_items.csv
			db.loadRoomItemsFromCSV("WebContent/CSV/room_items.csv");

			// Load rooms from rooms.csv
			db.loadRoomsFromCSV("WebContent/CSV/rooms.csv");

			// Load connections from connection.csv
			db.loadConnectionsFromCSV("WebContent/CSV/connections.csv");
			
			// Load NPC Dialogue from npcDialogue.csv
            db.loadNPCDialogueFromCSV("WebContent/CSV/npcDialogue.csv");

			// Load NPCs from npcs.csv (add this line)
			db.loadNPCsFromCSV("WebContent/CSV/npcs.csv");

			// Load enemies from enemies.csv (add this line)
			db.loadEnemyFromCSV("WebContent/CSV/enemy.csv");

			// Load events from events.csv
			db.loadEventsFromCSV("WebContent/CSV/events.csv");
		
			System.out.println("Database successfully initialized with items.");
		} catch (Exception e) {
			System.err.println("Failed to load CSV files: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private Connection connect() throws SQLException {
		return DriverManager.getConnection(DB_PATH);
	}

	public void createTables() {
		executeTransaction(conn -> {
			// --- Upgrade path: add time column if it doesn’t already exist ---
			try {
				conn.prepareStatement("ALTER TABLE player ADD COLUMN time INT").executeUpdate();
				conn.prepareStatement("UPDATE player SET time = 500 WHERE time IS NULL").executeUpdate();
			} catch (SQLException e) {
				String state = e.getSQLState();
				if (!"X0Y32".equals(state) && !"42Y55".equals(state)) {
					throw e;
				}
			}

			// --- Upgrade path: add money column if it doesn’t already exist ---
			try {
				conn.prepareStatement("ALTER TABLE player ADD COLUMN money INT").executeUpdate();
				conn.prepareStatement("UPDATE player SET money = 50 WHERE money IS NULL").executeUpdate();
			} catch (SQLException e) {
				String state = e.getSQLState();
				if (!"X0Y32".equals(state) && !"42Y55".equals(state)) {
					throw e;
				}
			}

			// --- Upgrade path: add attack column if it doesn’t already exist ---
			try {
				conn.prepareStatement("ALTER TABLE player ADD COLUMN attack INT").executeUpdate();
				conn.prepareStatement("UPDATE player SET attack = 10 WHERE attack IS NULL").executeUpdate();
			} catch (SQLException e) {
				String state = e.getSQLState();
				if (!"X0Y32".equals(state) && !"42Y55".equals(state)) {
					throw e;
				}
			}

			// --- Upgrade path: add attack_multiplier if it doesn’t already exist ---
			try {
				conn.prepareStatement("ALTER TABLE player ADD COLUMN attack_multiplier FLOAT").executeUpdate();
				conn.prepareStatement("UPDATE player SET attack_multiplier = 1.0 WHERE attack_multiplier IS NULL")
						.executeUpdate();
			} catch (SQLException e) {
				String state = e.getSQLState();
				if (!"X0Y32".equals(state) && !"42Y55".equals(state)) {
					throw e;
				}
			}

			try {
				conn.prepareStatement(
						"CREATE TABLE player (" + "id INT PRIMARY KEY, " + "hp INT, " + "current_room_id INT, "
								+ "time INT, " + "money INT, " + "attack INT, " + "attack_multiplier FLOAT" + ")")
						.executeUpdate();
			} catch (SQLException e) {
				if (!"X0Y32".equals(e.getSQLState())) {
					throw e;
				}
			}

			try {
				conn.prepareStatement(
						"CREATE TABLE item_definitions (" + "item_id INT PRIMARY KEY, " + "item_name VARCHAR(50), "
								+ "description VARCHAR(1000), " + "weight DOUBLE, " + "value DOUBLE, " + "type VARCHAR(50), "
								+ "damage DOUBLE, " + "effect INT" + ")" 
								).executeUpdate();
			} catch (SQLException e) {
				if (!"X0Y32".equals(e.getSQLState()))
					throw e;
			}

			try {
				conn.prepareStatement(
						"CREATE TABLE item_instances (" + "instance_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
								+ "item_id INT REFERENCES item_definitions(item_id), " + "location_type VARCHAR(10), "
								+ "location_id INT)")
						.executeUpdate();
			} catch (SQLException e) {
				if (!"X0Y32".equals(e.getSQLState()))
					throw e;
			}

			try {
				conn.prepareStatement("CREATE TABLE rooms (" + "room_id INT PRIMARY KEY, " + "name VARCHAR(100), "
						+ "long_description VARCHAR(1000))").executeUpdate();
			} catch (SQLException e) {
				if (!"X0Y32".equals(e.getSQLState()))
					throw e;
			}

			try {
				conn.prepareStatement(
						"CREATE TABLE connections (" + "connection_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
								+ "from_room_id INT REFERENCES rooms(room_id), " + "direction VARCHAR(50), "
								+ "to_room_id INT REFERENCES rooms(room_id))")
						.executeUpdate();
			} catch (SQLException e) {
				if (!"X0Y32".equals(e.getSQLState()))
					throw e;
			}

			try {
                conn.prepareStatement(
                    "CREATE TABLE npc_dialogue (" +
                    "dialogue_id INT PRIMARY KEY, " +
                    "dialogue_text VARCHAR(1000), " +
                    "response_1 VARCHAR(1000), " +
                    "response_2 VARCHAR(1000), " +
                    "response_3 VARCHAR(1000), " +
                    "next_1 INT, " +
                    "next_2 INT, " +
                    "next_3 INT" +
                    ")"
                ).executeUpdate();
            } catch(SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
            }
			
			try {
                conn.prepareStatement(
                    "CREATE TABLE npc (" +
                    "npc_id INT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "dialogue1 VARCHAR(1000), " +
                    "dialogue2 VARCHAR(1000), " +
                    "dialogue3 VARCHAR(1000), " +
                    "room_id INT REFERENCES rooms(room_id), " +
                    "has_advanced_dialogue BOOLEAN, " +
                    "starting_dialogue_id INT REFERENCES npc_dialogue(dialogue_id)" +
                    ")"
                ).executeUpdate();
            } catch(SQLException e) {
            	if (!"X0Y32".equals(e.getSQLState())) throw e;
            }
			
			try {
			    conn.prepareStatement(
			        "CREATE TABLE enemy (" +
			        "enemy_id INT PRIMARY KEY, " +
			        "name VARCHAR(100), " +
			        "room_id INT REFERENCES rooms(room_id), " +
			        "health INT, " +
			        "attack INT, " +
			        "encounter DOUBLE, " +
			        "runAway DOUBLE, " +
			        "dialogue1 VARCHAR(250), " +
			        "dialogue2 VARCHAR(250), " +
			        "dialogue3 VARCHAR(250))"
			    ).executeUpdate();
			} catch (SQLException e) {
			    if (!"X0Y32".equals(e.getSQLState()))
			        throw e;
			}
			try {
				conn.prepareStatement(
						"CREATE TABLE achievements (" + "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
								+ "player_id INT, " + "achievement_id VARCHAR(50), " + "description VARCHAR(1000), "
								+ "completed BOOLEAN, " + "FOREIGN KEY (player_id) REFERENCES player(id))")
						.executeUpdate();
			} catch (SQLException e) {
				if (!"X0Y32".equals(e.getSQLState())) {
					throw e;
				}
			}
			try {
				conn.prepareStatement("CREATE TABLE events (" + "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
						+ "room_id INT, " + "event_type VARCHAR(50), " + "description VARCHAR(1000), "
						+ "probability DOUBLE, " + "dialogue_text VARCHAR(1000), " + "damage INT, "
						+ "FOREIGN KEY (room_id) REFERENCES rooms(room_id))").executeUpdate();
			} catch (SQLException e) {
				if (!"X0Y32".equals(e.getSQLState())) {
					throw e;
				}
			}

			try {
				// Only for a fresh DB: insert the starting player with ID = 1
				conn.prepareStatement(
						"INSERT INTO player (id, hp, current_room_id, time, money, attack, attack_multiplier) "
								+ "VALUES (1, 100, 1, 500, 50, 10, 1.0)")
						.executeUpdate();
			} catch (SQLException e) {
				String state = e.getSQLState();
				// X0Y32 = column already exists, 42Y55 = table doesn't exist,
				// 23505 = duplicate primary key (player row already seeded)
				if (!"X0Y32".equals(state) && !"42Y55".equals(state) && !"23505".equals(state)) {
					throw e;
				}
			}
			
			

			System.out.println("Tables created (or already existed).");
			return true;
		});
	}

	public void resetGameData() {
		executeTransaction(conn -> {
			// Clear tables
			conn.prepareStatement("DELETE FROM achievements").executeUpdate();
			conn.prepareStatement("DELETE FROM item_instances").executeUpdate();
			conn.prepareStatement("DELETE FROM item_definitions").executeUpdate();
			// conn.prepareStatement("DELETE FROM rooms").executeUpdate();
			conn.prepareStatement("DELETE FROM player").executeUpdate();
			conn.prepareStatement("DELETE FROM enemy").executeUpdate();
			conn.prepareStatement("DELETE FROM connections").executeUpdate();
			conn.prepareStatement("DELETE FROM npc").executeUpdate();
			

			return null;
		});

		// Reload data from CSVs
		loadItemDefinitionsFromCSV("WebContent/CSV/items.csv");
		// loadRoomsFromCSV("WebContent/CSV/rooms.csv");
		loadRoomItemsFromCSV("WebContent/CSV/room_items.csv");
		loadConnectionsFromCSV("WebContent/CSV/connections.csv");
		loadNPCsFromCSV("Webcontent/CSV/npcs.csv");
		loadEnemyFromCSV("WebContent/CSV/enemy.csv");
		

		// Recreate default player
		savePlayerState(100, 1); // HP = 100, room ID = 1
	}

	public void savePlayerState(int hp, int roomId) {
		executeTransaction(conn -> {
			// Check if player already exists
			PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM player WHERE id = 1");
			ResultSet rs = checkStmt.executeQuery();

			if (rs.next()) {
				// Update if exists
				PreparedStatement updateStmt = conn
						.prepareStatement("UPDATE player SET hp = ?, current_room_id = ? WHERE id = 1");
				updateStmt.setInt(1, hp);
				updateStmt.setInt(2, roomId);
				updateStmt.executeUpdate();
			} else {
				// Insert with fixed id = 1
				PreparedStatement insertStmt = conn.prepareStatement(
						"INSERT INTO player (id, hp, current_room_id, time, money, attack, attack_multiplier) VALUES (?, ?, ?, ?, ?, ?, ?)");
				insertStmt.setInt(1, 1);
				insertStmt.setInt(2, hp);
				insertStmt.setInt(3, roomId);
				insertStmt.setInt(4, 500);
				insertStmt.setInt(5, 50); // starting money
				insertStmt.setInt(6, 10); // base attack
				insertStmt.setDouble(7, 1.0); // default multiplier
				insertStmt.executeUpdate();
			}

			return null;
		});
	}

	@Override
	public Player loadPlayerState() {
		return executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM player");
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				Player player = new Player();

				// *** NEW: pick up the DB id so unlockAchievement() uses a valid key ***
				player.setId(rs.getInt("id"));

				// Existing fields
				player.setHealth(rs.getInt("hp"));

				// *** OPTIONAL: if you’ve already added money/attack columns ***
				player.setMoney(rs.getInt("money"));

				player.setAttackMultiplier(rs.getDouble("attack_multiplier"));

				player.setTime(rs.getInt("time"));

				// Restore room
				int playerRoomId = rs.getInt("current_room_id");
				// (you’ll still need to hook this back to the Room object in initGame)
				player.setCurrentRoom(null);

				return player;
			} else {
				return null;
			}
		});
	}

	public void updatePlayerLocation(int roomId) {
		executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("UPDATE player SET current_room_id = ?");
			stmt.setInt(1, roomId);
			stmt.executeUpdate();
			return null;
		});
	}

	@Override
	public double getPlayerAttackMultiplier(int playerId) {
		return executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("SELECT attack_multiplier FROM player WHERE id = ?");
			stmt.setInt(1, playerId);
			ResultSet rs = stmt.executeQuery();
			return (rs.next() ? rs.getDouble("attack_multiplier") : 1.0);
		});
	}

	@Override
	public void updatePlayerAttackMultiplier(int playerId, double multiplier) {
		executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("UPDATE player SET attack_multiplier = ? WHERE id = ?");
			stmt.setDouble(1, multiplier);
			stmt.setInt(2, playerId);
			stmt.executeUpdate();
			return null;
		});
	}

	public int getPlayerRoomId() {
		return executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("SELECT current_room_id FROM player");
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("current_room_id");
			}
			return 1; // fallback to room 1 if not found
		});
	}

	// ----- New player‐money methods -----
	@Override
	public int getPlayerMoney(int playerId) {
		return executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("SELECT money FROM player WHERE id = ?");
			stmt.setInt(1, playerId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("money");
			}
			return 0;
		});
	}

	@Override
	public void updatePlayerMoney(int playerId, int newBalance) {
		executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("UPDATE player SET money = ? WHERE id = ?");
			stmt.setInt(1, newBalance);
			stmt.setInt(2, playerId);
			stmt.executeUpdate();
			return null;
		});
	}

	@Override
	public int getPlayerTime(int playerId) {
		return executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("SELECT time FROM player WHERE id = ?");
			stmt.setInt(1, playerId);
			ResultSet rs = stmt.executeQuery();
			return (rs.next() ? rs.getInt("time") : 0);
		});
	}

	@Override
	public void updatePlayerTime(int playerId, int newTime) {
		executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("UPDATE player SET time = ? WHERE id = ?");
			stmt.setInt(1, newTime);
			stmt.setInt(2, playerId);
			stmt.executeUpdate();
			return null;
		});
	}

	// ----- New player‐attack methods -----
	@Override
	public int getPlayerAttack(int playerId) {
		return executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("SELECT attack FROM player WHERE id = ?");
			stmt.setInt(1, playerId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("attack");
			}
			return 0;
		});
	}

	@Override
	public void updatePlayerAttack(int playerId, int newAttack) {
		executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("UPDATE player SET attack = ? WHERE id = ?");
			stmt.setInt(1, newAttack);
			stmt.setInt(2, playerId);
			stmt.executeUpdate();
			return null;
		});
	}
	

	// ----- New player‐health update method -----
	@Override
	public void updatePlayerHealth(int playerId, int newHealth) {
		executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("UPDATE player SET hp = ? WHERE id = ?");
			stmt.setInt(1, newHealth);
			stmt.setInt(2, playerId);
			stmt.executeUpdate();
			return null;
		});
	}

	public void loadEventsFromCSV(String filePath) {
		executeTransaction(conn -> {
			List<String[]> events;
			try {
				events = CSVLoader.loadCSV(filePath, "\\|");
			} catch (IOException e) {
				throw new SQLException("Failed to load CSV: " + filePath, e);
			}
			conn.prepareStatement("DELETE FROM events").executeUpdate();

			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO events (room_id, event_type, description, probability, dialogue_text, damage) VALUES (?, ?, ?, ?, ?, ?)");

			for (String[] row : events) {
				stmt.setInt(1, Integer.parseInt(row[0].trim()));
				stmt.setString(2, row[1].trim());
				stmt.setString(3, row[2].trim());
				stmt.setDouble(4, Double.parseDouble(row[3].trim()));
				stmt.setString(5, row[4].trim());
				stmt.setInt(6, Integer.parseInt(row[5].trim()));
				stmt.addBatch();
			}

			stmt.executeBatch();
			return null;
		});
	}
	

	public List<Event> loadAllEvents() {
		return executeTransaction(conn -> {
			List<Event> events = new ArrayList<>();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM events");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String eventType = rs.getString("event_type");
				double probability = rs.getDouble("probability");
				String dialogue = rs.getString("dialogue_text");
				int damage = rs.getInt("damage");
				int roomId = rs.getInt("room_id");

				Event event = null;

				if (eventType.equalsIgnoreCase("dialogue")) {
					event = new Dialogue(probability, dialogue);
				} else if (eventType.equalsIgnoreCase("damage")) {
					event = new Damage(probability, dialogue, damage);
				} else {
					System.err.println("Unknown event type: " + eventType);
				}

				if (event != null) {
					event.setRoomId(roomId);
					events.add(event);
				}
			}
			return events;
		});
	}

	public void loadItemDefinitionsFromCSV(String filePath) {
		executeTransaction(conn -> {
			List<String[]> records;
			try {
				records = CSVLoader.loadCSV(filePath, "\\|");
			} catch (IOException e) {
				throw new SQLException("Failed to load CSV: " + filePath, e);
			}
			
			//conn.prepareStatement("DELETE FROM room_items").executeUpdate(); // or whatever table references item_definitions
			
			//conn.prepareStatement("DELETE FROM player_inventory").executeUpdate(); // if applicable

			conn.prepareStatement("DELETE FROM item_definitions").executeUpdate(); // optional cleanup

			PreparedStatement stmt = conn.prepareStatement(
			        "INSERT INTO item_definitions (item_id, item_name, description, weight, value, type, damage, effect) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");


					for (String[] row : records) {
			            int id = Integer.parseInt(row[0].trim());
			            String name = row[1].trim();
			            String description = row[2].trim();
			            double weight = Double.parseDouble(row[3].trim());
			            double value = Double.parseDouble(row[4].trim());
			            String type = row[5].trim();
			            Double damage = type.equalsIgnoreCase("weapon") ? Double.parseDouble(row[6].trim()) : null; // Only weapons have damage
			            Integer consumable = type.equalsIgnoreCase("consumable") ? Integer.parseInt(row[7].trim()) : null; //Only for consumables
			            
			         // Insert common item details
			            stmt.setInt(1, id);
			            stmt.setString(2, name);
			            stmt.setString(3, description);
			            stmt.setDouble(4, weight);
			            stmt.setDouble(5, value);

			            // Set type and damage if applicable
			            stmt.setString(6, type);
			            if (damage != null) {
			            	stmt.setDouble(7, damage);
			            } else {
			            	stmt.setNull(7, Types.DOUBLE);
			            }

			            if (consumable != null) {
			            	stmt.setInt(8, consumable);
			            } else {
			            	stmt.setNull(8, Types.INTEGER);
			            }

			            stmt.addBatch();
					}
					

		            
			stmt.executeBatch();
			return null;
		});
	}

	public void loadRoomItemsFromCSV(String filePath) {
		executeTransaction(conn -> {
			List<String[]> records;
			try {
				records = CSVLoader.loadCSV(filePath, "\\|");
			} catch (IOException e) {
				throw new SQLException("Failed to load CSV: " + filePath, e);
			}

			// Lookup item_id → item_name
			Map<Integer, String> itemIdToName = new HashMap<>();
			PreparedStatement lookup = conn.prepareStatement("SELECT item_id, item_name FROM item_definitions");
			ResultSet rs = lookup.executeQuery();
			while (rs.next()) {
				itemIdToName.put(rs.getInt("item_id"), rs.getString("item_name"));
			}

			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO item_instances (item_id, location_type, location_id) VALUES (?, 'room', ?)");

			for (String[] row : records) {
				int roomId = Integer.parseInt(row[0].trim());
				int itemId = Integer.parseInt(row[1].trim());

				String name = itemIdToName.get(itemId);
				if (name != null) {
					stmt.setInt(1, itemId);
					stmt.setInt(2, roomId);
					stmt.addBatch();
				} else {
					System.err.println("Item ID not found in definitions: " + itemId);
				}
			}

			stmt.executeBatch();
			return null;
		});
	}
	

	public void loadRoomsFromCSV(String filePath) {
		executeTransaction(conn -> {
			List<String[]> roomRecords;
			try {
				roomRecords = CSVLoader.loadCSV(filePath, "\\|");
			} catch (IOException e) {
				throw new SQLException("Failed to load CSV: " + filePath, e);
			}

			// Clear existing rooms (optional)
			conn.prepareStatement("DELETE FROM rooms").executeUpdate();

			// Insert rooms
			PreparedStatement stmt = conn
					.prepareStatement("INSERT INTO rooms (room_id, name, long_description) VALUES (?, ?, ?)");

			for (String[] record : roomRecords) {
				try {
					int roomId = Integer.parseInt(record[0].trim());
					String roomName = record[1].trim();
					String description = record[2].trim();
					System.out.println("Inserting room: " + roomId + " - " + roomName);
					// Check for data integrity
					if (roomName.isEmpty() || description.isEmpty()) {
						throw new IllegalArgumentException("Room name or description cannot be empty.");
					}

					stmt.setInt(1, roomId);
					stmt.setString(2, roomName);
					stmt.setString(3, description);
					stmt.addBatch();
				} catch (NumberFormatException e) {
					// Log or handle invalid roomId format
					System.err.println("Invalid room ID in CSV: " + record[0]);
				} catch (IllegalArgumentException e) {
					// Handle empty room name/description
					System.err.println("Invalid room data: " + record[1] + " - " + record[2]);
				}

			}

			stmt.executeBatch();
			return null;
		});
	}

	public void loadConnectionsFromCSV(String filePath) {
		executeTransaction(conn -> {
			List<String[]> connectionRecords;
			try {
				connectionRecords = CSVLoader.loadCSV(filePath, "\\|");
			} catch (IOException e) {
				throw new SQLException("Failed to load CSV: " + filePath, e);
			}

			// Clear existing connections (optional)
			conn.prepareStatement("DELETE FROM connections").executeUpdate();

			// Insert connections
			PreparedStatement stmt = conn
					.prepareStatement("INSERT INTO connections (from_room_id, direction, to_room_id) VALUES (?, ?, ?)");
			for (String[] record : connectionRecords) {
				stmt.setInt(1, Integer.parseInt(record[0].trim()));
				stmt.setString(2, record[1].trim().toLowerCase());
				stmt.setInt(3, Integer.parseInt(record[2].trim()));
				stmt.addBatch();
			}
			stmt.executeBatch();
			return null;
		});
	}

	public void loadNPCsFromCSV(String filePath) {
		executeTransaction(conn -> {
			List<String[]> records;
			try {
				// Read NPC definitions: npc_id | name | dialogue1 | dialogue2 | dialogue3 | room_id | has_advanced_dialogue | starting_dialogue_id
				records = CSVLoader.loadCSV(filePath, "\\|");
			} catch (IOException e) {
				throw new SQLException("Failed to load CSV: " + filePath, e);
			}

			// Clear existing NPCs
			conn.prepareStatement("DELETE FROM npc").executeUpdate();

			// Prepare batch‐insert into npc table
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO npc (npc_id, name, dialogue1, dialogue2, dialogue3, room_id, has_advanced_dialogue, starting_dialogue_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

			for (String[] row : records) {
				int npcId = Integer.parseInt(row[0].trim());
                String name = row[1].trim();
                String dialog1 = row[2].trim();
                String dialog2 = row[3].trim();
                String dialog3 = row[4].trim();
                int roomId = Integer.parseInt(row[5].trim());
                boolean hasAdvancedDialogue = Boolean.parseBoolean(row[6].trim());

                // Handle starting dialogue ID, avoiding NumberFormatException if empty
                Integer startingDialogueId = null;
                if (!row[7].trim().isEmpty()) {
                    startingDialogueId = Integer.parseInt(row[7].trim());
                }

                System.out.println("Inserting NPC: " + npcId + " - " + name);

				stmt.setInt(1, npcId);
				stmt.setString(2, name);
				stmt.setString(3, dialog1);
                stmt.setString(4, dialog2);
                stmt.setString(5, dialog3);
                stmt.setInt(6, roomId);
                stmt.setBoolean(7, hasAdvancedDialogue);

                // Handle possible null value
                if (startingDialogueId != null) {
                    stmt.setInt(8, startingDialogueId);
                } else {
                    stmt.setNull(8, java.sql.Types.INTEGER);
                }

                stmt.addBatch();
            }

            stmt.executeBatch();
            return null;
        });
    }
    
    public void loadNPCDialogueFromCSV(String filePath) {
        executeTransaction(conn -> {
            List<String[]> records;
            try {
                // Load CSV file using the pipe separator "|"
                records = CSVLoader.loadCSV(filePath, "\\|");
            } catch (IOException e) {
                throw new SQLException("Failed to load CSV: " + filePath, e);
            }

            // Clear existing dialogue entries
            conn.prepareStatement("DELETE FROM npc_dialogue").executeUpdate();

            // Prepare batch insert into npc_dialogue table
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO npc_dialogue (dialogue_id, dialogue_text, response_1, response_2, response_3, next_1, next_2, next_3) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );

            for (String[] row : records) {
                int dialogueId = Integer.parseInt(row[0].trim());
                String dialogueText = row[1].trim();
                String response1 = row[2].trim();
                String response2 = row[3].trim();
                String response3 = row[4].trim();
                
                // Handle potential NULL values for next dialogue IDs
                Integer next1 = (!row[5].trim().equalsIgnoreCase("NULL") && !row[5].trim().isEmpty()) 
                        ? Integer.parseInt(row[5].trim()) : null;

                Integer next2 = (!row[6].trim().equalsIgnoreCase("NULL") && !row[6].trim().isEmpty()) 
                        ? Integer.parseInt(row[6].trim()) : null;

                Integer next3 = (!row[7].trim().equalsIgnoreCase("NULL") && !row[7].trim().isEmpty()) 
                        ? Integer.parseInt(row[7].trim()) : null;


                System.out.println("Inserting Dialogue " + dialogueId + " - " + dialogueText);
                stmt.setInt(1, dialogueId);
                stmt.setString(2, dialogueText);
                stmt.setString(3, response1);
                stmt.setString(4, response2);
                stmt.setString(5, response3);

                if (next1 != null) stmt.setInt(6, next1); else stmt.setNull(6, java.sql.Types.INTEGER);
                if (next2 != null) stmt.setInt(7, next2); else stmt.setNull(7, java.sql.Types.INTEGER);
                if (next3 != null) stmt.setInt(8, next3); else stmt.setNull(8, java.sql.Types.INTEGER);

				stmt.addBatch();
			}

			stmt.executeBatch();
			return null;
		});
	}

	public void loadEnemyFromCSV(String filePath) {

		executeTransaction(conn -> {
			List<String[]> records;
			try {
				// Read enemy definitions: enemy_id | name | room_id | health | attack |
				// encounter | runAway | dialogue1 | dialogue2 | dialogue3
				records = CSVLoader.loadCSV(filePath, "\\|");
			} catch (IOException e) {
				throw new SQLException("Failed to load CSV: " + filePath, e);
			}

			// Clear existing enemies
			try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM enemy")) {
				deleteStmt.executeUpdate();
			}

			// Prepare batch-insert into enemy table
			try (PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO enemy (enemy_id, name, room_id, health, attack, encounter, runAway, dialogue1, dialogue2, dialogue3) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
				
				for (String[] row : records) {
					// Prepare the values
					int enemyId = Integer.parseInt(row[0].trim());
					String name = row[1].trim();
					int roomId = Integer.parseInt(row[2].trim());
					int health = Integer.parseInt(row[3].trim());
					int attack = Integer.parseInt(row[4].trim());
					double encounter = Double.parseDouble(row[5].trim());
					double runAway = Double.parseDouble(row[6].trim());
					String dialogue1 = row[7].trim();
					String dialogue2 =  row[8].trim();
		    	    String dialogue3 =  row[9].trim();

					// Log the data
					System.out.println("Inserting enemy: " + enemyId + ", " + name + ", " + roomId + ", " + health
							+ ", " + attack + ", " + encounter + ", " + runAway + ", " + dialogue1);

					stmt.setInt(1, enemyId);
					stmt.setString(2, name);
					stmt.setInt(3, roomId);
					stmt.setInt(4, health);
					stmt.setInt(5, attack);
					stmt.setDouble(6, encounter);
					stmt.setDouble(7, runAway);
					stmt.setString(8, dialogue1);
					stmt.setString(9, dialogue2);
		    	    stmt.setString(10, dialogue3);

					stmt.addBatch();
				}

				stmt.executeBatch();
			}

			return null;
		});
	}

	public void addAchievement(int playerId, Achievement achievement) {
		executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO achievements (player_id, achievement_id, description, completed) VALUES (?, ?, ?, ?)");
			stmt.setInt(1, playerId);
			stmt.setString(2, achievement.getId());
			stmt.setString(3, achievement.getDescription());
			stmt.setBoolean(4, achievement.isCompleted());
			stmt.executeUpdate();
			return null;
		});
	}

	public void removeAchievement(String id) {
		executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM achievements WHERE achievement_id=?");
			stmt.setString(1, id);
			stmt.executeUpdate();

			return null;
		});
	}

	public List<Achievement> getAchievementsForPlayer(int playerId) {
		return executeTransaction(conn -> {
			List<Achievement> list = new ArrayList<>();
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT achievement_id, description, completed FROM achievements WHERE player_id = ?");
			stmt.setInt(1, playerId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(new Achievement(rs.getString("achievement_id"), rs.getString("description"),
						rs.getBoolean("completed")));
			}
			return list;
		});
	}

	public void transferItem(int instanceId, String fromType, int fromId, String toType, int toId) {
		executeTransaction(conn -> {
			PreparedStatement update = conn.prepareStatement(
					"UPDATE item_instances SET location_type = ?, location_id = ? WHERE instance_id = ?");
			update.setString(1, toType);
			update.setInt(2, toId);
			update.setInt(3, instanceId);
			int rows = update.executeUpdate();

			if (rows == 0) {
				System.err.println("No item instance found to transfer: instance_id=" + instanceId);
			}
			return null;
		});
	}

	public List<ItemLocation> getItemsAtLocation(String locationType, int locationId) {
		return executeTransaction(conn -> {
			List<ItemLocation> items = new ArrayList<>();

			PreparedStatement stmt = conn.prepareStatement("SELECT instance_id, item_id, location_type, location_id "
					+ "FROM item_instances " + "WHERE location_type = ? AND location_id = ?");
			stmt.setString(1, locationType);
			stmt.setInt(2, locationId);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int instanceId = rs.getInt("instance_id");
				int itemId = rs.getInt("item_id");
				String locType = rs.getString("location_type");
				int locId = rs.getInt("location_id");

				items.add(new ItemLocation(instanceId, itemId, locType, locId));
			}

			return items;
		});
	}

	public List<NPC> loadAllNPCs() {
		return executeTransaction(conn -> {
			List<NPC> npcs = new ArrayList<>();

			// Fetch every NPC definition
			PreparedStatement stmt = conn.prepareStatement("SELECT name, dialogue1, dialogue2, dialogue3, room_id, has_advanced_dialogue, starting_dialogue_id FROM npc");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString("name");
				String dialogue1 = rs.getString("dialogue1");
                String dialogue2 = rs.getString("dialogue2");
                String dialogue3 = rs.getString("dialogue3");
				int roomId = rs.getInt("room_id");
				boolean hasAdvancedDialogue = rs.getBoolean("has_advanced_dialogue");
                int startingDialogueId = rs.getInt("starting_dialogue_id");
                

				// Construct the NPC
                NPC npc = new NPC(name, dialogue1, dialogue2, dialogue3, roomId, hasAdvancedDialogue, startingDialogueId);
				// npc.setRoomId(roomId);

				npcs.add(npc);
			}

			return npcs;
		});

	}

	public List<Enemy> loadAllEnemies() {
		return executeTransaction(conn -> {
			List<Enemy> enemies = new ArrayList<>();

			// Fetch all enemy definitions from the enemy table
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM enemy");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int enemyId = rs.getInt("enemy_id");
				String name = rs.getString("name");
				int roomId = rs.getInt("room_id");
				int health = rs.getInt("health");
				int attack = rs.getInt("attack");
				double encounter = rs.getDouble("encounter");
				double runAway = rs.getDouble("runAway");
				String dialogue1 = rs.getString("dialogue1");
				String dialogue2 = rs.getString("dialogue2");
                String dialogue3 = rs.getString("dialogue3");
                Enemy enemy = new Enemy(enemyId, name, roomId, health, attack, encounter, runAway, dialogue1, dialogue2, dialogue3);

				enemies.add(enemy);
			}
			return enemies;
		});
	}
	

	public List<ItemLocation> loadAllItemLocations() {
		return executeTransaction(conn -> {
			List<ItemLocation> items = new ArrayList<>();
			PreparedStatement stmt = conn
					.prepareStatement("SELECT instance_id, item_id, location_type, location_id FROM item_instances");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ItemLocation item = new ItemLocation(rs.getInt("instance_id"), rs.getInt("item_id"),
						rs.getString("location_type"), rs.getInt("location_id"));
				items.add(item);
			}
			return items;
		});
	}

	public Map<Integer, Items> loadItemDefinitions() {
		return executeTransaction(conn -> {
			Map<Integer, Items> map = new HashMap<>();

			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_definitions");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("item_id");
				String name = rs.getString("item_name");
				String description = rs.getString("description");
				double weight = rs.getDouble("weight");
				double value = rs.getDouble("value");
				String type = rs.getString("type");
				double damage = rs.getDouble("damage");
				int effect = rs.getInt("effect");

				Items item;

				switch (type.toLowerCase()) {
					case "weapon":
						item = new Weapons(id, name, description, weight, value, damage);
						break;
					case "normal":
						item = new RegularItem(id, name, description, weight, value);
						break;
					// Optionally add "consumable" later when implemented
					default:
						System.err.println("Unknown item type for ID " + id + ": " + type);
						continue;
				}
				

				map.put(id, item);
			}

			return map;
		});
	}

	public Map<Integer, Room> loadRooms() {
		return executeTransaction(conn -> {
			Map<Integer, Room> roomMap = new HashMap<>();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rooms");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int roomId = rs.getInt("room_id");
				String name = rs.getString("name");
				String description = rs.getString("long_description");
				Room room = new Room(roomId, name, description);
				roomMap.put(roomId, room);
			}

			return roomMap;
		});
	}

	public void loadConnections(Map<Integer, Room> roomMap) {
		executeTransaction(conn -> {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM connections");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int fromRoomId = rs.getInt("from_room_id");
				String direction = rs.getString("direction");
				int toRoomId = rs.getInt("to_room_id");

				Room fromRoom = roomMap.get(fromRoomId);
				Room toRoom = roomMap.get(toRoomId);
				if (fromRoom != null && toRoom != null) {
					fromRoom.addConnection(direction, toRoom);
				}
			}
			return null;
		});
	}

	public NPCDialogue getDialogueById(int dialogueId) {
        return executeTransaction(conn -> {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM npc_dialogue WHERE dialogue_id = ?"
            );
            stmt.setInt(1, dialogueId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new NPCDialogue(
                    rs.getInt("dialogue_id"),
                    rs.getString("dialogue_text"),
                    rs.getString("response_1"),
                    rs.getString("response_2"),
                    rs.getString("response_3"),
                    rs.getInt("next_1"),
                    rs.getInt("next_2"),
                    rs.getInt("next_3")
                );
            }

            return null; // No dialogue found
        });
    }
	
	public <ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
		try {
			return doExecuteTransaction(txn);
		} catch (SQLException e) {
			throw new PersistenceException("Transaction failed", e);
		}
	}

	private interface Transaction<ResultType> {
		ResultType execute(Connection conn) throws SQLException;
	}

	private <ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
		Connection conn = connect();

		try {
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;

			while (!success && numAttempts < 10) {
				try {
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} catch (SQLException e) {
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) {
						numAttempts++;
					} else {
						throw e;
					}
				}
			}

			if (!success) {
				throw new SQLException("Transaction failed (too many retries)");
			}

			return result;
		} finally {
			DBUtil.closeQuietly(conn);
		}
	}

	public static void main(String[] args) {
		new DerbyGameDatabase().createTables();
	}

}
