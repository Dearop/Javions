package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoPosTest {
    private static final double DELTA = 1.0e-7;

    @Test
    void geoPosIsValidLatitudeT32WorksOnPowersOfTwo() {
        for (var i = 0; 30 >= i; i += 1) {
            final var twoToTheI = 1 << i;
            assertTrue(GeoPos.isValidLatitudeT32(twoToTheI));
            assertTrue(GeoPos.isValidLatitudeT32(-twoToTheI));
        }
        assertFalse(GeoPos.isValidLatitudeT32(1 << 31));
        assertFalse(GeoPos.isValidLatitudeT32(-(1 << 31)));

        assertFalse(GeoPos.isValidLatitudeT32((1 << 30) + 1));
        assertFalse(GeoPos.isValidLatitudeT32(-((1 << 30) + 1)));
    }

    @Test
    void geoPosWorksWithMinMaxLonLat() {
        final var min = new GeoPos(Integer.MIN_VALUE, -(1 << 30));
        assertEquals(-180, Math.toDegrees(min.longitude()));
        assertEquals(-90, Math.toDegrees(min.latitude()));

        final var max = new GeoPos(Integer.MAX_VALUE, 1 << 30);
        assertEquals(180, Math.toDegrees(max.longitude()), 1.0e-5);
        assertEquals(90, Math.toDegrees(max.latitude()));
    }

    @Test
    void geoPosConstructorThrowsOnInvalidLatitude() {
        assertThrows(IllegalArgumentException.class, () -> new GeoPos(0, 1 << 31));
        assertThrows(IllegalArgumentException.class, () -> new GeoPos(0, -(1 << 31)));
    }

    @Test
    void geoPosLongitudeLatitudeReturnsValuesInRadians() {
        final var halfTurnT32 = 1L << 31;
        final var halfTurnRad = Math.PI;

        for (int i = 1; 16 > i; i += 1) {
            final var t32 = ((int) (halfTurnT32 >> i));
            final var rad = Math.scalb(halfTurnRad, -i);
            final var geoPos = new GeoPos(t32, t32);
            assertEquals(rad, geoPos.longitude(), GeoPosTest.DELTA);
            assertEquals(rad, geoPos.latitude(), GeoPosTest.DELTA);
        }
    }

    @Test
    void geoPosToStringReturnsValuesInDegree() {
        final var quarterTurnT32 = 1 << 30;
        final var geoPos = new GeoPos(quarterTurnT32, quarterTurnT32);
        assertEquals("(90.0\u00B0, 90.0\u00B0)", geoPos.toString());
    }
}