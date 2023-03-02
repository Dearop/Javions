package ch.epfl.javions;

import ch.epfl.javions.adsb.CallSign;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CallsignTest {

    @Test
    public void callsignTest(){
        assertThrows(IllegalArgumentException.class , () -> new CallSign("a"));
        assertThrows(IllegalArgumentException.class , () -> new CallSign(""));
        assertThrows(IllegalArgumentException.class , () -> new CallSign("A/"));
        assertThrows(IllegalArgumentException.class , () -> new CallSign("A2UIBFVFB4436543"));
    }
}
