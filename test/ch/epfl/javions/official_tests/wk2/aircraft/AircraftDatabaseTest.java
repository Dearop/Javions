package ch.epfl.javions.official_tests.wk2.aircraft;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

class AircraftDatabaseTest {
    private AircraftDatabase getDatabase() {
        // Try to get the database from the resources
        final var aircraftResourceUrl = this.getClass().getResource("/aircraft.zip");
        if (null != aircraftResourceUrl)
            return new AircraftDatabase(URLDecoder.decode(aircraftResourceUrl.getFile(), UTF_8));

        // Try to get the database from the JAVIONS_AIRCRAFT_DATABASE environment variable
        // (only meant to simplify testing of several projects with a single database)
        final var aircraftFileName = System.getenv("JAVIONS_AIRCRAFT_DATABASE");
        if (null != aircraftFileName)
            return new AircraftDatabase(aircraftFileName);

        throw new Error("Could not find aircraft database");
    }

    @Test
    void aircraftDatabaseGetReturnsNullWhenAddressDoesNotExist() throws IOException {
        final var aircraftDatabase = this.getDatabase();
        assertNull(aircraftDatabase.get(new IcaoAddress("123456")));
    }

    @Test
    void aircraftDatabaseGetWorksWithFirstLineOfFile() throws IOException {
        final var aircraftDatabase = this.getDatabase();
        final var aircraftData = aircraftDatabase.get(new IcaoAddress("0086AB"));
        assertNotNull(aircraftData);
        assertEquals(new AircraftRegistration("ZS-CNA"), aircraftData.registration());
    }

    @Test
    void aircraftDatabaseGetWorksWithLastLineOfFile() throws IOException {
        final var aircraftDatabase = this.getDatabase();
        final var aircraftData = aircraftDatabase.get(new IcaoAddress("E808C0"));
        assertNotNull(aircraftData);
        assertEquals(new AircraftRegistration("CC-DAW"), aircraftData.registration());
    }

    @Test
    void aircraftDatabaseGetWorksWithAddressGreaterThanLastOneOfFile() throws IOException {
        final var aircraftDatabase = this.getDatabase();
        final var aircraftData = aircraftDatabase.get(new IcaoAddress("FFFF01"));
        assertNull(aircraftData);
    }

    @Test
    void aircraftDatabaseGetReturnsCorrectData() throws IOException {
        final var aircraftDatabase = this.getDatabase();
        final var aircraftData = aircraftDatabase.get(new IcaoAddress("4B1805"));
        assertNotNull(aircraftData);
        assertEquals(new AircraftRegistration("HB-JCN"), aircraftData.registration());
        assertEquals(new AircraftTypeDesignator("BCS3"), aircraftData.typeDesignator());
        assertEquals("AIRBUS A220-300", aircraftData.model());
        assertEquals(new AircraftDescription("L2J"), aircraftData.description());
        assertEquals(WakeTurbulenceCategory.MEDIUM, aircraftData.wakeTurbulenceCategory());
    }

    @Test
    void aircraftDatabaseGetWorksWithEmptyColumns() throws IOException {
        final var aircraftDatabase = this.getDatabase();
        final var aircraftData = aircraftDatabase.get(new IcaoAddress("AAAAAA"));
        assertNotNull(aircraftData);
        assertEquals(new AircraftRegistration("N787BK"), aircraftData.registration());
        assertEquals(new AircraftTypeDesignator(""), aircraftData.typeDesignator());
        assertEquals("", aircraftData.model());
        assertEquals(new AircraftDescription(""), aircraftData.description());
        assertEquals(WakeTurbulenceCategory.UNKNOWN, aircraftData.wakeTurbulenceCategory());
    }
}