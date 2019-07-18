package username;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that can keep count of sprites.
 */
public class SpriteCounter {

	/**
	 * HashMap that stores the count for each sprite.
	 */
	private final Map<String, Integer> counter = new ConcurrentHashMap<>();

	/**
	 * Increases the count by one, for each sprite.
	 * @param sprites The sprite to increase the count for.
	 */
	public void increment(List<String> sprites) {
		sprites.forEach(sprite -> counter.put(sprite, counter.getOrDefault(sprite, 0) + 1));
	}

	/**
	 * Increases the count by one, for each sprite.
	 * @param sprites The sprite to increase the count for.
	 */
	public void increment(String... sprites) {
		increment(Arrays.asList(sprites));
	}

	/**
	 * Decreases the count by one, for each sprite.
	 * @param sprites The sprite to decrease the count for.
	 */
	public void decrement(List<String> sprites) {
		sprites.forEach(sprite -> {
			if (!counter.containsKey(sprite)) {
				Constants.warning("Tried to decrement the sprite count of %s, while it was already at 0.", sprite);
				return;
			}

			int count = counter.get(sprite) - 1;
			if (count == 0) {
				counter.remove(sprite);
			} else {
				counter.put(sprite, count);
			}
		});
	}

	/**
	 * Decreases the count by one, for each sprite.
	 * @param sprites The sprite to decrease the count for.
	 */
	public void decrement(String... sprites) {
		decrement(Arrays.asList(sprites));
	}

	/**
	 * Calculates the sum of the counts of the given sprites.
	 * @param sprites The sprites to calculate the sum of counts for.
	 * @return The sum of the counts of the given sprites.
	 */
	public int get(List<String> sprites) {
		return sprites.stream().mapToInt(sprite -> counter.getOrDefault(sprite, 0)).sum();
	}

	/**
	 * Calculates the sum of the counts of the given sprites.
	 * @param sprites The sprites to calculate the sum of counts for.
	 * @return The sum of the counts of the given sprites.
	 */
	public int get(String... sprites) {
		return get(Arrays.asList(sprites));
	}
}
