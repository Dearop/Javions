package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

/**
 * In this class we create two tables called batchOne and batchTwo that take in the information we get from PowerComputer
 * when we read the stream that is given through the constructor. We read through these two tables with the help
 * of the methods that we created in this class. To access the data inside the batches we create a window with a
 * windowSize that is given in the parameter of the constructor. This window doesn't store any data, but it defines,
 * what part of the batches we can access. It always has the same starting position as the integer positionCounter.
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class PowerWindow {
    public PowerComputer computer;
    private final int windowSize;
    private int positionCounter;
    public final static int batchSize = (int) Math.pow(2, 16);
    private int[] batchOne;
    private int[] batchTwo;
    private final int availableStream;

    /**
     * this constructor first checks that the window size is smaller or equal to the batch size. If not an
     * IllegalArgumentException gets thrown. Then we check how long the given stream is and save that number. We will
     * use it later for the isFull() method. Then a new PowerComputer gets created with the parameters batchSize and
     * stream. In the end the batchOne and batchTwo get filled up with the values that we get from PowerComputer.
     *
     * @param stream     stream that contains zero's and one's that will get decoded inside SampleDecoder.
     * @param windowSize windowSize that defines how many numbers we can access from the batches.
     * @throws IOException Throws Exception when there is a problem reading the stream.
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        if (windowSize <= 0 || windowSize > batchSize)
            throw new IllegalArgumentException("windowSize out of bound, size : " + windowSize);
        availableStream = stream.available();
        this.windowSize = windowSize;
        this.computer = new PowerComputer(stream, batchSize);
        batchOne = new int[batchSize];
        batchTwo = new int[batchSize];
        computer.readBatch(batchOne);
        computer.readBatch(batchTwo);
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

    /** TODO ask if this is allowed
     * @return boolean value that says if the window is full.
     * This statement is true as long as all the inputs are coming from within the stream size.
     * Once the positionCounter + windowSize is bigger than the stream/4 then it return false.
     */
    public boolean isFull() {
        return availableStream/4 >= positionCounter +windowSize ;
    }

    /**
     * With this function we can access the values inside the window. If the window contains information from two batches
     * we check from which batch we need to get the information from which we do in the return line.
     * @param i integer value representing the position in the window we want to get
     * @return PowerComputerValue computed at the ith spot of the window
     */
    public int get(int i) {
        if (i < 0 || i >= windowSize) throw new IllegalArgumentException();
        int positionInBatch = positionCounter % batchSize;
        return positionInBatch + i < batchSize ? batchOne[positionInBatch + i] : batchTwo[(positionInBatch + i) % batchSize];
    }

    /**
     * Moves the position by one step forward and then checks if the position entered the end of the batch. If this
     * happens the first batch copies the values from the second batch and the second batch loads new values from the
     * Power computer.
     */
    public void advance() throws IOException{
        ++positionCounter;
        int positionInsideBatch = positionCounter % batchSize;
        // we check if the new position is at the end of the current priority batch.
        if (positionInsideBatch == batchSize-1) {
            // because batchTwo is now priority batch we can replace all values inside batchOne with
            // the new info from output
            batchOne = batchTwo.clone();
            computer.readBatch(batchTwo);
        }
    }

    /**
     * This method applies the advance method for a certain number of times which is specified
     * with the parameter offset.
     *
     * @param offset integer value representing how much we are skipping forwards
     */
    public void advanceBy(int offset) throws IOException{
        if (offset <= 0) throw new IllegalArgumentException();
        for (int i = 0; i < offset; i++) advance();
    }

}