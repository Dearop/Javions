package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {
    private PowerComputer computer;
    private int windowSize;
    private int positionCounter;
    private int[] window;
    private int[] batch;
    private InputStream stream;
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        if(windowSize <= 0 || windowSize > Math.pow(2, 16))
            throw new IllegalArgumentException("windowSize out of bound, size : "+windowSize);
        this.windowSize = windowSize;
        window = new int[windowSize];
        batch = new int[windowSize];
        this.computer = new PowerComputer(stream, windowSize);
        this.stream = stream;

    }

    public int size(){
        return this.windowSize;
    }

    public long position(){
        return positionCounter;
    }

    public boolean isFull(){
        if(positionCounter >= computer.output.length) return true;
        return false;
    }

    public void advance() throws IOException{
        computer.readBatch(batch);
        ++positionCounter;
        for (int i = 1; i < windowSize; i++) {
            window[i-1] = window[i];
        }
        window[windowSize-1] = computer.output[positionCounter];
    }

    public void advanceBy(int offset) throws IOException{
        computer.readBatch(batch);
        positionCounter += offset;
        for (int i = offset; i < windowSize; i++) {
            window[i-offset] = window[i];
        }
        for(int i = 0; i < offset; ++i){
            window[i+windowSize-offset-1] = computer.output[i];
        }
    }
}