package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class myAirborneVelocityTest {

    @Test
    public void AirborneVelocityThrowsExceptions(){
        assertThrows(NullPointerException.class, ()-> new AirborneVelocityMessage(0, null, 0, 0));
        assertThrows(IllegalArgumentException.class, ()-> new AirborneVelocityMessage(-1, new IcaoAddress("ABCDEF"), 0, 0));
        assertThrows(IllegalArgumentException.class, ()-> new AirborneVelocityMessage(0, new IcaoAddress("ABCDEF"), -1, 0));
        assertThrows(IllegalArgumentException.class, ()-> new AirborneVelocityMessage(0, new IcaoAddress("ABCDEF"), 0, -1));
    }

    @Test
    public void oneMessageTest(){
        RawMessage message = new RawMessage(0, new ByteString( new byte[]{(byte) 0x8D,0x48,0x50,0x20,(byte) 0x99,0x44,0x09,(byte) 0x94,0x08,0x38,0x17,0x5B,0x28,0x4F}));
        AirborneVelocityMessage velocityMessage = AirborneVelocityMessage.of(message);
        System.out.println(velocityMessage);
    }
}
