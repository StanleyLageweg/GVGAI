package username;

import username.random.ExtendedRandom;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class containing constants that are used by the level generator.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public final class Constants {

	/**
	 * The minimum width of the level, excluding its border.
	 */
	public static final int MIN_WIDTH = 6;

	/**
	 * The maximum width of the level, excluding its border.
	 */
	public static final int MAX_WIDTH = 18;

	/**
	 * How much the width of the level can randomly change, as a multiplier.
	 */
	public static final double RANDOM_WIDTH = 0.25;

	/**
	 * The minimum height of the level, excluding its border.
	 */
	public static final int MIN_HEIGHT = 6;

	/**
	 * The maximum height of the level, excluding its border.
	 */
	public static final int MAX_HEIGHT = 18;

	/**
	 * How much the height of the level can randomly change, as a multiplier.
	 */
	public static final double RANDOM_HEIGHT = 0.25;

	/**
	 * Random number generator.
	 */
	public static ExtendedRandom rng = new ExtendedRandom();

	/**
	 * Logger.
	 */
	public static final Logger LOGGER = Logger.getLogger(Constants.class.getName());

	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s");
	}

	/**
	 * Hide the constructor.
	 */
	private Constants() {}

	/**
	 * Logs a warning, with a stack trace.
	 * @param  message
	 *         A <a href="../util/Formatter.html#syntax">format message string</a>
	 *
	 * @param  args
	 *         Arguments referenced by the format specifiers in the format
	 *         string.  If there are more arguments than format specifiers, the
	 *         extra arguments are ignored.  The number of arguments is
	 *         variable and may be zero.  The maximum number of arguments is
	 *         limited by the maximum dimension of a Java array as defined by
	 *         <cite>The Java&trade; Virtual Machine Specification</cite>.
	 *         The behaviour on a
	 *         {@code null} argument depends on the <a
	 *         href="../util/Formatter.html#syntax">conversion</a>.
	 */
	public static void warning(String message, Object... args) {
		if (LOGGER.isLoggable(Level.WARNING)) {
			// Get the stack trace
			StringWriter stringWriter = new StringWriter();
			new Throwable().printStackTrace(new PrintWriter(stringWriter));
			String stackTrace = stringWriter.toString();
			stackTrace = stackTrace.substring(stackTrace.indexOf('\n', stackTrace.indexOf('\n') + 1));

			// Log the message and stack trace
			LOGGER.warning(String.format(message, args) + stackTrace);
		}
	}
}
