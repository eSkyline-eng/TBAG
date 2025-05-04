package edu.ycp.cs320.tbag.model;

/**
 * Represents an item available in the shop.
 */
public class ShopItem {
    private String id;
    private String name;
    private String description;
    private String type;
    private int price;
    private int effectValue;

    /**
     * Default constructor for CSV loading or frameworks.
     */
    public ShopItem() {
    }

    /**
     * Full constructor.
     * 
     * @param id           Unique item identifier
     * @param name         Display name
     * @param description  Description for listing
     * @param type         Effect type: "lottery", "heal", "boost"
     * @param price        Cost in game currency
     * @param effectValue  Numeric value of the effect
     */
    public ShopItem(String id, String name, String description, String type, int price, int effectValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
        this.effectValue = effectValue;
    }
    
    /** Convenience ctor for non-ID CSV parsing */  
    public ShopItem(int price, String name, String description, String type, int effectValue) {  
        this.id          = name.toLowerCase();       // or however you want to generate IDs  
        this.name        = name;  
        this.description = description;  
        this.type        = type;  
        this.price       = price;  
        this.effectValue = effectValue;  
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getEffectValue() {
        return effectValue;
    }

    public void setEffectValue(int effectValue) {
        this.effectValue = effectValue;
    }

    @Override
    public String toString() {
        return String.format("ShopItem[id=%s, name=%s, price=%d, type=%s, effect=%d]", 
                              id, name, price, type, effectValue);
    }
}
