package username;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A class containing a level mapping, which maps characters to lists of sprites.
 */
class LevelMapping {

	/**
	 * Maps characters to multi sprite counter.
	 */
	private final BiMap<Character, MultiCounter<String>> mapping = new BiMap<>();

	/**
	 * The char that gets used, when a new mapping needs to be created.
	 */
	private char mappingChar = 'A';

	/**
	 * @return Copy of {@link #mapping}
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	HashMap<Character, ArrayList<String>> getMapping() {
		return new HashMap<>(mapping.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry ->
				entry.getValue().toList())));
	}

	/**
	 * Gets from the charMapping.
	 * @param character A character.
	 * @return The list of sprites to which the specified character is mapped.
	 */
	List<String> get(char character) {
		return mapping.get(character).toList();
	}

	/**
	 * Maps the given sprites to a char, if it wasn't in a mapping already.
	 * @param sprites Strings representing sprites.
	 * @return The char the sprites got mapped to.
	 */
	char get(final List<String> sprites) {
		MultiCounter<String> spriteCounter = new MultiCounter<>(sprites);

		// If this mapping already exists, return the char
		Character character = mapping.getReverse(spriteCounter);
		if (character != null) return character;

		// Put the new mapping in the map
		mapping.put(mappingChar, spriteCounter);

		// Return and update the mapping character
		char result = mappingChar;
		mappingChar++;
		return result;
	}

	/**
	 * Maps the given sprites to a char, if it wasn't in a mapping already.
	 * @param sprites Strings representing sprites.
	 * @return The char the sprites got mapped to.
	 */
	char get(final String... sprites) {
		return get(Arrays.asList(sprites));
	}

	/**
	 * Given a list of sprites, adds sprites to that list and creates a mapping for that new list,
	 * if there didn't exist one already.
	 * @param sprites A list of Strings representing sprites.
	 * @param extraSprites Strings representing sprites to be added to the mapping.
	 * @return The char the list of sprites got mapped to.
	 */
	char getWith(List<String> sprites, List<String> extraSprites) {
		// Get the new set of sprites
		ArrayList<String> newSprites = new ArrayList<>(sprites);
		newSprites.addAll(extraSprites);

		// Create a mapping for the new set of sprites
		return get(newSprites);
	}

	/**
	 * Given a list of sprites, adds sprites to that list and creates a mapping for that new list,
	 * if there didn't exist one already.
	 * @param sprites A list of Strings representing sprites.
	 * @param extraSprites Strings representing sprites to be added to the mapping.
	 * @return The char the list of sprites got mapped to.
	 */
	char getWith(List<String> sprites, String... extraSprites) {
		return getWith(sprites, Arrays.asList(extraSprites));
	}

	/**
	 * Given a character which is mapped to a list of sprites, adds a sprite to that list and creates a mapping for that
	 * new list, if there didn't exists such a mapping already. If the character is not mapped to a list of sprites,
	 * this method will create a new list with only the given sprite
	 * @param character A character which is mapped to a list of sprites.
	 * @param sprites Strings representing sprites to be added to the mapping.
	 * @return The char the list of sprites got mapped to.
	 */
	char getWith(char character, List<String> sprites) {
		return getWith(mapping.getOrDefault(character, new MultiCounter<>()).toList(), sprites);
	}

	/**
	 * Given a character which is mapped to a list of sprites, adds a sprite to that list and creates a mapping for that
	 * new list, if there didn't exists such a mapping already. If the character is not mapped to a list of sprites,
	 * this method will create a new list with only the given sprite
	 * @param character A character which is mapped to a list of sprites.
	 * @param sprites Strings representing sprites to be added to the mapping.
	 * @return The char the list of sprites got mapped to.
	 */
	char getWith(char character, String... sprites) {
		return getWith(character, Arrays.asList(sprites));
	}

	/**
	 * Given a list of sprites, removes sprites from that lists and creates a mapping for that new list,
	 * if there didn't exist one already.
	 * @param sprites A list of Strings representing sprites.
	 * @param removeSprites Strings representing sprites to be removed from the mapping.
	 * @return The char the list of sprites got mapped to.
	 */
	char getWithout(List<String> sprites, List<String> removeSprites) {
		// Get the new set of sprites
		ArrayList<String> newSprites = new ArrayList<>(sprites);
		newSprites.removeAll(removeSprites);

		// Create a mapping for the new set of sprites
		return get(newSprites);
	}

	/**
	 * Given a list of sprites, removes sprites from that lists and creates a mapping for that new list,
	 * if there didn't exist one already.
	 * @param sprites A list of Strings representing sprites.
	 * @param removeSprites Strings representing sprites to be removed from the mapping.
	 * @return The char the list of sprites got mapped to.
	 */
	char getWithout(List<String> sprites, String... removeSprites) {
		return getWithout(sprites, Arrays.asList(removeSprites));
	}

	/**
	 * Given a character which is mapped to a list of sprites, removes sprites from that list and creates a mapping for
	 * that new list, if there didn't exists such a mapping already. If the character is not mapped to a list of
	 * sprites, this method will create a new list with only the given sprite
	 * @param character A character which is mapped to a list of sprites.
	 * @param removeSprites Strings representing sprites to be removed from the mapping.
	 * @return The char the list of sprites got mapped to.
	 */
	char getWithout(char character, List<String> removeSprites) {
		return getWithout(mapping.getOrDefault(character, new MultiCounter<>()).toList(), removeSprites);
	}

	/**
	 * Given a character which is mapped to a list of sprites, removes sprites from that list and creates a mapping for
	 * that new list, if there didn't exists such a mapping already. If the character is not mapped to a list of
	 * sprites, this method will create a new list with only the given sprite
	 * @param character A character which is mapped to a list of sprites.
	 * @param removeSprites Strings representing sprites to be removed from the mapping.
	 * @return The char the list of sprites got mapped to.
	 */
	char getWithout(char character, String... removeSprites) {
		return getWithout(character, Arrays.asList(removeSprites));
	}

	/**
	 * Applies the given filter to the sprite lists, and returns a list of characters mapped to them.
	 * @param predicate The predicate to be applied to the lists of sprites
	 * @return The list of characters which are mapped to the lists of sprites for which the predicate holds.
	 */
	List<Character> getWhere(Predicate<List<String>> predicate) {
		return mapping.getReversedMap().entrySet().stream().filter(entry -> predicate.test(entry.getKey().toList()))
				.map(Map.Entry::getValue).collect(Collectors.toList());
	}

	/**
	 * Returns the lists of characters which are mapped to the lists of sprites which contain any of the given sprites.
	 * @param sprites The sprites of which any should be in the mapped list of sprites.
	 * @return The lists of characters which are mapped to the lists of sprites which contain any of the given sprites.
	 */
	List<Character> getContainingAny(List<String> sprites) {
		return getWhere(spriteList -> sprites.stream().anyMatch(spriteList::contains));
	}

	/**
	 * Returns the lists of characters which are mapped to the lists of sprites which contain any of the given sprites.
	 * @param sprites The sprites of which any should be in the mapped list of sprites.
	 * @return The lists of characters which are mapped to the lists of sprites which contain any of the given sprites.
	 */
	List<Character> getContainingAny(String... sprites) {
		return getContainingAny(Arrays.asList(sprites));
	}

	/**
	 * Returns the lists of characters which are mapped to the lists of sprites which contain all of the given sprites.
	 * @param sprites The sprites of which all should be in the mapped list of sprites.
	 * @return The lists of characters which are mapped to the lists of sprites which contain all of the given sprites.
	 */
	List<Character> getContainingAll(List<String> sprites) {
		return getWhere(spriteList -> spriteList.containsAll(sprites));
	}

	/**
	 * Returns the lists of characters which are mapped to the lists of sprites which contain all of the given sprites.
	 * @param sprites The sprites of which all should be in the mapped list of sprites.
	 * @return The lists of characters which are mapped to the lists of sprites which contain all of the given sprites.
	 */
	List<Character> getContainingAll(String... sprites) {
		return getContainingAll(Arrays.asList(sprites));
	}
}
