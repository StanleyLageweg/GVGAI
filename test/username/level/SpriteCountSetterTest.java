package username.level;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import username.Constants;
import username.TestLogHandler;
import username.random.ExtendedRandom;

import java.util.List;
import java.util.logging.LogRecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class SpriteCountSetterTest {

	private static final String SPRITE = "Mario";

	private static final List<LogRecord> LOG_RECORDS = new TestLogHandler(Constants.LOGGER).getRecords();

	private static final ExtendedRandom RNG = spy(ExtendedRandom.class);

	private Level level;

	private MultiCounter<String> spriteCounter;

	private SpriteCountSetter spriteCountSetter;

	@BeforeAll
	static void beforeAll() {
		Constants.rng = RNG;
	}

	@BeforeEach
	void beforeEach() {
		LOG_RECORDS.clear();
		level = new Level(10, 7);
		spriteCounter = level.getSpriteCounter();
		spriteCountSetter = new SpriteCountSetter(level);
	}

	@Test
	void makeLessThanNoChange() {
		spriteCountSetter.makeLessThan(1, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isZero();
		assertThat(LOG_RECORDS).isEmpty();
	}

	@Test
	void makeLessThanChange() {
		level.addSpriteRandomly(SPRITE);
		spriteCountSetter.makeLessThan(1, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isZero();
	}

	@Test
	void makeLessThanWarning() {
		spriteCountSetter.makeLessThan(0, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isZero();
		assertThat(LOG_RECORDS).extracting(LogRecord::getLevel).containsExactly(java.util.logging.Level.WARNING);
	}


	@Test
	void makeMoreThanNoChange() {
		spriteCountSetter.makeMoreThan(-1, 21, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isZero();
	}

	@Test
	void makeMoreThanChange() {
		spriteCountSetter.makeMoreThan(42, 43, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isEqualTo(43);
	}

	@Test
	void makeMoreThanWarning() {
		spriteCountSetter.makeMoreThan(0, 0, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isOne();
		assertThat(LOG_RECORDS).extracting(LogRecord::getLevel).containsExactly(java.util.logging.Level.WARNING);
	}

	@Test
	void makeNotEqualNoChange() {
		spriteCountSetter.makeNotEqual(1, 21, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isZero();
	}

	@Test
	void makeNotEqualIncrease() {
		spriteCountSetter.makeNotEqual(0, 21, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isBetween(1, 21);
	}

	@Test
	void makeNotEqualDecrease() {
		level.addSpriteRandomly(SPRITE);
		spriteCountSetter.makeNotEqual(1, 1, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isZero();
	}

	@Test
	void makeNotEqualRandomIncrease() {
		level.addSpriteRandomly(SPRITE);

		when(RNG.nextBoolean()).thenReturn(false);
		spriteCountSetter.makeNotEqual(1, 21, SPRITE);

		assertThat(spriteCounter.get(SPRITE)).isBetween(2, 21);
	}

	@Test
	void makeNotEqualRandomDecrease() {
		level.addSpriteRandomly(SPRITE);

		when(RNG.nextBoolean()).thenReturn(true);
		spriteCountSetter.makeNotEqual(1, 21, SPRITE);

		assertThat(spriteCounter.get(SPRITE)).isZero();
	}

	@Test
	void makeNotEqualWarning() {
		spriteCountSetter.makeNotEqual(0, 0, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isOne();
		assertThat(LOG_RECORDS).extracting(LogRecord::getLevel).containsExactly(java.util.logging.Level.WARNING);
	}

	@Test
	void makeEqualNoChange() {
		spriteCountSetter.makeEqual(0, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isZero();
	}

	@Test
	void makeEqualIncrease() {
		spriteCountSetter.makeEqual(12, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isEqualTo(12);
	}

	@Test
	void makeEqualDecrease() {
		level.addSpriteRandomly(SPRITE);
		level.addSpriteRandomly(SPRITE);
		spriteCountSetter.makeEqual(1, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isOne();
	}

	@Test
	void makeEqualWarning() {
		spriteCountSetter.makeEqual(-1, SPRITE);
		assertThat(spriteCounter.get(SPRITE)).isZero();
		assertThat(LOG_RECORDS).extracting(LogRecord::getLevel).containsExactly(java.util.logging.Level.WARNING);
	}
}