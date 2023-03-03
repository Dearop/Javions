package ch.epfl.javions.demodulation;
import ch.epfl.javions.Bits;

import java.io.IOException;
import java.io.InputStream;

public final class SamplesDecoder {
    private InputStream stream;
    private int batchSize;
    private byte[] bytes = new byte[2*batchSize];
    private byte[] batch;

    public SamplesDecoder(InputStream stream, int batchSize) {
        if (batchSize <= 0) throw new IllegalArgumentException();
        if(stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
    }

    public int readBatch(short[] batch) throws IOException{
        if(batch.length != batchSize) throw new IllegalArgumentException();
        if(stream.readAllBytes().length <= batchSize) return (int) Math.floor(batchSize/2);
        bytes = stream.readNBytes(batchSize);
        for(int i = 0; i < bytes.length/2; i+=2){
            short lowerWeight = bytes[i];
            short higherWeight = (short) Bits.extractUInt(bytes[i+1], 4, 4);
            higherWeight <<= 8;
            batch[i/2] = (short) (higherWeight | lowerWeight);
        }
        return batchSize;
    }
}