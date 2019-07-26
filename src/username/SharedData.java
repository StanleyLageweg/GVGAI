package username;

import core.game.GameDescription;
import tools.ElapsedCpuTimer;
import tools.GameAnalyzer;

/**
 * Class that stores shared data, which are initialized at runtime.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public final class SharedData {

	/**
	 * An abstract class encapsulating all the data required for generating and testing game levels.
	 */
	public static GameDescription gameDescription;

	/**
	 * Analyzes the {@link #gameDescription} and supplies extra information.
	 */
	public static GameAnalyzer gameAnalyzer;

	/**
	 * Count down until level generation is due.
	 */
	public static ElapsedCpuTimer elapsedCpuTimer;

	/**
	 * Hide constructor.
	 */
	private SharedData() { }
}
