package ch.epfl.javions.aircraft;

import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftRegistration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftRegistrationTest {

    @Test
    public void AircraftRegistrationTest() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("FOPJEWF"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("a"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription(""));
    }
}
