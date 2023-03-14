package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
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
    private final static int LENGTH = 14;
    private final static int CAStart= 0;
    private final static int CASize = 3;
    private final static int DFSize = 5;
    private final static int timeBetweenTwoBatches = 120000;

    public RawMessage(long timeStampNs, ByteString bytes){
        Preconditions.checkArgument(timeStampNs > 0 && LENGTH == bytes.size());
        this.timeStampNs = timeStampNs;
        this.bytes = bytes;
    }

    public static RawMessage of(long timeStampNs, byte[] bytes){
        Crc24 crc = new Crc24(Crc24.GENERATOR);
        if(crc.crc(bytes) != 0) return null;
        timeStampNs += 120000;
        return new RawMessage(timeStampNs, new ByteString(bytes));
    }

    public static int size(byte byte0){
        if(Bits.extractUInt(byte0,CAStart + CASize,DFSize) == 17) return LENGTH;
        return 0;
    }

    public static int typeCode(long payload){
        return (int) (payload >> 51);
    }

    public int downLinkFormat(){
        return bytes.byteAt(0) >> 3;
    }

    public IcaoAddress icaoAddress(){
        byte[] byteAdress = new byte[3];
        for(int i = 1; i < 4; ++i){
            byteAdress[i-1] = (byte) bytes.byteAt(i);
        }
       ByteString address = new ByteString(byteAdress);
        return new IcaoAddress(address.toString());
    }

    /**
     *
     * @return
     */
    // TODO: 3/12/2023 Get this to work 
    public long payload(){
        return bytes.bytesInRange(4,11);
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
