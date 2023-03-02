package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftTypeDesignatorTest {
    @Test
    public void  AircraftTypeDesignatorTestStringWithNothing(){
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("F/5"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("FSFNAF5"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("a"));
    }
}
