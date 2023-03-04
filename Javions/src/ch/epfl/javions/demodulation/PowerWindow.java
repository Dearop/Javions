package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {

    private PowerComputer p;

    private short[] batchEven;
    private short[] batchOdd;
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        if(windowSize <= 0 || windowSize > Math.pow(2, 16)) throw new IllegalArgumentException("windowSize out of bound, size : "+windowSize);

        //this probably wrong but how i understood it
        this.p = new PowerComputer(stream, windowSize);


    }
}
