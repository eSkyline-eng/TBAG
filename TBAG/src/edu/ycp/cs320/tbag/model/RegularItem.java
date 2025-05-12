package edu.ycp.cs320.tbag.model;

/**
 * Represents an item in the TBAG game.
 * Items have an id, name, description, weight, and value.
 */
public class RegularItem extends Items {
    public RegularItem(int id, String name, String description, double weight, double value) {
        super(id, name, description, weight, value);
    }
    
    @Override
    public String getType() {
        return "normal";
    }
}
