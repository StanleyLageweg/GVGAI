package username;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A handler class that stores all log records in a list.
 */
public class TestLogHandler extends Handler {

	/**
	 * The list of log records, created by the logger.
	 */
	@Getter private final List<LogRecord> records = new ArrayList<>();

	/**
	 * Modifies the logger to log all levels, but only to this handler.
	 * @param logger The logger which should be handled by this handler.
	 */
	TestLogHandler(Logger logger) {
		// Make the logger only use this handler
		logger.setUseParentHandlers(false);
		Arrays.stream(logger.getHandlers()).forEach(logger::removeHandler);
		logger.addHandler(this);

		// Handle all log levels
		logger.setLevel(Level.ALL);
		setLevel(Level.ALL);
	}

	/**
	 * Stores the given record in a list.
	 * @param record The record to be stored.
	 */
	@Override
	public void publish(LogRecord record) {
		records.add(record);
	}

	@Override
	public void flush() { }

	@Override
	public void close() throws SecurityException { }
}
