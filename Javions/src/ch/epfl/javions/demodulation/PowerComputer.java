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
        byte[] bytes = stream.readAllBytes();
        int[] powerSignal = new int[(bytes.length-8)/2];


        for(int i = 0; i < powerSignal.length ; ++i){
            int j = (i*2) + 7;
            powerSignal[i] = (int) (Math.pow(bytes[j-6]-bytes[j-4]+bytes[j-2], 2)
                    + Math.pow(bytes[j-7] - bytes[j-5] + bytes[j-3] -bytes[j-1], 2));
        }

        return 1212121212;
    }
}
