package ch.epfl.javions.official_tests.wk2.aircraft;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDataTest {
    @Test
    void aircraftDataConstructorThrowsWithNullAttribute() {
        final var registration = new AircraftRegistration("HB-JAV");
        final var typeDesignator = new AircraftTypeDesignator("B738");
        final var model = "Boeing 737-800";
        final var description = new AircraftDescription("L2J");
        final var wakeTurbulenceCategory = WakeTurbulenceCategory.LIGHT;
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(null, typeDesignator, model, description, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, null, model, description, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, null, description, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, null, wakeTurbulenceCategory);
        });
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, description, null);
        });
    }
}