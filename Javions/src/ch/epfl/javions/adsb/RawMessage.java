package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Record representing a message in its raw form before extraction of the different parts of the message.
 *
 * @param timeStampNs
 * @param bytes representing the bytes that represent the messages
 */
public record RawMessage(long timeStampNs, ByteString bytes) {
    public final static int LENGTH = 14;
    private final static int CAStart= 0;
    private final static int CASize = 3;
    private final static int DFSize = 5;
    private final static int DFLocation = 0;
    private final static int ExpectedDF = 17;
    private static Crc24 crc = new Crc24(Crc24.GENERATOR);

    public RawMessage(long timeStampNs, ByteString bytes){
        Preconditions.checkArgument(timeStampNs > 0 && LENGTH == bytes.size());
        this.timeStampNs = timeStampNs;
        this.bytes = bytes;
    }

    /**
     *
     * @param timeStampNs long value representing the time from the reception of the ads-b waves to the time of
     *                    processing the current instance of RawMessage.
     * @param bytes byte table containing the message.
     * @return RawMessage with the parameters given to the function if the crc of the message is 0 or null otherwise
     */
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

    /**
     * @param byte0 first byte of the message
     * @return integer value representing either the length of the message if the Down Link Format
     * is the expected value (17) or 0 if it isn't
     */
    public static int size(byte byte0){
        byte shiftedByte = (byte) (byte0 >> 3);
        if(Byte.toUnsignedInt(shiftedByte) != ExpectedDF) return LENGTH;
        return 0;
    }

    public static int typeCode(long payload){
        return (int) (payload >> 51);
    }

    /**
     * @return integer value representing the DF format of the message
     */
    public int downLinkFormat(){
        return bytes.byteAt(DFLocation) >> CASize;
    }

    /**
     *
     * @return Icao address contained in the message
     */
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
