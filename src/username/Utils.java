package username;

import java.util.Arrays;
import java.util.List;

/**
 * A utilities class.
 */
final class Utils {

	/**
	 * Hide the constructor.
	 */
	private Utils() {}

	/**
	 * Clamps the value between the given bounds.
	 * @param min The lower bound.
	 * @param val The value which should be clamped.
	 * @param max The upper bound.
	 * @param <T> The type of the value.
	 * @return The clamped value
	 */
	static <T extends Comparable<T>> T clamp(T min, T val, T max) {
		if (val.compareTo(min) < 0) return min;
		if (val.compareTo(max) > 0) return max;
		return val;
	}

	/**
	 * Randomizes a matrix.
	 * @param matrix The matrix to randomize.
	 * @param <T> The type of the matrix.
	 * @return The randomized matrix.
	 */
	@SafeVarargs
	public static <T> T[][] shuffle(T[]... matrix) {
		T[][] result = copyMatrix(matrix);

		// Fisher–Yates shuffle
		for (int y1 = matrix.length - 1; y1 > 0; y1--) {
			for (int x1 = matrix[y1].length - 1; x1 > 0; x1--) {
				int x2 = Constants.rng.nextInt(x1 + 1);
				int y2 = Constants.rng.nextInt(y1 + 1);

				swap(result, x1, y1, x2, y2);
			}
		}

		return result;
	}

	/**
	 * Swaps two coordinates in a matrix.
	 * @param matrix The matrix to swap coordinates in.
	 * @param x1 The x coordinate of the first coordinate.
	 * @param y1 The y coordinate of the first coordinate.
	 * @param x2 The x coordinate of the second coordinate.
	 * @param y2 The y coordinate of the second coordinate.
	 * @param <T> The type of the matrix.
	 */
	public static <T> void swap(T[][] matrix, int x1, int y1, int x2, int y2) {
		T temp = matrix[y1][x1];
		matrix[y1][x1] = matrix[y2][x2];
		matrix[y2][x2] = temp;
	}

	/**
	 * Creates a copy of the given matrix.
	 * @param matrix The matrix to copy.
	 * @param <T> The type of the matrix.
	 * @return A copy of the matrix.
	 */
	@SafeVarargs
	public static <T> T[][] copyMatrix(T[]... matrix) {
		return Arrays.stream(matrix).map(array -> Arrays.copyOf(array, array.length)).toArray(array -> matrix.clone());
	}

	/**
	 * Returns a random element from the list.
	 * @param list The list to get a random element from.
	 * @param <T> The type of the list.
	 * @return A random element from the list.
	 */
	public static <T> T random(List<T> list) {
		return list.get(Constants.rng.nextInt(list.size()));
	}
}
