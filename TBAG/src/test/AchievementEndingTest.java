package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ycp.cs320.tbag.model.Enemy;
import edu.ycp.cs320.tbag.model.RegularItem;
import edu.ycp.cs320.tbag.model.ItemLocation;
import edu.ycp.cs320.tbag.model.NPC;
import edu.ycp.cs320.tbag.model.NPCDialogue;
import edu.ycp.cs320.tbag.model.Player;
import edu.ycp.cs320.tbag.model.Room;
import edu.ycp.cs320.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.tbag.db.persist.IDatabase;
import edu.ycp.cs320.tbag.ending.*;
import edu.ycp.cs320.tbag.events.Event;

public class AchievementEndingTest {
    private Player player;

    @BeforeEach
    public void setup() {
        DatabaseProvider.setInstance(new IDatabase() {
            @Override
            public void savePlayerState(int hp, int roomId) {}

            @Override
            public Player loadPlayerState() {
                return null;
            }

            @Override
            public void updatePlayerLocation(int roomId) {}

            @Override
            public int getPlayerRoomId() {
                return 1;
            }

            @Override
            public void resetGameData() {}

            @Override
            public void transferItem(int itemId, String fromType, int fromId, String toType, int toId) {}

            @Override
            public List<ItemLocation> getItemsAtLocation(String locationType, int locationId) {
                return new java.util.ArrayList<>();
            }

            @Override
            public List<ItemLocation> loadAllItemLocations() {
                return new java.util.ArrayList<>();
            }

            @Override
            public Map loadItemDefinitions() {
                return new java.util.HashMap<>();
            }

            @Override
            public Map<Integer, Room> loadRooms() {
                return new java.util.HashMap<>();
            }

            @Override
            public void loadConnections(Map<Integer, Room> roomMap) {}

            @Override
            public List<Event> loadAllEvents() {
                return new java.util.ArrayList<>();
            }

            @Override
            public void addAchievement(int playerId, Achievement achievement) {}

            @Override
            public void removeAchievement(String id) {}

            @Override
            public List<Achievement> getAchievementsForPlayer(int playerId) {
                return new java.util.ArrayList<>();
            }

            @Override
            public void loadNPCsFromCSV(String filePath) {}

            @Override
            public List<NPC> loadAllNPCs() {
                return new java.util.ArrayList<>();
            }

            @Override
            public void loadEnemyFromCSV(String filePath) {}

            @Override
            public List<Enemy> loadAllEnemies() {
                return new java.util.ArrayList<>();
            }

			@Override
			public int getPlayerMoney(int playerId) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void updatePlayerMoney(int playerId, int newBalance) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getPlayerAttack(int playerId) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void updatePlayerAttack(int playerId, int newAttack) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updatePlayerHealth(int playerId, int newHealth) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public double getPlayerAttackMultiplier(int playerId) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void updatePlayerAttackMultiplier(int playerId, double multiplier) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getPlayerTime(int playerId) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void updatePlayerTime(int playerId, int newTime) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public NPCDialogue getDialogueById(int dialogueId) {
				// TODO Auto-generated method stub
				return null;
			}
        });

        player = new Player();
    }


    @Test
    public void testLotteryEndingAchieved() {
        player.unlockAchievement("lottery_win", "You won the lottery!");
        assertTrue(player.hasAchievement("lottery_win"));
        assertTrue(new LotteryEnding().isMet(player));
    }

    @Test
    public void testKickedOutEndingAchieved() {
        player.unlockAchievement("Out of time", "test");
        assertTrue(player.hasAchievement("Out of time"));
        assertTrue(new KickedOutEnding().isMet(player));
    }

    @Test
    public void testYCPEndingAchieved() {
        player.setCurrentRoom(new Room(5, "School", ""));
        player.unlockAchievement("professor_trigger", "Became a professor");
        assertEquals("School", player.getCurrentRoom().getName());
        assertTrue(player.hasAchievement("professor_trigger"));
        assertTrue(new YCPEnding().isMet(player));
    }

    @Test
    public void testMazonDriverEndingAchieved() {
        player.setCurrentRoom(new Room(6, "Mazon", ""));
        player.unlockAchievement("Mazon Driver Ending", "Had the Mazon driver interview");
        player.pickUpItem(new RegularItem(12, "resume", "", 0.1, 0));
        assertEquals("Mazon", player.getCurrentRoom().getName());
        assertTrue(player.checkInventory("resume"));
        assertTrue(player.hasAchievement("Mazon Driver Ending"));
        assertTrue(new MazonDriverEnding().isMet(player));
    }

    @Test
    public void testRatKingEndingAchieved() {
        player.pickUpItem(new RegularItem(14, "crowbar", "", 2.5, 15));
        player.unlockAchievement("defeated_rat_king", "You defeated the Rat King");
        assertTrue(player.checkInventory("crowbar"));
        assertTrue(player.hasAchievement("defeated_rat_king"));
        assertTrue(new RatKingEnding().isMet(player));
    }

    @Test
    public void testWallMartEndingAchieved() {
        player.setCurrentRoom(new Room(8, "Wall Mart", ""));
        player.pickUpItem(new RegularItem(12, "resume", "", 0.1, 0));
        player.unlockAchievement("Wall Mart Ending", "test");
        assertEquals("Wall Mart", player.getCurrentRoom().getName());
        assertTrue(player.checkInventory("resume"));
        assertTrue(new WallMartEnding().isMet(player));
    }

    @Test
    public void testMcRonaldsEndingAchieved() {
        player.setCurrentRoom(new Room(10, "McRonalds", ""));
        player.pickUpItem(new RegularItem(12, "resume", "", 0.1, 0));
        assertEquals("McRonalds", player.getCurrentRoom().getName());
        assertTrue(player.checkInventory("resume"));
        assertTrue(new McRonaldsEnding().isMet(player));
    }

    @Test
    public void testMazonCEOEndingAchieved() {
        player.setCurrentRoom(new Room(6, "Mazon", ""));
        player.pickUpItem(new RegularItem(12, "resume", "", 0.1, 0));
        player.pickUpItem(new RegularItem(13, "suit", "", 1.5, 20));
        player.pickUpItem(new RegularItem(15, "Degree", "", 0.2, 0));
        player.unlockAchievement("Mazon CEO Ending", "Had the Mazon CEO interview");
        assertEquals("Mazon", player.getCurrentRoom().getName());
        assertTrue(player.checkInventory("resume"));
        assertTrue(player.checkInventory("suit"));
        assertTrue(player.checkInventory("Degree"));
        assertTrue(player.hasAchievement("Mazon CEO Ending"));
        assertTrue(new MazonCEOEnding().isMet(player));
    }

}
