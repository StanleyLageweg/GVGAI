package username.level;

import lombok.Getter;

/**
 * Class that represents the player avatar in a level state.
 */
public class Avatar {

	/**
	 * The sprite that represents this avatar.
	 */
	@Getter private final String sprite;

	/**
	 * The level state this avatar belongs to.
	 */
	private final LevelTree.LevelState levelState;

	/**
	 * The x coordinate of the avatar.
	 */
	@Getter private int x;

	/**
	 * The y coordinate of the avatar.
	 */
	@Getter private int y;

	/**
	 * Constructs a new Avatar.
	 * @param x The x coordinate of the avatar.
	 * @param y The y coordinate of the avatar.
	 * @param sprite The sprite that represents this avatar.
	 * @param levelState The level state this avatar belongs to.
	 */
	private Avatar(int x, int y, String sprite, LevelTree.LevelState levelState) {
		this.x = x;
		this.y = y;
		this.sprite = sprite;
		this.levelState = levelState;

		levelState.addSprite(x, y, sprite);
	}

	/**
	 * Constructs a new Avatar.
	 * @param levelState the level state this avatar belongs to.
	 */
	protected Avatar(LevelTree.LevelState levelState) {
		this(levelState.getRandomX(1), levelState.getRandomY(1), levelState.getAvatarSprite(), levelState);
	}

	/**
	 * Constructs an Avatar, from another Avatar.
	 * @param other The avatar to copy.
	 * @param levelState The level state this avatar belongs to.
	 */
	protected Avatar(Avatar other, LevelTree.LevelState levelState) {
		this(other.x, other.y, other.sprite, levelState);
	}
}
