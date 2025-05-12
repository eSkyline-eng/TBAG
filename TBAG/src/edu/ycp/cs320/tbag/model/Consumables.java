package edu.ycp.cs320.tbag.model;

/**
 * Represents an item in the TBAG game.
 * weapons have a Id, name, description, weight, value, and damageMultiplier.
 */
public class Consumables extends Items {
	private int effect;  // how much health it restores
    private boolean isOneTimeUse;

    public Consumables(int consumableId, String name, String description, double weight, double value, int effect, boolean isOneTimeUse) {
        super(consumableId, name, description, weight, value);
        this.effect = effect;
        this.isOneTimeUse = isOneTimeUse;
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public boolean isOneTimeUse() {
        return isOneTimeUse;
    }

    public void setOneTimeUse(boolean isOneTimeUse) {
        this.isOneTimeUse = isOneTimeUse;
    }

    @Override
    public String getType() {
        return "consumable";
    }
}
