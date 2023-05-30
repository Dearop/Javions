package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDescriptionTest {
    @Test
    public void AircraftDescriptionThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("FOPJEWF"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("A3"));

        new AircraftDescription("");
    }
}
