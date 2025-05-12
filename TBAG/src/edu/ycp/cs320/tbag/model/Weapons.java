package edu.ycp.cs320.tbag.model;

/**
 * Represents an item in the TBAG game.
 * weapons have a Id, name, description, weight, value, and damageMultiplier.
 */
public class Weapons extends Items {
	private double damageMultiplier;
    
    /**
     * Constructs a new Item with the specified attributes.
     */
    public Weapons(int weaponId, String name, String description, double weight, double value, double damageMultiplier) {
    	super(weaponId, name, description, weight, value);
    	this.damageMultiplier = damageMultiplier;
    }
    
    // Getters and setters 
    public double getDamageMultiplier() {
    	return damageMultiplier;
    }
    
    public void setDamage(double damageMultiplier) {
    	this.damageMultiplier = damageMultiplier;
    }
    
    
    @Override
    public String getType() {
        return "weapon";
    }
}
