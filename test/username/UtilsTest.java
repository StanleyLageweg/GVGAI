package username;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UtilsTest {

	@Test void clampLow() {
		assertThat(Utils.clamp(42, 21, 69)).isEqualTo(42);
	}

	@Test void clampMid() {
		assertThat(Utils.clamp(21, 42, 69)).isEqualTo(42);
	}

	@Test void clampHigh() {
		assertThat(Utils.clamp(21, 69, 42)).isEqualTo(42);
	}

	@Test void shuffle() {
		// Setup
		Integer[][] matrix = {{1, 2, 3, 4, 5, 6, 7}, {8, 9, 10, 11, 12, 13, 14}, {15, 16, 17, 18, 19, 20, 21}};

		// Test
		Integer[][] result = Utils.shuffle(matrix);

		// Expected
		Integer[][] matrixExpected = {{1, 2, 3, 4, 5, 6, 7}, {8, 9, 10, 11, 12, 13, 14}, {15, 16, 17, 18, 19, 20, 21}};
		Integer[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};

		// Assert matrix has not changed
		assertThat(matrix).isEqualTo(matrixExpected);

		// Assert result has same size and elements
		assertThat(result).hasSize(3);
		Arrays.stream(result).forEach(array -> assertThat(array).hasSize(7));
		assertThat(Arrays.stream(result).flatMap(Stream::of)).containsExactlyInAnyOrder(expected);
	}

	@Test void swap() {
		// Setup
		Integer[][] matrix = {{1, 2, 3, 4, 5, 6, 7}, {8, 9, 10, 11, 12, 13, 14}, {15, 16, 17, 18, 19, 20, 21}};

		// Test
		Utils.swap(matrix, 3, 1, 5, 2);

		// Expected
		Integer[][] expected = {{1, 2, 3, 4, 5, 6, 7}, {8, 9, 10, 20, 12, 13, 14}, {15, 16, 17, 18, 19, 11, 21}};

		// Assert
		assertThat(matrix).isEqualTo(expected);
	}

	@Test void copyMatrix() {
		// Setup
		Integer[][] matrix = {{1, 2, 3, 4, 5, 6, 7}, {8, 9, 10, 11, 12, 13, 14}, {15, 16, 17, 18, 19, 20, 21}};
		Integer[][] matrixCopy = Utils.copyMatrix(matrix);

		// Test
		matrix[2][5] = 42;
		matrixCopy[1][3] = 69;

		// Expected
		Integer[][] expected = {{1, 2, 3, 4, 5, 6, 7}, {8, 9, 10, 11, 12, 13, 14}, {15, 16, 17, 18, 19, 42, 21}};
		Integer[][] expectedCopy = {{1, 2, 3, 4, 5, 6, 7}, {8, 9, 10, 69, 12, 13, 14}, {15, 16, 17, 18, 19, 20, 21}};

		// Assert
		assertThat(matrix).isEqualTo(expected);
		assertThat(matrixCopy).isEqualTo(expectedCopy);
	}
}
