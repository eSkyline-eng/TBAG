package edu.ycp.cs320.tbag.events;

import java.util.ArrayList;
import java.util.List;
import edu.ycp.cs320.tbag.model.*;

public class EventManager {
	private List<Event> events;
    
    public EventManager() {
        events = new ArrayList<>();
    }
    
    // Add an event to the manager
    public void addEvent(Event event) {
        events.add(event);
    }
    
    // Trigger events based on their probability for the given list of events.
    public String triggerEvents(List<Event> events, Player player) {
    	StringBuilder eventMessages = new StringBuilder();
        for (Event event : events) {
            if (Math.random() < event.getProbability()) {
                event.trigger(player);
                eventMessages.append(event.trigger(player)).append("\n");
                // Optionally, break if you want only one event per room entry.
                break;
            }
        }
        
        return eventMessages.toString();
    }
}

