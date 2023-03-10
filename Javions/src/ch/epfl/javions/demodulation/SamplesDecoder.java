package ch.epfl.javions.demodulation;
import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is responsible for decoding a batch of samples from an input stream.
 * A batch contains a fixed number of samples and is represented as an array of shorts.
 * Each sample is represented as a 12-bit signed integer and is packed into two bytes.
 * The first byte contains the lower 8bits of the sample,while the second byte contains the
 * higher 4bits of the sample in the most significant bits and the sign bit in the least significant bit.
 * This class reads the bytes from the input stream and decodes them into an array of shorts.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class SamplesDecoder {
    private final InputStream stream;
    private final int batchSize;
    private final byte[] bytes;

    /**
     * Constructs a SamplesDecoder object with the given input stream and batch size.
     *
     * @param stream    the input stream to read from
     * @param batchSize the number of samples in each batch
     * @throws IllegalArgumentException if the batch size is not positive
     * @throws NullPointerException     if the input stream is null
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        if (batchSize <= 0) throw new IllegalArgumentException();
        if(stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
        bytes = new byte[2*batchSize];
    }

    /**
     * Reads a batch of samples from the input stream and decodes them into an array of shorts.
     *
     * @param batch the array to store the decoded samples in
     * @return the number of samples read from the input stream and stored in the batch array
     * @throws IOException              if an I/O error occurs while reading from the input stream*
     * @throws IllegalArgumentException if the size of the batch array is not equal to the batch size
     */
    public int readBatch(short[] batch) throws IOException{
        Preconditions.checkArgument(batch.length == batchSize);

        int byteCounter = 0;
        int bytesRead = stream.readNBytes(bytes, 0 ,2*batchSize);
        for(int i = 0; i < bytesRead/2 ; ++i){

            short lowerWeight = bytes[2*i];
            short higherWeight = (short) Bits.extractUInt(bytes[2*i+1], 4, 4);
            higherWeight <<= 8;

            batch[i] = (short) (higherWeight | lowerWeight);
        }
        return bytesRead/2;
    }
}