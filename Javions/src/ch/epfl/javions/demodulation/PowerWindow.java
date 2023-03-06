package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {
    private PowerComputer computer;
    private int windowSize;
    private int positionCounter;
    private int[] window;
    private int[] batch;
    public int[] batchOne;
    public int[] batchTwo;
    boolean batchOneActive;
    private int tableCounter;
    private InputStream stream;

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        if(windowSize <= 0 || windowSize > Math.pow(2, 16))
            throw new IllegalArgumentException("windowSize out of bound, size : "+windowSize);
        this.windowSize = windowSize;
        window = new int[windowSize];
        batch = new int[windowSize];
        // TODO: 3/6/2023 How big should the batchSize be, are batchsize and windowsize are the same
        this.computer = new PowerComputer(stream, windowSize);
        computer.readBatch(batch);
        this.stream = stream;
        batchOneActive = true;
        batchOne = new int[windowSize];
        batchTwo = new int[windowSize];
        for (int i = 0; i < windowSize; i++) {
            batchOne[i] = computer.output[i];
        }
    }

    public int size(){
        return this.windowSize;
    }

    public long position(){
        return positionCounter;
    }

    public boolean isFull(){
        return positionCounter >= computer.output.length;
    }

    public int get(int i){
        if(i<0 || i >= windowSize) throw new IllegalArgumentException();
        return window[i];
    }

    public void advance() throws IOException{
        ++positionCounter;

        if(batchOneActive){
            // TODO: 3/6/2023 not correct
            if(positionCounter % windowSize == windowSize-1){
                batchOneActive = false;
            }
            batchOne[tableCounter] = computer.output[positionCounter];
        }

        for (int i = 1; i < windowSize; i++) {
            window[i-1] = window[i];
        }

        batchTwo[tableCounter] = computer.output[positionCounter];
        if (tableCounter < windowSize) ++tableCounter;
        if (tableCounter == windowSize-1) {

        }
    }

    public void advanceBy(int offset) throws IOException{
        if(offset <= 0) throw new IllegalArgumentException();
        int internalPositionCounter = this.positionCounter;
        positionCounter += offset;
        for (int i = offset; i < windowSize; i++) {
            window[i-offset] = window[i];
        }
        for(int i = 0; i < offset; ++i){
            window[i+windowSize-offset-1] = computer.output[internalPositionCounter+i];
        }
    }
}