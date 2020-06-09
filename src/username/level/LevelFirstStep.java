package username.level;

import core.game.GameDescription;

import java.util.List;

import static username.SharedData.gameDescription;

public class LevelFirstStep {

	LevelTree.LevelState firstStep(LevelTree levelTree) {
		LevelTree.LevelState levelState = levelTree.getRoot().getNewChild();
		List<GameDescription.TerminationData> terminationConditions = gameDescription.getTerminationConditions();
		terminationConditions.forEach(condition -> makeNotHold(condition, levelState));
		return levelState;
	}

	void makeNotHold(GameDescription.TerminationData condition, LevelTree.LevelState levelState) {
		// TODO check if condition doesn't hold already

		switch (condition.type) {
			// Level is terminated if the number of a certain sprite is less than or equal to a certain value
			case "SpriteCounter":
				// TODO
				break;

			// Level is terminated if the number of a certain sprite is more than or equal to a certain value
			case "SpriteCounterMore":
				// TODO
				break;

			// Level is terminated if the summation of three different sprites is equal to a certain value
			case "MultiSpriteCounter":
				// TODO
				break;

			// Level is NOT terminated if the summation of three different sprites is equal to a certain value
			// (prevents any other terminations from triggering)
			case "StopCounter":
				// TODO

			// Level is terminated if the game time is larger than or equal to a certain value
			case "Timeout":
				// TODO
				break;

			default:
				throw new IllegalArgumentException("Undefined termination condition type: " + condition.type);
		}
	}
}
