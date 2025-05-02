package edu.ycp.cs320.tbag.model;
import java.util.Random;
public class Enemy extends NPC {
  private int enemyID;
  private int health;
  private int attack;
  private double encounter;
  private double runAway;
  //private String combatDialogue1; // First combat dialogue
  //private String combatDialogue2; // Second combat dialogue
  //private String combatDialogue3; // Third combat dialogue
  //private Random random = new Random();
 
  public Enemy(int enemyID, String name, int roomId, int health, int attack, double encounter, double runAway,
          String dialogue1) {
 super(name, dialogue1, roomId );  // Inherit the name and dialogue from the NPC class
 this.enemyID = enemyID;
 this.health = health;
 this.attack = attack;
 this.encounter = encounter;
 this.runAway = runAway;
}
  // Getters and setters
  public int getEnemyID() {
      return enemyID;
  }
  
  public void setEnemyID(int enemyID) {
	   this.enemyID = enemyID;
  }
  
  public String getEnemyName() {
	    return getName(); // or just use getName() directly from NPC
	}
  
  public int getEnemyHealth() {
      return health;
  }
   public void setEnemyHealth(int health) {
  	this.health=health;
  }
   public int getEnemyAttack() {
      return attack;
  }
   public void setEnemyAttack(int attack) {
  	this.attack=attack;
  }
 
  public double getEncounter() {
	   return encounter;
  }
 
  public void setEncounter(double encounter) {
	   this.encounter=encounter;
  }
 
  public double getRunAway() {
	   return runAway;
  }
 
  public void setRunAway(double runAway) {
	   this.runAway = runAway;
  }
 
  public void attack(Player player) {
  	player.takeDamage(attack);
  }
   public void takeDamage(int amount) {
      health = Math.max(0, health - amount);
  }
 }






