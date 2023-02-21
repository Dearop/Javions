package ch.epfl.javions;

public final class ByteString {

    public ByteString(byte[] bytes){


    }
    public static String ofHexadecimalString(String hexString){
        //what about the case when there is length == 0? //TODO we do this later too complicated
        if(hexString.length() % 2 == 1) throw new IllegalArgumentException();
        return "";
    }

}
