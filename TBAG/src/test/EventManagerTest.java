package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import edu.ycp.cs320.tbag.events.*;
import edu.ycp.cs320.tbag.model.*;

import java.util.ArrayList;
import java.util.List;

public class EventManagerTest {
    private Player player;
    private EventManager eventManager;
    private List<Event> events;

    @BeforeEach
    public void setup() {
        player = new Player();
        eventManager = new EventManager();
        events = new ArrayList<>();
    }

    @Test
    public void testDialogueEventAlwaysTriggers() {
        events.add(new Dialogue(1.0, "Mom says hi!"));
        String result = eventManager.triggerEvents(events, player);
        assertTrue(result.contains("Mom says hi!"));
        assertEquals(100, player.getHealth());
    }

    @Test
    public void testDamageEventAlwaysTriggers() {
        events.add(new Damage(1.0, "A hobo attacks!", 10));
        String result = eventManager.triggerEvents(events, player);
        assertTrue(result.contains("A hobo attacks!"));
        assertEquals(90, player.getHealth());
    }

    @Test
    public void testDamageEventDoesNotTrigger() {
        events.add(new Damage(0.0, "Invisible banana peel!", 10));
        String result = eventManager.triggerEvents(events, player);
        assertTrue(result.trim().isEmpty());
        assertEquals(100, player.getHealth());
    }

    @Test
    public void testOnlyOneEventTriggers() {
        events.add(new Dialogue(1.0, "Mom yells at you!"));
        events.add(new Damage(1.0, "You stub your toe!", 5));
        events.add(new Dialogue(1.0, "Bird poops on you!"));

        String result = eventManager.triggerEvents(events, player);
        int triggerCount = 0;
        if (result.contains("Mom yells at you!")) triggerCount++;
        if (result.contains("You stub your toe!")) triggerCount++;
        if (result.contains("Bird poops on you!")) triggerCount++;
        assertEquals(1, triggerCount);
    }

    @Test
    public void testMultipleDamageEventsOnlyOneTriggers() {
        events.add(new Damage(1.0, "First hit!", 10));
        events.add(new Damage(1.0, "Second hit!", 10));
        events.add(new Damage(1.0, "Third hit!", 10));

        String result = eventManager.triggerEvents(events, player);
        assertTrue(result.contains("hit!"));
        assertEquals(90, player.getHealth());
    }

    @Test
    public void testTriggerEventsRandomizesOrder() {
        events.add(new Dialogue(1.0, "First"));
        events.add(new Dialogue(1.0, "Second"));
        events.add(new Dialogue(1.0, "Third"));

        boolean seenFirst = false, seenSecond = false, seenThird = false;
        for (int i = 0; i < 20; i++) {
            String result = eventManager.triggerEvents(events, player);
            if (result.contains("First")) seenFirst = true;
            if (result.contains("Second")) seenSecond = true;
            if (result.contains("Third")) seenThird = true;
        }
        assertTrue(seenFirst && seenSecond && seenThird);
    }

    // ---- Extra Tests for Coverage ----


    @Test
    public void testSetAndGetHealth() {
        player.setHealth(80);
        assertEquals(80, player.getHealth());
    }

    @Test
    public void testInventoryAddAndRemove() {
        Item pen = new Item(1, "Pen", "Blue pen", 0.1, 1.0);
        assertTrue(player.getInventory().addItem(pen));
        assertTrue(player.getInventory().removeItem(pen));
    }

    @Test
    public void testRoomAddAndGetEvent() {
        Room room = new Room(1, "Room", "Just a room");
        Event e = new Dialogue(1.0, "hello");
        room.addEvent(e);
        assertTrue(room.getEvents().contains(e));
    }

    @Test
    public void testRoomConnections() {
        Room r1 = new Room(1, "R1", "Room 1");
        Room r2 = new Room(2, "R2", "Room 2");
        r1.addConnection("north", r2);
        assertEquals(r2, r1.getConnection("north"));
    }

    @Test
    public void testNPCInteraction() {
        NPC npc = new NPC("Joe", "Hello there!");
        assertEquals("Hello there!", npc.talk());
        assertEquals("Joe", npc.getName());
    }

    @Test
    public void testItemToString() {
        Item item = new Item(1, "Key", "Shiny", 0.2, 5);
        assertTrue(item.toString().contains("Key"));
    }
}
