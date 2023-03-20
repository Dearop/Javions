package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MyAirbornePositionMessage {
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
}
