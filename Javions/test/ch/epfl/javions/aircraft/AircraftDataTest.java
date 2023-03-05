package ch.epfl.javions.aircraft;
import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AircraftDataTest {
    @Test
    public void AircraftDataThrowsNullExceptionWithNull(){
        assertThrows(NullPointerException.class, () ->new AircraftData(null,
                new AircraftTypeDesignator("A2"), "A320", new AircraftDescription("A0J"),
                WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class, () ->new AircraftData(new AircraftRegistration("H"),
                null, "A320", new AircraftDescription("A0J"),
                WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class, () -> new AircraftData(new AircraftRegistration("H"),
                new AircraftTypeDesignator("A2"), null, new AircraftDescription("A0J"),
                WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class, () -> new AircraftData(new AircraftRegistration("H"),
                new AircraftTypeDesignator("A2"), "A320", null, WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class, () -> new AircraftData(new AircraftRegistration("H"),
                new AircraftTypeDesignator("A2"), "A320", new AircraftDescription("A0J"),
                null));
    }
}
