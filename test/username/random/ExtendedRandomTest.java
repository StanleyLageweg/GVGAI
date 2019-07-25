package username.random;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtendedRandomTest {

	private static final ExtendedRandom RNG = new ExtendedRandom();

	private static final List<Integer> LIST = List.of(21, 42, 69);

	@Test
	void nextInt() {
		assertThat(RNG.nextInt(42, 43)).isEqualTo(42);
	}

	@Test
	void nextIntRange() {
		assertThat(RNG.nextInt(21, 42)).isBetween(21, 41);
	}

	@Test
	void elementOfListOneElement() {
		assertThat(RNG.elementOf(List.of(21))).isEqualTo(21);
	}

	@Test
	void elementOfList() {
		assertThat(RNG.elementOf(LIST)).isIn(LIST);
	}

	@Test
	void elementOfStreamOneElement() {
		assertThat(RNG.elementOf(List.of(21).stream())).isEqualTo(21);
	}

	@Test
	void elementOfStream() {
		assertThat(RNG.elementOf(LIST.stream())).isIn(LIST);
	}

	@Test
	void elementOfMatrixOneElement() {
		char[][] matrix = {{'a'}};
		assertThat(RNG.elementOf(matrix)).isEqualToComparingFieldByField(new MatrixSelection<>('a', 0, 0));
	}

	@Test
	void elementOfMatrix() {
		char[][] matrix = {{'a', 'b', 'c'}, {'d', 'e'}};
		assertThat(RNG.elementOf(matrix)).extracting(MatrixSelection::getSelection).isIn('a', 'b', 'c', 'd', 'e');
	}

	@Test
	void elementOfWhereList() {
		assertThat(RNG.elementOfWhere(LIST, element -> element == 42)).isEqualTo(42);
	}

	@Test
	void elementOfWhereMatrix() {
		char[][] matrix = {{'a', 'b', 'c'}, {'d', 'e'}};
		assertThat(RNG.elementOfWhere(matrix, element -> element == 'd')).isEqualToComparingFieldByField(
				new MatrixSelection<>('d', 0, 1));
	}
}
