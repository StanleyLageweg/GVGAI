package username.random;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Adds extra functionality to {@link Random}.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ExtendedRandom extends Random {

	/**
	 * Returns a random number within the bounds.
	 * @param lowerBound The lower bound (inclusive)
	 * @param upperBound The upper bound (exclusive)
	 * @return A random number
	 */
	public int nextInt(int lowerBound, int upperBound) {
		return lowerBound + nextInt(upperBound - lowerBound);
	}

	/**
	 * Returns a random element from the list.
	 * @param list The list to return a random element from.
	 * @param <T> The type the list is storing.
	 * @return A random element from the list.
	 */
	public <T> T elementOf(List<T> list) {
		return list.get(nextInt(list.size()));
	}

	/**
	 * Returns a random element from a finite stream.
	 * @param stream Finite stream to return a random element from.
	 * @param <T> The type of the stream.
	 * @return A random element of the stream.
	 */
	public <T> T elementOf(Stream<T> stream) {
		return elementOf(stream.collect(Collectors.toList()));
	}

	/**
	 * Returns a random element from a char matrix.
	 * @param matrix The matrix to return a random element from.
	 * @return A random element of the matrix.
	 */
	public MatrixSelection<Character> elementOf(char[]... matrix) {
		return elementOf(MatrixSelection.getMatrixSelectionStream(matrix));
	}

	/**
	 * Returns a random element from the list, but only one for which the given predicate holds.
	 * @param list The list to return a random element from.
	 * @param func The predicate that must hold.
	 * @param <T> The type the list is storing.
	 * @return A random element from the list.
	 */
	public <T> T elementOfWhere(List<T> list, Predicate<T> func) {
		return elementOf(list.stream().filter(func));
	}

	/**
	 * Returns a random element from the matrix, but only one for which the given predicate holds.
	 * @param matrix The matrix to return a random element from.
	 * @param func The predicate that must hold.
	 * @return A random element from the matrix.
	 */
	public MatrixSelection<Character> elementOfWhere(char[][] matrix, Predicate<Character> func) {
		return elementOf(MatrixSelection.getMatrixSelectionStream(matrix).filter(selection ->
				func.test(selection.getSelection())));
	}
}

