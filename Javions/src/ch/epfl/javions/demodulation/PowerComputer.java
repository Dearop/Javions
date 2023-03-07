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
    private int batchSize;
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
    public PowerComputer(InputStream stream, int batchSize) throws IOException {
        if(batchSize % 8 != 0) throw new IllegalArgumentException();
        this.batchSize = batchSize;
        this.stream = stream;
        this.decoder = new SamplesDecoder(stream, 2*batchSize);
        intermediary = new short[2*batchSize];
        this.batchSize = decoder.readBatch(intermediary);
    }

    /**
     *
     * @param batch
     * @return
     * @throws IOException
     */
    public int readBatch(int[] batch){
        Preconditions.checkArgument(batch.length == batchSize);

        int counter = 0;
        for (int i = 0; i < batch.length; i+=2) {
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
        return batchSize;
    }
}
