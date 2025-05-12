package test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import edu.ycp.cs320.tbag.events.Dialogue;
import edu.ycp.cs320.tbag.model.Player;
public class DialogueTest {
   private Dialogue dialogueEvent;
   private Player player;
   @Before
   public void setUp() {
       player = new Player();
       dialogueEvent = new Dialogue(0.75, "Welcome to the world!");
   }
   @Test
   public void testDialogueInitialization() {
       assertEquals("Probability should be set correctly", 0.75, dialogueEvent.getProbability(), 0.001);
       assertEquals("Dialogue should be set correctly", "Welcome to the world!", dialogueEvent.trigger(player));
   }
   @Test
   public void testTriggerReturnsCorrectDialogue() {
       String result = dialogueEvent.trigger(player);
       assertEquals("Dialogue should match expected output", "Welcome to the world!", result);
   }
}
