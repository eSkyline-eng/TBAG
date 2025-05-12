package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import edu.ycp.cs320.tbag.model.*;

public class PlayerRoomInventoryTest {
    private Player player;
    private Room room;
    private RegularItem item;
    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        player = new Player();
        room = new Room(1, "Test Room", "A room for testing.");
        item = new RegularItem(1, "Test Item", "A useful test item", 1.0, 10.0);
        inventory = new Inventory();
    }

    // Inventory Tests
    @Test
    public void testAddItemToInventory() {
        assertTrue(inventory.addItem(item));
        assertTrue(inventory.getItems().contains(item));
    }

    @Test
    public void testRemoveItemFromInventory() {
        inventory.addItem(item);
        assertTrue(inventory.removeItem(item));
        assertFalse(inventory.getItems().contains(item));
    }

    @Test
    public void testRemoveItemByName() {
        inventory.addItem(item);
        assertEquals(item, inventory.removeItemByName("Test Item"));
    }

    @Test
    public void testInventoryWeightLimit() {
        Inventory limitedInventory = new Inventory(0.5);
        assertFalse(limitedInventory.addItem(item));
    }

    @Test
    public void testInventoryGetInventoryString() {
        inventory.addItem(item);
        String result = inventory.getInventoryString();
        assertTrue(result.contains("Test Item"));
    }

    @Test
    public void testInventoryOverMaxWeight() {
        Inventory tightInventory = new Inventory(1.0);
        RegularItem heavyItem = new RegularItem(2, "Heavy", "Very heavy", 1.5, 10.0);
        assertFalse(tightInventory.addItem(heavyItem));
    }

    @Test
    public void testInventorySetMaxWeight() {
        inventory.setMaxWeight(100.0);
        assertEquals(100.0, inventory.getMaxWeight());
    }

    @Test
    public void testInventoryTotalWeightCalculation() {
        RegularItem second = new RegularItem(2, "Second", "Second item", 2.0, 5.0);
        inventory.addItem(item);
        inventory.addItem(second);
        assertEquals(3.0, inventory.getTotalWeight());
    }

    // Room Tests
    @Test
    public void testRoomItemAddAndRemove() {
        room.addItem(item);
        assertTrue(room.getItems().contains(item));
        room.removeItem(item);
        assertFalse(room.getItems().contains(item));
    }

    @Test
    public void testRoomRemoveItemByName() {
        room.addItem(item);
        assertEquals(item, room.removeItemByName("Test Item"));
    }

    @Test
    public void testRoomItemDescriptionString() {
        room.addItem(item);
        String result = room.getRoomItemsString();
        assertTrue(result.contains("Test Item"));
    }

    @Test
    public void testRoomNoItemsMessage() {
        String result = room.getRoomItemsString();
        assertTrue(result.toLowerCase().contains("no items"));
    }

    @Test
    public void testRoomAvailableDirectionsNone() {
        assertEquals("None", room.getAvailableDirections());
    }

    @Test
    public void testRoomAvailableDirectionsList() {
        Room northRoom = new Room(2, "North", "North room");
        room.addConnection("north", northRoom);
        assertEquals("north", room.getAvailableDirections());
    }

    @Test
    public void testRoomMultipleConnections() {
        Room r1 = new Room(2, "North", "North");
        Room r2 = new Room(3, "South", "South");
        room.addConnection("north", r1);
        room.addConnection("south", r2);
        String result = room.getAvailableDirections();
        assertTrue(result.contains("north") && result.contains("south"));
    }

    // Player Tests
    @Test
    public void testPlayerHealthSetGet() {
        player.setHealth(85);
        assertEquals(85, player.getHealth());
    }

    @Test
    public void testPlayerPickUpAndDropItem() {
        assertTrue(player.pickUpItem(item));
        assertTrue(player.getInventory().getItems().contains(item));
        assertTrue(player.dropItem(item));
        assertFalse(player.getInventory().getItems().contains(item));
    }

    @Test
    public void testPlayerRoomAssignment() {
        player.setCurrentRoom(room);
        assertEquals(room, player.getCurrentRoom());
    }

    @Test
    public void testPlayerInventoryStringMatches() {
        player.pickUpItem(item);
        String output = player.getInventoryString();
        assertTrue(output.contains("Test Item"));
    }

    @Test
    public void testPlayerCannotPickUpHeavyItem() {
        RegularItem big = new RegularItem(5, "Big Rock", "Too heavy", 999, 1);
        assertFalse(player.pickUpItem(big));
    }
}
