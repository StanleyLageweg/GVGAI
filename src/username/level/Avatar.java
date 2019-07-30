package username.level;

import lombok.Getter;
import ontology.Types;
import username.Constants;

import java.util.List;

import static username.SharedData.gameDescription;

public class Avatar {

	/**
	 * The sprite that represents this avatar.
	 */
	@Getter private final String sprite;

	/**
	 * The actions which the avatar can perform.
	 */
	@Getter private final List<Types.ACTIONS> actions = gameDescription.getAvailableActions(true);

	/**
	 * Constructs a new Avatar.
	 * @param sprite The sprite which represents this avatar.
	 */
	Avatar(String sprite) {
		this.sprite = sprite;
	}

	/**
	 * @param levelState The LevelState this AvatarState belongs to.
	 * @return A new AvatarState
	 */
	AvatarState getNewState(LevelTree.LevelState levelState) {
		return new AvatarState(levelState.getRandomX(1), levelState.getRandomY(1), levelState);
	}


	public class AvatarState {

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
		 * Constructs a new AvatarState, and spawns it randomly in the LevelState.
		 * @param levelState The level state this avatar belongs to.
		 */
		private AvatarState(LevelTree.LevelState levelState) {
			this(levelState.getRandomX(1), levelState.getRandomY(1), levelState);

			levelState.addSprite(x, y, sprite);
		}

		/**
		 * Constructs a new Avatar state.
		 * @param x The x coordinate of the avatar.
		 * @param y The y coordinate of the avatar.
		 * @param levelState The level state this avatar belongs to.
		 */
		private AvatarState(int x, int y, LevelTree.LevelState levelState) {
			this.x = x;
			this.y = y;
			this.levelState = levelState;
		}

		/**
		 * Copies this AvatarState into another LevelState.
		 * @param levelState The level state the copied avatar belongs to.
		 * @return A copy of this avatar state.
		 */
		AvatarState copy(LevelTree.LevelState levelState) {
			return new AvatarState(x, y, levelState);
		}

		void doAction(Types.ACTIONS action) {
			if (!actions.contains(action)) {
				Constants.warning("Tried to perform unavailable action");
				return;
			}

			int xOld = x;
			int yOld = y;

			switch (action) {
				case ACTION_NIL:
					// No implementation necessary
					return;
				case ACTION_UP:
					y--;
					break;
				case ACTION_DOWN:
					y++;
					break;
				case ACTION_LEFT:
					x--;
					break;
				case ACTION_RIGHT:
					x++;
					break;
				case ACTION_USE:
					// TODO
					return;
				case ACTION_ESCAPE:
					// TODO
					return;
				default:
					Constants.warning("Undefined action type &s", action.name());
					return;
			}

			levelState.moveSpriteWithCollisions(xOld, yOld, x, y, sprite);
		}
	}
}
