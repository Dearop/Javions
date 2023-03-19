package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
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
        double computedAltitude = altitudeComputer(ALT);
        if(computedAltitude == -0xFFFFF) return null;
        return new AirbornePositionMessage(rawMessage.timeStampNs(),
                rawMessage.icaoAddress(), ALT, FORMAT, longitude, latitude);
    }

    public static double altitudeComputer(int ALT) {
        //Q=1
        if(Bits.extractUInt(ALT, 4, 1) == 1){
            double altitudeInFeet = Bits.extractUInt(ALT, 0, 3) + (Bits.extractUInt(ALT, 5, 8) << 3);
            double baseAltitude = -1000;
            return Units.convertFrom(altitudeInFeet * 25 - baseAltitude, Units.Length.FOOT);
        }
        //Q=0
        int MSBGray = 0;
        int LSBGray = 0;
        // reorganising the bits
        for (int i = 0; i < 5; i+=2) {
            // D
            MSBGray += (Bits.extractUInt(ALT, i, 1) << (9 + i/2));
            // A
            MSBGray += (Bits.extractUInt(ALT, 6 + i, 1) << (6 + i/2));
            // B
            MSBGray += (Bits.extractUInt(ALT, 1 + i, 1) << (3 + i/2));
            // C
            LSBGray += (Bits.extractUInt(ALT, 7 + i, 1) << i/2);
        }
        int MSB = grayToBinary(MSBGray);
        int LSB = grayToBinary(LSBGray);
        if(LSB == 0 || LSB == 5|| LSB == 6) return -0xFFFFF;
        // Dumb Question : does this bit exclude 7 too?
        if(LSB == 7) LSB = 5;
        if( MSB % 2 == 1)  LSB += (6-LSB);
        return(Units.convertFrom(-1300 + MSB * 500 + LSB * 100, Units.Length.FOOT));
    }

    public static int grayToBinary(int grey){
        int binary = grey;
        while (grey > 0){
            grey >>= 1;
            binary ^= grey;
        }
        return binary;
    }
}
