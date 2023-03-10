package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class PowerWindow {
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
    private int availableStream;

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        if (windowSize <= 0 || windowSize > batchSize)
            throw new IllegalArgumentException("windowSize out of bound, size : " + windowSize);
        availableStream = stream.available();
        System.out.println(stream.available());
        this.windowSize = windowSize;
        this.computer = new PowerComputer(stream, batchSize);
        batchOne = new int[batchSize];
        int batchSize = computer.readBatch(batchOne);;
        batchTwo = new int[batchSize];
        this.stream = stream;
        //System.out.println(Arrays.toString(batchOne));

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
     * @return boolean value that says if the window is full which itself is reliant upon the fact that the window is
     * in the batch
     */
    public boolean isFull() {
        return availableStream/4 >= positionCounter +windowSize ;
    }

    /**
     *
     * @param i integer value representing the position in the window we want to get
     * @return power computed at the ith spot of the window
     */
    public int get(int i) {
        if (i < 0 || i >= windowSize) throw new IllegalArgumentException();
        int positionInBatch = positionCounter % batchSize;
        return positionInBatch + i < batchSize ? batchOne[positionInBatch + i] : batchTwo[positionInBatch + i % batchSize];
    }

    /**
     * Moves either the first or the second batch forwards by one which simulates
     */
    public void advance() throws IOException{
        ++positionCounter;
        int positionInsideBatch = positionCounter % batchSize;


        // we check if the new position is at the end of the current priority batch.
        if (positionInsideBatch == 0) {
            batchOneActive = false; // TODO: 3/9/2023 show Henri
            // because batchTwo is now priority batch we can replace all values inside batchOne with
            // the new info from output
            for (int i = 0; i < batchSize; i++) {
                batchOne[i] = batchTwo[i];
                //System.out.println(positionCounter);
                //computer.readBatch(batchTwo);
               computer.readBatch(batchTwo);
            }
        }
}

    /**
     * This method applies the advance method but instead of letting the window move forwards one-by-one
     * we move it by the integer value given as a parameter.
     *
     * @param offset integer value representing how much we are skipping forwards
     */

    public void advanceBy(int offset) throws IOException{
        if (offset <= 0) throw new IllegalArgumentException();
        for (int i = 0; i < offset; i++) advance();
    }

}