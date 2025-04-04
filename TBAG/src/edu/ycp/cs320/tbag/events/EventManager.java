package edu.ycp.cs320.tbag.events;

import java.util.ArrayList;
import java.util.Collections;
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
    	
        Collections.shuffle(events);

        for (Event event : events) {
            if (Math.random() < event.getProbability()) {
            	String result = event.trigger(player);
                eventMessages.append(result).append("\n");
                break;
            }
        }
        
        return eventMessages.toString();
    }
}

