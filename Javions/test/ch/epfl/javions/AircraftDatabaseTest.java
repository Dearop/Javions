package ch.epfl.javions;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDatabaseTest {

    @Test
    public void AircraftDatabaseTest(){
        AircraftData data = null;
        AircraftDatabase address = new AircraftDatabase(new IcaoAddress("C88014"));
        try {
            address.get(new IcaoAddress("C88014"));
        } catch (IOException e) {
            System.out.println("here");
        }
        assertEquals("DE HAVILLAND DHC-6 Twin Otter", modelGetter());
    }
}
