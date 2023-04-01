package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * This class represents an ADS-B message of type "Airborne Position".
 * With this class we want to calculate the altitude uf the aircraft.
 *
 * @author Paul Quesnot (347572)
 * @author Henri Antal (339444)
 */
public record AirbornePositionMessage
        (long timeStampNs, IcaoAddress icaoAddress, double altitude,
         int parity, double x, double y) implements Message {

    /**
     * Constructs an instance of the AirbornePositionMessage class.
     * Throws a NullPointerException if the IcaoAddress is null.
     * Throws an IllegalArgumentException if the given values do not meet the required constraints.
     *
     * @param timeStampNs The timestamp of the message in nanoseconds.
     * @param icaoAddress The ICAO address of the aircraft.
     * @param altitude    The altitude of the aircraft in feet.
     * @param parity      The parity bit of the message.
     * @param x           The longitude of the aircraft, between 0 and 1.
     * @param y           The latitude of the aircraft, between 0 and 1.
     */
    public AirbornePositionMessage {
        if (null == icaoAddress) throw new NullPointerException();
        Preconditions.checkArgument((0 <= timeStampNs)
            && ((0 == parity) || (1 == parity))
            && (0 <= x) && (1 > x)
            && (0 <= y) && (1 > y));
    }

    /**
     * Creates an instance of the AirbornePositionMessage class from a RawMessage object.
     * Returns null if the RawMessage object does not correspond to an airborne position message.
     *
     * @param rawMessage The RawMessage object to create the AirbornePositionMessage from.
     * @return An instance of the AirbornePositionMessage class.
     */
    public static AirbornePositionMessage of(final RawMessage rawMessage) {
        if (9 > rawMessage.typeCode() || 22 < rawMessage.typeCode() || 19 == rawMessage.typeCode()) return null;

        // extracting Bits from the payload of the rawMessage
        final long payload = rawMessage.payload();
        final double longitude = (Bits.extractUInt(payload, 0, 17)) / Math.pow(2, 17);
        final double latitude = (Bits.extractUInt(payload, 17, 17)) / Math.pow(2, 17);
        final int FORMAT = (int) ((payload >> 34) & 1);
        int ALT = Bits.extractUInt(payload, 36, 12);
        double computedAltitude = altitudeComputer(ALT);

        // if the computedAltitude is invalid, it has the value of -0xFFFFF
        if (-0xFFFFF == computedAltitude) return null;

        return new AirbornePositionMessage(rawMessage.timeStampNs(),
                rawMessage.icaoAddress(), computedAltitude, FORMAT, longitude, latitude);
    }

    /**
     * Computes the altitude of the aircraft from the given altitude code.
     * Returns -0xFFFFF if the computed altitude is invalid.
     * In this there are two cases how we compute the altitude.
     * Case one is that the bit with index 4 is equal to 1 then we use the first algorithm that is defined below the
     * comment with "// Q=1"
     * Case two is that the bit with index 4 is equal to 0 then we use the second algorithm that is defined below the
     * comment with "// Q=0"
     *
     * @param ALT The altitude code.
     * @return The altitude of the aircraft in translated from feet to meters.
     */
    public static double altitudeComputer(final int ALT) {
        //Q=1
        if (1 == Bits.extractUInt(ALT, 4, 1)) {
            final double altitudeInFeet = Bits.extractUInt(ALT, 0, 4) | (Bits.extractUInt(ALT, 5, 8) << 4);
            final double baseAltitude = -1000;

            return Units.convertFrom(altitudeInFeet * 25 + baseAltitude, Units.Length.FOOT);
        }

        //Q=0
        int MSBGray = 0;
        int LSBGray = 0;

        // reorganising the bits
        for (int i = 0; 5 > i; i += 2) {
            // D
            MSBGray |= ((Bits.extractUInt(ALT, i, 1) << (6 + i / 2)));
            // A
            MSBGray |= ((Bits.extractUInt(ALT, 6 + i, 1) << (3 + i / 2)));
            // B
            MSBGray |= ((Bits.extractUInt(ALT, 1 + i, 1) << (i / 2)));
            // C
            LSBGray |= ((Bits.extractUInt(ALT, 7 + i, 1) << i / 2));
        }

        final double MSB = AirbornePositionMessage.grayToBinary(MSBGray);
        double LSB = AirbornePositionMessage.grayToBinary(LSBGray);

        if (0 == LSB || 5 == LSB || 6 == LSB) return -0xFFFFF;
        if (7 == LSB) LSB = 5;
        if (1 == MSB % 2) LSB = (6 - LSB);

        return Units.convertFrom(-1300 + (MSB * 500) + (LSB * 100), Units.Length.FOOT);
    }

    /**
     * Converts a Gray code to a binary code.
     *
     * @param gray The Gray code to convert.
     * @return The binary code.
     */
    public static int grayToBinary(int gray) {
        int binary = gray;

        while (0 < gray) {
            gray >>= 1;
            binary ^= gray;
        }
        
        return binary;
    }
}
