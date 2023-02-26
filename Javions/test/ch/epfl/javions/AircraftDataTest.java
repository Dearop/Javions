package ch.epfl.javions;
import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AircraftDataTest {
    @Test
    public void AircraftDateThrowsNullWithNull(){
        assertThrows(NullPointerException.class, () ->new AircraftData(null, new AircraftTypeDesignator("A2"),
                "A320", new AircraftDescription("A0J"), WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class, () ->new AircraftData(new AircraftRegistration("HELLO"),
                null, "A320", new AircraftDescription("A0J"),
                WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class, () -> new AircraftData(new AircraftRegistration("HELLO"),
                new AircraftTypeDesignator("A2"), null, new AircraftDescription("A0J"),
                WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class, () -> new AircraftData(new AircraftRegistration("HELLO"),
                new AircraftTypeDesignator("A2"), "A320", null, WakeTurbulenceCategory.LIGHT));
        assertThrows(NullPointerException.class, () -> new AircraftData(new AircraftRegistration("HELLO"),
                new AircraftTypeDesignator("A2"), "A320", new AircraftDescription("A0J"),
                null));
    }
}
