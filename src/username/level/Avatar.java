package username.level;

import lombok.Getter;
import username.Constants;

/**
 * Class that represents the player avatar in the level.
 */
public class Avatar {

	/**
	 * The sprite that represents this avatar.
	 */
	@Getter private static String sprite;

	/**
	 * The level this avatar belongs to.
	 */
	private Level level;

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
	 * {@link #sprite} has to be set first.
	 * @param x The x coordinate of the avatar.
	 * @param y The y coordinate of the avatar.
	 * @param level The level this avatar belongs to.
	 */
	public Avatar(int x, int y, Level level) {
		if (Avatar.sprite == null) Constants.warning("Avatar sprite was not initialized");
		this.level = level;
		setPosition(x, y);
	}

	/**
	 * Sets the sprite field, only once.
	 * @param sprite The sprite that represents this avatar.
	 */
	public static void setSprite(String sprite) {
		if (Avatar.sprite != null) {
			Constants.warning("Tried to change avatar sprite '%1$s' to '%2$s'.", Avatar.sprite, sprite);
			return;
		}

		Avatar.sprite = sprite;
	}

	/**
	 * Moves the avatar to the new position.
	 * @param x The x coordinate of the avatar.
	 * @param y The y coordinate of the avatar.
	 */
	@SuppressWarnings("checkstyle:hiddenfield")
	public void setPosition(int x, int y) {
		level.moveSprite(this.x, this.y, x, y, sprite);

		this.x = x;
		this.y = y;
	}
}
