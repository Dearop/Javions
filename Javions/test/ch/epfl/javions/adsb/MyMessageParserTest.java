package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


class MyMessageParserTest {

    @Test
    void parse() throws IOException {
        try (final InputStream s = new FileInputStream("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\samples_20230304_1442.bin")) {
            final AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            Message test;
            final int index = 0;
            while (null != (m = d.nextMessage())) {
                if (null != (test = MessageParser.parse(m))) {
                    System.out.println(test);
                }
            }
        }
    }
}