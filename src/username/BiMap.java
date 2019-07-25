package username;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Two-way HashMap.
 * @param <K> The key type.
 * @param <V> The value type.
 */
@SuppressWarnings("SuspiciousMethodCalls")
public class BiMap<K, V> implements Map<K, V> {

	/**
	 * The regular backing HashMap.
	 */
	private final Map<K, V> forwardsMap = new ConcurrentHashMap<>();

	/**
	 * Copy of {@link #forwardsMap}, but with the keys and values flipped, for faster reverse lookups.
	 */
	private final Map<V, K> reversedMap = new ConcurrentHashMap<>();

	@Override
	public int size() {
		return forwardsMap.size();
	}

	@Override
	public boolean isEmpty() {
		return forwardsMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return forwardsMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return reversedMap.containsKey(value);
	}

	@Override
	public V get(Object key) {
		return forwardsMap.get(key);
	}

	/**
	 * Returns the key to which the specified value is mapped.
	 * @param value The value to which a key is mapped.
	 * @return The key to which the specified value is mapped.
	 */
	public K getReverse(Object value) {
		return reversedMap.get(value);
	}

	@Override
	public V put(K key, V value) {
		// Don't support null values
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);

		V oldValue = forwardsMap.put(key, value);
		if (oldValue != null) reversedMap.remove(oldValue);
		reversedMap.put(value, key);
		return oldValue;
	}

	@Override
	public V remove(Object key) {
		V value = forwardsMap.remove(key);
		if (value != null) reversedMap.remove(value);
		return value;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		map.forEach(this::put);
	}

	@Override
	public void clear() {
		forwardsMap.clear();
		reversedMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return forwardsMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return forwardsMap.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return forwardsMap.entrySet();
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object other) {
		return forwardsMap.equals(other);
	}

	@Override
	public int hashCode() {
		return forwardsMap.hashCode();
	}

	/**
	 * Returns {@link #forwardsMap}.
	 * @return {@link #forwardsMap}
	 */
	public Map<K, V> getMap() {
		return new HashMap<>(forwardsMap);
	}

	/**
	 * Returns {@link #reversedMap}.
	 * @return {@link #reversedMap}
	 */
	public Map<V, K> getReversedMap() {
		return new HashMap<>(reversedMap);
	}
}
