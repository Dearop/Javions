package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class ByteString {
    private final byte[] bytes;

    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * @param hexString the hexadecimal string to convert
     * @return the ByteString array
     * @throws IllegalArgumentException if the input string has an odd length
     * @throws NumberFormatException if the input string contains a non-hexadecimal character
     */

    public static ByteString ofHexadecimalString(String hexString){
        if(hexString.length() % 2 == 1) throw new IllegalArgumentException();
        if(!hexString.matches("^[a-fA-F0-9]+$")){throw new NumberFormatException();}
        byte[] bytes = HexFormat.of().withUpperCase().parseHex(hexString);
        return new ByteString(bytes);
    }

    /**
     * @return int representing the number of bytes in the ByteString
     */
    public int size() {
        return bytes.length;
    }

    /**
     * @param index int representing the index of the byte to retrieve
     * @return int representing the byte at the specified index, interpreted as an unsigned value
     * @throws IndexOutOfBoundsException if the index is negative or greater than the size of the ByteString
     */
    public int byteAt(int index) {
        if (index < 0 || index > bytes.length) throw new IndexOutOfBoundsException();
        byte byteAtIndex = bytes[index];
        return byteAtIndex & 0xFF;
    }

    /**
     * @param fromIndex int representing the starting index of the range (inclusive)
     * @param toIndex int representing the ending index of the range (exclusive)
     * @return long value representing the bytes between fromIndex and toIndex
     * @throws IndexOutOfBoundsException if the  range described by fromIndex and toIndex is
     *         not entirely within the bounds of the ByteString
     * @throws IllegalArgumentException If the range between fromIndex and toIndex is greater than or equal
     *         to the size of a long
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, this.bytes.length);
        // went to go and look at the Long class; there's a constant for how many bytes are in a long
        if (toIndex - fromIndex >= Long.BYTES) {
            throw new IllegalArgumentException();
        }
        long result = 0;
        for (int i = fromIndex; i < toIndex; ++i) {
            result <<= 8;
            result |= (this.bytes[i] & 0xFF);
        }
        return result;
    }


    /**
     * @param that0 object to compare against the bytes attribute
     * @return boolean indicating whether that0 is equal to the attribute or not
     */
    public boolean equals(Object that0) {
        if (that0 instanceof ByteString that) return Arrays.equals(bytes, that.bytes);
        return false;
    }

    /**
     * @return the hashcode of the table of bytes
     */
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    /**
     * @return the String of bytes in hexdecimal
     */
    public String toString() {
        return HexFormat.of().withUpperCase().formatHex(bytes);
    }
}
