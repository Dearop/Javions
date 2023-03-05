package ch.epfl.javions.demodulation;
import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class SamplesDecoder {
    private InputStream stream;
    private int batchSize;
    private byte[] bytes;
    public short[] batch;

    public SamplesDecoder(InputStream stream, int batchSize) {
        if (batchSize <= 0) throw new IllegalArgumentException();
        if(stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
        bytes = new byte[2*batchSize];
        batch = new short[batchSize];
    }

    public int readBatch(short[] batch) throws IOException{
        Preconditions.checkArgument(batch.length == batchSize);
        if(stream.available() <= batchSize) return (int) Math.floor(stream.available() /2);
        int streamSize = stream.readNBytes(bytes, 0,batchSize);
        for(int i = 0; i < bytes.length/2; i+=2){
            short lowerWeight = bytes[i];
            short higherWeight = (short) Bits.extractUInt(bytes[i+1], 4, 4);
            higherWeight <<= 8;
            batch[i/2] = (short) (higherWeight | lowerWeight);
        }
        this.batch = batch.clone();
        return batchSize/2;
    }
}