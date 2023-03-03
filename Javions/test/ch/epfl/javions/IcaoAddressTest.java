package ch.epfl.javions;

import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IcaoAddressTest {
    @Test
    public void ICAOAddressTest1(){
        assertThrows(IllegalArgumentException.class,() -> new IcaoAddress(""));
        assertThrows(IllegalArgumentException.class,() -> new IcaoAddress("FDNOWINFW"));
        assertThrows(IllegalArgumentException.class,() -> new IcaoAddress("dhf"));
    }
}
