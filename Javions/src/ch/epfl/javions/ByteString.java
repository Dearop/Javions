package ch.epfl.javions;

import java.util.ArrayList;
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
        if(!hexString.matches("^[A-F0-9]+$")){throw new NumberFormatException();}
        byte[] bytes = HexFormat.of().withUpperCase().parseHex(hexString);
        return new ByteString(bytes);
    }

    /**
     * @return size of the finalBytes Array
     */
    public int size(){
        return bytes.length;
    }

    /**
     *
     * @param index
     * @return the byte contained at the index
     */
    public int byteAt(int index){
        if (index < 0 || index > bytes.length) throw new IndexOutOfBoundsException();
        byte byteAtIndex = bytes[index];
        return byteAtIndex & 0xFF;
    }

    public long bytesInRange(int fromIndex, int toIndex){
        Objects.checkFromToIndex(fromIndex,toIndex,this.bytes.length);
        // went to go and look at the Long class; there's a constant for how many bytes are in a long
        if(toIndex - fromIndex >= Long.BYTES){
            throw new IllegalArgumentException();
        }
        long result = 0;
        for (int i = fromIndex; i < toIndex; ++i){
            result <<=8;
            result |= (this.bytes[i] & 0xFF);
        }
        return result;
    }


    public boolean equals(Object that0){
        if (that0 instanceof ByteString that) return Arrays.equals(bytes, that.bytes);
        return false;
    }

    public int hashcode(){
        return Arrays.hashCode(bytes);
    }

    public String toString(){
        return HexFormat.of().withUpperCase().formatHex(bytes);
    }
}
