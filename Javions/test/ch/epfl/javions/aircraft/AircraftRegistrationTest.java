package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftRegistrationTest {

    @Test
    public void AircraftRegistrationTest() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("[OPJEWF"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("a"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
    }
}
