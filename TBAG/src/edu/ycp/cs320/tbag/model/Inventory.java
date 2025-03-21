package edu.ycp.cs320.tbag.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a collection of Items in the game.
 * The Inventory can be used by the Player or for a Room's item collection.
 */
public class Inventory {
    private List<Item> items;
    
    /**
     * Constructs an empty Inventory.
     */
    public Inventory() {
        items = new ArrayList<>();
    }
    
    /**
     * Adds an item to the inventory.
     *
     * @param item the Item to add
     */
    public void addItem(Item item) {
        items.add(item);
    }
    
    /**
     * Removes an item from the inventory.
     *
     * @param item the Item to remove
     * @return true if the item was present and removed; false otherwise
     */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }
    
    /**
     * Returns a list of all items in the inventory.
     *
     * @return the list of items
     */
    public List<Item> getItems() {
        return items;
    }
    
    /**
     * Checks whether the inventory contains a specific item.
     *
     * @param item the Item to check for
     * @return true if the inventory contains the item, false otherwise
     */
    public boolean containsItem(Item item) {
        return items.contains(item);
    }
    
    /**
     * Returns the number of items in the inventory.
     *
     * @return the item count
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Searches for an item in the inventory by its id.
     *
     * @param id the unique identifier of the item
     * @return the matching Item if found, otherwise null
     */
    public Item getItemById(int id) {
        for (Item item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Searches for an item in the inventory by its name.
     *
     * @param name the name of the item
     * @return the matching Item if found, otherwise null
     */
    public Item getItemByName(String name) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }
}