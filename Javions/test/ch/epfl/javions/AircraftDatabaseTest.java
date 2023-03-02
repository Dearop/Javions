package ch.epfl.javions;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDatabaseTest {
    //Todo make this test working, best way is to get it from Ricardo he has working test - how to convice to share - free blowie
    // TODO: 02.03.23 Make more tests for next time
    @Test //this works for all the input values
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

    @Test // this test works as well.
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
        assertEquals("M", address.returnWTCValue());
    }


}
