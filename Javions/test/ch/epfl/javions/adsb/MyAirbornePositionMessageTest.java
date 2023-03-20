package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyAirbornePositionMessageTest {
    private final String f = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\samples_20230304_1442.bin";
    @Test
    public void PrintFirstAibornePositionMessage() throws IOException {
        try (
                InputStream s = new FileInputStream(f)) {
                AdsbDemodulator d = new AdsbDemodulator(s);
                RawMessage m;
                while ((m = d.nextMessage()) != null) {
                    if (AirbornePositionMessage.of(m) != null) {
                        System.out.println(AirbornePositionMessage.of(m));
                    }
                }
        }
    }
    @Test
    public void AltitudeComputerTestQis0(){
        assertEquals(AirbornePositionMessage.altitudeComputer(0b100010110011), 8130.54);
    }

    @Test
    public void AltitudeComputerTestQis1(){
        byte[] bytes = {(byte) 0x8D,0x39,0x20,0x35,0x59, (byte) 0xB2,0x25, (byte) 0xF0,0x75,0x50, (byte) 0xAD, (byte) 0xBE,0x32, (byte) 0x8F};
        System.out.println(AirbornePositionMessage.of(new RawMessage(0, new ByteString(bytes))));
    }
}
