package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading)
implements Message{
    public AirborneVelocityMessage{
        if(icaoAddress == null) throw new NullPointerException();
        Preconditions.checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        if(rawMessage.typeCode() != 19) return null;
        int bits22 = Bits.extractUInt(rawMessage.payload(), 21, 22);
        int ST = Bits.extractUInt(rawMessage.payload(), 48, 3);

        if (ST < 1 || ST > 4) return null;

        double speed;
        double trackOrHeading;

        // Ground speed
        if (ST == 1 || ST == 2) {
            int Dew = Bits.extractUInt(bits22, 21, 1);
            int Vew = Bits.extractUInt(bits22, 11, 10) - 1;
            int Dns = Bits.extractUInt(bits22, 10, 1);
            int Vns = Bits.extractUInt(bits22, 0, 10) - 1;

            // If Vns or Vew, which represent the speed of the aircraft, are zero the value is invalid and return null
            if (Vns == 0 || Vew == 0) return null;

            speed = Math.hypot(Vns, Vew);
            Vns = (Dns == 0) ? Vns : -Vns;
            Vew = (Dew == 0) ? Vew : -Vew;

            trackOrHeading = Math.atan2(Vew, Vns);
            trackOrHeading = (trackOrHeading < 0) ? trackOrHeading + 2 * Math.PI : trackOrHeading;

            if (ST == 1) {
                speed = Units.convertFrom(speed, Units.Speed.KNOT);
            } else {
                speed = Units.convertFrom(speed, 4 * Units.Speed.KNOT);
            }

        } else {
            int SH = Bits.extractUInt(bits22, 21, 1);

            // SH is not allowed to be zero, so null gets returned
            if (SH == 0) return null;

            trackOrHeading = Units.convertFrom(Bits.extractUInt(bits22, 11, 10) /
                    Math.scalb(1, 10), Units.Angle.TURN);

            if (ST == 3) {
                speed = Units.convertFrom(Bits.extractUInt(bits22, 0, 10) - 1 , Units.Speed.KNOT);
            } else {
                speed = Units.convertFrom(Bits.extractUInt(bits22,0,10) - 1,4 * Units.Speed.KNOT);
            }
        }
        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, trackOrHeading);
    }
}
