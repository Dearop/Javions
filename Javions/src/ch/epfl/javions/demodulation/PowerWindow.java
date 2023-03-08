package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

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

        batchOneActive = true;
        for (int i = 0; i < batchSize; i++)
            batchOne[i] = computer.output[i];
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
        if (batchOneActive) {
            if ((positionInBatch) + i < batchSize) {
                return batchOne[positionInBatch + i];
            } else {
                return batchTwo[(positionInBatch + i) & batchSize];
            }
        }
        //here batch one is not active
        //TODO is it batchsize -1 or without -1??
        if ((positionInBatch) + i < batchSize) {
            return batchTwo[positionInBatch + i];
        } else {
            return batchOne[(positionInBatch + i) & batchSize];
        }
    }

    /**
     * Moves either the first or the second batch forwards by one which simulates
     */
    public void advance() {
        ++positionCounter;
        int positionInsideBatch = positionCounter - 1 % batchSize;
        if (batchOneActive) {

            // we first check if we need to take the new window value from the other batch or not.
            if (positionInsideBatch + windowSize >= batchSize) {

                // we check if the new position is at the end of the current priority batch.
                if (positionInsideBatch == 0) {
                    //make batchTwo priority batch
                    batchOneActive = false;

                    // because batchTwo is now priority batch we can replace all values inside batchOne with
                    // the new info from output
                    for (int i = 0; i < batchSize; i++)
                        batchOne[i] = computer.output[positionCounter + i];
                }
            }

            // case where we just move by one and also get the new window value from the priority batch.

        } else {

            // we advance by one and check if we got to the end of the batch
            if ((positionInsideBatch) + windowSize >= batchSize) {

                if (positionInsideBatch == 0) {
                    //make batchOne priority batch
                    batchOneActive = true;

                    // because batchOne is now priority batch we can replace all values inside batchTwo with
                    // the new info from output
                    for (int i = 0; i < batchSize; i++)
                        batchTwo[i] = computer.output[positionCounter + i];
                }

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