package ch.epfl.javions;

public final class ByteString {
    private final byte[] finalBytes;
    public byte[] bytes;
    public ByteString(byte[] bytes){
        this.bytes = bytes;
        finalBytes = bytes.clone();
    }
    public static ByteString ofHexadecimalString(String hexString){
        //what about the case when there is length == 0?
        // @TODO we do this later too complicated
        if(hexString.length() % 2 == 1) throw new IllegalArgumentException();
        return "";
    }

    public int size(){
        return finalBytes.length;
    }

    public int byteAt(int index){
        if (index <= 0 ^ index > 7) throw new IndexOutOfBoundsException();
        return finalBytes[index];
    }

    public long bytesInRange(int fromIndex, int toIndex){

    }
}
