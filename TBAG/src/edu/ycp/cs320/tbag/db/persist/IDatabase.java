package edu.ycp.cs320.tbag.db.persist;

import java.util.List;
import java.util.Map;

import edu.ycp.cs320.tbag.model.Item;
import edu.ycp.cs320.tbag.model.ItemLocation;
import edu.ycp.cs320.tbag.model.Player;
import edu.ycp.cs320.tbag.model.Room;

public interface IDatabase {
	void savePlayerState(int hp, int roomId);
	Player loadPlayerState();
	void updatePlayerLocation(int roomId);
	int getPlayerRoomId();
	void resetGameData();
	void transferItem(int itemId, String fromType, int fromId, String toType, int toId);

	List<ItemLocation> getItemsAtLocation(String locationType, int locationId);
	List<ItemLocation> loadAllItemLocations();
	Map<Integer, Item> loadItemDefinitions();
	Map<Integer, Room> loadRooms();
	void loadConnections(Map<Integer, Room> roomMap);
}
