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
    // I think the ByteString is actually an array, this would make a lot of sense for the rest of the
    //program
    private final ArrayList<Byte> finalBytes = new ArrayList<>();

    /**
     * This constructor takes the table of bytes feeded in and makes all the values positive before
     * cloning the table and storing it in the final byte table
     * @param bytes
     */
    public ByteString(byte[] bytes){
        byte[] nonSigne = new byte[bytes.length];
        for(int i = 0; i < bytes.length; ++i){
            nonSigne[i] = bytes[i];
        } /**
=======
        /**
>>>>>>> Stashed changes
         TODO i think this is wrong how can one just use abs(of each value in bytes because they are positive
         anyways (0-9,A-F) but if index 7 is 1 then java makes it negative so we need to change that, here is a
         link for it: https://stackoverflow.com/questions/4266756/can-we-make-unsigned-byte-in-java use this in
         byteAt, i think this would fix it, also our max would then be 256 and not 127... it fixed problem 3/11!!
        */
        for(int j = 0; j <  bytes.length; ++j){
            finalBytes.add(bytes[j]);
        }
    }

    /**
     *
     * @param hexString
     * @return
     */
    public static ByteString ofHexadecimalString(String hexString){

        if(hexString.length() % 2 == 1) throw new IllegalArgumentException();
        if(!hexString.matches("^[A-F0-9]+$")){throw new NumberFormatException();}

        HexFormat hf = HexFormat.of().withUpperCase();
        byte[] bytes = hf.parseHex(hexString);
        ByteString output = new ByteString(bytes);
        return output;
    }

    /**
     * @return size of the finalBytes Array
     */
    public int size(){
        return finalBytes.size();
    }

    /**
     *
     * @param index
     * @return the byte contained at the index
     */
    public int byteAt(int index){
        if (index < 0 || index > finalBytes.size()) throw new IndexOutOfBoundsException();
        byte byteAtIndex = finalBytes.get(index);
        return byteAtIndex & 0xFF;
    }

    public long bytesInRange(int fromIndex, int toIndex){
        long result;
        // need to use checkFromToIndex
        Objects.checkFromToIndex(fromIndex,toIndex,finalBytes.size());
        if(toIndex-fromIndex < 0 || toIndex-fromIndex > finalBytes.size()) throw new IndexOutOfBoundsException();
        if(toIndex-fromIndex >= 8) throw new IllegalArgumentException();
        for(int i = fromIndex; i < toIndex; ++i){
           finalBytes.get(i);
        }
        return 0;
    }


    public boolean equals(Object input){
        if (input instanceof ByteString that && input.equals(finalBytes)) return true;
        return false;
    }

    public int hashcode(){
        byte[] byteTable = new byte[finalBytes.size()];
        for (int i = 0; i < finalBytes.size(); ++i){
            byteTable[i] = finalBytes.get(i);
        }
        Arrays.hashCode(byteTable);
        return 0;
    }

}
