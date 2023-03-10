package ch.epfl.javions.demodulation;
import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class SamplesDecoder {
    private final InputStream stream;
    private int batchSize;
    private final byte[] bytes;

    public SamplesDecoder(InputStream stream, int batchSize) {
        if (batchSize <= 0) throw new IllegalArgumentException();
        if(stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
        bytes = new byte[2*batchSize];
    }

    /**
     *
     * @param batch
     * @return
     * @throws IOException
     */
    public int readBatch(short[] batch) throws IOException{
        Preconditions.checkArgument(batch.length == batchSize);
        if(stream.available() <= batchSize) batchSize = stream.available();
        int byteCounter = 0;
        byteCounter += stream.readNBytes(bytes, 0,batchSize);
        for(int i = 0; i < batchSize /2; i+=2){
            short lowerWeight = bytes[i];
            short higherWeight = (short) Bits.extractUInt(bytes[i+1], 4, 4);
            higherWeight <<= 8;
            batch[i/2] = (short) (higherWeight | lowerWeight);
        }
        return (int) Math.floor(byteCounter);
    }
}