package edu.ycp.cs320.tbag.model;

public class ItemLocation {
    private int instanceId;
    private int itemId;
    private String locationType;
    private int locationId;
    private int quantity;

    public ItemLocation(int instanceId, int itemId, String locationType, int locationId) {
        this.instanceId = instanceId;
        this.itemId = itemId;
        this.locationType = locationType;
        this.locationId = locationId;
        this.quantity = 1;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getLocationType() {
        return locationType;
    }

    public int getLocationId() {
        return locationId;
    }
    
    public int getQuantity() { 
    	return quantity; 
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
}
