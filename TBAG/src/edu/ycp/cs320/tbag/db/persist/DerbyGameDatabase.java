package edu.ycp.cs320.tbag.db.persist;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ycp.cs320.tbag.model.Item;
import edu.ycp.cs320.tbag.model.ItemLocation;
import edu.ycp.cs320.tbag.model.Player;
import edu.ycp.cs320.tbag.util.CSVLoader;

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
            try {
                conn.prepareStatement(
                    "CREATE TABLE player (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "hp INT, " +
                    "current_room_id INT)"
                ).executeUpdate();
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
            }

            try {
                conn.prepareStatement(
                    "CREATE TABLE item_definitions (" +
                    "item_id INT PRIMARY KEY, " +
                    "item_name VARCHAR(50), " +
                    "description VARCHAR(1000), " +
                    "weight DOUBLE, " +
                    "value DOUBLE)"
                ).executeUpdate();
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
            }

            try {
                conn.prepareStatement(
                    "CREATE TABLE item_instances (" +
                    "instance_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "item_id INT REFERENCES item_definitions(item_id), " +
                    "location_type VARCHAR(10), " +
                    "location_id INT)"
                ).executeUpdate();
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
            }

            System.out.println("Tables created (or already existed).");
            return true;
        });
    }



    public void resetGameData() {
    	executeTransaction(conn -> {
    		// Clear tables
    		conn.prepareStatement("DELETE FROM item_instances").executeUpdate();
            conn.prepareStatement("DELETE FROM item_definitions").executeUpdate();
            conn.prepareStatement("DELETE FROM player").executeUpdate();
    		return null;
    	});

    	// Reload data from CSVs
    	loadItemDefinitionsFromCSV("WebContent/CSV/items.csv");
    	loadRoomItemsFromCSV("WebContent/CSV/room_items.csv");

    	// Recreate default player
    	savePlayerState(100, 1);  // HP = 100, room ID = 1
    }

    
    public void savePlayerState(int hp, int roomId) {
    	executeTransaction(conn -> {
    		PreparedStatement delete = conn.prepareStatement("DELETE FROM player");
    		delete.executeUpdate();

    		PreparedStatement insert = conn.prepareStatement(
    			"INSERT INTO player (hp, current_room_id) VALUES (?, ?)"
    		);
    		insert.setInt(1, hp);
    		insert.setInt(2, roomId);
    		insert.executeUpdate();
    		return null;
    	});
    }
    
    public Player loadPlayerState() {
    	return executeTransaction(conn -> {
    		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM player");
    		ResultSet rs = stmt.executeQuery();

    		if (rs.next()) {
    			Player player = new Player();
    			
    			player.setHealth(rs.getInt("hp"));
    			
    			player.setCurrentRoom(null); 
    			int playerRoomId = rs.getInt("current_room_id");
    			
    			return player;
    		} else {
    			return null;
    		}
    	});
    }

    public void updatePlayerLocation(int roomId) {
    	executeTransaction(conn -> {
    		PreparedStatement stmt = conn.prepareStatement(
    			"UPDATE player SET current_room_id = ?"
    		);
    		stmt.setInt(1, roomId);
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
    
    public void loadItemDefinitionsFromCSV(String filePath) {
        executeTransaction(conn -> {
            List<String[]> records;
            try {
                records = CSVLoader.loadCSV(filePath, "\\|");
            } catch (IOException e) {
                throw new SQLException("Failed to load CSV: " + filePath, e);
            }

            conn.prepareStatement("DELETE FROM item_definitions").executeUpdate(); // optional cleanup

            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO item_definitions (item_id, item_name, description, weight, value) VALUES (?, ?, ?, ?, ?)"
            );

            for (String[] row : records) {
                stmt.setInt(1, Integer.parseInt(row[0].trim()));
                stmt.setString(2, row[1].trim());
                stmt.setString(3, row[2].trim());
                stmt.setDouble(4, Double.parseDouble(row[3].trim()));
                stmt.setDouble(5, Double.parseDouble(row[4].trim()));
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
            	    "INSERT INTO item_instances (item_id, location_type, location_id) VALUES (?, 'room', ?)"
            );

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

    @Override
    public void transferItem(int instanceId, String fromType, int fromId, String toType, int toId) {
        executeTransaction(conn -> {
            PreparedStatement update = conn.prepareStatement(
                "UPDATE item_instances SET location_type = ?, location_id = ? WHERE instance_id = ?"
            );
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


    @Override
    public List<ItemLocation> getItemsAtLocation(String locationType, int locationId) {
        return executeTransaction(conn -> {
            List<ItemLocation> items = new ArrayList<>();

            PreparedStatement stmt = conn.prepareStatement(
                "SELECT instance_id, item_id, location_type, location_id " +
                "FROM item_instances " +
                "WHERE location_type = ? AND location_id = ?"
            );
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


    public List<ItemLocation> loadAllItemLocations() {
        return executeTransaction(conn -> {
            List<ItemLocation> items = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT instance_id, item_id, location_type, location_id FROM item_instances"
            );
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ItemLocation item = new ItemLocation(
                    rs.getInt("instance_id"),
                    rs.getInt("item_id"),
                    rs.getString("location_type"),
                    rs.getInt("location_id")
                );
                items.add(item);
            }
            return items;
        });
    }


    public Map<Integer, Item> loadItemDefinitions() {
        return executeTransaction(conn -> {
            Map<Integer, Item> map = new HashMap<>();

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_definitions");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("item_name");
                String description = rs.getString("description");
                double weight = rs.getDouble("weight");
                double value = rs.getDouble("value");

                map.put(id, new Item(id, name, description, weight, value));
            }

            return map;
        });
    }

    public<ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
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
