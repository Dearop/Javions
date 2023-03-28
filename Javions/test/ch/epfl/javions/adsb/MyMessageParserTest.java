package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


class MyMessageParserTest {

    @Test
    void parse() throws IOException {
        try (InputStream s = new FileInputStream("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\samples_20230304_1442.bin")) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            Message test;
            int index = 0;
            while ((m = d.nextMessage()) != null) {
                if ((test = MessageParser.parse(m)) != null) {
                    System.out.println(test);
                }
            }
        }
    }
}