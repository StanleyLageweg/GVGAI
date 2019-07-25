package username;

import core.game.GameDescription;
import core.game.GameDescription.TerminationData;
import core.generator.AbstractLevelGenerator;
import tools.ElapsedCpuTimer;
import tools.GameAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent that generates a level from a GameDescription.
 */
public class Agent extends AbstractLevelGenerator {

	/**
	 * GameDescription object holding all game information.
	 */
	private final GameDescription gameDescription;

	/**
	 * GameAnalyzer object which adds some tools for extracting information from the gameDescription object.
	 */
	private final GameAnalyzer gameAnalyzer;

	/**
	 * The level we are generating.
	 */
	private Level level;

	/**
	 * Count down until level generation is due.
	 */
	private final ElapsedCpuTimer elapsedCpuTimer;

	/**
	 * The x position of the player avatar.
	 */
	private int avatarX;

	/**
	 * The y position of the player avatar.
	 */
	private int avatarY;

	/**
	 * Constructor for the level generator.
	 * @param game GameDescription object holding all game information.
	 * @param elapsedTimer Count down until level generation is due.
	 */
	public Agent(GameDescription game, ElapsedCpuTimer elapsedTimer) {
		gameDescription = game;
		gameAnalyzer = new GameAnalyzer(game);
		elapsedCpuTimer = elapsedTimer;
	}

	/**
	 * Determines the width and height of the leven and initializes a new Level object.
	 * @see Level
	 */
	private void initializeLevel() {
		// Determine if we need to add clearance to add a border
		int borderClearance = 2;
		if (gameAnalyzer.getSolidSprites().isEmpty()) borderClearance = 0;

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
	private void addSolidBorder() {
		level.setHasBorder(true);

		// Get the sprite to use for the border
		List<String> solidSprites = gameAnalyzer.getSolidSprites();
		String solidSprite = solidSprites.get(Constants.rng.nextInt(solidSprites.size()));

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
	private void addPlayerAvatar() {
		level.forRandomPosition((x, y) -> {
			level.addSprite(x, y, gameAnalyzer.getAvatarSprites().get(0));
			avatarX = x;
			avatarY = y;
		}, true);
	}

	/**
	 * Adds and removes sprites from the level, to create a win state.
	 */
	private void createWinState() {
		List<TerminationData> terminationConditions = gameDescription.getTerminationConditions();
		List<TerminationData> winConditions = terminationConditions.stream().filter(terminationData ->
				Boolean.parseBoolean(terminationData.win.split(",", 2)[0])).collect(Collectors.toList());

		TerminationData winCondition = Utils.random(winConditions);

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
	private void applyCondition(TerminationData condition, boolean makeConditionHold) {

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

			// Level is terminated if the number of sprites of a certain type is equal to a certain value
			// and the number of different subtypes of the main type is equal to another value
			case "MultiSpriteCounterSubTypes":
				// TODO This case does not seem to be fully implemented in the GVGAI framework yet
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

	/**
	 * Fills every space that has not been assigned a spite yet, with a floor sprite if there exists such a sprite.
	 */
	private void fillEmptySpaceWithFloor() {
		gameDescription.getStatic().stream().filter(spriteData -> "Immovable".equals(spriteData.type)
				&& !spriteData.isSingleton && !spriteData.isResource && !spriteData.isNPC && !spriteData.isAvatar
				&& !spriteData.isPortal && gameAnalyzer.getOtherSprites().contains(spriteData.name)
		).findAny().ifPresent(spriteData -> {
			// If we can find a sprite the looks like a floor tile, add it to all coordinates
			String sprite = spriteData.name;
			level.forEachPosition((x, y) -> level.addSprite(x, y, sprite));
		});
	}

	/**
	 * Fills unreachable areas of the level with solid sprites.
	 * @param reachable Matrix indicating which positions are reachable, and which are not.
	 */
	private void fillUnreachablePositions(boolean[]... reachable) {
		List<String> solidSprites = gameAnalyzer.getSolidSprites();

		// Fill unreachable areas with solid sprites
		String solidSprite = solidSprites.get(Constants.rng.nextInt(solidSprites.size()));
		level.forEachPosition((x, y) -> {
			if (!reachable[y][x]) level.setSprite(x, y, solidSprite);
		});
	}

	/**
	 * Transforms the level matrix into a single String, and cuts the level to size, using only the relevant parts.
	 * @param reachable Matrix indicating which positions are reachable, and which are not.
	 * @return The level matrix as a single String.
	 */
	private String getLevelCutToSize(boolean[]... reachable) {
		int minX = avatarX;
		int maxX = avatarX;
		int minY = avatarY;
		int maxY = avatarY;

		for (int x = 0; x < level.getWidth(); x++) {
			for (int y = 0; y < level.getHeight(); y++) {
				if (reachable[y][x]) {
					if (x < minX) minX = x;
					if (x > maxX) maxX = x;
					if (y < minY) minY = y;
					if (y > maxY) maxY = y;
				}
			}
		}

		return level.getLevel(minX, maxX + 1, minY, maxY + 1);
	}

	/**
	 * Finds positions which are reachable from the given position.
	 * @param reachable Matrix of booleans, indicating which positions are reachable, and which are not.
	 * @param x The x position we are trying to reach other positions from.
	 * @param y The y position we are trying to reach other positions from.
	 * @param sprites The list of solid sprites.
	 */
	private void findReachablePositions(final boolean[][] reachable, int x, int y, List<String> sprites) {
		// If this coordinate has been checked before, do nothing
		if (reachable[y][x]) return;
		reachable[y][x] = true;

		// If this coordinate contains a solid sprite, then neighbouring sprites won't be reachable from here
		if (sprites.stream().anyMatch(level.get(x, y)::contains)) return;

		// Get coordinates
		int[] xCoordinates = {x - 1, x, x + 1, x};
		int[] yCoordinates = {y, y + 1, y, y - 1};

		for (int i = 0; i < xCoordinates.length; i++) {
			// Check x coordinate is within bounds
			int xCoordinate = xCoordinates[i];
			if (xCoordinate < 0 || xCoordinate >= level.getWidth()) continue;

			// Check y coordinate is within bounds
			int yCoordinate = yCoordinates[i];
			if (yCoordinate < 0 || yCoordinate >= level.getHeight()) continue;

			// Check the the coordinate
			findReachablePositions(reachable, xCoordinate, yCoordinate, sprites);
		}
	}

	@Override
	public String generateLevel(GameDescription game, ElapsedCpuTimer elapsedTimer) {
		List<String> solidSprites = gameAnalyzer.getSolidSprites();
		boolean solidSpritesExist = !solidSprites.isEmpty();

		initializeLevel();

		if (solidSpritesExist) addSolidBorder();

		addPlayerAvatar();
		createWinState();
		fillEmptySpaceWithFloor();

		if (solidSpritesExist) {
			// Check which areas are reachable
			boolean[][] reachable = new boolean[level.getHeight()][level.getWidth()];
			findReachablePositions(reachable, avatarX, avatarY, solidSprites);

			fillUnreachablePositions(reachable);

			return getLevelCutToSize(reachable);
		}

		return level.getLevel();
	}

	@Override
	public HashMap<Character, ArrayList<String>> getLevelMapping() {
		return Level.getLEVEL_MAPPING().getMapping();
	}
}