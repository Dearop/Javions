package ch.epfl.javions;

public final class Crc24 {

    public final static int GENERATOR = 0xFFF409;
    private final int generator;
    public Crc24(int generator){
        this.generator = Bits.extractUInt(generator, 0, 24);
    }

    public int crc(byte[] bytes){
        return Bits.extractUInt(crc_bitwise(generator, bytes), 0,24);
    }
    private static int crc_bitwise(int generator, byte[] bytes){
        int crc = 0;
        int[] table = {0, generator};
        //traitement du message
        for(int o = 0; o < bytes.length; o++){
            byte usedByte = bytes[o];
            crc = ((crc << 8) | usedByte) ^ table[(crc >> countBits(crc)-1)];
        }
        return crc;

    }

    private static int countBits(int number) {

        return (int)(Math.log(2)/Math.log(number))+1;
    }

    private static int indexFinder(int crc){
        int index = 0;
        while(crc != 1){
            crc >>= 1;
            ++index;
        }
        return index-1;
    }

    public static int valMinusOne(int crc, int NMinOne){
        crc >>= NMinOne;
        return crc &= 1;
    }
}
