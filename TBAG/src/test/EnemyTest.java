package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.tbag.model.Enemy;
import edu.ycp.cs320.tbag.model.Player;

public class EnemyTest {
    private Enemy enemy;
    private Player player;

    @Before
    public void setUp() {
        enemy = new Enemy(1, "Goblin", 2, 100, 15, 0.75, 0.25, "Snarl!", "Growl!", "Charge!");
        player = new Player();
        player.setHealth(100);
    }

    @Test
    public void testEnemyInitialization() {
        assertEquals("Enemy ID should be set correctly", 1, enemy.getEnemyID());
        assertEquals("Enemy name should be set correctly", "Goblin", enemy.getEnemyName());
        assertEquals("Enemy health should be initialized correctly", 100, enemy.getEnemyHealth());
        assertEquals("Enemy attack should be set correctly", 15, enemy.getEnemyAttack());
        assertEquals("Encounter chance should be set correctly", 0.75, enemy.getEncounter(), 0.001);
        assertEquals("RunAway chance should be set correctly", 0.25, enemy.getRunAway(), 0.001);
    }

    @Test
    public void testEnemyHealthModification() {
        enemy.setEnemyHealth(50);
        assertEquals("Health should update correctly", 50, enemy.getEnemyHealth());
    }

    @Test
    public void testEnemyAttackModification() {
        enemy.setEnemyAttack(20);
        assertEquals("Attack should update correctly", 20, enemy.getEnemyAttack());
    }

    @Test
    public void testEnemyTakeDamage() {
        enemy.takeDamage(30);
        assertEquals("Enemy health should decrease after taking damage", 70, enemy.getEnemyHealth());

        enemy.takeDamage(100); // Excess damage shouldn't make health negative
        assertEquals("Enemy health should never drop below zero", 0, enemy.getEnemyHealth());
    }

    @Test
    public void testEnemyAttackPlayer() {
        enemy.attack(player);
        assertEquals("Player should lose health equal to enemy attack", 85, player.getHealth());
    }
}
