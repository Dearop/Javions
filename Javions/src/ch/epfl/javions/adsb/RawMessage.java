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
    public final static int LENGTH = 14;
    private final static int CAStart= 0;
    private final static int CASize = 3;
    private final static int DFSize = 5;

    private static Crc24 crc = new Crc24(Crc24.GENERATOR);

    public RawMessage(long timeStampNs, ByteString bytes){
        Preconditions.checkArgument(timeStampNs > 0 && LENGTH == bytes.size());
        this.timeStampNs = timeStampNs;
        this.bytes = bytes;
    }

    public static RawMessage of(long timeStampNs, byte[] bytes){
        byte[] crcByte = new byte[3];
        // getting the 3 last bytes to check for crc TODO this probably does not work we only get 3 crc=0 when there should be 384
        for (int i = 0; i < 3; i++) {
            crcByte[i] = bytes[i+11];
        }
        //System.out.println(crc.crc(crcByte));
        if(crc.crc(crcByte) != 0) return null;

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
        byte[] byteAddress = new byte[3];
        for(int i = 1; i < 4; ++i){
            byteAddress[i-1] = (byte) bytes.byteAt(i);
        }
        ByteString address = new ByteString(byteAddress);
        return new IcaoAddress(address.toString());
    }

    /**
     *
     * @return long value representing the crucial part of the ADS-B message
     */
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
