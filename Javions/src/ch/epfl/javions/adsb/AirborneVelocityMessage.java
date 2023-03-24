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

    public static AirborneVelocityMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        int ST = Bits.extractUInt(payload, 48, 3);
        if (ST < 1 || ST > 4) return null;
        double speed;
        double trackOrHeading;

        // Ground speed
        if( ST == 1 || ST == 2){
            int Dew = Bits.extractUInt(payload, 21, 1);
            int Vew = Bits.extractUInt(payload, 11, 10) + 1;
            int Dns = Bits.extractUInt(payload, 10, 1);
            int Vns = Bits.extractUInt(payload, 0, 10) + 1;
            if(Vns == 0|| Vew == 0) return null;
            speed = Math.hypot(Vns, Vew);
            Vns = (Dns == 0) ? -Vns : Vns;
            Vew = (Dew == 0) ? Vew : -Vew;

            trackOrHeading =  Math.atan2(Vew, Vns) + Math.PI;
            if(ST == 1) speed = Units.convertFrom(speed, Units.Speed.KNOT);
            speed = Units.convertFrom(4*speed, Units.Speed.KNOT);
        }
        int SH = Bits.extractUInt(payload, 21, 1);
        if(SH == 0) return null;
        trackOrHeading = Units.convertFrom(Bits.extractUInt(payload, 11, 10)/
                Math.scalb(1,10), Units.Angle.TURN);
        if(ST == 3) speed = Units.convertTo(Bits.extractUInt(payload, 0, 10), Units.Speed.KNOT);
        speed = Units.convertTo(Bits.extractUInt(payload,0,10) * 4 , Units.Speed.KNOT);
        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speed, trackOrHeading);
    }
}
