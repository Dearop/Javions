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
 * @param bytes       representing the bytes that represent the messages
 */
public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;
    private static final int CASize = 3;
    private static final int DFLocation = 0;
    private static final int messageLength = 56;
    private static final int typeCodeLength = 5;
    private static final Crc24 crc = new Crc24(Crc24.GENERATOR);

    public RawMessage(final long timeStampNs, final ByteString bytes) {
        Preconditions.checkArgument(0 <= timeStampNs && RawMessage.LENGTH == bytes.size());

        this.timeStampNs = timeStampNs;
        this.bytes = bytes;
    }

    /**
     * @param timeStampNs long value representing the time from the reception of the ads-b waves to the time of
     *                    processing the current instance of RawMessage.
     * @param bytes       byte table containing the message.
     * @return RawMessage with the parameters given to the function if the crc of the message is 0 or null otherwise
     */
    public static RawMessage of(final long timeStampNs, final byte[] bytes) {
        return 0 == crc.crc(bytes) ? new RawMessage(timeStampNs, new ByteString(bytes)) : null;
    }

    /**
     * @param byte0 first byte of the message
     * @return integer value representing either the length of the message if the Down Link Format
     * is the expected value (17) or 0 if it isn't
     */
    public static int size(final byte byte0) {
        return 17 == Byte.toUnsignedInt(byte0) >> 3 ? RawMessage.LENGTH : 0;
    }

    /**
     * @param payload long value representing the heart of the message
     * @return integer value representing the typeCode (first 5 bits of the payload long value) of the message
     */
    public static int typeCode(final long payload) {
        return Bits.extractUInt(payload, RawMessage.messageLength - RawMessage.typeCodeLength, RawMessage.typeCodeLength);
    }

    /**
     * @return integer value representing the DF format of the message
     */
    public int downLinkFormat() {
        return this.bytes.byteAt(RawMessage.DFLocation) >> RawMessage.CASize;
    }

    /**
     * @return Icao address contained in the message
     */
    public IcaoAddress icaoAddress() {
        final byte[] byteAddress = new byte[3];

        for (int i = 1; 4 > i; ++i) {
            byteAddress[i - 1] = (byte) this.bytes.byteAt(i);
        }

        final ByteString address = new ByteString(byteAddress);
        return new IcaoAddress(address.toString());
    }

    /**
     * @return long value representing the crucial part of the ADS-B message
     */
    public long payload() {
        return this.bytes.bytesInRange(4, 11);
    }

    /**
     * Gets byte in question for the 8 strongest bits of ME and then shifts it by 3 to isolate the 5 containing
     * the message time.
     *
     * @return integer value with the expected bits
     */
    public int typeCode() {
        return this.bytes.byteAt(4) >> 3;
    }
}
