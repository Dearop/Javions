package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AircraftIdentificationMessage
        (long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {
    public AircraftIdentificationMessage{
        Preconditions.checkArgument(timeStampNs >=0);
        if(callSign == null || icaoAddress == null) throw new NullPointerException();
    }
    @Override
    public long timeStampNs() {
        return 0;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return null;
    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        //computing the category
        int MSB = (14 - rawMessage.typeCode()) << 4;
        int LSB = rawMessage.bytes().byteAt(4) & 0b111;
        int category = MSB | LSB;
        //computing the CallSign
        StringBuilder sign = new StringBuilder();
        char intermediary = '\0';
        long payload = rawMessage.payload();
        int b;
        for (int i = 42; i >= 0; i-=6) {
            b =  (int) (payload >> i) & 127;
            if(!isValidCharCode(b)) return null;
            System.out.println(b);
            if(b >= 48)intermediary = (char) ((b-48)+'0');
            if(b <= 26) intermediary = (char) (b+64);
            if(b == 32) intermediary = 32;
            sign.append(intermediary);
            }
        CallSign callSign1 = new CallSign(sign.toString());
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign1);
    }

    private static boolean isValidCharCode(int code) {
        return (code >= 1 && code <= 26) || (code >= 48 && code <= 57) || code == 32;
    }
}

