package test;

import static org.junit.Assert.*;

import edu.ycp.cs320.tbag.events.Event;

import org.junit.Before;
import org.junit.Test;
import edu.ycp.cs320.tbag.model.Player;

public class EventTest {
    private Event mockEvent;
    private Player player;

    // Create a mock implementation of Event for testing
    private class MockEvent extends Event {
        public MockEvent(double probability) {
            super(probability);
        }

        @Override
        public String trigger(Player player) {
            return "Mock event triggered!";
        }
    }

    @Before
    public void setUp() {
        mockEvent = new MockEvent(0.5); // 50% probability
        player = new Player();
    }

    @Test
    public void testEventInitialization() {
        assertEquals("Probability should be set correctly", 0.5, mockEvent.getProbability(), 0.001);
        assertEquals("Default roomId should be 0", 0, mockEvent.getRoomId());
    }

    @Test
    public void testSetRoomId() {
        mockEvent.setRoomId(10);
        assertEquals("Room ID should update correctly", 10, mockEvent.getRoomId());
    }

    @Test
    public void testTriggerMethod() {
        String result = mockEvent.trigger(player);
        assertEquals("Trigger method should return expected output", "Mock event triggered!", result);
    }
}
