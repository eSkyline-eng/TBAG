package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import edu.ycp.cs320.tbag.model.Item;
import edu.ycp.cs320.tbag.model.NPC;

public class NPCTest {
    private Item item;
    private NPC npc;

    @BeforeEach
    public void setUp() {
        item = new Item(101, "Magic Key", "Unlocks hidden doors", 0.5, 50.0);
        npc = new NPC("Frank", "Welcome to the city, traveler!",1);
    }

    // --- Item Tests ---

    @Test
    public void testItemProperties() {
        assertEquals(101, item.getId());
        assertEquals("Magic Key", item.getName());
        assertEquals("Unlocks hidden doors", item.getDescription());
        assertEquals(0.5, item.getWeight());
        assertEquals(50.0, item.getValue());
    }

    @Test
    public void testItemSetters() {
        item.setName("Golden Key");
        item.setDescription("Shiny and rare");
        item.setWeight(1.2);
        item.setValue(75.0);

        assertEquals("Golden Key", item.getName());
        assertEquals("Shiny and rare", item.getDescription());
        assertEquals(1.2, item.getWeight());
        assertEquals(75.0, item.getValue());
    }

    @Test
    public void testItemToString() {
        String output = item.toString();
        assertTrue(output.contains("Magic Key"));
        assertTrue(output.contains("Unlocks hidden doors"));
    }

    // --- NPC Tests ---

    @Test
    public void testNPCTalk() {
        assertEquals("Welcome to the city, traveler!", npc.talk());
    }

    @Test
    public void testNPCGettersAndSetters() {
        npc.setName("Joe");
        npc.setDialogue("Got any spare change?");

        assertEquals("Joe", npc.getName());
        assertEquals("Got any spare change?", npc.getDialogue());
    }

    @Test
    public void testNPCInventoryEmptyOnCreation() {
        assertTrue(npc.getInventory().getItems().isEmpty());
    }

    @Test
    public void testNPCInventoryAddItem() {
        Item mug = new Item(33, "Coffee Mug", "Stained with regret", 0.3, 2.0);
        assertTrue(npc.getInventory().addItem(mug));
        assertTrue(npc.getInventory().getItems().contains(mug));
    }
}
