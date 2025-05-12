package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.tbag.model.Consumables;

public class ConsumablesTest {
    private Consumables potion;

    @Before
    public void setUp() {
        // Creating a test consumable item
        potion = new Consumables(1, "Health Potion", "Restores health", 0.5, 25.0, 50, true);
    }

    @Test
    public void testConsumableInitialization() {
        assertEquals("ID should be set correctly", 1, potion.getId());
        assertEquals("Name should be set correctly", "Health Potion", potion.getName());
        assertEquals("Description should be set correctly", "Restores health", potion.getDescription());
        assertEquals("Weight should be set correctly", 0.5, potion.getWeight(), 0.001);
        assertEquals("Value should be set correctly", 25.0, potion.getValue(), 0.001);
        assertEquals("Effect should be set correctly", 50, potion.getEffect());
        assertTrue("Potion should be one-time use", potion.isOneTimeUse());
    }

    @Test
    public void testSetEffect() {
        potion.setEffect(100);
        assertEquals("Effect should update correctly", 100, potion.getEffect());
    }

    @Test
    public void testOneTimeUseSetter() {
        potion.setOneTimeUse(false);
        assertFalse("Item should now be reusable", potion.isOneTimeUse());
    }

    @Test
    public void testGetType() {
        assertEquals("Item type should be 'consumable'", "consumable", potion.getType());
    }
}
