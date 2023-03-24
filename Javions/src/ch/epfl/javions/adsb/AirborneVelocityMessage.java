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

    /**
     * Returns the timestamp of the AircraftIdentificationMessage.
     *
     * @return the timestamp in nanoseconds
     */
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    /**
     * Returns the ICAO address of the AircraftIdentificationMessage.
     *
     * @return the ICAO address
     */
    @Override
    public IcaoAddress icaoAddress() { return icaoAddress;}

    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        int ST = Bits.extractUInt(payload, 48, 3);
        if (ST < 1 || ST > 4) return null;
        double speed;
        double trackOrHeading;

        // Ground speed
        if (ST == 1 || ST == 2) {
            int Dew = Bits.extractUInt(payload, 21, 1);
            int Vew = Bits.extractUInt(payload, 11, 10) + 1; //TODO why are these with plus one? because then the if below will never be triggered
            int Dns = Bits.extractUInt(payload, 10, 1);
            int Vns = Bits.extractUInt(payload, 0, 10) + 1;
            if (Vns == 0 || Vew == 0) return null;

            speed = Math.hypot(Vns, Vew);
            Vns = (Dns == 0) ? -Vns : Vns; // todo this one sure? positive defined as from north to south?
            Vew = (Dew == 0) ? Vew : -Vew;

            trackOrHeading = Math.atan2(Vew, Vns) + Math.PI; // todo is this in degree?
            if (ST == 1) {
                speed = Units.convertFrom(speed, Units.Speed.KNOT);
            } else {
                speed = Units.convertFrom(4 * speed, Units.Speed.KNOT);
            }
        } else {
            int SH = Bits.extractUInt(payload, 21, 1);
            if (SH == 0) return null;
            trackOrHeading = Units.convertFrom(Bits.extractUInt(payload, 11, 10) /
                    Math.scalb(1, 10), Units.Angle.TURN);
            if (ST == 3) {
                speed = Units.convertTo(Bits.extractUInt(payload, 0, 10), Units.Speed.KNOT);
            } else {
                speed = Units.convertTo(Bits.extractUInt(payload,0,10) * 4 , Units.Speed.KNOT);
            }
        }
        //todo heading needs to be in degree
        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, trackOrHeading);
    }
}
