package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.tbag.model.ItemLocation;

public class ItemLocationTest {
    private ItemLocation itemLocation;

    @Before
    public void setUp() {
        itemLocation = new ItemLocation(1, 101, "room", 5);
    }

    @Test
    public void testInitialization() {
        assertEquals("Instance ID should be set correctly", 1, itemLocation.getInstanceId());
        assertEquals("Item ID should be set correctly", 101, itemLocation.getItemId());
        assertEquals("Location type should be set correctly", "room", itemLocation.getLocationType());
        assertEquals("Location ID should be set correctly", 5, itemLocation.getLocationId());
        assertEquals("Default quantity should be 1", 1, itemLocation.getQuantity());
    }

    @Test
    public void testSetLocationType() {
        itemLocation.setLocationType("inventory");
        assertEquals("Location type should update correctly", "inventory", itemLocation.getLocationType());
    }

    @Test
    public void testSetLocationId() {
        itemLocation.setLocationId(10);
        assertEquals("Location ID should update correctly", 10, itemLocation.getLocationId());
    }
}
