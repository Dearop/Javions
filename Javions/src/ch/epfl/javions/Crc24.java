package ch.epfl.javions;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class Crc24 {
    public static final int GENERATOR = 0xFFF409;
    private final int generator;
    private static int[] crc_b = new int[256];
    private static final int mask = 16777215;

    /**
     * his method creates a CRC24 calculator that uses a generator with the 24 least significant bits set to the value
     * of the generator parameter. The CRC24 calculator can be used to calculate the CRC24 value of a given input data.
     *
     * @param generator integer value that specifies the 24 least significant bits of the generator to be
     *                  used in the CRC24 calculation
     */
    public Crc24(final int generator) {
        this.generator = generator & Crc24.mask;
        Crc24.crc_b = Crc24.buildTable(this.generator);
    }

    /**
     * This function takes an array of bytes as input and calculates the CRC24 of the array using the byte-per-byte
     * algorithm specified in the project instructions. The CRC24 is a 24-bit cyclic redundancy check value that is
     * commonly used in communication protocols to detect errors in data transmission.
     *
     * @param bytes an array of bytes representing the data for which the CRC24 is to be calculated.
     * @return integer value representing the CRC24 of the input array.
     */
    public int crc(final byte[] bytes) {
        int crc = 0;

        for (final byte o : bytes) {
            crc = ((crc << 8) | Byte.toUnsignedInt(o)) ^ Crc24.crc_b[Bits.extractUInt(crc, 16, 8)];
        }

        for (int j = 0; 3 > j; ++j) {
            crc = (crc << 8) ^ Crc24.crc_b[Bits.extractUInt(crc, 16, 8)];
        }

        return crc & Crc24.mask;
    }

    /**
     * This function takes an array of bytes as input and calculates the CRC24 of the array using the bit-per-bit
     * algorithm specified in the project instructions.
     *
     * @param generator integer value that specifies the 24 least significant bits of the generator to be
     *                  used in the CRC24 calculation.
     * @param bytes     an array of bytes representing the data for which the CRC24 is to be calculated.
     * @return integer value representing the CRC24 of the input array.
     */
    private static int crc_bitwise(final int generator, final byte[] bytes) {
        final int[] table = {0, generator & Crc24.mask};
        int crc = 0;

        for (final byte b : bytes) {
            for (int i = 7; 0 <= i; --i) {
                crc = ((crc << 1) | Bits.extractUInt(b, i, 1)) ^ table[Bits.extractUInt(crc, 23, 1)];
            }
        }

        for (int i = 0; 3 > i; i++) {
            for (int j = 7; 0 <= j; --j) {
                crc = (crc << 1) ^ table[Bits.extractUInt(crc, 23, 1)];
            }
        }

        return crc & Crc24.mask;
    }

    /**
     * The method uses the crc_bitwise method to calculate the CRC24 value for each possible byte value (0-255),
     * using the given generator§.The resulting CRC24 values are stored in the lookup table,
     * which can then be used in the calculation of CRC24 values for input data arrays.
     *
     * @param generator integer value that specifies the 24 least significant bits of the generator to be
     *                  used in the CRC24 calculation.
     * @return table of bytes
     */
    private static int[] buildTable(final int generator) {
        final int[] table = new int[256];

        for (int i = 0; 256 > i; ++i) {
            table[i] = Crc24.crc_bitwise(generator, new byte[]{(byte) i});
        }

        return table;
    }
}
