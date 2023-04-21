package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Represents an airborne velocity message containing the speed and heading/track of an aircraft.
 * @param timeStampNs The timestamp of the message in nanoseconds.
 * @param icaoAddress The ICAO address of the aircraft.
 * @param speed The speed of the aircraft, in groups of 10 bits.
 * @param trackOrHeading The heading or track angle of the aircraft, in groups of 10 bits.
 * @author Paul Quesnot (347572)
 */
public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading)
implements Message{

    private static final int ST_START = 48;
    private static final int ST_SIZE = 3;

    private static final int BITS22_START = 21;
    private static final int BITS_SIZE = 22;

    private static final int DIRECTION_SIZE= 1;
    private static final int SPEED_SIZE = 10;
    private static final int DEW_START = 21;
    private static final int VEW_START = 11;
    private static final int DNS_START = 10;
    private static final int VNS_START = 0;

    private static final int SH_START = 21;
    private static final int SH_SIZE = 1;

    private static final int AS_START = 0;
    private static final int AS_SIZE = 10;

    /**
     * The constructor checks if the given parameters are valid, if not then either a NullPointerException or a
     * IllegalArgumentException gets thrown.
     * @param timeStampNs The timestamp of the message in nanoseconds.
     * @param icaoAddress The ICAO address of the aircraft.
     * @param speed The speed of the aircraft, in knots.
     * @param trackOrHeading The heading or track angle of the aircraft, in radians.
     * @throws NullPointerException if the icaoAddress is null.
     * @throws IllegalArgumentException if any of the parameters have invalid values.
     */
    public AirborneVelocityMessage{
        if(null == icaoAddress)
            throw new NullPointerException();
        Preconditions.checkArgument(0 <= timeStampNs && 0 <= speed && 0 <= trackOrHeading);
    }

    /**
     * Constructs an airborne velocity message from a raw message.
     * First it check if it is the correct TypeCode which is equal to 19.
     * Then we check which Type the ST has it is either 1,2,3,4.
     * In Case(ST = 1 or 3) the speed gets returned in Knots without alteration.
     * In Case(ST = 2 or 4) the speed gets returned altered with a factor of 4.
     * This is in the case of the aircraft flying supersonic
     * Depending on ST the way the trackOrHeading gets calculated is also different.
     * @param rawMessage The raw message to parse.
     * @return An AirborneVelocityMessage object, or null if the raw message is not a type 19 message.
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        if(19 != rawMessage.typeCode())
            return null;

        int bits22 = Bits.extractUInt(rawMessage.payload(), BITS22_START, BITS_SIZE);
        int ST = Bits.extractUInt(rawMessage.payload(), ST_START, ST_SIZE);

        if (1 > ST || 4 < ST)
            return null;

        double speed;
        double trackOrHeading;

        // Ground speed
        if (1 == ST || 2 == ST) {
            int Dew = Bits.extractUInt(bits22, DEW_START, DIRECTION_SIZE);
            int Vew = Bits.extractUInt(bits22, VEW_START, SPEED_SIZE) - 1;
            int Dns = Bits.extractUInt(bits22, DNS_START, DIRECTION_SIZE);
            int Vns = Bits.extractUInt(bits22, VNS_START, SPEED_SIZE) - 1;

            // If Vns or Vew, which represent the speed of the aircraft, are zero the value is invalid and return null
            if (-1 == Vns || -1 == Vew)
                return null;

            speed = Math.hypot(Vns, Vew);
            Vns = (0 == Dns) ? Vns : -Vns;
            Vew = (0 == Dew) ? Vew : -Vew;
            trackOrHeading = Math.atan2(Vew, Vns);
            trackOrHeading = (0 > trackOrHeading) ? trackOrHeading + 2 * Math.PI : trackOrHeading;

            if (1 == ST) {
                speed = Units.convertFrom(speed, Units.Speed.KNOT);
            } else {
                speed = Units.convertFrom(speed, 4 * Units.Speed.KNOT);
            }

        } else {
            int SH = Bits.extractUInt(bits22, SH_START, SH_SIZE);

            // SH is not allowed to be zero, so null gets returned
            if (0 == SH)
                return null;

            trackOrHeading = Units.convertFrom(Bits.extractUInt(bits22, 11, 10) /
                    Math.scalb(1, 10), Units.Angle.TURN);

            final int AS = Bits.extractUInt(bits22, AS_START, AS_SIZE) - 1;
            if(-1 == AS)
                return null;
            if (3 == ST) {
                speed = Units.convertFrom(AS, Units.Speed.KNOT);
            } else {
                speed = Units.convertFrom(AS,4 * Units.Speed.KNOT);
            }
        }
        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, trackOrHeading);
    }
}
