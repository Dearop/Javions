package ch.epfl.javions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class Math2Test {
    private static final double DELTA = 1.0e-7;


    @Test
    void math2ClampClampsValueBelowMin() {
        final var rng = newRandom();
        for (int i = 0; RANDOM_ITERATIONS > i; i += 1) {
            final var min = rng.nextInt(-100_000, 100_000);
            final var max = min + rng.nextInt(100_000);
            final var v = min - rng.nextInt(500);
            assertEquals(min, Math2.clamp(min, v, max));
        }
    }

    @Test
    void math2ClampClampsValueAboveMax() {
        final var rng = newRandom();
        for (int i = 0; RANDOM_ITERATIONS > i; i += 1) {
            final var min = rng.nextInt(-100_000, 100_000);
            final var max = min + rng.nextInt(100_000);
            final var v = max + rng.nextInt(500);
            assertEquals(max, Math2.clamp(min, v, max));
        }
    }

    @Test
    void math2ClampPreservesValuesBetweenMinAndMax() {
        final var rng = newRandom();
        for (int i = 0; RANDOM_ITERATIONS > i; i += 1) {
            final var min = rng.nextInt(-100_000, 100_000);
            final var v = min + rng.nextInt(100_000);
            final var max = v + rng.nextInt(100_000);
            assertEquals(v, Math2.clamp(min, v, max));
        }
    }

    @Test
    void math2AsinhWorksOnKnownValues() {
        final var actual1 = Math2.asinh(Math.PI);
        final var expected1 = 1.8622957433108482;
        assertEquals(expected1, actual1, Math2Test.DELTA);

        final var actual2 = Math2.asinh(Math.E);
        final var expected2 = 1.7253825588523148;
        assertEquals(expected2, actual2, Math2Test.DELTA);

        final var actual3 = Math2.asinh(2022);
        final var expected3 = 8.304989641287715;
        assertEquals(expected3, actual3, Math2Test.DELTA);

        final var actual4 = Math2.asinh(-2022);
        final var expected4 = -8.304989641057409;
        assertEquals(expected4, actual4, Math2Test.DELTA);

        final var actual5 = Math2.asinh(-1.23456);
        final var expected5 = -1.0379112743027366;
        assertEquals(expected5, actual5, Math2Test.DELTA);
    }
}