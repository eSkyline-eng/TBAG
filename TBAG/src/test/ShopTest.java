package test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.ycp.cs320.tbag.model.Player;
import edu.ycp.cs320.tbag.model.RegularItem;
import edu.ycp.cs320.tbag.model.ShopItem;
import edu.ycp.cs320.tbag.model.ShopManager;
import edu.ycp.cs320.tbag.model.ShopItem;
import java.util.List;
public class ShopTest {
   private ShopManager shopManager;
   private Player player;
   private ShopItem shopItem;
   @BeforeEach
   public void setUp() {
       shopManager = new ShopManager();
       player = new Player();
       player.setMoney(50);
       player.setHealth(100);
       shopItem = new ShopItem("lottery", "Lottery Ticket", "10% chance to win $100,000", "lottery", 10, 10);
   }
   @Test
   public void testShopInitialization() {
       List<ShopItem> items = shopManager.listItems();
       assertEquals(3, items.size(), "Shop should contain exactly 3 items");
       assertTrue(items.stream().anyMatch(item -> item.getName().equalsIgnoreCase("lottery ticket")));
       assertTrue(items.stream().anyMatch(item -> item.getName().equalsIgnoreCase("health pack")));
       assertTrue(items.stream().anyMatch(item -> item.getName().equalsIgnoreCase("adrenaline shot")));
   }
   @Test
   public void testBuyHealthPack() {
       StringBuilder output = new StringBuilder();
       player.takeDamage(90);
       assertTrue(shopManager.initiateBuy("health pack", player, output));
       assertEquals(35, player.getMoney(), "Player's money should decrease after purchase");
       assertTrue(output.toString().contains("You gained"), "Buying health pack should affect player's health");
       assertEquals(30, player.getHealth(), "Health should increase after buying health pack");
   }
   @Test
   public void testBuyLotteryTicket() {
       StringBuilder output = new StringBuilder();
      
       shopManager.initiateBuy("lottery ticket", player, output);
      
       String result = output.toString().toLowerCase();
       assertTrue(result.contains("congratulations!") || result.contains("sorry, you didn't win"),
           "Lottery ticket should either trigger a win or a loss message.");
   }
   @Test
   public void testBuyItemWithoutEnoughMoney() {
       player.setMoney(5);
       StringBuilder output = new StringBuilder();
       assertTrue(shopManager.initiateBuy("health pack", player, output));
       assertTrue(output.toString().contains("Insufficient funds"), "Player should not be able to buy with insufficient money");
   }
   @Test
   public void testSellItem_PlayerOwnsItem() {
       player.getInventory().addItem(new RegularItem(1, "Rusty Key", "A key with no clear purpose", 0.1, 5));
       StringBuilder output = new StringBuilder();
       assertTrue(shopManager.initiateSell("Rusty Key", player, output));
       assertTrue(output.toString().contains("Do you want to sell Rusty Key"), "Sale prompt should appear");
   }
   @Test
   public void testConfirmSale_Success() {
       player.getInventory().addItem(new RegularItem(1, "Rusty Key", "A key with no clear purpose", 0.1, 10));
       StringBuilder output = new StringBuilder();
       shopManager.initiateSell("Rusty Key", player, output);
       assertTrue(shopManager.confirmSale("yes", player, output));
       assertFalse(player.hasItem("Rusty Key"), "Item should be removed after sale");
       assertEquals(55, player.getMoney(), "Player's balance should increase by half the item value");
   }
   @Test
   public void testConfirmSale_Cancel() {
       player.getInventory().addItem(new RegularItem(1, "Rusty Key", "A key with no clear purpose", 0.1, 10));
       StringBuilder output = new StringBuilder();
       shopManager.initiateSell("Rusty Key", player, output);
       assertTrue(shopManager.confirmSale("no", player, output));
       assertTrue(player.hasItem("Rusty Key"), "Item should remain after canceled sale");
       assertEquals(50, player.getMoney(), "Player's balance should remain unchanged after canceling sale");
   }
   @Test
   public void testHasPendingSale() {
       assertFalse(shopManager.hasPendingSale(), "No sale should be pending initially");
       player.getInventory().addItem(new RegularItem(1, "Rusty Key", "A key with no clear purpose", 0.1, 10));
       shopManager.initiateSell("Rusty Key", player, new StringBuilder());
       assertTrue(shopManager.hasPendingSale(), "Sale should be pending after initiating sale");
   }
  
   @Test
   public void testInitialization() {
       assertEquals("lottery", shopItem.getId(), "ID should be correctly initialized");
       assertEquals("Lottery Ticket", shopItem.getName(), "Name should be correctly initialized");
       assertEquals("10% chance to win $100,000", shopItem.getDescription(), "Description should be correctly initialized");
       assertEquals("lottery", shopItem.getType(), "Type should be correctly initialized");
       assertEquals(10, shopItem.getPrice(), "Price should be correctly initialized");
       assertEquals(10, shopItem.getEffectValue(), "Effect value should be correctly initialized");
   }
  
   @Test
   public void testSetters() {
       shopItem.setId("boost");
       shopItem.setName("Adrenaline Shot");
       shopItem.setDescription("Boosts combat power by 50%");
       shopItem.setType("boost");
       shopItem.setPrice(20);
       shopItem.setEffectValue(50);
       assertEquals("boost", shopItem.getId(), "ID should update correctly");
       assertEquals("Adrenaline Shot", shopItem.getName(), "Name should update correctly");
       assertEquals("Boosts combat power by 50%", shopItem.getDescription(), "Description should update correctly");
       assertEquals("boost", shopItem.getType(), "Type should update correctly");
       assertEquals(20, shopItem.getPrice(), "Price should update correctly");
       assertEquals(50, shopItem.getEffectValue(), "Effect value should update correctly");
   }
   @Test
   public void testToStringMethod() {
       String expected = "ShopItem[id=lottery, name=Lottery Ticket, price=10, type=lottery, effect=10]";
       assertEquals(expected, shopItem.toString(), "toString() output should match expected format");
   }
}
