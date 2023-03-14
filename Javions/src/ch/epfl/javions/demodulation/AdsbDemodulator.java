package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private InputStream stream;
    private final PowerWindow window;
    private final static int windowSize = 1200;
    private long timeStamp;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.window = new PowerWindow(samplesStream, windowSize);
        timeStamp = 0;
    }

    public RawMessage nextMessage() throws IOException{
        if(!window.isFull()) return null;
        return RawMessage.of(timeStamp, );
    }
}
