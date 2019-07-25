package username.random;

import lombok.Getter;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class that stores the element and coordinates of a selection from a matrix.
 * @param <T> The type of the matrix.
 */
public class MatrixSelection<T> {

	/**
	 * The element that was selected.
	 */
	@Getter private T selection;

	/**
	 * The x coordinate of the element that was selected.
	 */
	@Getter private int x;

	/**
	 * The y coordinate of the element that was selected.
	 */
	@Getter private int y;

	/**
	 * Constructs a new MatrixSelection.
	 * @param selection The element that was selected.
	 * @param x The x coordinate of the element that was selected.
	 * @param y The y coordinate of the element that was selected.
	 */
	public MatrixSelection(T selection, int x, int y) {
		this.selection = selection;
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns a stream of MatrixSelections, for the given matrix.
	 * @param matrix The matirx to create a stream for.
	 * @return A stream of MatrixSelections, for the given matrix.
	 */
	protected static Stream<MatrixSelection<Character>> getMatrixSelectionStream(char[]... matrix) {
		return IntStream.range(0, matrix.length).boxed().map(y ->
				IntStream.range(0, matrix[y].length).boxed().map(x ->
						new MatrixSelection<>(matrix[y][x], x, y))).flatMap(Function.identity());
	}
}
