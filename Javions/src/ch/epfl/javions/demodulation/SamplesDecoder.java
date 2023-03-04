package ch.epfl.javions.demodulation;
import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class SamplesDecoder {
    private InputStream stream;
    private static int batchSize;
    private byte[] bytes = new byte[2*batchSize];
    public short[] batch = new short[batchSize];

    public SamplesDecoder(InputStream stream, int batchSize) {
        if (batchSize <= 0) throw new IllegalArgumentException();
        if(stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
    }

    public int readBatch(short[] batch) throws IOException{
        Preconditions.checkArgument(batch.length == batchSize);
        if(stream.readAllBytes().length <= batchSize) return (int) Math.floor(stream.readAllBytes().length/2);
        bytes = stream.readNBytes(batchSize);
        int counter = 0;
        for(int i = 0; i < bytes.length/2; i+=2){
            short lowerWeight = (short) (bytes[i]-2048);
            short higherWeight = (short) (Bits.extractUInt(bytes[i+1], 4, 4)-2048);
            higherWeight <<= 8;
            batch[i/2] = (short) (higherWeight | lowerWeight);
            ++counter;
        }
        this.batch = batch.clone();
        return counter;
    }
}