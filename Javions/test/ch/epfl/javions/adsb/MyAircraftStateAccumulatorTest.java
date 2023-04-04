package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyAircraftStateAccumulatorTest {

    public static void main(final String[] args) throws IOException {
        final String f = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\samples_20230304_1442.bin";
        final IcaoAddress expectedAddress = new IcaoAddress("4D2228");
        try (final InputStream s = new FileInputStream(f)) {
            final AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            final AircraftStateAccumulator<AircraftState> a =
                    new AircraftStateAccumulator<>(new AircraftState());
            while (null != (m = d.nextMessage())) {
                if (!m.icaoAddress().equals(expectedAddress)) continue;
                final Message pm = MessageParser.parse(m);
                if (null != pm) a. update(pm);
            }
        }
    }

}