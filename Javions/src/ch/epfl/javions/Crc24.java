package ch.epfl.javions;

public final class Crc24 {

    public final static int GENERATOR = 0xFFF409;
    private final int generator;
    private int[] crc_b = new int[256];

    public Crc24(int generator) {
        this.generator = Bits.extractUInt(generator, 0, 24);
        for (int i = 0; i < 256; ++i) {
            this.crc_b[i] = crc_bitwise(generator, new byte[]{(byte) i});
        }

    }

    public int crc(byte[] bytes) {
        int crc = 0;
        byte[] zeros = new byte[3];
        for(int i = 0; i < bytes.length; ++i){
            crc = ((crc << 8)| bytes[bytes.length-1]) ^ crc_b[crc_b.length-1];

        }
        for(int j = 0; j < zeros.length; ++j ){
            crc = ((crc << 8)| zeros[zeros.length-1]) ^ crc_b[crc_b.length-1];
        }
        return Bits.extractUInt(crc, 0, 24);
    }

    public static int crc_bitwise(int generator, byte[] bytes) {
        int crc = 0;
        int[] table = {0, generator};
        for (int o = 0; o < bytes.length; ++o) {
            byte usedByte = bytes[o];
            for (int j = 7; j >= 0; --j) {
                int bit = Bits.extractUInt(usedByte, j, 1);
                crc = ((crc << 1) | bit) ^ table[Bits.extractUInt(crc, 23, 1)];
            }
        }
        for (int i = 0; i < 24; ++i) {
            crc = (crc << 1) ^ table[Bits.extractUInt(crc, 23, 1)];
        }
        return Bits.extractUInt(crc, 0, 24);
    }
}
