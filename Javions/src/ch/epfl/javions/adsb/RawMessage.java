package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 *
 * @param timeStampNs
 * @param bytes representing the bytes that represent the messages
 */
public record RawMessage(long timeStampNs, ByteString bytes) {
    public final static int LENGTH = 14;
    public RawMessage{
        Preconditions.checkArgument(timeStampNs > 0 && LENGTH == bytes.size());
    }

    public static RawMessage of(long timeStampNs, byte[] bytes){
        Crc24 crc = new Crc24(Crc24.GENERATOR);
        if(crc.crc(bytes) != 0) return null;
        return new RawMessage(timeStampNs, new ByteString(bytes));
    }

    public static int size(byte byte0){
        if(byte0 == 17) return LENGTH;
        return 0;
    }

    public static int typeCode(long payLoad){
        return (int) payLoad >> 51;
    }

    public int downLinkFormat(){
        return bytes.byteAt(bytes.size()-1) >> 3;
    }

    public IcaoAddress icaoAddress(){
        byte[] byteAdress = new byte[3];
        for(int i = 2; i >= 0; --i){
            byteAdress[i] = (byte) bytes.byteAt(i);
        }
       ByteString address = new ByteString(byteAdress);
        return new IcaoAddress(address.toString());
    }

    public long payload(){
        long ME56To0 = bytes.bytesInRange(4,10);
        return ME56To0 ^= bytes().byteAt(4) >> 3;
    }

    /**
     * Gets byte in question for the 8 strongest bits of ME and then shifts it by 3 to isolate the 5 containing
     * the message time.
     *
     * @return integer value with the expected bits
     */
    public int typeCode(){
        return bytes.byteAt(4) >> 3;
    }
}
