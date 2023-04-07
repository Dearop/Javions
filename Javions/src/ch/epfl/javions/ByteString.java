package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * This is an immutable class representing a sequence of unsigned bytes. Instances of this class are very similar to
 * a byte[] array, with the two differences being that ByteString is immutable, so it is not possible to change the
 * bytes that an instance contains once it has been created, and the bytes are interpreted as unsigned.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class ByteString {
    private final byte[] bytes;

    private final int byteFullOfOnes = 0xFF;

    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * This method returns a ByteString representing the sequence of bytes corresponding to the input hexadecimal
     * string. It throws an IllegalArgumentException if the input string has an odd length, or a NumberFormatException
     * if it contains a character that is not a valid hexadecimal digit.
     *
     * @param hexString the hexadecimal string to convert
     * @return the ByteString array
     * @throws IllegalArgumentException if the input string has an odd length
     * @throws NumberFormatException    if the input string contains a non-hexadecimal character
     */
    public static ByteString ofHexadecimalString(String hexString) {

        // hexString length is odd then return Exception
        if (1 == hexString.length() % 2)
            throw new IllegalArgumentException();

        if (!hexString.matches("^[a-fA-F0-9]+$"))
            throw new NumberFormatException();

        byte[] bytes = HexFormat.of().withUpperCase().parseHex(hexString);
        return new ByteString(bytes);
    }

    /**
     * This method returns the size of the ByteString in bytes.
     *
     * @return int representing the number of bytes in the ByteString
     */
    public int size() {
        return this.bytes.length;
    }

    /**
     * This method returns the byte at the specified index, interpreted as an unsigned value.
     * It throws an IndexOutOfBoundsException if the index is out of range.
     *
     * @param index int representing the index of the byte to retrieve
     * @return int representing the byte at the specified index, interpreted as an unsigned value
     * @throws IndexOutOfBoundsException if the index is negative or greater than the size of the ByteString
     */
    public int byteAt(int index) {
        if (0 > index || index > this.bytes.length)
            throw new IndexOutOfBoundsException();

        byte byteAtIndex = this.bytes[index];
        return byteAtIndex & byteFullOfOnes;
    }

    /**
     * This method returns the bytes between the fromIndex (inclusive) and toIndex (exclusive) as a long value.
     * The byte at toIndex is the least significant byte of the result. It throws an IndexOutOfBoundsException if the
     * range described by fromIndex and toIndex is not entirely within the bounds of the ByteString, and an
     * IllegalArgumentException if the range is greater than or equal to the size of a long.
     *
     * @param fromIndex int representing the starting index of the range (inclusive)
     * @param toIndex   int representing the ending index of the range (exclusive)
     * @return long value representing the bytes between fromIndex and toIndex
     * @throws IndexOutOfBoundsException if the  range described by fromIndex and toIndex is
     *                                   not entirely within the bounds of the ByteString
     * @throws IllegalArgumentException  If the range between fromIndex and toIndex is greater than or equal
     *                                   to the size of a long
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, bytes.length);

        if (Long.BYTES <= toIndex - fromIndex)
            throw new IllegalArgumentException();

        long result = 0;

        for (int i = fromIndex; i < toIndex; ++i) {
            result <<= Byte.SIZE;
            result |= (bytes[i] & byteFullOfOnes);
        }

        return result;
    }


    /**
     * This method returns true if and only if the value passed to it is an instance of ByteString and
     * its bytes are identical to those of the receiver.
     *
     * @param that0 object to compare against the bytes attribute
     * @return boolean indicating whether that0 is equal to the attribute or not
     */
    public boolean equals(Object that0) {
        if (that0 instanceof ByteString that) return Arrays.equals(this.bytes, that.bytes);
        return false;
    }

    /**
     * This method returns the hash code of the ByteString instance. The hash code is calculated using the
     * Arrays.hashCode(byte[]) method applied to the byte array containing the bytes of the ByteString.
     *
     * @return int value representing the hash code of the ByteString
     */
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    /**
     * This method returns a string representation of the bytes in the ByteString in hexadecimal format.
     * Each byte is represented by exactly two characters.
     *
     * @return String value representing the bytes in the ByteString in hexadecimal format
     */
    public String toString() {
        return HexFormat.of().withUpperCase().formatHex(this.bytes);
    }
}
