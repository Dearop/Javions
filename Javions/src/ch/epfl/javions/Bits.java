package ch.epfl.javions;

/**
 * This is a utility class containing static methods for extracting a subset of the 64 bits from a long value.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public class Bits {

    private Bits(){}
    private static final int MIN_SIZE = 0, MIN_START = 0,
            MAX_SIZE = 32, MAX_START_PLUS_SIZE = 64,
            MIN_INDEX = 0, MAX_INDEX = 63;

    /**
     * This static method extracts a range of bits from the 64-bit vector value, starting at bit index start and
     * continuing for size bits, and interprets the resulting range as an unsigned integer value.
     *
     * @param value long value representing the 64-bit vector from which to extract the bits.
     * @param start int value representing the index of the first bit to extract, counting from the least significant
     *              bit (index 0)
     * @param size  int value representing the number of bits to extract
     * @return int value representing the unsigned integer value of the extracted bit range
     * @throws IllegalArgumentException  if size is not strictly greater than 0 and strictly less than 32
     * @throws IndexOutOfBoundsException if the bit range described by start and size is not completely contained
     *                                   within the range of bit indices from 0 (exclusive) to 64 (exclusive)
     */
    public static int extractUInt(final long value, final int start, final int size) {

        if (MIN_SIZE >= size || MAX_SIZE <= size)
            throw new IllegalArgumentException();
        else if (MIN_START > start || MAX_START_PLUS_SIZE < start + size)
            throw new IndexOutOfBoundsException();

        long extractedValue = value >>> (start);
        long changedValueSize = (long) (Math.pow(2, size) - 1);

        extractedValue &= changedValueSize;
        return (int) extractedValue;
    }


    /**
     * This static method tests whether the bit at the given index in the 64-bit value is 1.
     *
     * @param value long value representing the 64-bit vector to test.
     * @param index int value representing the index of the bit to test, counting from the least
     *              significant bit (index 0)
     * @return boolean value that is true if the bit at the given index in the 64-bit value is 1, and false otherwise
     * @throws IndexOutOfBoundsException if the bit index is not within the range of valid bit indices
     *                                   from 0 (exclusive) to 64 (exclusive)
     */
    public static boolean testBit(final long value, final int index) {
        if (MIN_INDEX > index || MAX_INDEX < index)
            throw new IndexOutOfBoundsException();

        long extractedValue = value >>> index;

        // test if the LSB of extracted value is 1
        return 1 == (extractedValue &= 1);
    }
}

