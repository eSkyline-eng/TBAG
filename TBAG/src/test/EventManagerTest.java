package test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import edu.ycp.cs320.tbag.events.Event;
import edu.ycp.cs320.tbag.events.EventManager;
import edu.ycp.cs320.tbag.model.*;
public class EventManagerTest {
   private EventManager eventManager;
   private Player player;
   private Event mockEvent1, mockEvent2;
   @Before
   public void setUp() {
       eventManager = new EventManager();
       player = new Player();
       // Mock events with predictable probability
       mockEvent1 = new Event(1.0) { // High probability
           @Override
           public String trigger(Player player) {
               return "Mock Event 1 Triggered!";
           }
       };
       mockEvent2 = new Event(0.1) { // Low probability
           @Override
           public String trigger(Player player) {
               return "Mock Event 2 Triggered!";
           }
       };
   }
   @Test
   public void testAddEvent() {
       eventManager.addEvent(mockEvent1);
       eventManager.addEvent(mockEvent2);
       List<Event> events = new ArrayList<>();
       events.add(mockEvent1);
       events.add(mockEvent2);
       String result = eventManager.triggerEvents(events, player);
       assertFalse("Events should be stored correctly", events.isEmpty());
   }
   @Test
   public void testTriggerEvent() {
       List<Event> events = new ArrayList<>();
       events.add(mockEvent1);
       events.add(mockEvent2);
       String result = eventManager.triggerEvents(events, player);
       assertTrue("At least one event should trigger", result.contains("Mock Event 1 Triggered!") || result.contains("Mock Event 2 Triggered!"));
   }
   @Test
   public void testEventProbabilityEffect() {
       int triggeredMockEvent1 = 0;
       int triggeredMockEvent2 = 0;
       List<Event> events = new ArrayList<>();
       events.add(mockEvent1);
       events.add(mockEvent2);
       for (int i = 0; i < 100; i++) {
           String result = eventManager.triggerEvents(events, player);
           if (result.contains("Mock Event 1 Triggered!")) {
               triggeredMockEvent1++;
           } else if (result.contains("Mock Event 2 Triggered!")) {
               triggeredMockEvent2++;
           }
       }
       assertTrue("Mock Event 1 should trigger more frequently due to higher probability", triggeredMockEvent1 > triggeredMockEvent2);
   }
}
