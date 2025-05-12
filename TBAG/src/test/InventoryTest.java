package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.tbag.model.Consumables;
import edu.ycp.cs320.tbag.model.Inventory;
import edu.ycp.cs320.tbag.model.Items;
import edu.ycp.cs320.tbag.model.Weapons;

public class InventoryTest {
    private Inventory inventory;
    private Items item1, item2;

    @Before
    public void setUp() {
        inventory = new Inventory(10.0); // Max weight limit of 10.0
        item1 = new Consumables(1, "Health Potion", "Restores health", 0.5, 25.0, 50, true);
        item2 = new Weapons(2, "Sword", "A sharp blade", 3.0, 50.0, 2.0);
    }

    @Test
    public void testInventoryInitialization() {
        assertNotNull("Inventory should not be null", inventory.getItems());
        assertEquals("Max weight should be set correctly", 10.0, inventory.getMaxWeight(), 0.001);
        assertEquals("Initial weight should be 0", 0.0, inventory.getTotalWeight(), 0.001);
    }

    @Test
    public void testAddItem() {
        assertTrue("Item should be added successfully", inventory.addItem(item1));
        assertEquals("Total weight should update", 0.5, inventory.getTotalWeight(), 0.001);
    }

    @Test
    public void testRemoveItem() {
        inventory.addItem(item1);
        assertTrue("Item should be removed successfully", inventory.removeItem(item1));
        assertEquals("Total weight should update correctly", 0.0, inventory.getTotalWeight(), 0.001);
    }

    @Test
    public void testRemoveItemByName() {
        inventory.addItem(item1);
        assertNotNull("Item should be removed by name", inventory.removeItemByName("Health Potion"));
        assertEquals("Inventory should be empty after removal", 0.0, inventory.getTotalWeight(), 0.001);
    }

    @Test
    public void testGetItemByName() {
        inventory.addItem(item2);
        assertEquals("Should retrieve correct item by name", item2, inventory.getItemByName("Sword"));
    }

    @Test
    public void testGetTotalWeight() {
        inventory.addItem(item1);
        inventory.addItem(item2);
        assertEquals("Total weight should be correct", 3.5, inventory.getTotalWeight(), 0.001);
    }

    @Test
    public void testGetInventoryString() {
        inventory.addItem(item1);
        inventory.addItem(item2);
        String expected = "Inventory (Total Weight: 9.0 / 10.0):\n- Sword (A sharp blade)\n- Health Potion (Restores health)\n";        assertTrue("Inventory string should contain correct items", inventory.getInventoryString().contains("Sword"));
        assertTrue("Inventory string should contain correct items", inventory.getInventoryString().contains("Sword"));
    }
}
