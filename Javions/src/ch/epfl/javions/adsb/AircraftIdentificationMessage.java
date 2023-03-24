package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Creates a record that identifies the messages of the aircraft and checks that they are correct.
 *
 * @param timeStampNs the timestamp of the message in nanoseconds
 * @param icaoAddress the unique identifier of the aircraft
 * @param category the category of the aircraft
 * @param callSign the call sign of the aircraft
 */
public record AircraftIdentificationMessage
        (long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {
    /**
     * Creates a new AircraftIdentificationMessage with the given timestamp, ICAO address, category and call sign.
     *
     * @param timeStampNs the timestamp of the message in nanoseconds
     * @param icaoAddress the unique identifier of the aircraft
     * @param category the category of the aircraft
     * @param callSign the call sign of the aircraft
     * @throws NullPointerException if either the icaoAddress or callSign is null
     */
    public AircraftIdentificationMessage{
        Preconditions.checkArgument(timeStampNs >=0);
        if(callSign == null || icaoAddress == null) throw new NullPointerException();
    }

    /**
     * Creates a new AircraftIdentificationMessage from a RawMessage object.
     *
     * @param rawMessage the RawMessage to create the AircraftIdentificationMessage from
     * @return a new AircraftIdentificationMessage, or null if the RawMessage is invalid
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        //computing the category
        if(rawMessage.typeCode() == 0) return null;

        //computing the CallSign
        StringBuilder sign = new StringBuilder();
        char intermediary = '\0';
        long payload = rawMessage.payload();
        int b;
        for (int i = 42; i >= 0; i-=6) {
            b = Bits.extractUInt(payload, i, 6);
            if(!isValidCharCode(b)) return null;
            // the values I substract or add to b are for the corresponding values associated to the characters in ASCII
            if(b >= 48)intermediary = (char) ((b-48)+'0');
            if(b <= 26) intermediary = (char) (b+64);
            if(b == 32) intermediary = (char) 32;
            sign.append(intermediary);
        }

        byte MSB = (byte) ((14 - rawMessage.typeCode()) << 4);
        byte LSB = (byte) (rawMessage.bytes().byteAt(4) & 0b111);
        int category = Byte.toUnsignedInt((byte) (MSB | LSB));

        String finishedSign = sign.toString();
        String strippedFinishedSign = finishedSign.stripTrailing();

        if(finishedSign.equals(strippedFinishedSign)) return null;
        // weird, this line gives us the right messages, what in the actual fuck
        CallSign callSign1 = new CallSign(strippedFinishedSign);
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign1);
    }

    /**
     * Checks whether a given ASCII character code is valid.
     *
     * Valid character codes include alphanumeric characters and the space character.
     *
     * @param code the ASCII code of the character
     * @return true if the character code is valid, false otherwise
     */
    private static boolean isValidCharCode(int code) {
        return (code >= 1 && code <= 26) || (code >= 48 && code <= 57) || code == 32;
    }
}

