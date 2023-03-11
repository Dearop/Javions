package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public class AdsbDemodulator {
    private final static int windowSize = 1200;
    private PowerWindow window;
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.window = new PowerWindow(samplesStream, windowSize);
    }

    public RawMessage nextMessage() throws IOException{
        if(window.isFull()) return null;

    }
}
