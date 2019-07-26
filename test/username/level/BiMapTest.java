package username.level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.data.MapEntry.entry;

class BiMapTest {

	private static final Map<Integer, Integer> MAP = Map.of(42, 21, 1, 2, 69, 69);

	private BiMap<Integer, Integer> biMap;

	@BeforeEach
	void beforeEach() {
		biMap = new BiMap<>();
	}

	@Test
	void empty() {
		assertThat(biMap).isEmpty();
		assertThat(biMap).hasSize(0);
	}

	@Test
	void notEmpty() {
		biMap.put(42, 21);
		assertThat(biMap).isNotEmpty();
		assertThat(biMap).hasSize(1);
	}

	@Test
	void containsKey() {
		biMap.put(42, 21);
		assertThat(biMap).containsOnlyKeys(42);
	}

	@Test
	void containsValue() {
		biMap.put(42, 21);
		assertThat(biMap).containsValue(21);
		assertThat(biMap).doesNotContainKeys(20, 22);
	}

	@Test
	void get() {
		biMap.put(42, 21);
		assertThat(biMap.get(42)).isEqualTo(21);
	}

	@Test
	void getFail() {
		biMap.put(42, 21);
		assertThat(biMap.get(41)).isNull();
	}

	@Test
	void getReverse() {
		biMap.put(42, 21);
		assertThat(biMap.getReverse(21)).isEqualTo(42);
	}

	@Test
	void getReverseFail() {
		biMap.put(42, 21);
		assertThat(biMap.getReverse(22)).isNull();
	}

	@Test
	void put() {
		assertThat(biMap.put(42, 21)).isNull();
		assertThat(biMap.put(42, 22)).isEqualTo(21);
	}

	@Test
	void putNull() {
		assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> biMap.put(null, 42));
		assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> biMap.put(42, null));
		empty();
	}

	@Test
	void remove() {
		biMap.put(42, 21);
		assertThat(biMap.remove(42)).isEqualTo(21);
		empty();
		assertThat(biMap.get(42)).isNull();
		assertThat(biMap.getReverse(42)).isNull();
	}

	@Test
	void removeFail() {
		assertThat(biMap.remove(42)).isNull();
	}

	@Test
	void putAll() {
		biMap.putAll(MAP);
		assertThat(biMap).containsOnly(entry(42, 21), entry(1, 2), entry(69, 69));
	}

	@Test
	void clear() {
		biMap.putAll(MAP);
		biMap.clear();
		assertThat(biMap).isEmpty();
		assertThat(biMap.getMap()).isEmpty();
		assertThat(biMap.getReversedMap()).isEmpty();
	}

	@Test
	void keySet() {
		biMap.putAll(MAP);
		assertThat(biMap.keySet()).containsExactlyInAnyOrder(MAP.keySet().toArray(Integer[]::new));
	}

	@Test
	void values() {
		biMap.putAll(MAP);
		assertThat(biMap.values()).containsExactlyInAnyOrder(MAP.values().toArray(Integer[]::new));
	}

	@Test
	void equals() {
		BiMap<Integer, Integer> biMap2 = new BiMap<>();
		biMap.putAll(MAP);
		biMap2.putAll(MAP);
		assertThat(biMap).isEqualTo(biMap2);
		assertThat(biMap).hasSameHashCodeAs(biMap2);
	}

	@Test
	void notEquals() {
		BiMap<Integer, Integer> biMap2 = new BiMap<>();
		biMap.putAll(MAP);
		biMap2.put(21, 42);
		assertThat(biMap).isNotEqualTo(biMap2);
		assertThat(biMap.hashCode()).isNotEqualTo(biMap2.hashCode());
	}

	@Test
	void getMap() {
		biMap.putAll(MAP);
		assertThat(biMap.getMap()).isEqualTo(MAP);
	}

	@Test
	void getReversedMap() {
		biMap.putAll(MAP);
		assertThat(biMap.getReversedMap()).isEqualTo(MAP.entrySet().stream().collect(Collectors.toMap(
				Map.Entry::getValue, Map.Entry::getKey)));
	}
}
