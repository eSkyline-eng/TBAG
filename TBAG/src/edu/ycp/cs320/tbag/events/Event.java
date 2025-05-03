package edu.ycp.cs320.tbag.events;

import edu.ycp.cs320.tbag.model.*;

//Abstract Event class
public abstract class Event {
	private double probability; // e.g., 0.3 means a 30% chance to trigger
	private int roomId;

	public Event(double probability) {
		this.probability = probability;
	}
 
	public double getProbability() {
		return probability;
	}
	
	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
 
	// Each event defines its own action on the player
	public abstract String trigger(Player player);
}

