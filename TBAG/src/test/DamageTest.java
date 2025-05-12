package test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import edu.ycp.cs320.tbag.events.Damage;
import edu.ycp.cs320.tbag.model.Player;
public class DamageTest {
   private Damage damageEvent;
   private Player player;
   @Before
   public void setUp() {
       player = new Player();
       player.setHealth(100); // Set initial health
       damageEvent = new Damage(0.5, "You take damage!", 20);
   }
   @Test
   public void testDamageInitialization() {
       assertEquals("Probability should be set correctly", 0.5, damageEvent.getProbability(), 0.001);
       assertEquals("Dialogue should be set correctly", "You take damage!", damageEvent.trigger(player));
       assertEquals("Damage value should be set correctly", 20, damageEvent.getDamage());
   }
   @Test
   public void testTriggerDamageEffect() {
       damageEvent.trigger(player);
       assertEquals("Player health should be reduced by damage amount", 80, player.getHealth());
   }
   @Test
   public void testTriggerReturnsCorrectDialogue() {
       String result = damageEvent.trigger(player);
       assertEquals("Dialogue should match expected output", "You take damage!", result);
   }
}
