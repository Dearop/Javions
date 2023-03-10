package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class PowerComputer {
    public int batchSize;
    private final InputStream stream;
    private final SamplesDecoder decoder;
    private final short[] powerCalculationTable = new short[8];
    /**
     * Table that contains the batch of computed powers
     */
    public int[] output;
    private short[] intermediary;
    /**
     *
     * @param stream
     * @param batchSize
     */
    public PowerComputer(InputStream stream, int batchSize){
        if(batchSize % 8 != 0) throw new IllegalArgumentException();
        if(batchSize < 0) throw new IllegalArgumentException();
        this.batchSize = batchSize;
        this.stream = stream;
        this.decoder = new SamplesDecoder(stream, batchSize);
    }

    /**
     *
     * @param batch
     * @return integer value representing the size of the batch
     */
    public int readBatch(int[] batch) throws IOException {
//        System.out.println(batch.length);
//        System.out.println(batchSize);
        Preconditions.checkArgument(batch.length == batchSize);
        this.batchSize = decoder.readBatch(new short[batchSize]);
        int counter = 0;
        for (int i = 0; i < batchSize; i+=2) {
            powerCalculationTable[7] = powerCalculationTable[5];
            powerCalculationTable[6] = powerCalculationTable[4];
            powerCalculationTable[5] = powerCalculationTable[3];
            powerCalculationTable[4] = powerCalculationTable[2];
            powerCalculationTable[3] = powerCalculationTable[1];
            powerCalculationTable[2] = powerCalculationTable[0];

            powerCalculationTable[0] = decoder.batch[i+1];
            powerCalculationTable[1] = decoder.batch[i];


            batch[counter] =  (int) (Math.pow(powerCalculationTable[1]-powerCalculationTable[3]+
                                    powerCalculationTable[5]-powerCalculationTable[7], 2) //even
                                    +
                                    Math.pow(powerCalculationTable[0] - powerCalculationTable[2] + powerCalculationTable[4] -
                                    powerCalculationTable[6],2)); //odd
            counter++;
        }
        output = batch.clone();
        return counter;
    }
}
