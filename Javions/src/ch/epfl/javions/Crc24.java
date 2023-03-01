package ch.epfl.javions;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class Crc24 {
    public final static int GENERATOR = 0xFFF409;
    private final int generator;
    private static int[] crc_b = new int[256];
    private static final int mask = 16777215;

    /**
     *
     * @param generator
     */
    public Crc24(int generator){
        this.generator = generator & mask;
        crc_b = buildTable(this.generator);
    }

    /**
     *
     * @param bytes
     * @return
     */
    public int crc(byte[] bytes){
        int crc = 0;
        for(byte o : bytes){
                crc = ((crc << 8) | Byte.toUnsignedInt(o)) ^ crc_b[Bits.extractUInt(crc,16,8)];
        }
        for(int j = 0; j < 3; ++j){
            crc = (crc << 8) ^ crc_b[Bits.extractUInt(crc,16,8)];
        }
        return crc & mask;
    }

    /**
     *
     * @param generator
     * @param bytes
     * @return
     */
    public static int crc_bitwise(int generator, byte[] bytes){
        int[] table = {0,generator & mask};
        int crc = 0;
        for(byte b : bytes){
            for(int i = 7; i >= 0; --i){
                crc = ((crc << 1) | Bits.extractUInt(b,i,1)) ^ table[Bits.extractUInt(crc,23,1)];
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 7; j >= 0; --j){
                crc = (crc << 1) ^ table[Bits.extractUInt(crc,23,1)];
            }
        }
        return crc & mask;
    }

    private static int[] buildTable(int generator){
        int[] table = new int[256];
        for(int i = 0; i < 256; ++i){
            table[i] = crc_bitwise(generator, new byte[] {(byte) i});
        }
        return table;
    }
}
