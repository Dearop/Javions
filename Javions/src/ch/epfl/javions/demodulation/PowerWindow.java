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
    private final static int batchSize = 2400;
    private int[] window;
    public int[] batchOne;
    public int[] batchTwo;
    private boolean batchOneActive;
    private int tableCounter;
    private InputStream stream;

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        if (windowSize <= 0 || windowSize > batchSize)
            throw new IllegalArgumentException("windowSize out of bound, size : " + windowSize);
        this.windowSize = windowSize;
        window = new int[windowSize];
        batchOne = new int[batchSize];
        this.computer = new PowerComputer(stream, batchSize);
        batchOne = new int[batchSize];
        batchTwo = new int[batchSize];
        computer.readBatch(batchOne);
        this.stream = stream;
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
        if (i + (positionCounter % batchSize) > batchSize) {
            /**
             * then take value from non-active batch
             * example for following code why it is written the way it is:
             * positioncounter = 198, batchsize = 100, i = 6
             * we want position 4 from batch that is not active
             */
            if (batchOneActive) return batchTwo[(i + positionCounter) % batchSize];
            else return batchOne[(i + positionCounter) % batchSize];
        } else { // here we want the information from the batch that is active because the get i stays inside the batch.
            return window[i];
        }
    }

    /**
     * Moves either the first or the second batch forwards by one which simulates
     */
    public void advance() {
        ++positionCounter;
        int positionInsideBatch = positionCounter % batchSize;
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

                // we "move" the window by one.
                for (int i = 1; i < windowSize; i++)
                    window[i - 1] = window[i];

                // example posInsideB = 150, windowSize = 50, batchSize = 200, then we add to the window the value
                // from batchTwo (with modulo we see which value has to be taken)
                window[windowSize - 1] = batchTwo[(positionInsideBatch + windowSize) % batchSize];


                // case where we just move by one and also get the new window value from the priority batch.
            } else {
                for (int i = 1; i < windowSize; i++)
                    window[i - 1] = window[i];
                window[windowSize - 1] = batchOne[(positionInsideBatch + windowSize) % batchSize];
            }


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

                // we "move" the window by one.
                for (int i = 1; i < windowSize; i++)
                    window[i - 1] = window[i];

                // example posInsideB = 150, windowSize = 50, batchSize = 200, then we add to the window the value
                // from batchOne (with modulo we see which value has to be taken)
                window[windowSize - 1] = batchOne[positionInsideBatch + windowSize % batchSize];


                // case where we just move by one and also get the new window value from the priority batch.
            } else {
                for (int i = 1; i < windowSize; i++)
                    window[i - 1] = window[i];
                window[windowSize - 1] = batchOne[(positionInsideBatch + windowSize) % batchSize];
            }


        }
    }



    public void advanceBy(int offset) {
        //todo offset can't be bigger than window size no???
        if(offset >= windowSize) throw new IllegalArgumentException();
        if (offset <= 0) throw new IllegalArgumentException();
        for (int i = 0; i < offset; i++) advance();
    }

}