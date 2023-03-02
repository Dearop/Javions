package ch.epfl.javions;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDatabaseTest {
    //Todo make this test working, best way is to get it from Ricardo he has working test - how to convice to share - free blowie
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
