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
        (long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) implements Message {
    
    private static final int LON_CPR_START = 0;
    private static final int LON_CPR_SIZE = 17;
    private static final int LAT_CPR_START = 17;
    private static final int LAT_CPR_SIZE = 17;

    private static final int FORMAT_START = 34;
    private static final int ALT_START = 36;
    private static final int ALT_SIZE = 12;

    private static final int BASE_ALTITUDE_Q1 = -1000;
    private static final int BASE_ALTITUDE_Q0 = -1300;

    /**
     * Constructs an instance of the AirbornePositionMessage class.
     *
     * @param timeStampNs The timestamp of the message in nanoseconds.
     * @param icaoAddress The ICAO address of the aircraft.
     * @param altitude    The altitude of the aircraft in feet.
     * @param parity      The parity bit of the message.
     * @param x           The longitude of the aircraft, between 0 and 1.
     * @param y           The latitude of the aircraft, between 0 and 1.
     * @throws NullPointerException if the IcaoAddress is null.
     * @throws IllegalArgumentException if the given values do not meet the required constraints.
     */
    public AirbornePositionMessage {
        if (null == icaoAddress) throw new NullPointerException();
        Preconditions.checkArgument((0 <= timeStampNs));
        Preconditions.checkArgument((0 == parity) || (1 == parity));
        Preconditions.checkArgument((0 <= x) && (1 > x));
        Preconditions.checkArgument((0 <= y) && (1 > y));
    }

    /**
     * Creates an instance of the AirbornePositionMessage class from a RawMessage object.
     *
     * @param rawMessage The RawMessage object from which we will create the AirbornePositionMessage.
     * @return An instance of the AirbornePositionMessage class with the data contained in the raw message.
     * @return null if the RawMessage object does not correspond to an airborne position message.
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {

        // extracting Bits from the payload of the rawMessage
        long payload = rawMessage.payload();
        double longitude = (Bits.extractUInt(payload, LON_CPR_START, LON_CPR_SIZE)) / Math.scalb(1, 17);
        double latitude = (Bits.extractUInt(payload, LAT_CPR_START, LAT_CPR_SIZE)) / Math.scalb(1, 17);

        int FORMAT = (int) ((payload >> FORMAT_START) & 1);
        int ALT = Bits.extractUInt(payload, ALT_START, ALT_SIZE);
        double computedAltitude = altitudeComputer(ALT);

        // if the computedAltitude is invalid, it has the value of -0xFFFFF
        if (Double.isNaN(computedAltitude))
            return null;

        return new AirbornePositionMessage(rawMessage.timeStampNs(),
                rawMessage.icaoAddress(), computedAltitude, FORMAT, longitude, latitude);
    }

    /**
     * Computes the altitude of the aircraft from the given altitude code.
     * In this there are two cases how we compute the altitude.
     * Case one is that the bit with index 4 is equal to 1 then we use the first algorithm that is defined below the
     * comment with "// Q=1"
     * Case two is that the bit with index 4 is equal to 0 then we use the second algorithm that is defined below the
     * comment with "// Q=0"
     *
     * @param ALT The altitude code.
     * @return The altitude of the aircraft in translated from feet to meters.
     * @return -0xFFFFF if the computed altitude is invalid.
     */
    public static double altitudeComputer(int ALT) {
        //Q=1
        if (1 == Bits.extractUInt(ALT, 4, 1)) {
            double altitudeInFeet = Bits.extractUInt(ALT, 0, 4) | (Bits.extractUInt(ALT, 5, 8) << 4);
            return Units.convertFrom(altitudeInFeet * 25 + BASE_ALTITUDE_Q1, Units.Length.FOOT);
        }

        //Q=0
        int MSBGray = 0;
        int LSBGray = 0;

        // reorganising the bits
        for (int i = 0; 5 > i; i += 2) {
            // part D of the altitude bit expression
            MSBGray |= ((Bits.extractUInt(ALT, i, 1) << (6 + i / 2)));
            // part A of the altitude bit expression
            MSBGray |= ((Bits.extractUInt(ALT, 6 + i, 1) << (3 + i / 2)));
            // part B of the altitude bit expression
            MSBGray |= ((Bits.extractUInt(ALT, 1 + i, 1) << (i / 2)));
            // part C of the altitude bit expression
            LSBGray |= ((Bits.extractUInt(ALT, 7 + i, 1) << i / 2));
        }

        double MSB = grayToBinary(MSBGray);
        double LSB = grayToBinary(LSBGray);

        // these three lines handle special cases about the LSB
        if (0 == LSB || 5 == LSB || 6 == LSB)
            return Double.NaN;
        if (7 == LSB)
            LSB = 5;
        if (1 == MSB % 2)
            LSB = (6 - LSB);

        return Units.convertFrom(BASE_ALTITUDE_Q0 + (MSB * 500) + (LSB * 100), Units.Length.FOOT);
    }

    /**
     * Converts a Gray code to a binary code.
     *
     * @param gray The Gray code to convert.
     * @return The binary code.
     */
    private static int grayToBinary(int gray) {
        if(gray < 0){
            gray *= -1;
        }
        int binary = gray;

        while (0 < gray) {
            gray >>= 1;
            binary ^= gray;
        }
        return binary;
    }
}
