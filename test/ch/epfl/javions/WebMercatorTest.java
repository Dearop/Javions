package ch.epfl.javions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebMercatorTest {

    private static final double DELTA = 1.0e-7;

    @Test
    void webMercatorXWorksOnKnownValues() {
        final var actual1 = WebMercator.x(1, -3.141592653589793);
        final var expected1 = 0.0;
        assertEquals(expected1, actual1, WebMercatorTest.DELTA);

        final var actual2 = WebMercator.x(2, -1.5707963267948966);
        final var expected2 = 256.0;
        assertEquals(expected2, actual2, WebMercatorTest.DELTA);

        final var actual3 = WebMercator.x(3, -0.7853981633974483);
        final var expected3 = 768.0;
        assertEquals(expected3, actual3, WebMercatorTest.DELTA);

        final var actual4 = WebMercator.x(4, 0.0);
        final var expected4 = 2048.0;
        assertEquals(expected4, actual4, WebMercatorTest.DELTA);

        final var actual5 = WebMercator.x(5, 0.7853981633974483);
        final var expected5 = 5120.0;
        assertEquals(expected5, actual5, WebMercatorTest.DELTA);

        final var actual6 = WebMercator.x(6, 1.5707963267948966);
        final var expected6 = 12288.0;
        assertEquals(expected6, actual6, WebMercatorTest.DELTA);

        final var actual7 = WebMercator.x(7, 3.141592653589793);
        final var expected7 = 32768.0;
        assertEquals(expected7, actual7, WebMercatorTest.DELTA);

        final var actual8 = WebMercator.x(8, 0.21547136813421194);
        final var expected8 = 35015.44789333333;
        assertEquals(expected8, actual8, WebMercatorTest.DELTA);
    }

    @Test
    void webMercatorYWorksOnKnownValues() {
        final var actual1 = WebMercator.y(1, -1.4835298641951802);
        final var expected1 = 511.16138762953835;
        assertEquals(expected1, actual1, WebMercatorTest.DELTA);

        final var actual2 = WebMercator.y(2, -0.7853981633974483);
        final var expected2 = 655.6415621988301;
        assertEquals(expected2, actual2, WebMercatorTest.DELTA);

        final var actual3 = WebMercator.y(3, 0.0);
        final var expected3 = 1024.0;
        assertEquals(expected3, actual3, WebMercatorTest.DELTA);

        final var actual4 = WebMercator.y(4, 0.7853981633974483);
        final var expected4 = 1473.4337512046795;
        assertEquals(expected4, actual4, WebMercatorTest.DELTA);

        final var actual5 = WebMercator.y(5, 1.4835298641951802);
        final var expected5 = 13.417797927355878;
        assertEquals(expected5, actual5, WebMercatorTest.DELTA);

        final var actual6 = WebMercator.y(6, 0.21547136813421194);
        final var expected6 = 7625.739193000258;
        assertEquals(expected6, actual6, WebMercatorTest.DELTA);
    }
}