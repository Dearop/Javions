package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class PowerWindow {
    // TODO: 3/9/2023 Should be public 
    public PowerComputer computer;
    private final int windowSize;
    private int positionCounter;
    private final static int batchSize = (int) Math.pow(2, 16);
    // TODO: 3/7/2023 should be private
    public int[] batchOne;
    public int[] batchTwo;
    private boolean batchOneActive;
    private int tableCounter;
    private final InputStream stream;

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        if (windowSize <= 0 || windowSize > batchSize)
            throw new IllegalArgumentException("windowSize out of bound, size : " + windowSize);
        this.windowSize = windowSize;
        this.computer = new PowerComputer(stream, batchSize);
        batchOne = new int[batchSize];
        int batchSize = computer.readBatch(batchOne);
        batchOne = computer.output.clone();
        batchTwo = new int[batchSize];
        this.stream = stream;


        //System.out.println(Arrays.toString(batchOne));

        batchOneActive = true;
        for (int i = 0; i < batchSize; i++) {
            batchOne[i] = computer.output[i];
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
     * @return a boolean value telling us if the counter tracking our position in the window is greater than the
     * size of the window
     */
    public boolean isFull() {
        return positionCounter <= windowSize;
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
            batchOneActive = false; // TODO: 3/9/2023 show Henri
            // because batchTwo is now priority batch we can replace all values inside batchOne with
            // the new info from output
            for (int i = 0; i < batchSize; i++) {
                batchOne[i] = batchTwo[i];
                System.out.println(positionCounter);
                //computer.readBatch(batchTwo);
                batchTwo[i] = computer.output[i];
            }
        }
}

    /**
     * This method applies the advance method but instead of letting the window move forwards one-by-one
     * we move it by the integer value given as a parameter.
     *
     * @param offset integer value representing how much we are skipping forwards
     */

    public void advanceBy(int offset) {
        if (offset <= 0) throw new IllegalArgumentException();
        for (int i = 0; i < offset; i++) advance();
    }

}