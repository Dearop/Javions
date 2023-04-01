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

            try (final InputStream s = new FileInputStream(this.f)) {
                final AdsbDemodulator d = new AdsbDemodulator(s);
                RawMessage m;
                while (null != (m = d.nextMessage())) {
                    System.out.println(m);
                }
            }
        }
        @Test
        public void RawMessageTest2() throws IOException{
            try (final InputStream s = new FileInputStream(this.f)) {
                final AdsbDemodulator d = new AdsbDemodulator(s);
                final RawMessage m;
                m = d.nextMessage();
                System.out.println(m);
                assert null != m;
                assertEquals(m.downLinkFormat(), 17);
                assertEquals(m.icaoAddress(), new IcaoAddress("4B17E5"));
                assertEquals(m.typeCode(), 0xF8 >> 3);
            }
        }
    }
