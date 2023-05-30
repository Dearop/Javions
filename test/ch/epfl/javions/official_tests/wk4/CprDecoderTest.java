package ch.epfl.javions.official_tests.wk4;

import ch.epfl.javions.adsb.CprDecoder;
import org.junit.jupiter.api.Test;

import static java.lang.Math.scalb;
import static java.lang.Math.toDegrees;
import static org.junit.jupiter.api.Assertions.*;

class CprDecoderTest {
    private static double cpr(final double cpr) {
        return scalb(cpr, -17);
    }

    void checkDecodePosition(final int cprX0,
                             final int cprY0,
                             final int cprX1,
                             final int cprY1,
                             final int mostRecent,
                             final double expectedLonDeg,
                             final double expectedLatDeg,
                             final double delta) {
        final var x0 = CprDecoderTest.cpr(cprX0);
        final var x1 = CprDecoderTest.cpr(cprX1);
        final var y0 = CprDecoderTest.cpr(cprY0);
        final var y1 = CprDecoderTest.cpr(cprY1);
        final var p = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(p);
        assertEquals(expectedLonDeg, toDegrees(p.longitude()), delta);
        assertEquals(expectedLatDeg, toDegrees(p.latitude()), delta);
    }

    @Test
    void cprDecoderDecodePositionWorksOnKnownExamples() {
        // Example given in stage 5
        final var delta = 1.0e-6;
        this.checkDecodePosition(111600, 94445, 108865, 77558, 0, 7.476062, 46.323349, delta);

        // Example from https://mode-s.org/decode/content/ads-b/3-airborne-position.html#decoding-example
        this.checkDecodePosition(0b01100100010101100, 0b10110101101001000, 0b01100010000010010, 0b10010000110101110, 0, 3.919373, 52.257202, delta);

        // Examples from https://github.com/flightaware/dump1090/blob/master/cprtests.c
        this.checkDecodePosition(9432, 80536, 9192, 61720, 0, 0.700156, 51.686646, delta);
        this.checkDecodePosition(9432, 80536, 9192, 61720, 1, 0.701294, 51.686763, delta);
        this.checkDecodePosition(9413, 80534, 9144, 61714, 0, 0.698745, 51.686554, delta);
        this.checkDecodePosition(9413, 80534, 9144, 61714, 1, 0.697632, 51.686484, delta);
    }

    @Test
    void cprDecoderDecodePositionWorksWithOnlyOneLatitudeBand() {
        this.checkDecodePosition(2458, 92843, 2458, 60712, 0, 6.75, 88.25, 1.0e-2);
        this.checkDecodePosition(2458, 92843, 2458, 60712, 1, 6.75, 88.25, 1.0e-2);
    }

    @Test
    void cprDecoderDecodePositionWorksWithPositiveAndNegativeCoordinates() {
        for (var i = 0; 1 >= i; i += 1) {
            this.checkDecodePosition(94663, 43691, 101945, 47332, i, -20.0d, -10.0d, 1.0e-4);
            this.checkDecodePosition(94663, 87381, 101945, 83740, i, -20.0d, 10.0d, 1.0e-4);
            this.checkDecodePosition(36409, 43691, 29127, 47332, i, 20.0d, -10.0d, 1.0e-4);
            this.checkDecodePosition(36409, 87381, 29127, 83740, i, 20.0d, 10.0d, 1.0e-4);
        }
    }

    @Test
    void cprDecoderDecodePositionReturnsNullWhenLatitudeIsInvalid() {
        assertNull(CprDecoder.decodePosition(0, 0, 0, CprDecoderTest.cpr(34776), 0));
        assertNull(CprDecoder.decodePosition(0, 0, 0, CprDecoderTest.cpr(34776), 1));
        assertNull(CprDecoder.decodePosition(0, CprDecoderTest.cpr(5), 0, CprDecoderTest.cpr(66706), 0));
        assertNull(CprDecoder.decodePosition(0, CprDecoderTest.cpr(5), 0, CprDecoderTest.cpr(66706), 1));
    }

    @Test
    void cprDecoderDecodePositionReturnsNullWhenSwitchingLatitudeBands() {
        final var args = new int[][]{
                // Random values
                {43253, 99779, 122033, 118260},
                {67454, 100681, 123802, 124315},
                {129578, 70001, 82905, 105074},
                {30966, 110907, 122716, 79872},
                // Real values
                {85707, 77459, 81435, 60931},
                {100762, 106328, 98304, 89265},
                {104941, 106331, 104905, 89210},
        };

        for (final var as : args) {
            final var x0 = CprDecoderTest.cpr(as[0]);
            final var y0 = CprDecoderTest.cpr(as[1]);
            final var x1 = CprDecoderTest.cpr(as[2]);
            final var y1 = CprDecoderTest.cpr(as[3]);
            assertNull(CprDecoder.decodePosition(x0, y0, x1, y1, 0));
            assertNull(CprDecoder.decodePosition(x0, y0, x1, y1, 1));
        }
    }
}