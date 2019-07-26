package username.level;

import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class LevelMappingTest {

	private LevelMapping levelMapping;

	private String[] inputArray;

	private List<String> inputList;

	private String[] expectedArray;

	private List<String> expectedList;

	@BeforeEach void beforeEach() {
		levelMapping = new LevelMapping();
		inputArray = new String[]{"s1", "s2", "s3"};
		inputList = new ArrayList<>(Arrays.asList(inputArray));
		expectedArray = Arrays.copyOf(inputArray, inputArray.length);
		expectedList = new ArrayList<>(Arrays.asList(expectedArray));
	}

	@Test void empty() {
		assertThat(levelMapping.getMapping()).isEmpty();
	}

	@Test void putOne() {
		putAndAssert('A', inputArray);

		assertThat(levelMapping.getMapping()).satisfies(mapConsumer(entry('A', expectedList)));
	}

	@Test void getDifferentOrder() {
		String[] spritesQuery = {"s3", "s2", "s1"};

		putAndAssert('A', inputArray);

		assertThat(levelMapping.get(spritesQuery)).isEqualTo('A');
	}

	@Test void putSameTwice() {
		String[] inputArray2 = Arrays.copyOf(inputArray, inputArray.length);

		putAndAssert('A', inputArray);
		putAndAssert('A', inputArray2);

		assertThat(levelMapping.getMapping()).as("If we try to put the same list of sprites in the map twice,"
				+ "it should only be added once.").satisfies(mapConsumer(entry('A', expectedList)));
	}

	@Test void putThree() {
		String[] inputArray2 = {"s1", "s2", "s3", "s4"};
		String[] inputArray3 = {"s1", "s2", "s3", "s5"};

		putAndAssert('A', inputArray);
		putAndAssert('B', inputArray2);
		putAndAssert('C', inputArray3);

		assertThat(levelMapping.getMapping()).hasSize(3);
	}

	@Test void getWithList() {
		ArrayList<String> inputList2 = new ArrayList<>(inputList);
		ArrayList<String> expectedList2 = new ArrayList<>(List.of("s1", "s2", "s3", "s4"));

		putAndAssert(inputList, 'A');
		assertThat(levelMapping.getWith(inputList2, "s4")).isEqualTo('B');

		assertThat(levelMapping.getMapping()).as("If we expand the mapping of a list of sprites,"
						+ "a new list of sprites (with the added sprite) should be added.")
				.satisfies(mapConsumer(entry('A', expectedList), entry('B', expectedList2)));
	}

	@Test void getWithChar() {
		ArrayList<String> expectedList2 = new ArrayList<>(List.of("s1", "s2", "s3", "s4"));

		putAndAssert('A', inputArray);
		assertThat(levelMapping.getWith('A', "s4")).isEqualTo('B');

		assertThat(levelMapping.getMapping()).satisfies(mapConsumer(
				entry('A', expectedList), entry('B', expectedList2)));
	}

	@Test void getWithCharNotFound() {
		ArrayList<String> expectedList2 = new ArrayList<>(List.of("s4"));

		putAndAssert('A', inputArray);
		assertThat(levelMapping.getWith('Z', "s4")).isEqualTo('B');

		assertThat(levelMapping.getMapping()).as("If the given char does not map to a list,"
				+ "a new list should be created.").satisfies(mapConsumer(entry('A', expectedList),
				entry('B', expectedList2)));
	}

	@Test void getWithoutList() {
		ArrayList<String> inputList2 = new ArrayList<>(inputList);
		ArrayList<String> expectedList2 = new ArrayList<>(List.of("s1", "s2"));

		putAndAssert(inputList, 'A');
		assertThat(levelMapping.getWithout(inputList2, "s3")).isEqualTo('B');

		assertThat(levelMapping.getMapping()).as("If we expand the mapping of a list of sprites,"
				+ "a new list of sprites (with the added sprite) should be added.").satisfies(mapConsumer(
				entry('A', expectedList), entry('B', expectedList2)));
	}

	@Test void getWithoutChar() {
		ArrayList<String> expectedList2 = new ArrayList<>(List.of("s1", "s2"));

		putAndAssert('A', inputArray);
		assertThat(levelMapping.getWithout('A', "s3")).isEqualTo('B');

		assertThat(levelMapping.getMapping()).satisfies(mapConsumer(
				entry('A', expectedList), entry('B', expectedList2)));
	}

	@Test void getWithoutCharNotFound() {
		ArrayList<String> expectedList2 = new ArrayList<>();

		putAndAssert('A', inputArray);
		assertThat(levelMapping.getWithout('Z', "s3")).isEqualTo('B');

		assertThat(levelMapping.getMapping()).as("If the given char does not map to a list,"
				+ "a new list should be created.").satisfies(mapConsumer(entry('A', expectedList),
				entry('B', expectedList2)));
	}

	@Test void modifyInputList() {
		putAndAssert(inputList, 'A');
		inputList.add("s4");

		assertThat(levelMapping.get('A')).containsExactlyInAnyOrder(expectedArray);
		assertThat(levelMapping.get(expectedList)).isEqualTo('A');
	}

	@Test void getContainingAnyEmpty() {
		putAndAssert(inputList, 'A');

		assertThat(levelMapping.getContainingAny("s4", "s5")).isEmpty();
	}

	@Test void getContainingAny() {
		putAndAssert(inputList, 'A');

		assertThat(levelMapping.getContainingAny("s2")).containsExactly('A');
	}

	@Test void getContainingAllEmpty() {
		putAndAssert(inputList, 'A');

		assertThat(levelMapping.getContainingAll("s1", "s2", "s3", "s4")).isEmpty();
	}

	@Test void getContainingAll() {
		putAndAssert(inputList, 'A');

		assertThat(levelMapping.getContainingAll("s1", "s2", "s3")).containsExactly('A');
	}

	@SafeVarargs
	private Consumer<Map<Character, ArrayList<String>>> mapConsumer(MapEntry<Character, List<String>>... entries) {
		return map -> assertThat(map).hasSameSizeAs(entries).allSatisfy((character, sprites) ->
				assertThat(entry(character, sprites)).matches(actual -> Arrays.stream(entries).anyMatch(expected ->
						expected.getKey().equals(actual.getKey())
								&& expected.getValue().size() == actual.getValue().size()
								&& expected.getValue().containsAll(actual.getValue()))));
	}

	private void putAndAssert(List<String> sprites, char expectedChar) {
		String[] spritesExpected = sprites.toArray(new String[0]);

		assertThat(levelMapping.get(sprites)).isEqualTo(expectedChar);
		assertThat(levelMapping.get(expectedChar)).containsExactlyInAnyOrder(spritesExpected);
		assertThat(levelMapping.get(new ArrayList<>(Arrays.asList(spritesExpected)))).isEqualTo(expectedChar);
	}

	private void putAndAssert(char expectedChar, String... sprites) {
		String[] spritesExpected = new String[sprites.length];
		System.arraycopy(sprites, 0, spritesExpected, 0, sprites.length);

		assertThat(levelMapping.get(sprites)).isEqualTo(expectedChar);
		assertThat(levelMapping.get(expectedChar)).containsExactlyInAnyOrder(spritesExpected);
		assertThat(levelMapping.get(spritesExpected)).isEqualTo(expectedChar);
	}
}
