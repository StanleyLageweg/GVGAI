package username;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.assertj.core.api.Assertions.assertThat;

class ConstantsTest {

	private static final String WARNING = "If you let my daughter go now, that'll be the end of it. I will not look "
			+ "for you, I will not pursue you. But if you don't, I will look for you, I will find you, and I will "
			+ "kill you.";

	private static final List<LogRecord> LOG_RECORDS = new TestLogHandler(Constants.LOGGER).getRecords();

	@BeforeEach
	void beforeEach() {
		LOG_RECORDS.clear();
	}

	@Test
	void warningOn() {
		// Turn warnings on
		Constants.LOGGER.setLevel(Level.WARNING);

		// Log a warning
		Constants.warning(WARNING);

		// Assert the warning was logged
		assertThat(LOG_RECORDS).extracting(LogRecord::getLevel).containsExactly(Level.WARNING);
		assertThat(LOG_RECORDS.get(0).getMessage().split("\n\tat " + Thread.currentThread().getStackTrace()[1]
				.toString().replaceAll("[\\W]", "\\\\$0").replaceAll("[0-9]+", "[0-9]+"))[0]).isEqualTo(WARNING);
	}

	@Test
	void warningOff() {
		// Turn warnings off
		Constants.LOGGER.setLevel(Level.OFF);

		// Log a warning
		Constants.warning(WARNING);

		// Assert nothing was logged
		assertThat(LOG_RECORDS).isEmpty();
	}
}
