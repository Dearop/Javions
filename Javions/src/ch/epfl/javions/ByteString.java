package ch.epfl.javions;

import java.util.HexFormat;
import java.util.Objects;

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
        } /**
         TODO i think this is wrong how can one just use abs(of each value in bytes because they are positive
         anyways (0-9,A-F) but if index 7 is 1 then java makes it negative so we need to change that, here is a
         link for it: https://stackoverflow.com/questions/4266756/can-we-make-unsigned-byte-in-java use this in
         byteAt, i think this would fix it, also our max would then be 256 and not 127
        */
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
        // changed index <= 0 because index 0 can be inside the byte
        if (index < 0 || index > 7) throw new IndexOutOfBoundsException();
        if (finalBytes[7] == 1){
            //Todo how do i change the whole value of finalBytes, also 0xFF is from link above
            //finalBytes &= 0xFF;
        }
        return finalBytes[index];
    }

    public long bytesInRange(int fromIndex, int toIndex){
        // need to use checkFromToIndex
        Objects.checkFromToIndex(fromIndex,toIndex,8);
        //if(toIndex-fromIndex < 0 || toIndex-fromIndex > finalBytes.length) throw new IndexOutOfBoundsException();
        //if(toIndex-fromIndex >= 4) throw new IllegalArgumentException();
        return 121212121;
    }

    //public boolean equals()
}
