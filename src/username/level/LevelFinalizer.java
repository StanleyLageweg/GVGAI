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
	 * The level to finalize.
	 */
	private static Level level;

	/**
	 * Hide constructor.
	 */
	private LevelFinalizer() { }

	/**
	 * Returns a String representing a finalized version of the given level.
	 * @param level The level to finalize
	 * @return A String representing a finalized version of the given level
	 */
	@SuppressWarnings("checkstyle:hiddenfield")
	public static String finalizeLevel(Level level) {
		LevelFinalizer.level = level;

		addFloor();

		if (!gameAnalyzer.getSolidSprites().isEmpty()) {
			// Check which areas are reachable
			boolean[][] reachable = new boolean[level.getHeight()][level.getWidth()];
			findReachablePositions(reachable, level.getAvatar().getX(), level.getAvatar().getY());

			fillUnreachablePositions(reachable);

			return getLevelCutToSize(reachable);
		}

		return level.getLevel();
	}

	/**
	 * Adds a floor sprite to all spaces, if there exists such a sprite.
	 */
	private static void addFloor() {
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
				level.forEachPosition((x, y) -> level.addSprite(x, y, spriteData.name)));
	}

	/**
	 * Finds positions which are reachable from the given position.
	 * @param reachable Matrix of booleans, indicating which positions are reachable, and which are not.
	 * @param x The x position we are trying to reach other positions from.
	 * @param y The y position we are trying to reach other positions from.
	 */
	private static void findReachablePositions(final boolean[][] reachable, int x, int y) {
		// If this coordinate has been checked before, do nothing
		if (reachable[y][x]) return;
		reachable[y][x] = true;

		// If this coordinate contains a solid sprite, then neighbouring sprites won't be reachable from here
		if (gameAnalyzer.getSolidSprites().stream().anyMatch(level.get(x, y)::contains)) return;

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
			findReachablePositions(reachable, xCoordinate, yCoordinate);
		}
	}

	/**
	 * Fills unreachable areas of the level with solid sprites.
	 * @param reachable Matrix indicating which positions are reachable, and which are not.
	 */
	private static void fillUnreachablePositions(boolean[]... reachable) {
		List<String> solidSprites = gameAnalyzer.getSolidSprites();

		// Fill unreachable areas with solid sprites
		level.forEachPosition((x, y) -> {
			if (!reachable[y][x] && level.get(x, y).stream().noneMatch(solidSprites::contains)) {
				level.setSprite(x, y, Constants.rng.elementOf(solidSprites));
			}
		});
	}

	/**
	 * Transforms the level matrix into a single String, and cuts the level to size, using only the relevant parts.
	 * @param reachable Matrix indicating which positions are reachable, and which are not.
	 * @return The level matrix as a single String.
	 */
	private static String getLevelCutToSize(boolean[]... reachable) {
		int minX = level.getAvatar().getX();
		int maxX = level.getAvatar().getX();
		int minY = level.getAvatar().getY();
		int maxY = level.getAvatar().getY();

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
}
