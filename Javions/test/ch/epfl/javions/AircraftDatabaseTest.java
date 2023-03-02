package ch.epfl.javions;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDatabaseTest {
    @Test
    public void AircraftDatabaseTest1(){
        AircraftData data = null;
        AircraftDatabase address = new AircraftDatabase(new IcaoAddress("E88014"));
        try {
            address.get(new IcaoAddress("E88014"));
        } catch (IOException e) {
            System.out.println("here");
        }
        assertEquals("BEECH 200 Super King Air", address.returnModelString());
    }

    @Test
    public void AircraftDatabaseTestElementDoesNotExist() throws IOException {
        AircraftData data = null;
        AircraftDatabase address = new AircraftDatabase(new IcaoAddress("C0A415"));
        assertEquals(null, address.get(new IcaoAddress("C0A415")));

    }

    @Test
    public void AircraftDatabaseTestWTC(){
        AircraftData data = null;
        AircraftDatabase address = new AircraftDatabase(new IcaoAddress("E88014"));
        try{
            address.get(new IcaoAddress("E88014"));
        } catch (IOException e){
            System.out.println("gang gang");
        }
        assertEquals(WakeTurbulenceCategory.of("M"), address.returnWTCValue());
    }
}
