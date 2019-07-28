package username.level;

import core.game.GameDescription;
import username.Constants;
import username.Utils;

import java.util.List;
import java.util.stream.Collectors;

import static username.SharedData.gameAnalyzer;
import static username.SharedData.gameDescription;

/**
 * Class that initializes a new level.
 */
public final class LevelInitializer {

	/**
	 * Hide constructor.
	 */
	private LevelInitializer() { }

	/**
	 * Returns an initialized LevelTree.
	 * @return An initialized LevelTree.
	 */
	public static LevelTree initializeLevelTree() {
		boolean hasBorder = !gameAnalyzer.getSolidSprites().isEmpty();

		// Determine if we need to add clearance to add a border
		int borderClearance = hasBorder ? 2 : 0;

		// Determine the width and height
		int width = Utils.clamp(Constants.MIN_WIDTH,
				(int) (gameDescription.getAllSpriteData().size() * (1 + Constants.RANDOM_WIDTH
						* Constants.rng.nextDouble())), Constants.MAX_WIDTH) + borderClearance;
		int height = Utils.clamp(Constants.MIN_HEIGHT,
				(int) (gameDescription.getAllSpriteData().size() * (1 + Constants.RANDOM_HEIGHT
						* Constants.rng.nextDouble())), Constants.MAX_HEIGHT) + borderClearance;

		String avatarSprite = gameDescription.getAvatar().get(0).name;

		int tick = Constants.rng.nextInt(Constants.MIN_TICK, Constants.MAX_TICK);

		return new LevelTree(hasBorder, width, height, avatarSprite, tick);

	}

	/**
	 * Adds a border of random solid sprites around the level state.
	 * @param levelState The level to add a solid border around.
	 */
	static void addSolidBorder(LevelTree.LevelState levelState) {
		if (!levelState.hasBorder()) return;

		// Get the sprite to use for the border
		String solidSprite = Constants.rng.elementOf(gameAnalyzer.getSolidSprites());

		// Add a border along the top and bottom side of the level
		for (int x = 0; x < levelState.getWidth(); x++) {
			levelState.setSprite(x, 0, solidSprite);
			levelState.setSprite(x, levelState.getHeight() - 1, solidSprite);
		}

		// Add a border along the left and right side of the level
		for (int y = 1; y < levelState.getHeight() - 1; y++) {
			levelState.setSprite(0, y, solidSprite);
			levelState.setSprite(levelState.getWidth() - 1, y, solidSprite);
		}
	}

	/**
	 * Adds and removes sprites from the level state, to create a win state.
	 * @param spriteCountSetter The spriteCountSetter of the level state.
	 */
	static void createWinState(SpriteCountSetter spriteCountSetter) {
		List<GameDescription.TerminationData> terminationConditions = gameDescription.getTerminationConditions();
		List<GameDescription.TerminationData> winConditions = terminationConditions.stream().filter(terminationData ->
				Boolean.parseBoolean(terminationData.win.split(",", 2)[0])).collect(Collectors.toList());

		if (winConditions.isEmpty()) {
			Constants.warning("GameDescription has no win conditions");
			return;
		}
		GameDescription.TerminationData winCondition = Constants.rng.elementOf(winConditions);

		// Make the conditions, before the win condition, not hold
		terminationConditions.subList(0, terminationConditions.indexOf(winCondition)).forEach(condition ->
				applyCondition(condition, false, spriteCountSetter));

		// Make the win condition hold
		applyCondition(winCondition, true, spriteCountSetter);
	}

	/**
	 * Applies a Termination condition to the level.
	 * @param condition Termination condition
	 * @param makeConditionHold Whether the condition should hold
	 * @param spriteCountSetter The spriteCountSetter of the level state
	 */
	private static void applyCondition(GameDescription.TerminationData condition, boolean makeConditionHold,
	                                  SpriteCountSetter spriteCountSetter) {
		// TODO make sure that different conditions don't overwrite each other

		int maxCount = (condition.limit + 1) * 2;

		switch (condition.type) {
			// Level is terminated if the number of a certain sprite is less than or equal to a certain value
			case "SpriteCounter":
				if (makeConditionHold) {
					// Make sure the number of sprites is less than or equal to the value
					spriteCountSetter.makeLessThan(condition.limit + 1, condition.sprites.get(0));
				} else {
					// Make sure the number of sprites is more than the value
					spriteCountSetter.makeMoreThan(condition.limit, maxCount, condition.sprites.get(0));
				}
				break;

			// Level is terminated if the number of a certain sprite is more than or equal to a certain value
			case "SpriteCounterMore":
				if (makeConditionHold) {
					// Make sure the number of sprites is more than or equal to the value
					spriteCountSetter.makeMoreThan(condition.limit - 1, maxCount, condition.sprites.get(0));
				} else {
					// Make sure the number of sprites is less than the value
					spriteCountSetter.makeLessThan(condition.limit, condition.sprites.get(0));
				}
				break;

			// Level is terminated if the summation of three different sprites is equal to a certain value
			case "MultiSpriteCounter":
				if (makeConditionHold) {
					// Make sure the number of sprites is equal to the value
					spriteCountSetter.makeEqual(condition.limit, condition.sprites);
				} else {
					// Make sure the number of sprites is not equal to the value
					spriteCountSetter.makeNotEqual(condition.limit, maxCount, condition.sprites);
				}
				break;

			// Level is NOT terminated if the summation of three different sprites is equal to a certain value
			// (prevents any other terminations from triggering)
			case "StopCounter":
				// Make sure the stop counter never holds
				spriteCountSetter.makeNotEqual(condition.limit, maxCount, condition.sprites);
				// TODO what if makeHold is true
				break;

			// Level is terminated if the game time is larger than or equal to a certain value
			case "Timeout":
				if (makeConditionHold) {
					spriteCountSetter.setTick(Constants.rng.nextInt(condition.limit) + condition.limit);
				} else {
					spriteCountSetter.setTick(Constants.rng.nextInt(condition.limit));
				}
				break;

			default:
				throw new IllegalArgumentException("Undefined termination condition type: " + condition.type);
		}
	}
}
