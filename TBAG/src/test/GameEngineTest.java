package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import edu.ycp.cs320.tbag.model.GameEngine;

public class GameEngineTest {
	private GameEngine engine;
	
	@Before
	public void setUp() {
		engine = new GameEngine();
	}
}
