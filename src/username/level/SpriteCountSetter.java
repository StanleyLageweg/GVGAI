package username.level;

import lombok.Getter;
import username.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Class that adds or removes sprites from a level.
 */
class SpriteCountSetter {

	/**
	 * The Level which this SpriteCountSetter makes changes to.
	 */
	private final LevelTree.LevelState levelState;

	/**
	 * The SpriteCounter for the given level.
	 */
	private final MultiCounter<String> spriteCounter;

	/**
	 * The game tick which is required for the level state to be in a final state.
	 * Null if it does not matter.
	 */
	@Getter private Integer tick;

	/**
	 * Constructs a new SpriteCountSetter, for the given level.
	 * @param levelState {@link #levelState}
	 */
	SpriteCountSetter(LevelTree.LevelState levelState) {
		this.levelState = levelState;
		spriteCounter = levelState.getSpriteCounter();
	}

	/**
	 * Removes sprites from the level, of the given types, until the total count of the types is some random amount
	 * below the given count.
	 * If the number of sprites is already below the given count, this method does nothing.
	 * @param count The number of sprites should be at least below this amount.
	 * @param sprites The sprite types which should be removed.
	 */
	void makeLessThan(int count, List<String> sprites) {
		count = countShouldBeMoreThan(count, 1);
		int spriteCount = spriteCounter.get(sprites);

		// The sprite count is already correct
		if (spriteCount < count) return;

		// The sprite count needs to be decreased
		makeEqual(Constants.rng.nextInt(count), sprites, spriteCount);
	}

	/**
	 * Removes sprites from the level, of the given types, until the total count of the types is some random amount
	 * below the given count.
	 * If the number of sprites is already below the given count, this method does nothing.
	 * @param count The number of sprites should be at least below this amount.
	 * @param sprites The sprite types which should be removed.
	 */
	void makeLessThan(int count, String... sprites) {
		makeLessThan(count, Arrays.asList(sprites));
	}

	/**
	 * Adds sprites to the level, of the given types, until the total count of the types is some random amount higher
	 * than the given count, but still below the given maximum count.
	 * If the number of sprites is already above the given count, this method does nothing.
	 * If the count >= maxCount, just one sprite will be added.
	 * @param count The number of sprites should be at least above this amount.
	 * @param maxCount The number of sprites should be below this amount.
	 * @param sprites The sprite types which should be added.
	 */
	void makeMoreThan(int count, int maxCount, List<String> sprites) {
		// The sprite count is already correct
		int spriteCount = spriteCounter.get(sprites);
		if (spriteCount > count) return;

		// Correct max count
		if (maxCount <= count) {
			int newMaxCount = count + 1;
			Constants.warning("MaxCount (%1$d) was less than count (%2$d), thus %3$d is the new value for maxCount",
					count, maxCount, newMaxCount);
			maxCount = newMaxCount;
		}

		// The sprite count needs to be increased
		makeEqual(count + Constants.rng.nextInt(maxCount - count) + 1, sprites, spriteCount);
	}

	/**
	 * Adds sprites to the level, of the given types, until the total count of the types is some random amount higher
	 * than the given count, but still below the given maximum count.
	 * If the number of sprites is already above the given count, this method does nothing.
	 * If the count >= maxCount, just one sprite will be added.
	 * @param count The number of sprites should be at least above this amount.
	 * @param maxCount The number of sprites should be below this amount.
	 * @param sprites The sprite types which should be added.
	 */
	void makeMoreThan(int count, int maxCount, String... sprites) {
		makeMoreThan(count, maxCount, Arrays.asList(sprites));
	}

	/**
	 * Adds or removes sprites from the level, of the given types, until the total count of the types is some random
	 * amount not equal to the given count, but still below the given maximum count.
	 * If the number of sprites is already not equal to the given count, this method does nothing.
	 * @param count The number of sprites should not be equal to this amount.
	 * @param maxCount The number of sprites should be below this amount.
	 * @param sprites The sprite types which should be added or removed.
	 */
	void makeNotEqual(int count, int maxCount, List<String> sprites) {
		int spriteCount = spriteCounter.get(sprites);
		if (count != spriteCount) return;

		// The sprite count needs to be decreased
		if (count != 0 && (Constants.rng.nextBoolean() || count >= maxCount)) makeLessThan(count, sprites);

		// The sprite count needs to be increased
		else makeMoreThan(count, maxCount, sprites);
	}

	/**
	 * Adds or removes sprites from the level, of the given types, until the total count of the types is some random
	 * amount not equal to the given count, but still below the given maximum count.
	 * If the number of sprites is already not equal to the given count, this method does nothing.
	 * @param count The number of sprites should not be equal to this amount.
	 * @param maxCount The number of sprites should be below this amount.
	 * @param sprites The sprite types which should be added or removed.
	 */
	void makeNotEqual(int count, int maxCount, String... sprites) {
		makeNotEqual(count, maxCount, Arrays.asList(sprites));
	}

	/**
	 * Adds or removes sprites from the level, of the given types, until the total count of the types is equal to
	 * the given count.
	 * @param count The number of sprites should be equal to this amount.
	 * @param sprites The sprite types which should be added or removed.
	 * @param spriteCount The number of sprites of the given type that exist.
	 */
	private void makeEqual(int count, List<String> sprites, int spriteCount) {
		int nrSpritesToAdd = count - spriteCount;
		for (int i = 0; i < nrSpritesToAdd; i++) levelState.addSpriteRandomly(Constants.rng.elementOf(sprites));
		for (int i = 0; i > nrSpritesToAdd; i--) levelState.removeSpriteRandomly(Constants.rng.elementOf(sprites));
	}

	/**
	 * Adds or removes sprites from the level, of the given types, until the total count of the types is equal to
	 * the given count.
	 * @param count The number of sprites should be equal to this amount.
	 * @param sprites The sprite types which should be added or removed.
	 */
	void makeEqual(int count, List<String> sprites) {
		count = countShouldBeMoreThan(count, 0);
		makeEqual(count, sprites, spriteCounter.get(sprites));
	}

	/**
	 * Adds or removes sprites from the level, of the given types, until the total count of the types is equal to
	 * the given count.
	 * @param count The number of sprites should be equal to this amount.
	 * @param sprites The sprite types which should be added or removed.
	 */
	void makeEqual(int count, String... sprites) {
		makeEqual(count, Arrays.asList(sprites));
	}

	/**
	 * Makes sure that the value for count is more than the minimum count.
	 * @param count The count to check.
	 * @param minCount The value count should be above.
	 * @return The new value for count.
	 */
	static int countShouldBeMoreThan(int count, int minCount) {
		if (count >= minCount) return count;

		Constants.warning("Count (%1$d) was less than minCount (%2$d), thus %2$d is the new vale for count", count,
				minCount);
		return minCount;
	}

	/**
	 * Set {@link #tick} to the given value, or returns false if it's not able to do so.
	 * @param tick The required tick value.
	 * @return Whether the tick field was set or not.
	 */
	@SuppressWarnings("PMD.LinguisticNaming")
	boolean setTick(int tick) {
		if (this.tick != null) return false;

		this.tick = tick;
		return true;
	}
}
