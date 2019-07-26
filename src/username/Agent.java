package username;

import core.game.GameDescription;
import core.generator.AbstractLevelGenerator;
import tools.ElapsedCpuTimer;
import tools.GameAnalyzer;
import username.level.Level;
import username.level.LevelFinalizer;
import username.level.LevelInitializer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Agent that generates a level from a GameDescription.
 */
public class Agent extends AbstractLevelGenerator {

	/**
	 * Constructor for the level generator.
	 * @param game GameDescription object holding all game information.
	 * @param elapsedTimer Count down until level generation is due.
	 */
	public Agent(GameDescription game, ElapsedCpuTimer elapsedTimer) {
		SharedData.gameDescription = game;
		SharedData.gameAnalyzer = new GameAnalyzer(game);
		SharedData.elapsedCpuTimer = elapsedTimer;
	}

	@Override
	public String generateLevel(GameDescription game, ElapsedCpuTimer elapsedTimer) {
		return LevelFinalizer.finalizeLevel(LevelInitializer.getLevel());
	}

	@Override
	public HashMap<Character, ArrayList<String>> getLevelMapping() {
		return Level.getLEVEL_MAPPING().getMapping();
	}
}