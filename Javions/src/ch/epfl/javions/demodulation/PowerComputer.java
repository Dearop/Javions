package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {
    private final int batchSize;
    private final InputStream stream;
    private final SamplesDecoder decoder;
    private final short[] powerCalculationTable = new short[8];
    /**
     * Table that contains the batch of computed powers
     */
    public int[] output;

    /**
     *
     * @param stream
     * @param batchSize
     */
    public PowerComputer(InputStream stream, int batchSize){
        if(batchSize % 8 != 0) throw new IllegalArgumentException();
        this.batchSize = batchSize;
        this.stream = stream;
        this.decoder = new SamplesDecoder(stream, 2*batchSize);
    }

    /**
     *
     * @param batch
     * @return
     * @throws IOException
     */
    public int readBatch(int[] batch) throws IOException{
        Preconditions.checkArgument(batch.length != batchSize);
        for (int i = 1; i < batchSize; i+=2) {
            powerCalculationTable[i%8] = decoder.batch[i];
            powerCalculationTable[(i-1)%8] = decoder.batch[i-1];
            batch[i] =(int) (Math.pow(-powerCalculationTable[i%8]+powerCalculationTable[8-Math.abs((i%8)-2)]-
                             powerCalculationTable[8-Math.abs((i%8)-4)]+powerCalculationTable[8-Math.abs((i%8)-6)],2)+
                    Math.pow(powerCalculationTable[8-Math.abs((i%8)-7)]-powerCalculationTable[8-Math.abs((i%8)-5)]+
                            powerCalculationTable[8-Math.abs((i%8)-3)]-powerCalculationTable[8-Math.abs((i%8)-1)],2));
        }
        output = batch.clone();
        int counter = 0;
        while(counter < output.length){
            if(output[counter] != 0) ++counter;
        }
        return batchSize;
    }
}
