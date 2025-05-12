package edu.ycp.cs320.tbag.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an inventory that holds items for the player.
 * It maintains a list of items and enforces a maximum weight capacity.
 */
public class Inventory {
    private List<Items> items;
    private double maxWeight; // Maximum total weight allowed

    /**
     * Creates an inventory with a default maximum weight (e.g., 50.0).
     */
    public Inventory() {
        this.items = new ArrayList<>();
        this.maxWeight = 50.0; // default maximum weight (adjust as needed)
    }

    /**
     * Creates an inventory with a specified maximum weight.
     *
     * @param maxWeight the maximum weight the inventory can hold
     */
    public Inventory(double maxWeight) {
        this.items = new ArrayList<>();
        this.maxWeight = maxWeight;
    }

    /**
     * Checks if adding the specified item would exceed the maximum weight.
     *
     * @param item the item to check
     * @return true if the item can be added; false otherwise
     */
    public boolean canAddItem(Items item) {
        return (getTotalWeight() + item.getWeight()) <= maxWeight;
    }

    /**
     * Adds an item to the inventory if it doesn't exceed the max weight.
     *
     * @param item the item to add
     * @return true if the item was added; false if it would exceed the max weight
     */
    public boolean addItem(Items item) {
        if (canAddItem(item)) {
            items.add(item);
            return true;
        }
        return false;
    }

    /**
     * Removes the specified item from the inventory.
     *
     * @param item the item to remove
     * @return true if the item was removed; false if not found
     */
    public boolean removeItem(Items item) {
        return items.remove(item);
    }


    public Items removeItemByName(String name) {
        for (Items item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                items.remove(item);
                return item;
            }
        }
        return null;
    }
    
    public Items getItemByName(String name) {
        for (Items item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }
    


    /**
     * Calculates the total weight of all items in the inventory.
     *
     * @return the total weight
     */
    public double getTotalWeight() {
        double total = 0.0;
        for (Items item : items) {
            total += item.getWeight();
        }
        return total;
    }

    /**
     * Returns the list of items in the inventory.
     *
     * @return an unmodifiable list of items
     */
    public List<Items> getItems() {
        return items;
    }

    /**
     * Returns the maximum weight capacity of the inventory.
     *
     * @return the maximum weight
     */
    public double getMaxWeight() {
        return maxWeight;
    }

    /**
     * Sets a new maximum weight capacity for the inventory.
     *
     * @param maxWeight the new maximum weight
     */
    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    /**
     * Provides a formatted string of the inventory's contents and current weight.
     * Example:
     *   "Inventory (Total Weight: 5.0 / 50.0):
     *    - Fast Food Uniform (A neat uniform...) - Weight: 0.5, Value: 10.0
     *    - Resume (A carefully prepared resume...) - Weight: 0.1, Value: 0.0"
     *
     * @return a string representation of the inventory
     */
    public String getInventoryString() {
        if (items.isEmpty()) {
            return "Inventory is empty.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Inventory (Total Weight: ")
          .append(getTotalWeight())
          .append(" / ")
          .append(maxWeight)
          .append("):\n");
        for (Items item : items) {
            sb.append("- ")
              .append(item.toString())
              .append("\n");
        }
        return sb.toString();
    }
}
