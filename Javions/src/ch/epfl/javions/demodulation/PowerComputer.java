package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * In this class we compute the values that we get from the SampleDecoder. To do this, we apply a well-defined algorithm
 * that gives you a power value, so if the stream length is 4804 we will get a total of 1201 power values.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class PowerComputer {
    private final int batchSize;
    private final SamplesDecoder decoder;
    private final int[] powerCalculationTable = new int[8];
    private final short[] decodedBatch;

    /**
     * Creates a new PowerComputer object that also creates a calls a new SampleDecoder with the parameters stream and
     * two times the batchSize.
     *
     * @param stream    The input stream from which to decode the samples.
     * @param batchSize The number of samples in each batch.
     * @throws IllegalArgumentException if batchSize is negative or not divisible by 8.
     */
    public PowerComputer(InputStream stream, int batchSize) {
        if (0 != batchSize % 8)
            throw new IllegalArgumentException();
        if (0 > batchSize)
            throw new IllegalArgumentException();

        this.batchSize = batchSize;
        this.decodedBatch = new short[2 * batchSize];
        this.decoder = new SamplesDecoder(stream, 2 * batchSize);
    }

    /**
     * Reads the next batch of samples from the input stream, computes the power of each sample, and stores
     * the results in the provided batch table.
     *
     * @param batch The array in which to store the computed powers.
     * @return The number of samples processed.
     * @throws IOException              if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the batch array does not match the batch size.
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == this.batchSize);

        int bytesRead = decoder.readBatch(decodedBatch);
        int counter = 0;

        for (int i = 0; i < bytesRead; i += 2) {
            for (int j = 5; j >= 0; j--)
                powerCalculationTable[2+j] = powerCalculationTable[j];

            powerCalculationTable[0] = decodedBatch[i + 1];
            powerCalculationTable[1] = decodedBatch[i];


            batch[i / 2] = (int) (Math.pow(powerCalculationTable[1] - powerCalculationTable[3] +
                    powerCalculationTable[5] - powerCalculationTable[7], 2) //even
                    +
                    Math.pow(powerCalculationTable[0] - powerCalculationTable[2] + powerCalculationTable[4] -
                            powerCalculationTable[6], 2)); //odd
            counter++;
        }
        return counter;
    }
}
