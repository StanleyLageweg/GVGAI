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
	 * Level that is being initialized.
	 */
	private static Level level;

	/**
	 * Hide constructor.
	 */
	private LevelInitializer() { }

	/**
	 * Returns an initialized level.
	 * @return An initialized level.
	 */
	public static Level getLevel() {
		Level.setHasBorder(!gameAnalyzer.getSolidSprites().isEmpty());

		initializeLevel();
		addSolidBorder();
		addPlayerAvatar();
		createWinState();

		return level;
	}

	/**
	 * Determines the width and height of the level and initializes a new Level object.
	 * @see Level
	 */
	private static void initializeLevel() {
		// Determine if we need to add clearance to add a border
		int borderClearance = Level.hasBorder() ? 2 : 0;

		// Determine the width and height
		int width = Utils.clamp(Constants.MIN_WIDTH,
				(int) (gameDescription.getAllSpriteData().size() * (1 + Constants.RANDOM_WIDTH
						* Constants.rng.nextDouble())), Constants.MAX_WIDTH) + borderClearance;
		int height = Utils.clamp(Constants.MIN_HEIGHT,
				(int) (gameDescription.getAllSpriteData().size() * (1 + Constants.RANDOM_HEIGHT
						* Constants.rng.nextDouble())), Constants.MAX_HEIGHT) + borderClearance;

		// Initialize the level
		level = new Level(width, height);
	}

	/**
	 * Adds a border of random solid sprites around the level.
	 */
	private static void addSolidBorder() {
		if (!Level.hasBorder()) return;

		// Get the sprite to use for the border
		String solidSprite = Constants.rng.elementOf(gameAnalyzer.getSolidSprites());

		// Add a border along the top and bottom side of the level
		for (int x = 0; x < level.getWidth(); x++) {
			level.setSprite(x, 0, solidSprite);
			level.setSprite(x, level.getHeight() - 1, solidSprite);
		}

		// Add a border along the left and right side of the level
		for (int y = 1; y < level.getHeight() - 1; y++) {
			level.setSprite(0, y, solidSprite);
			level.setSprite(level.getWidth() - 1, y, solidSprite);
		}
	}

	/**
	 * Places the player avatar into the level.
	 */
	private static void addPlayerAvatar() {
		Avatar.setSprite(Constants.rng.elementOf(gameAnalyzer.getAvatarSprites()));
		level.forRandomPosition((x, y) ->
				level.setAvatar(new Avatar(x, y, level)), true);
	}

	/**
	 * Adds and removes sprites from the level, to create a win state.
	 */
	private static void createWinState() {
		List<GameDescription.TerminationData> terminationConditions = gameDescription.getTerminationConditions();
		List<GameDescription.TerminationData> winConditions = terminationConditions.stream().filter(terminationData ->
				Boolean.parseBoolean(terminationData.win.split(",", 2)[0])).collect(Collectors.toList());

		GameDescription.TerminationData winCondition = Constants.rng.elementOf(winConditions);

		// Make the conditions, before the win condition, not hold
		terminationConditions.subList(0, terminationConditions.indexOf(winCondition)).forEach(condition ->
				applyCondition(condition, false));

		// Make the win condition hold
		applyCondition(winCondition, true);
	}

	/**
	 * Applies a Termination condition to the level.
	 * @param condition Termination condition
	 * @param makeConditionHold Whether the condition should hold
	 */
	private static void applyCondition(GameDescription.TerminationData condition, boolean makeConditionHold) {

		// TODO make sure that different conditions don't overwrite each other

		int maxCount = (condition.limit + 1) * 2;

		switch (condition.type) {
			// Level is terminated if the number of a certain sprite is less than or equal to a certain value
			case "SpriteCounter":
				if (makeConditionHold) {
					// Make sure the number of sprites is less than or equal to the value
					level.getSpriteCountSetter().makeLessThan(condition.limit + 1, condition.sprites.get(0));
				} else {
					// Make sure the number of sprites is more than the value
					level.getSpriteCountSetter().makeMoreThan(condition.limit, maxCount, condition.sprites.get(0));
				}
				break;

			// Level is terminated if the number of a certain sprite is more than or equal to a certain value
			case "SpriteCounterMore":
				if (makeConditionHold) {
					// Make sure the number of sprites is more than or equal to the value
					level.getSpriteCountSetter().makeMoreThan(condition.limit - 1, maxCount, condition.sprites.get(0));
				} else {
					// Make sure the number of sprites is less than the value
					level.getSpriteCountSetter().makeLessThan(condition.limit, condition.sprites.get(0));
				}
				break;

			// Level is terminated if the summation of three different sprites is equal to a certain value
			case "MultiSpriteCounter":
				if (makeConditionHold) {
					// Make sure the number of sprites is equal to the value
					level.getSpriteCountSetter().makeEqual(condition.limit, condition.sprites);
				} else {
					// Make sure the number of sprites is not equal to the value
					level.getSpriteCountSetter().makeNotEqual(condition.limit, maxCount, condition.sprites);
				}
				break;

			// Level is NOT terminated if the summation of three different sprites is equal to a certain value
			// (prevents any other terminations from triggering)
			case "StopCounter":
				// Make sure the stop counter never holds
				level.getSpriteCountSetter().makeNotEqual(condition.limit, maxCount, condition.sprites);
				break;

			// Level is terminated if the game time is larger than or equal to a certain value
			case "Timeout":
				if (makeConditionHold) level.setTick(Constants.rng.nextInt(condition.limit) + condition.limit);
				else level.setTick(Constants.rng.nextInt(condition.limit));
				break;

			default:
				throw new IllegalArgumentException("Undefined termination condition type: " + condition.type);
		}
	}
}
