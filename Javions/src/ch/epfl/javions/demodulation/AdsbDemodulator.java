package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public class AdsbDemodulator {
    private final static int windowSize = 1200;
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        PowerWindow window = new PowerWindow(samplesStream, windowSize);
    }

    public RawMessage nextMessage() throws IOException{

    }
}
