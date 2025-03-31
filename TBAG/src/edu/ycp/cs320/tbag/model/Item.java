package edu.ycp.cs320.tbag.model;

/**
 * Represents an item in the TBAG game.
 * Items have an id, name, description, weight, and value.
 */
public class Item {
    private int id;
    private String name;
    private String description;
    private double weight;
    private double value;
    
    /**
     * Constructs a new Item with the specified attributes.
     *
     * @param id the unique identifier for the item
     * @param name the name of the item
     * @param description a brief description of the item
     * @param weight the weight of the item (in arbitrary units)
     * @param value the monetary or game value of the item
     */
    public Item(int id, String name, String description, double weight, double value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.value = value;
    }
    
    // Getters and setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    /**
     * Returns a string representation of the item.
     *
     * @return a string containing the item's name, description, weight, and value
     */
    @Override
    public String toString() {
        return name + " (" + description + ") - Weight: " + weight + ", Value: " + value;
    }
}
