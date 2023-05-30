package ch.epfl.javions;

/**
 * Final class that describes the CRC algorithm for 24 bits. This algorithm helps us decipher bits out of a stream.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class Crc24 {
    // Constant representing the generator of the crc
    public static final int GENERATOR = 0xFFF409;
    private static final int tableSize = 256;
    private static int[] crc_b = new int[tableSize];
    private static final int mask = 16777215;
    private static final int CRC_START = 16;
    private static final int CRC_BITWISE_START = 23;
    private static final int CRC_BITWISE_SIZE = 1;


    /**
     * This method creates a CRC24 calculator that uses a generator with the 24 least significant bits set to the value
     * of the generator parameter. The CRC24 calculator can be used to calculate the CRC24 value of a given input data.
     *
     * @param generator integer value that specifies the 24 least significant bits of the generator to be
     *                  used in the CRC24 calculation
     */
    public Crc24(int generator) {
        int generatorMasked = generator & mask;
        crc_b = buildTable(generatorMasked);
    }

    /**
     * This function takes an array of bytes as input and calculates the CRC24 of the array using the byte-per-byte
     * algorithm specified in the project instructions. The CRC24 is a 24-bit cyclic redundancy check value that is
     * commonly used in communication protocols to detect errors in data transmission.
     *
     * @param bytes an array of bytes representing the data for which the CRC24 is to be calculated.
     * @return integer value representing the CRC24 of the input array.
     */
    public int crc(byte[] bytes) {
        int crc = 0;

        for (byte o : bytes) {
            crc = ((crc << Byte.SIZE) | Byte.toUnsignedInt(o)) ^ crc_b[Bits.extractUInt(crc, CRC_START, Byte.SIZE)];
        }

        for (int j = 0; 3 > j; ++j) {
            crc = (crc << Byte.SIZE) ^ crc_b[Bits.extractUInt(crc, CRC_START, Byte.SIZE)];
        }

        return crc & mask;
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
    private static int crc_bitwise(int generator, byte[] bytes) {
        int[] table = {0, generator & mask};
        int crc = 0;

        for (byte byteUsed : bytes) {
            for (int posInByte = 7; 0 <= posInByte; --posInByte) {
                crc = ((crc << CRC_BITWISE_SIZE) | Bits.extractUInt(byteUsed, posInByte, CRC_BITWISE_SIZE))
                        ^ table[Bits.extractUInt(crc, CRC_BITWISE_START, CRC_BITWISE_SIZE)];
            }
        }

        for (int i = 0; 3 > i; i++) {
            for (int j = 7; 0 <= j; --j) {
                crc = (crc << CRC_BITWISE_SIZE)
                        ^ table[Bits.extractUInt(crc, CRC_BITWISE_START, CRC_BITWISE_SIZE)];
            }
        }

        return crc & mask;
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
    private static int[] buildTable(int generator) {
        int[] table = new int[tableSize];

        for (int posInTable = 0; tableSize > posInTable; ++posInTable) {
            table[posInTable] = crc_bitwise(generator, new byte[]{(byte) posInTable});
        }

        return table;
    }
}
