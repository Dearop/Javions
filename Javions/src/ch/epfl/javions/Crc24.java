package ch.epfl.javions;

public final class Crc24 {

    /**
     * Given by the consignes, GENERATOR = FFF409 (base 16)
     */
    public final static int GENERATOR = 16774153;
    private static final int N24 = 24;
    private final int generator;
    public Crc24(int generator){
        this.generator = generator & (int)Math.pow(2,24)-1;
    }

    public int crc(byte[] bytes){
        return Bits.extractUInt(crc_bitwise(generator, bytes), 0,24);
    }
    private static int crc_bitwise(int generator, byte[] bytes){
        int crc = 0;
        for(int o = 0; o < bytes.length; o++){
            int table []= {0, generator};
            byte usedByte = bytes[o];
            for(int i = 7; i >= 0; --i){
                int N = indexFinder(crc);
                crc = (((crc << 1) | ((usedByte >> i)& 1)) ^ table[valMinusOne(crc, indexFinder(crc))]);
            }
        }
        return crc;

    }

    private static int indexFinder(int crc){
        if(crc == 0|| crc == 1) return 0;
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
