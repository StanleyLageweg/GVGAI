package username.level;

import core.game.GameDescription;
import username.Constants;

import java.util.ArrayList;
import java.util.List;

import static username.SharedData.gameAnalyzer;
import static username.SharedData.gameDescription;

/**
 * Class that finalizes the creation of a level.
 */
public final class LevelFinalizer {

	/**
	 * Hide constructor.
	 */
	private LevelFinalizer() { }

	/**
	 * Returns a String representing a finalized version of the given level state.
	 * @param levelState The level state to finalize
	 * @return A String representing a finalized version of the given level state
	 */
	public static String finalizeLevel(LevelTree.LevelState levelState) {
		addFloor(levelState);

		if (!gameAnalyzer.getSolidSprites().isEmpty()) {
			// Check which areas are reachable
			boolean[][] reachable = new boolean[levelState.getHeight()][levelState.getWidth()];
			findReachablePositions(levelState, reachable, levelState.getAvatar().getX(), levelState.getAvatar().getY());

			fillUnreachablePositions(levelState, reachable);

			return getLevelCutToSize(levelState, reachable);
		}

		return levelState.getLevel();
	}

	/**
	 * Adds a floor sprites to all spaces, if there exists such a sprite.
	 * @param levelState The level state to add a floor to.
	 */
	private static void addFloor(LevelTree.LevelState levelState) {
		ArrayList<GameDescription.SpriteData> staticSprites = gameDescription.getStatic();

		// Try to find a sprite called floor
		staticSprites.stream().filter(sprite -> "floor".equals(sprite.name)).findAny().or(() ->

				// Or try to find a sprite that has no interactions
				staticSprites.stream().filter(staticSprite -> {
					for (GameDescription.SpriteData otherSprite : gameDescription.getAllSpriteData()) {
						if (!gameDescription.getInteraction(staticSprite.name, otherSprite.name).isEmpty()
								|| !gameDescription.getInteraction(otherSprite.name, staticSprite.name).isEmpty()) {
							return false;
						}
					}
					return true;
				}).findAny()).ifPresent(spriteData ->

				// If a sprite is found, add it to all positions
				levelState.forEachPosition((x, y) -> levelState.addSprite(x, y, spriteData.name)));
	}

	/**
	 * Finds positions which are reachable from the given position.
	 * @param levelState The level state to find reachable positions for.
	 * @param reachable Matrix of booleans, indicating which positions are reachable, and which are not.
	 * @param x The x position we are trying to reach other positions from.
	 * @param y The y position we are trying to reach other positions from.
	 */
	private static void findReachablePositions(LevelTree.LevelState levelState, boolean[][] reachable, int x, int y) {
		// If this coordinate has been checked before, do nothing
		if (reachable[y][x]) return;
		reachable[y][x] = true;

		// If this coordinate contains a solid sprite, then neighbouring sprites won't be reachable from here
		if (gameAnalyzer.getSolidSprites().stream().anyMatch(levelState.get(x, y)::contains)) return;

		// Get coordinates
		int[] xCoordinates = {x - 1, x, x + 1, x};
		int[] yCoordinates = {y, y + 1, y, y - 1};

		for (int i = 0; i < xCoordinates.length; i++) {
			// Check x coordinate is within bounds
			int xCoordinate = xCoordinates[i];
			if (xCoordinate < 0 || xCoordinate >= levelState.getWidth()) continue;

			// Check y coordinate is within bounds
			int yCoordinate = yCoordinates[i];
			if (yCoordinate < 0 || yCoordinate >= levelState.getHeight()) continue;

			// Check the the coordinate
			findReachablePositions(levelState, reachable, xCoordinate, yCoordinate);
		}
	}

	/**
	 * Fills unreachable areas of the level state with solid sprites.
	 * @param levelState The level state for which to fill its unreachable positions.
	 * @param reachable Matrix indicating which positions are reachable, and which are not.
	 */
	private static void fillUnreachablePositions(LevelTree.LevelState levelState, boolean[]... reachable) {
		List<String> solidSprites = gameAnalyzer.getSolidSprites();

		// Fill unreachable areas with solid sprites
		levelState.forEachPosition((x, y) -> {
			if (!reachable[y][x] && levelState.get(x, y).stream().noneMatch(solidSprites::contains)) {
				levelState.setSprite(x, y, Constants.rng.elementOf(solidSprites));
			}
		});
	}

	/**
	 * Transforms the level matrix into a single String, and cuts the level to size, using only the relevant parts.
	 * @param levelState The level state to get the level string from.
	 * @param reachable Matrix indicating which positions are reachable, and which are not.
	 * @return The level matrix as a single String.
	 */
	private static String getLevelCutToSize(LevelTree.LevelState levelState, boolean[]... reachable) {
		Avatar avatar = levelState.getAvatar();
		int minX = avatar.getX();
		int maxX = avatar.getX();
		int minY = avatar.getY();
		int maxY = avatar.getY();

		for (int x = 0; x < levelState.getWidth(); x++) {
			for (int y = 0; y < levelState.getHeight(); y++) {
				if (reachable[y][x]) {
					if (x < minX) minX = x;
					if (x > maxX) maxX = x;
					if (y < minY) minY = y;
					if (y > maxY) maxY = y;
				}
			}
		}

		return levelState.getLevel(minX, maxX + 1, minY, maxY + 1);
	}
}
