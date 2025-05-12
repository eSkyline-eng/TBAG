package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.tbag.model.Enemy;
import edu.ycp.cs320.tbag.model.Player;

public class EnemyTest {
    private Enemy enemy;

    @Before
    public void setUp() {
        enemy = new Enemy(1, "Rat", 7, 10, 2, 0.10, 0.75,
            "The rat attempts a mighty attack... it does almost nothing.",
            "The rat lunges with surprising speed, its tiny teeth sinking into your boot. You barely feel it.",
            "The rat lets out a pathetic squeak.");
    }
    
    @Test
    public void testInitialization() {
        assertEquals(1, enemy.getEnemyID());
        assertEquals("Rat", enemy.getEnemyName());
        assertEquals(10, enemy.getEnemyHealth());
        assertEquals(2, enemy.getEnemyAttack());
        assertEquals(0.10, enemy.getEncounter(), 0.001);
        assertEquals(0.75, enemy.getRunAway(), 0.001);
    }
    
    @Test
    public void testTakeDamage() {
        enemy.takeDamage(3);
        assertEquals(7, enemy.getEnemyHealth());
    }
    
    @Test
    public void testTakeLethalDamage() {
        enemy.takeDamage(15);
        assertEquals(0, enemy.getEnemyHealth());
    }
    
    @Test
    public void testAttackPlayer() {
        Player mockPlayer = new Player();
        mockPlayer.setHealth(20);

        enemy.attack(mockPlayer);
        assertEquals(18, mockPlayer.getHealth());
    }
    
    @Test
    public void testSetters() {
        enemy.setEnemyHealth(5);
        enemy.setEnemyAttack(10);
        enemy.setEncounter(0.6);
        enemy.setRunAway(0.2);

        assertEquals(5, enemy.getEnemyHealth());
        assertEquals(10, enemy.getEnemyAttack());
        assertEquals(0.6, enemy.getEncounter(), 0.001);
        assertEquals(0.2, enemy.getRunAway(), 0.001);
    }
    
}
