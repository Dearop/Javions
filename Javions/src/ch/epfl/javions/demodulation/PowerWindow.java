package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {

    private PowerComputer p;
    private PowerWindow w;

    private short[] batchEven;
    private short[] batchOdd;
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        if(windowSize <= 0 || windowSize > Math.pow(2, 16)) throw new IllegalArgumentException("windowSize out of bound, size : "+windowSize);

        //this probably wrong but how i understood it
        p = new PowerComputer(stream, windowSize);

        // p will have some values that will then get put into the window with which we fill the batches... something like that
        w = new PowerWindow(stream, 12121212); //12121212 = p.length & stream = p.values

    }
}
