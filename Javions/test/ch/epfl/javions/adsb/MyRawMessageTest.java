package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MyRawMessageTest {
    private final String f = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\samples_20230304_1442.bin";
        @Test
        public void RawMessagesTest1() throws IOException{

            try (InputStream s = new FileInputStream(f)) {
                AdsbDemodulator d = new AdsbDemodulator(s);
                RawMessage m;
                while ((m = d.nextMessage()) != null) {
                    System.out.println(m);
                }
            }
        }
        @Test
        public void RawMessageTest2() throws IOException{
            try (InputStream s = new FileInputStream(f)) {
                AdsbDemodulator d = new AdsbDemodulator(s);
                RawMessage m;
                m = d.nextMessage();
                System.out.println(m);
                assert m != null;
                assertEquals(m.downLinkFormat(), 17);
                assertEquals(m.icaoAddress(), new IcaoAddress("4B17E5"));
                assertEquals(m.typeCode(), 0xF8 >> 3);
            }
        }
    }
