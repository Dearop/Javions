package ch.epfl.javions;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public class Bits {
    private Bits() {
    }

    /**
     * In this function we extract a number out of a given value. The value is given as a binary Number.
     * In the first three lines we look at the exceptions. The size can't be bigger than 31 or smaller than 0.
     *
     * @param value
     * @param start
     * @param size
     * @throws IllegalArgumentException if size is strictly smaller than 0 or start + size is bigger or equal to 64
     * @return
     */
    public static int extractUInt(long value, int start, int size) {

        if (size < 0 || size > 32) throw new IllegalArgumentException();
        else if (start < 0 || start + size > 63) throw new IndexOutOfBoundsException();

        int extractedValue = (int) value >>> (start);
        int changedValueSize = (int) (Math.pow(2, size) - 1);  //if size for example is 5 we get 31 which would be written
        // as ..011111
        extractedValue &= changedValueSize;
        return extractedValue;
    }


    /**
     * In this function, we test if this index is what we want and throw an Exception is it isn't
     * If the number on the index(parameter)-ed value of value is equal to 1 we return true
     * otherwise we return false.
     *
     * @param value
     * @param index
     * @throws IndexOutOfBoundsException if index is smaller than 0 or index is strictly bigger than 63
     * @return
     */
    public static boolean testBit(long value, int index) {
        if (index < 0 || index > 63) throw new IndexOutOfBoundsException();
        int extractedValue = (int) value >>> index;
        if((extractedValue &= 1) == 1) return true;
        return false;
    }
}

