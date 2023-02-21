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
        for(int i = 0; i < hexString.length(); ++i){
            switch(hexString.charAt(i)) {
                case 1,2,3,4,5,6,7,8,9,0 :
            }
        }
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
