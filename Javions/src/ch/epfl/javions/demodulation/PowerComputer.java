package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class PowerComputer {
    // TODO: 3/10/2023 ask TA
    public int batchSize;
    private final SamplesDecoder decoder;
    private final int[] powerCalculationTable = new int[8];
    /**
     * Table that contains the batch of computed powers
     */
    private final short[] decodedBatch;
    /**
     *
     * @param stream inputStream from which we decode the samples and compute the appropriate powers
     * @param batchSize integer value that corresponds to the size of the batch
     */
    public PowerComputer(InputStream stream, int batchSize){
        if(batchSize % 8 != 0) throw new IllegalArgumentException();
        if(batchSize < 0) throw new IllegalArgumentException();
        this.batchSize = batchSize;
        this.decodedBatch = new short[2*batchSize];
        this.decoder = new SamplesDecoder(stream, batchSize*2);
    }

    /**
     * read batches
     *
     * @param batch integer table in which the computed powers are stored
     * @return integer value representing the size of the batch
     */
    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int bytesRead = decoder.readBatch(decodedBatch);
        int counter = 0;
        for (int i = 0; i < bytesRead; i+=2) {
            powerCalculationTable[7] = powerCalculationTable[5];
            powerCalculationTable[6] = powerCalculationTable[4];
            powerCalculationTable[5] = powerCalculationTable[3];
            powerCalculationTable[4] = powerCalculationTable[2];
            powerCalculationTable[3] = powerCalculationTable[1];
            powerCalculationTable[2] = powerCalculationTable[0];

            powerCalculationTable[0] = decodedBatch[i+1];
            powerCalculationTable[1] = decodedBatch[i];


            batch[counter] =  (int) (Math.pow(powerCalculationTable[1]-powerCalculationTable[3]+
                                    powerCalculationTable[5]-powerCalculationTable[7], 2) //even
                                    +
                                    Math.pow(powerCalculationTable[0] - powerCalculationTable[2] + powerCalculationTable[4] -
                                    powerCalculationTable[6],2)); //odd
            counter++;
        }
        return counter;
    }
}
