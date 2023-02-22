package ch.epfl.javions;

import java.util.HexFormat;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class ByteString {
    private final byte[] finalBytes;

    /**
     * This constructor takes the table of bytes feeded in and makes all the values positive before
     * cloning the table and storing it in the final byte table
     * @param bytes
     */
    public ByteString(byte[] bytes){
        byte[] nonSigne = new byte[bytes.length];
        for(int i = 0; i < bytes.length; ++i){
            nonSigne[i] = (byte) Math.abs(bytes[i]);
        }
        finalBytes = nonSigne.clone();
    }
    public static ByteString ofHexadecimalString(String hexString){

        if(hexString.length() % 2 == 1) throw new IllegalArgumentException();
        if(!hexString.matches("^[A-F0-9]+$")){throw new NumberFormatException();}

        HexFormat hf = HexFormat.of().withUpperCase();
        byte[] bytes = hf.parseHex(hexString);
        ByteString output = new ByteString(bytes);
        return output;
    }

    public int size(){
        return finalBytes.length;
    }

    public int byteAt(int index){
        if (index <= 0 || index > 7) throw new IndexOutOfBoundsException();
        return finalBytes[index];
    }

    public long bytesInRange(int fromIndex, int toIndex){
        if(toIndex-fromIndex < 0 || toIndex-fromIndex > finalBytes.length) throw new IndexOutOfBoundsException();
        if(toIndex-fromIndex >= 4) throw new IllegalArgumentException();
        return 0;
    }

    public boolean equals()
}
