package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MyAircraftIdentificationMessageTest {
    private final String f = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\samples_20230304_1442.bin";
    @Test
    public void AircraftIdentificationMessagePrintsFirstMessageRight() throws IOException {
        try (
                final InputStream s = new FileInputStream(this.f)) {
                final AdsbDemodulator d = new AdsbDemodulator(s);
                RawMessage m;
                while (null != (m = d.nextMessage())) {
                    if(null != AircraftIdentificationMessage.of(m)){
                        System.out.println(AircraftIdentificationMessage.of(m));
                    }
                }
        }
    }

}


