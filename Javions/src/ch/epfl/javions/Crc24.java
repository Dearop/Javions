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
        for(int o = 0; o < bytes.length; o++){
            int[] table = {0, generator};
            byte usedByte = bytes[o];
            for(int i = 7; i >= 0; --i){
                crc = (((crc << 8) | ((usedByte >> i)& 1)) ^ table[valMinusOne(crc, indexFinder(crc))]);
            }
        }
        return crc;

    }

    private static int indexFinder(int crc){
        //todo dangerous
        if(crc == 0|| crc == 1) throw new IllegalArgumentException(crc +" crc");
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
