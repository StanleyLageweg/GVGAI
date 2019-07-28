package username.level;

import username.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A class that can keep count of elements.
 * @param <T> The type of the element to count.
 */
class MultiCounter<T> {

	/**
	 * HashMap that stores the count for each element.
	 */
	private final Map<T, Integer> counter;

	/**
	 * Constructs a new MultiCounter.
	 */
	MultiCounter() {
		counter = new ConcurrentHashMap<>();
	}

	/**
	 * Constructs a new MultiCounter, from another MultiCounter.
	 * @param other MultiCounter to create a copy of.
	 */
	MultiCounter(MultiCounter<T> other) {
		counter = new ConcurrentHashMap<>(other.counter);
	}

	/**
	 * Constructs a new MultiCounter, from a list of elements.
	 * @param elements Elements to fill the multiCounter with.
	 */
	MultiCounter(List<T> elements) {
		this();
		increment(elements);
	}

	/**
	 * Constructs a new MultiCounter, from an array of elements.
	 * @param elements Elements to fill the multiCounter with.
	 */
	@SafeVarargs
	MultiCounter(T... elements) {
		this();
		increment(elements);
	}

	/**
	 * Increases the count by one, for each element.
	 * @param elements The element to increase the count for.
	 */
	void increment(List<T> elements) {
		elements.forEach(element -> counter.put(element, counter.getOrDefault(element, 0) + 1));
	}

	/**
	 * Increases the count by one, for each element.
	 * @param elements The element to increase the count for.
	 */
	@SafeVarargs
	final void increment(T... elements) {
		increment(Arrays.asList(elements));
	}

	/**
	 * Decreases the count by one, for each element.
	 * @param elements The element to decrease the count for.
	 */
	void decrement(List<T> elements) {

		elements.forEach(element -> {
			if (!counter.containsKey(element)) {
				Constants.warning("Tried to decrement the element count of %s, while it was already at 0.", element);
				return;
			}

			int count = counter.get(element) - 1;
			if (count == 0) {
				counter.remove(element);
			} else {
				counter.put(element, count);
			}
		});
	}

	/**
	 * Decreases the count by one, for each element.
	 * @param elements The element to decrease the count for.
	 */
	@SafeVarargs
	final void decrement(T... elements) {
		decrement(Arrays.asList(elements));
	}

	/**
	 * Calculates the sum of the counts of the given elements.
	 * @param elements The elements to calculate the sum of counts for.
	 * @return The sum of the counts of the given elements.
	 */
	int get(List<T> elements) {
		return elements.stream().mapToInt(element -> counter.getOrDefault(element, 0)).sum();
	}

	/**
	 * Calculates the sum of the counts of the given elements.
	 * @param elements The elements to calculate the sum of counts for.
	 * @return The sum of the counts of the given elements.
	 */
	@SafeVarargs
	final int get(T... elements) {
		return get(Arrays.asList(elements));
	}

	/**
	 * Converts the multi counter to an ArrayList, where it contains each element equal to it's count.
	 * @return An ArrayList representation of the counter.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	ArrayList<T> toList() {
		return counter.entrySet().stream().map(entry -> Collections.nCopies(entry.getValue(), entry.getKey())).flatMap(
				Collection::stream).collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null) return false;
		if (getClass() != other.getClass()) return false;
		return counter.equals(((MultiCounter<?>) other).counter);
	}

	@Override
	public int hashCode() {
		return counter.hashCode();
	}
}
