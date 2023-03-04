package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
        int[] powerSignal = new int[numberOfBatches];
        for(int i = 0; i < powerSignal.length ; ++i){
            int j = (i*2) + 7;
            powerSignal[i] = (int) (Math.pow(SamplesDecoder.batch[j-6]-SamplesDecoder.batch[j-4]+
                    SamplesDecoder.batch[j-2], 2) + Math.pow(SamplesDecoder.batch[j-7] - SamplesDecoder.batch[j-5] +
                    SamplesDecoder.batch[j-3] -SamplesDecoder.batch[j-1], 2));
        }

        return 1212121212;
    }
}
