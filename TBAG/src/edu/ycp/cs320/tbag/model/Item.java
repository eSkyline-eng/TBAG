	package edu.ycp.cs320.tbag.model;

/**
 * Represents an item in the text-based adventure game.
 * Items can be picked up, dropped, or used by the player.
 */
public class Item {
    private int id;
    private String name;
    private String description;
    private double weight; // The weight of the item (e.g., for inventory limitations)
    private int value;     // The value or score associated with the item

    /**
     * Constructs a new Item with the specified details.
     *
     * @param id the unique identifier for the item
     * @param name the name of the item
     * @param description a description of the item
     * @param weight the weight of the item
     * @param value the value (or points/currency) associated with the item
     */
    public Item(int id, String name, String description, double weight, int value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.value = value;
    }

    /**
     * Gets the item's unique identifier.
     *
     * @return the id of the item
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the item's name.
     *
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the item's name.
     *
     * @param name the new name of the item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the item's description.
     *
     * @return the description of the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the item's description.
     *
     * @param description the new description of the item
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the weight of the item.
     *
     * @return the weight of the item
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the item.
     *
     * @param weight the new weight of the item
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Gets the value of the item.
     *
     * @return the value of the item
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the item.
     *
     * @param value the new value of the item
     */
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Item [id=" + id + ", name=" + name + ", description=" + description 
                + ", weight=" + weight + ", value=" + value + "]";
    }
}