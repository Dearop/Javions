package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage
        (long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) {
    public AirbornePositionMessage{
        if(icaoAddress == null) throw new NullPointerException();
        Preconditions.checkArgument((timeStampNs >= 0) && ((parity == 0) || (parity == 1)) &&
                (x >= 0) && (x < 1) && (y >= 0) && (y < 1));
    }

    public static AirbornePositionMessage of(RawMessage rawMessage){
        if(rawMessage.typeCode() < 9 || rawMessage.typeCode() > 22 || rawMessage.typeCode() == 19) return null;
        long payload = rawMessage.payload();
        // longitude and latitude are between 0 and 1 so this must be right (extractUInt gives back unsigned integer)
        int longitude = (int) (Bits.extractUInt(payload,0,17) / Math.pow(2,17));
        int latitude = (int) (Bits.extractUInt(payload,17,17) / Math.pow(2,17));
        int FORMAT = (int) ((payload >> 34) & 1);
        int ALT = Bits.extractUInt(payload,36,12);
        return new AirbornePositionMessage(rawMessage.timeStampNs(),
                rawMessage.icaoAddress(), ALT, FORMAT, longitude, latitude);
    }
}
