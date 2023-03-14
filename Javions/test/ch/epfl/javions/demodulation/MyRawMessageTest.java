package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyRawMessageTest {
    public final class PrintRawMessages {
        public static void main(String[] args) throws IOException {
            String f = "samples_20230304_1442.bin";
            try (InputStream s = new FileInputStream(f)) {
                AdsbDemodulator d = new AdsbDemodulator(s);
                RawMessage m;
                while ((m = d.nextMessage()) != null)
                    System.out.println(m);
            }
        }


    }
}
