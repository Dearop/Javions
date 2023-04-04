package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Creates a record that identifies the messages of the aircraft and checks that they are correct.
 *
 * @param timeStampNs the timestamp of the message in nanoseconds
 * @param icaoAddress the unique identifier of the aircraft
 * @param category    the category of the aircraft
 * @param callSign    the call sign of the aircraft
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public record AircraftIdentificationMessage
        (long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {
    /**
     * Creates a new AircraftIdentificationMessage with the given timestamp, ICAO address, category and call sign.
     *
     * @param timeStampNs the timestamp of the message in nanoseconds
     * @param icaoAddress the unique identifier of the aircraft
     * @param category    the category of the aircraft
     * @param callSign    the call sign of the aircraft
     * @throws NullPointerException if either the icaoAddress or callSign is null
     */
    public AircraftIdentificationMessage {
        Preconditions.checkArgument(0 <= timeStampNs);
        if (null == callSign || null == icaoAddress) throw new NullPointerException();
    }

    /**
     * Creates a new AircraftIdentificationMessage from a RawMessage object.
     *
     * @param rawMessage the RawMessage to create the AircraftIdentificationMessage from
     * @return a new AircraftIdentificationMessage, or null if the RawMessage is invalid
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {

        //computing the category
        if (0 == rawMessage.typeCode()) return null;

        //computing the CallSign
        StringBuilder sign = new StringBuilder();
        //intermediary is instantiated with the symbol corresponding to null
        char intermediary = '\0';
        long payload = rawMessage.payload();
        int b;

        for (int i = 42; 0 <= i; i -= 6) {
            b = Bits.extractUInt(payload, i, 6);
            // if b is an invalid chart null gets returned
            if (!isValidCharCode(b)) return null;

            /**
             * The values that we check using if statements follow the given rules :
             *          - less or equal than 26 -> we attribute to intermediary the latin alphabet
             *          - less or equal than 26 -> we attribute to intermediary the characters of the numbers from 0 to 9
             *          - 32 -> we attribute to intermediary the space bar character
             * the values subtracted or added to b are for the corresponding values associated to the characters in ASCII
             */
            if (48 <= b) intermediary = (char) ((b - 48) + '0');
            if (26 >= b) intermediary = (char) (b + 64);
            if (32 == b) intermediary = 32;
            sign.append(intermediary);
        }

        byte MSB = (byte) ((14 - rawMessage.typeCode()) << 4);
        byte LSB = (byte) (rawMessage.bytes().byteAt(4) & 0b111);
        int category = Byte.toUnsignedInt((byte) (MSB | LSB));

        String finishedSign = sign.toString();
        String strippedFinishedSign = finishedSign.stripTrailing();

        if (finishedSign.equals(strippedFinishedSign)) return null;

        CallSign AimCallSign = new CallSign(strippedFinishedSign);
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, AimCallSign);
    }

    /**
     * Checks whether a given number is valid, meaning it's value corresponds to a given code such that it's value
     * is in the following intervals:
     *  - 1 (included) and 26 (included),
     *  - 48 (included) and 57 (included)
     *  - 32
     *
     * Valid character codes include alphanumeric characters and the space character.
     *
     * @param code the ASCII code of the character
     * @return true if the character code is valid, false otherwise
     */
    private static boolean isValidCharCode(int code) {
        return (1 <= code && 26 >= code) || (48 <= code && 57 >= code) || 32 == code;
    }
}

