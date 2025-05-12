package edu.ycp.cs320.tbag.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.ycp.cs320.tbag.db.persist.DatabaseProvider;
import edu.ycp.cs320.tbag.db.persist.IDatabase;
import edu.ycp.cs320.tbag.ending.LotteryEnding;

/**
 * Manages the in-game shop: listing items, buying, and selling by name.
 */
public class ShopManager {
    private Map<String, ShopItem> items = new LinkedHashMap<>();
    private ShopItem pendingSaleItem;
    private int pendingSalePrice;
    private boolean pendingSaleIsShopItem;
    // track the instance id of a general inventory item pending sale
    private Integer pendingSaleInstanceId;

    /**
     * No-arg constructor sets up the gas station's fixed inventory.
     */
    public ShopManager() {
        // Hard-coded gas station shop items
        items.put("lottery ticket", new ShopItem(
            10,
            "Lottery Ticket",
            "10% chance to win $100,000",
            "lottery",
            10
        ));
        items.put("health pack", new ShopItem(
            15,
            "Health Pack",
            "+20 HP",
            "heal",
            20
        ));
        items.put("adrenaline shot", new ShopItem(
            20,
            "Adrenaline Shot",
            "+50% combat boost next attack",
            "boost",
            50
        ));
    }

    /**
     * List all available shop items.
     */
    public List<ShopItem> listItems() {
        return new ArrayList<>(items.values());
    }

    /**
     * Attempt to buy an item by its name (ignoring case). Applies effects immediately and persists changes.
     * @return true if processed, false if no matching item.
     */
    public boolean initiateBuy(String itemName, Player player, StringBuilder out) {
        ShopItem item = items.get(itemName.toLowerCase());
        if (item == null) {
            out.append("That item doesn't exist.\n");
            return false;
        }
        IDatabase db = DatabaseProvider.getInstance();
        int price = item.getPrice();
        if (player.getMoney() < price) {
            out.append("Insufficient funds to buy ").append(item.getName()).append(".\n");
            return true;
        }
        player.deductMoney(price);
        db.updatePlayerMoney(player.getId(), player.getMoney());

        switch (item.getType()) {
            case "lottery": {
                if (new Random().nextInt(100) < item.getEffectValue()) {
                    out.append("Congratulations! You won the lottery!\n");
                    player.unlockAchievement("lottery_win", new LotteryEnding().getEndingDescription());
                    String code = new LotteryEnding().apply(player);
                    out.setLength(0);        // clear any leftover text
                    out.append(code).append("\n");
                } else {
                    out.append("Sorry, you didn't win the lottery.\n");
                }
                break;
            }
            case "heal": {
                player.addHealth(item.getEffectValue());
                db.updatePlayerHealth(player.getId(), player.getHealth());
                out.append("You gained ")
                   .append(item.getEffectValue())
                   .append(" HP. New health: ")
                   .append(player.getHealth()).append(".\n");
                break;
            }
            case "boost": {
                // 1) apply to the Player object's multiplier
                player.applyAttackBoost(item.getEffectValue());

                // 2) persist into the DB so it survives across requests
                db.updatePlayerAttackMultiplier(player.getId(), player.getAttackMultiplier());

                out.append("Your attacks are boosted by +")
                   .append(item.getEffectValue())
                   .append("% for the next battle.\n");
                break;
            }
            default:
                break;
        }
        return true;
    }

    /**
     * Attempt to initiate selling an item by its name.
     * @return true if processed.
     */
    public boolean initiateSell(String itemName, Player player, StringBuilder out) {
        // never re-prompt if one’s already pending
        if (pendingSaleItem != null) {
            return false;
        }

        ShopItem shopItem = items.get(itemName.toLowerCase());
        int halfPrice;
        String saleName;
        IDatabase db = DatabaseProvider.getInstance();

        if (shopItem != null) {
            // selling a shop item
            halfPrice = shopItem.getPrice() / 2;
            saleName  = shopItem.getName();
            pendingSaleIsShopItem = true;

        } else if (player.hasItem(itemName)) {
            // selling a general inventory item
            // fetch the Item instance without removing yet
            Items inv = player.getInventory().getItems().stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst().orElse(null);

            if (inv == null) {
                out.append("You don't have a ").append(itemName).append(" to sell.\n");
                return true;
            }

            halfPrice = (int)(inv.getValue() / 2.0);
            if (halfPrice <= 0) {
                out.append("Item cannot be sold because it has 0 value.\n");
                return true;
            }

            saleName = inv.getName();
            // capture the instance ID for persistence
            List<ItemLocation> playerItems = db.getItemsAtLocation("player", player.getId());
            for (ItemLocation loc : playerItems) {
                if (loc.getItemId() == inv.getId()) {
                    pendingSaleInstanceId = loc.getInstanceId();
                    break;
                }
            }

            // wrap it so downstream confirmSale can treat it uniformly
            shopItem = new ShopItem("inv-" + saleName.toLowerCase(), saleName, "", "general", halfPrice, 0);
            pendingSaleIsShopItem = false;

        } else {
            // neither a shop item nor in inventory
            out.append("That item cannot be sold.\n");
            return false;
        }

        // set up the two-step confirmation
        pendingSaleItem  = shopItem;
        pendingSalePrice = halfPrice;

        out.append("Do you want to sell ")
           .append(saleName)
           .append(" for $")
           .append(halfPrice)
           .append("? Yes or no.\n");

        return true;
    }

    /**
     * Confirm or cancel the pending sale based on player input.
     * @return true if processed.
     */
    public boolean confirmSale(String response, Player player, StringBuilder out) {
        // Nothing to confirm if no sale is pending
        if (pendingSaleItem == null) {
            return false;
        }

        String resp     = response.trim().toLowerCase();
        String saleName = pendingSaleItem.getName();
        IDatabase db    = DatabaseProvider.getInstance();

        if (resp.equals("yes")) {
            // persist transfer for general inventory items
            if (!pendingSaleIsShopItem && pendingSaleInstanceId != null) {
                db.transferItem(
                    pendingSaleInstanceId,
                    "player", player.getId(),
                    "none", 0
                );
            }

            // Remove the item from the player's inventory
            boolean removed = player.removeItem(saleName);
            if (!removed) {
                out.append("Could not remove ").append(saleName).append(" from your inventory.\n");
            }

            // Credit half-price to the player
            player.addMoney(pendingSalePrice);
            db.updatePlayerMoney(player.getId(), player.getMoney());

            out.append("You sold ")
               .append(saleName)
               .append(" for $")
               .append(pendingSalePrice)
               .append(". New balance: $")
               .append(player.getMoney())
               .append(".\n");

        } else {
            out.append("Sale cancelled.\n");
        }

        // Clear pending state
        pendingSaleItem        = null;
        pendingSalePrice       = 0;
        pendingSaleIsShopItem  = false;
        pendingSaleInstanceId  = null;

        return true;
    }

    /**
     * Alias for GameEngine: confirmSell
     */
    public boolean confirmSell(String response, Player player, StringBuilder out) {
        return confirmSale(response, player, out);
    }

    /**
     * Alias for GameEngine: handlePendingSale
     */
    public boolean handlePendingSale(String response, Player player, StringBuilder out) {
        return confirmSale(response, player, out);
    }

    public boolean hasPendingSale() {
        return (pendingSaleItem != null);
    }
}
