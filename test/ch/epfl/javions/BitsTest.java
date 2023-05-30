package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitsTest {
    @Test
    void bitsExtractUIntThrowsIfSizeIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUInt(0, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUInt(0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUInt(0, 0, 32));
    }

    @Test
    void bitsExtractUIntThrowsIfStartAndSizeAreInvalid() {
        assertThrows(IndexOutOfBoundsException.class, () -> Bits.extractUInt(0, -1, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> Bits.extractUInt(0, 64, 1));
    }

    @Test
    void bitsExtractUIntCanExtractAllNibbles() {
        final var v = 0xFEDCBA9876543210L;
        for (var i = 0; 16 > i; i += 1) {
            final var n = Bits.extractUInt(v, i * 4, 4);
            assertEquals(i, n);
        }
    }

    @Test
    void bitsExtractCanExtract31Bits() {
        final var v = 0xFFFF_7654_ABCD_FFFFL;
        final var n = Bits.extractUInt(v, 16, 31);
        assertEquals(0x7654_ABCD, n);
    }

    @Test
    void bitsTestThrowsIfIndexIsInvalid() {
        assertThrows(IndexOutOfBoundsException.class, () -> Bits.testBit(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> Bits.testBit(0, Long.SIZE));
    }

    @Test
    void bitsTestBitWorksOnAllBits() {
        for (var i = 0; Long.SIZE > i; i += 1) {
            final var v = 1L << i;
            for (var j = 0; Long.SIZE > j; j += 1) {
                final var b = Bits.testBit(v, j);
                assertEquals(i == j, b);
            }
        }
    }
}