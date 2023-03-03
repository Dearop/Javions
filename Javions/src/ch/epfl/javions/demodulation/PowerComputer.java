package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {
    private int batchSize;
    private short[] output = new short[batchSize];
    private InputStream stream;
    private  SamplesDecoder decoder;
    public PowerComputer(InputStream stream, int batchSize){
        if(batchSize % 8 != 0) throw new IllegalArgumentException();
        this.batchSize = batchSize;
        this.stream = stream;
        decoder = new SamplesDecoder(stream, batchSize);
    }

    public int readBatch(int[] batch) throws IOException{
        int numberOfBatches = decoder.readBatch(output);
        for(int i = 7; i < batchSize; i+=2){

        }
    }
}
