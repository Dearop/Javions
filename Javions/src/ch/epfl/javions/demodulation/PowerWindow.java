package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class PowerWindow {
    // TODO: 3/7/2023 change back to private
    public PowerComputer computer;
    private final int windowSize;
    private int positionCounter;
    private final static int batchSize = (int) Math.pow(2, 16);
    // TODO: 3/7/2023 should be private
    public int[] batchOne;
    public int[] batchTwo;
    private boolean batchOneActive;
    private int tableCounter;
    private InputStream stream;

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        if (windowSize <= 0 || windowSize > batchSize)
            throw new IllegalArgumentException("windowSize out of bound, size : " + windowSize);
        this.windowSize = windowSize;

        batchOne = new int[batchSize];
        batchTwo = new int[batchSize];

        this.computer = new PowerComputer(stream, batchSize);
        this.stream = stream;
        computer.readBatch(batchOne);

        //System.out.println(Arrays.toString(batchOne));

        batchOneActive = true;
        for (int i = 0; i < batchSize; i++) {
            batchOne[i] = computer.output[i];
        }

        computer.readBatch(batchTwo);
        for (int i = 0; i < batchSize; i++) {
            batchTwo[i] = computer.output[i];
        }
    }

    /**
     * @return integer value representing size of the current instance of PowerWindow
     */
    public int size() {
        return this.windowSize;
    }

    /**
     * @return integer value corresponding to the current position of the window in reference to the table
     */
    public long position() {
        return positionCounter;
    }

    //here there can be a lot of mistakes with reading the files and positionCounter being too big

    /**
     * @return
     */
    public boolean isFull() {
        return positionCounter <= computer.output.length;
    }

    public int get(int i) {
        if (i < 0 || i >= windowSize) throw new IllegalArgumentException();
        int positionInBatch = positionCounter % batchSize;
//        if (batchOneActive) {
//            if ((positionInBatch) + i < batchSize) {
//                return batchOne[positionInBatch + i];
//            } else {
//                return batchTwo[(positionInBatch + i) & batchSize-1];
//            }
//        }
//        //here batch one is not active
//        //TODO is it batchsize -1 or without -1??
//        if ((positionInBatch) + i < batchSize) {
//            return batchTwo[positionInBatch + i];
//        } else {
//            return batchOne[(positionInBatch + i) & batchSize-1];
//        }
        return positionInBatch + i < batchSize ? batchOne[positionInBatch + i] : batchTwo[positionInBatch + i % batchSize];
    }

    /**
     * Moves either the first or the second batch forwards by one which simulates
     */
    public void advance() {
        ++positionCounter;
        int positionInsideBatch = positionCounter % batchSize;


        // we check if the new position is at the end of the current priority batch.
        if (positionInsideBatch == 0) {

            // because batchTwo is now priority batch we can replace all values inside batchOne with
            // the new info from output
            for (int i = 0; i < batchSize; i++) {
                batchOne[i] = batchTwo[i];
                computer.readBatch(batchTwo);
                batchTwo[i] = computer.output[i];
            }

        }



}


    public void advanceBy(int offset) {
        //todo offset can't be bigger than window size no???
        if (offset >= windowSize) throw new IllegalArgumentException();
        if (offset <= 0) throw new IllegalArgumentException();
        for (int i = 0; i < offset; i++) advance();
    }

}