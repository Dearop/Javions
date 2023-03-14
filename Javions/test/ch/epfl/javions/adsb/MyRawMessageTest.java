package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyRawMessageTest {
    public final class PrintRawMessages {
        public static void main(String[] args) throws IOException {
            String f = "samples_20230304_1442.bin";
            try (InputStream s = new FileInputStream(f)) {
                AdsbDemodulator d = new AdsbDemodulator(s);
                RawMessage m;
                while ((m = d.nextMessage()) != null) {
                    System.out.println(m);
                    assertEquals(new IcaoAddress("4B17E5"), m.icaoAddress());

                }
            }
        }
    }
}
