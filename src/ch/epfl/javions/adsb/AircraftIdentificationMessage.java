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
    private static final int INVALID_VALUE = 0;
    private static final int ALPHABET_A = 1;
    private static final int ALPHABET_Z = 26;
    private static final int CHAR_0 = 48;
    private static final int CHAR_9 = 57;
    private static final int CHAR_SPACE = 32;
    private static final int ASCII_ALPHABET_A = 64;

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
        if (null == callSign || null == icaoAddress)
            throw new NullPointerException();
    }

    /**
     * Creates a new AircraftIdentificationMessage from a RawMessage object.
     *
     * @param rawMessage the RawMessage to create the AircraftIdentificationMessage from
     * @return a new AircraftIdentificationMessage, or null if the RawMessage is invalid
     */
    // TODO: 4/28/2023 ask about method
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {

        //computing the category
        if (INVALID_VALUE == rawMessage.typeCode())
            return null;

        //computing the CallSign
        StringBuilder sign = new StringBuilder();
        //intermediary is instantiated with the symbol corresponding to null
        char intermediary = '\0';
        long payload = rawMessage.payload();
        int b;

        for (int i = 42; 0 <= i; i -= 6) {
            b = Bits.extractUInt(payload, i, 6);
            // if b is an invalid chart null gets returned
            if (!isValidCharCode(b))
                return null;

            /**
             * The values that we check using if statements follow the given rules :
             *          - less or equal than 26 -> we attribute to intermediary the latin alphabet
             *          - less or equal than 26 -> we attribute to intermediary the characters of the numbers from 0 to 9
             *          - 32 -> we attribute to intermediary the space bar character
             * the values subtracted or added to b are for the corresponding values associated to the characters in ASCII
             */
            if (CHAR_0 <= b)
                intermediary = (char) ((b - CHAR_0) + '0');
            if (ALPHABET_Z >= b)
                intermediary = (char) (b + ASCII_ALPHABET_A);
            if (CHAR_SPACE == b)
                intermediary = CHAR_SPACE;
            sign.append(intermediary);
        }

        byte MSB = (byte) ((14 - rawMessage.typeCode()) << 4);
        byte LSB = (byte) (rawMessage.bytes().byteAt(4) & 0b111);
        int category = Byte.toUnsignedInt((byte) (MSB | LSB));

        String finishedSign = sign.toString();
        String strippedFinishedSign = finishedSign.stripTrailing();

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
        return (ALPHABET_A <= code && ALPHABET_Z >= code)
                || (CHAR_0 <= code && CHAR_9 >= code)
                || CHAR_SPACE == code;
    }
}

