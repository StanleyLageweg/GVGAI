package username.level;

import lombok.Getter;
import lombok.experimental.Accessors;
import username.Constants;
import username.Utils;
import username.random.MatrixSelection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Structure for generating a level.
 * Contains a tree structure which allows for creating new level states an modifying them.
 */
public class LevelTree {

	/**
	 * The LevelMapping for this level.
	 */
	private final LevelMapping levelMapping = new LevelMapping();

	/**
	 * Whether the level has borders or not.
	 */
	@Accessors(fluent = true)
	@Getter private final boolean hasBorder;

	/**
	 * The width of the matrix.
	 */
	@Getter private final int width;

	/**
	 * The height of the matrix.
	 */
	@Getter private final int height;

	private final Avatar avatar;

	/**
	 * The root of the level tree.
	 * This level state should be in a finished state.
	 */
	@Getter private final LevelState root;

	/**
	 * Constructs a new LevelTree.
	 * @param hasBorder Whether the level has borders or not.
	 * @param width The width of the matrix.
	 * @param height The height of the matrix.
	 * @param avatarSprite The sprite that represents the player avatar.
	 * @param tick The game tick of the root sprite.
	 */
	LevelTree(boolean hasBorder, int width, int height, String avatarSprite, int tick) {
		this.hasBorder = hasBorder;
		this.width = width;
		this.height = height;
		avatar = new Avatar(avatarSprite);
		root = new LevelTree.LevelState(tick);
	}

	/**
	 * @return The levelMapping for this level.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	public HashMap<Character, ArrayList<String>> getLevelMapping() {
		return levelMapping.getMapping();
	}

	/**
	 * A state in the level tree.
	 */
	public class LevelState {

		/**
		 * The LevelState that was created before this one.
		 * Represents the level one tick later than this state.
		 */
		private final LevelState parent;

		/**
		 * The LevelStates that were created after this one.
		 * They represent the level one tick earlier than this state.
		 */
		private final Set<LevelState> children = new HashSet<>();

		/**
		 * The level state, represented as a matrix, containing chars which represent sprites.
		 * The outer array contains the rows, i.e. y coordinate.
		 * The inner array contain the columns, i.e. x coordinate.
		 */
		private final char[][] matrix;

		/**
		 * The game tick of this level state.
		 */
		private final int tick;

		/**
		 * Keeps count of the number of sprites in this level state.
		 */
		@Getter private final MultiCounter<String> spriteCounter;

		/**
		 * The avatar of this level state.
		 */
		@Getter private final Avatar.AvatarState avatarState;

		/**
		 * Constructs a new level state root, in a finished state.
		 * @param tick The game tick this level state should have. (Can be overwritten in order to generate a
		 *                   finished state.
		 */
		private LevelState(int tick) {
			parent = null;

			// Initialize the level matrix
			char emtpyChar = levelMapping.get();
			matrix = new char[height][width];
			for (char[] column : matrix) Arrays.fill(column, emtpyChar);

			spriteCounter = new MultiCounter<>();

			// Spawn the avatar in a random location
			avatarState = avatar.getNewState(this);

			// Add a solid border
			LevelInitializer.addSolidBorder(this);

			// Create a win state
			SpriteCountSetter spriteCountSetter = new SpriteCountSetter(this);
			LevelInitializer.createWinState(spriteCountSetter);

			// Set the tick
			this.tick = spriteCountSetter.getTick() == null ? tick : spriteCountSetter.getTick();
		}

		/**
		 * Constructs a new LevelState, as a child of another level state.
		 * @param parent The parent of this new LevelState.
		 */
		private LevelState(LevelState parent) {
			this.parent = parent;
			parent.children.add(this);
			matrix = Utils.copyMatrix(parent.matrix);
			this.tick = parent.tick - 1;
			spriteCounter = new MultiCounter<>(parent.spriteCounter);
			avatarState = parent.avatarState.copy(this);
		}

		/**
		 * Returns the list of sprites for a given coordinate.
		 * @param x The x coordinate.
		 * @param y The y coordinate.
		 * @return The list of sprites for a given coordinate.
		 */
		List<String> get(int x, int y) {
			return levelMapping.get(matrix[y][x]);
		}

		/**
		 * Adds the sprite at the specified location, in the level state matrix.
		 * @param x The x coordinate of where to set the sprite.
		 * @param y The y coordinate of where to set the sprite.
		 * @param sprite The sprite to set at the specified coordinates.
		 */
		void addSprite(int x, int y, String sprite) {
			// Add the sprite to the matrix
			matrix[y][x] = levelMapping.getWith(matrix[y][x], sprite);

			// Increase the sprite counter
			spriteCounter.increment(sprite);
		}

		/**
		 * Adds the specified sprite to the level state, at a random coordinate.
		 * @param sprite The sprite to be added to the level.
		 */
		void addSpriteRandomly(String sprite) {
			forRandomPosition((x, y) -> addSprite(x, y, sprite), true);
		}

		/**
		 * Sets the sprite at the specified location, in the level state matrix, while removing all others.
		 * @param x The x coordinate of where to set the sprite.
		 * @param y The y coordinate of where to set the sprite.
		 * @param sprite The sprite to set at the specified coordinates.
		 */
		void setSprite(int x, int y, String sprite) {
			// Update the sprite counter
			spriteCounter.decrement(levelMapping.get(matrix[y][x]));
			spriteCounter.increment(sprite);

			// Set the sprite in the matrix
			matrix[y][x] = levelMapping.get(sprite);
		}

		/**
		 * Removes the sprite from the specified location, in the level state matrix.
		 * @param x The x coordinate of where to set the sprite.
		 * @param y The y coordinate of where to set the sprite.
		 * @param sprite The sprite to remove from the specified coordinates.
		 */
		void removeSprite(int x, int y, String sprite) {
			// Do nothing if there is no such sprite at the given position
			List<String> sprites = levelMapping.get(matrix[y][x]);
			if (!sprites.contains(sprite)) return;

			// Remove the sprite from the matrix
			matrix[y][x] = levelMapping.getWithout(matrix[y][x], sprite);

			// Decrease the sprite counter
			spriteCounter.decrement(sprite);
		}

		/**
		 * Removes the specified sprite from the level, at a random coordinate.
		 * @param sprite The sprite to be removed from the level.
		 */
		void removeSpriteRandomly(String sprite) {
			int count = spriteCounter.get(sprite);
			if (count == 0) return;

			MatrixSelection<Character> selection = Constants.rng.elementOfWhere(matrix,
					levelMapping.getContainingAll(sprite)::contains);
			removeSprite(selection.getX(), selection.getY(), sprite);
		}

		/**
		 * Moves the sprite to a new location, in the level matrix.
		 * If the sprite does not exist at the old location, it will still be added to the new location.
		 * @param xOld The x coordinate of where the sprite is now.
		 * @param yOld The y coordinate of where the sprite is now.
		 * @param xNew The x coordinate of where the sprite should be moved to.
		 * @param yNew The y coordinate of where the sprite should be moved to.
		 * @param sprite The sprite to be moved.
		 */
		void moveSprite(int xOld, int yOld, int xNew, int yNew, String sprite) {
			removeSprite(xOld, yOld, sprite);
			addSprite(xNew, yNew, sprite);
		}

		void moveSpriteWithCollisions(int xOld, int yOld, int xNex, int yNew, String sprite) {
			moveSprite(xOld, yOld, xNex, yNew, sprite);
			// TODO
		}

		/**
		 * Applies a function that takes two integers (x and y position) to each position in the level.
		 * @param fun A function that takes an x and y position as an input.
		 */
		void forEachPosition(BiConsumer<Integer, Integer> fun) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					fun.accept(x, y);
				}
			}
		}

		/**
		 * Executes a function for a random coordinate in the level.
		 * @param fun Function that takes a x and y coordinate as an input.
		 * @param avoidBorder Whether to avoid coordinates which are on the border.
		 */
		void forRandomPosition(BiConsumer<Integer, Integer> fun, boolean avoidBorder) {
			// Determine if we need to add clearance to not add the sprite inside the border
			int borderClearance = hasBorder && avoidBorder ? 1 : 0;

			fun.accept(getRandomX(borderClearance), getRandomY(borderClearance));
		}

		/**
		 * Transforms the level matrix into a single String.
		 * @return The level matrix as a single String.
		 */
		String getLevel() {
			return getLevel(0, width, 0, height);
		}

		/**
		 * Transforms the level matrix into a single String, but only within the range of given coordinates.
		 * @param minX The minimum x coordinate (inclusive).
		 * @param maxX The maximum x coordinate (exclusive).
		 * @param minY The minimum y coordinate (inclusive).
		 * @param maxY The maximum y coordinate (exclusive).
		 * @return The level matrix as a single String.
		 */
		String getLevel(int minX, int maxX, int minY, int maxY) {
			StringBuilder result = new StringBuilder();
			for (int y = minY; y < maxY; y++) {
				result.append(String.valueOf(matrix[y], minX, maxX - minX)).append('\n');
			}
			return result.substring(0, result.length() - 1);
		}

		/**
		 * @return A new child for this level state.
		 */
		public LevelState getNewChild() {
			return new LevelState(this);
		}

		/**
		 * @param borderClearance How much tiles of clearance we should leave, from the edge.
		 * @return A random x coordinate within the level.
		 */
		int getRandomX(int borderClearance) {
			return Constants.rng.nextInt(borderClearance, width - borderClearance);
		}

		/**
		 * @param borderClearance How much tiles of clearance we should leave, from the edge.
		 * @return A random y coordinate within the level.
		 */
		int getRandomY(int borderClearance) {
			return Constants.rng.nextInt(borderClearance, height - borderClearance);
		}

		/**
		 * @return The height of the level.
		 */
		protected int getHeight() {
			return height;
		}

		/**
		 * @return The width of the level.
		 */
		protected int getWidth() {
			return width;
		}

		/**
		 * @return Whether the level has a border or not.
		 */
		boolean hasBorder() {
			return hasBorder;
		}
	}
}
