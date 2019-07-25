package username;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.assertj.core.api.Assertions.assertThat;

class MultiCounterTest {

	private static final String SPRITE = "sprite";

	private static final List<LogRecord> LOG_RECORDS = new TestLogHandler(Constants.LOGGER).getRecords();

	private MultiCounter<String> spriteCounter;

	@BeforeEach
	void beforeEach() {
		spriteCounter = new MultiCounter<>();
		LOG_RECORDS.clear();
	}

	@Test
	void zero() {
		assertThat(spriteCounter.get(SPRITE)).isZero();
	}

	@Test
	void increment() {
		// Increment
		spriteCounter.increment(SPRITE);

		// Assert count changed
		assertThat(spriteCounter.get(SPRITE)).isOne();
	}

	@Test
	void increment3Times() {
		// Increment 3 times
		spriteCounter.increment(SPRITE, SPRITE, SPRITE);

		// Assert count changed
		assertThat(spriteCounter.get(SPRITE)).isEqualTo(3);
	}

	@Test
	void decrementZero() {
		// Decrement
		spriteCounter.decrement(SPRITE);

		// Assert count did not change
		assertThat(spriteCounter.get(SPRITE)).isZero();

		// Assert one warning was logged
		assertThat(LOG_RECORDS).extracting(LogRecord::getLevel).containsExactly(Level.WARNING);
	}

	@Test
	void decrementOne() {
		// Increment
		spriteCounter.increment(SPRITE);

		// Decrement
		spriteCounter.decrement(SPRITE);

		// Assert count changed
		assertThat(spriteCounter.get(SPRITE)).isZero();
	}

	@Test
	void decrementTwo() {
		// Increment 2 times
		spriteCounter.increment(SPRITE, SPRITE);

		// Decrement
		spriteCounter.decrement(SPRITE);

		// Assert count changed
		assertThat(spriteCounter.get(SPRITE)).isOne();
	}

	@Test
	void toList() {
		// Increment
		spriteCounter.increment(SPRITE, SPRITE, "Another Sprite");

		// Assert
		assertThat(spriteCounter.toList()).containsExactly(SPRITE, SPRITE, "Another Sprite");
	}

	@Test
	void equals() {
		MultiCounter<String> other = new MultiCounter<>(SPRITE);

		spriteCounter.increment(SPRITE);
		assertThat(spriteCounter).isEqualTo(other);
		assertThat(spriteCounter).hasSameHashCodeAs(other);
	}

	@Test
	void equalsSame() {
		assertThat(spriteCounter).isEqualTo(spriteCounter);
		assertThat(spriteCounter).hasSameHashCodeAs(spriteCounter);
	}

	@Test
	void notEquals() {
		spriteCounter.increment(SPRITE);
		MultiCounter<String> other = new MultiCounter<>(SPRITE, SPRITE);
		assertThat(spriteCounter).isNotEqualTo(other);
		assertThat(spriteCounter.hashCode()).isNotEqualTo(other);
	}

	@Test
	void notEqualsNull() {
		assertThat(spriteCounter).isNotEqualTo(null);
	}

	@Test
	void notEqualsType() {
		assertThat(spriteCounter).isNotEqualTo(42);
	}

	@Test
	void notEqualsGenericType() {
		spriteCounter.increment(SPRITE);
		assertThat(spriteCounter).isNotEqualTo(new MultiCounter<>(42));
	}
}
