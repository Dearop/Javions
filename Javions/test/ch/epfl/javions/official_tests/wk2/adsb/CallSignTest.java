package ch.epfl.javions.official_tests.wk2.adsb;

import ch.epfl.javions.adsb.CallSign;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CallSignTest {
    @Test
    void callSignConstructorThrowsWithInvalidCallSign() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CallSign("callsign");
        });
    }

    @Test
    void callSignConstructorAcceptsEmptyCallSign() {
        assertDoesNotThrow(() -> {
            new CallSign("");
        });
    }

    @Test
    void callSignConstructorAcceptsValidCallSign() {
        assertDoesNotThrow(() -> {
            new CallSign("AFR39BR");
        });
    }
}