package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public record AircraftIdentificationMessage
        (long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {
    @Override
    public long timeStampNs() {
        return 0;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return null;
    }

    public AircraftIdentificationMessage of(RawMessage rawMessage){
        //computing the category
        int MSB = (14 - rawMessage.typeCode()) << 4;
        int LSB = rawMessage.bytes().byteAt(4) & 0b111;
        int category = MSB ^ LSB;
        //computing the CallSign
        String sign = "";
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String space = " ";
        char intermediary;
        //creating the table containing the values the byte cannot be equal to
        int[] table = new int[20];
        for(int i = 0; i < 20; ++i){
            if(i+27 != 32) table[i] = i+27;
        }
        long payload = rawMessage.payload();
        int b;
        for (int i = 42; i >= 0; i-=6) {
            b = Byte.toUnsignedInt( (byte) ((payload >> i) & 0xFF));
            if(b > 57) return null;
            for (int t : table) {
                if(t == b) return null;
            }
            intermediary = numbers.charAt(b-48);
            if(b < 26) intermediary = alphabet.charAt(b);
            if(b == 32) intermediary = space.charAt(0);
            sign += intermediary;
            }
        CallSign callSign1 = new CallSign(sign);
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign1);
    }
}

