package ch.epfl.javions.official_tests.wk3.demodulation;

import ch.epfl.javions.demodulation.MyPowerComputerTest;
import ch.epfl.javions.demodulation.PowerWindow;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {
    private static final int BATCH_SIZE = 1 << 16;
    private static final int BATCH_SIZE_BYTES = PowerWindowTest.bytesForPowerSamples(PowerWindowTest.BATCH_SIZE);
    private static final int STANDARD_WINDOW_SIZE = 1200;
    private static final int BIAS = 1 << 11;

    private static int bytesForPowerSamples(final int powerSamplesCount) {
        return powerSamplesCount * 2 * Short.BYTES;
    }

    @Test
    void powerWindowConstructorThrowsWithInvalidWindowSize() throws IOException {
        try (final var s = InputStream.nullInputStream()) {
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(s, 0));
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(s, -1));
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(s, (1 << 16) + 1));
        }
    }

    @Test
    void powerWindowSizeReturnsWindowSize() throws IOException {
        try (final var s = InputStream.nullInputStream()) {
            for (var i = 1; 1 << 16 >= i; i <<= 1) {
                final var w = new PowerWindow(s, i);
                assertEquals(i, w.size());
            }
        }
    }

    @Test
    void powerWindowPositionIsCorrectlyUpdatedByAdvance() throws IOException {
        final var batches16 = new byte[PowerWindowTest.BATCH_SIZE_BYTES * 16];
        try (final var s = new ByteArrayInputStream(batches16)) {
            final var w = new PowerWindow(s, PowerWindowTest.STANDARD_WINDOW_SIZE);
            var expectedPos = 0L;

            assertEquals(expectedPos, w.position());

            w.advance();
            expectedPos += 1;
            assertEquals(expectedPos, w.position());

            w.advanceBy(PowerWindowTest.BATCH_SIZE);
            expectedPos += PowerWindowTest.BATCH_SIZE;
            assertEquals(expectedPos, w.position());

            w.advanceBy(PowerWindowTest.BATCH_SIZE - 1);
            expectedPos += PowerWindowTest.BATCH_SIZE - 1;
            assertEquals(expectedPos, w.position());

            w.advance();
            expectedPos += 1;
            assertEquals(expectedPos, w.position());
        }
    }

    @Test
    void powerWindowAdvanceByCanAdvanceOverSeveralBatches() throws IOException {
        final var bytes = PowerWindowTest.bytesForZeroSamples(16);

        final var batchesToSkipOver = 2;
        final var inBatchOffset = 37;
        final var offset = batchesToSkipOver * PowerWindowTest.BATCH_SIZE + inBatchOffset;
        final var sampleBytes = Base64.getDecoder().decode(PowerComputerTest.SAMPLES_BIN_BASE64);
        System.arraycopy(sampleBytes, 0, bytes, PowerWindowTest.bytesForPowerSamples(offset), sampleBytes.length);

        try (final var s = new ByteArrayInputStream(bytes)) {
            final var w = new PowerWindow(s, PowerWindowTest.STANDARD_WINDOW_SIZE);
            w.advanceBy(inBatchOffset);
            w.advanceBy(batchesToSkipOver * PowerWindowTest.BATCH_SIZE);
            final var expected = Arrays.copyOfRange(PowerComputerTest.POWER_SAMPLES, 0, PowerWindowTest.STANDARD_WINDOW_SIZE);
            final var actual = new int[PowerWindowTest.STANDARD_WINDOW_SIZE];
            for (var i = 0; STANDARD_WINDOW_SIZE > i; i += 1) actual[i] = w.get(i);
            assertArrayEquals(expected, actual);
        }
    }

    @Test
    void powerWindowIsFullWorks() throws IOException {
        final var twoBatchesPlusOneWindowBytes =
                PowerWindowTest.bytesForPowerSamples(PowerWindowTest.BATCH_SIZE * 2 + PowerWindowTest.STANDARD_WINDOW_SIZE);
        final var twoBatchesPlusOneWindow = new byte[twoBatchesPlusOneWindowBytes];
        try (final var s = new ByteArrayInputStream(twoBatchesPlusOneWindow)) {
            final var w = new PowerWindow(s, PowerWindowTest.STANDARD_WINDOW_SIZE);
            assertTrue(w.isFull());

            w.advanceBy(PowerWindowTest.BATCH_SIZE);
            assertTrue(w.isFull());

            w.advanceBy(PowerWindowTest.BATCH_SIZE);
            assertTrue(w.isFull());

            w.advance();
            assertFalse(w.isFull());
        }
    }

    @Test
    void powerWindowGetWorksOnGivenSamples() throws IOException {
        try (final var sampleStream = PowerComputerTest.getSamplesStream()) {
            final var windowSize = 100;
            final var w = new PowerWindow(sampleStream, windowSize);
            for (var offset = 0; 100 > offset; offset += 1) {
                final var expected = Arrays.copyOfRange(PowerComputerTest.POWER_SAMPLES, offset, offset + windowSize);
                final var actual = new int[windowSize];
                for (var i = 0; i < windowSize; i += 1) actual[i] = w.get(i);
                assertArrayEquals(expected, actual);
                w.advance();
            }
        }
    }

    @Test
    void powerWindowGetWorksAcrossBatches() throws IOException {
        final byte[] bytes = PowerWindowTest.bytesForZeroSamples(2);
        final var firstBatchSamples = PowerWindowTest.STANDARD_WINDOW_SIZE / 2 - 13;
        final var offset = PowerWindowTest.BATCH_SIZE_BYTES - PowerWindowTest.bytesForPowerSamples(firstBatchSamples);
        final var sampleBytes = Base64.getDecoder().decode(PowerComputerTest.SAMPLES_BIN_BASE64);
        System.arraycopy(sampleBytes, 0, bytes, offset, sampleBytes.length);
        try (final var s = new ByteArrayInputStream(bytes)) {
            final var w = new PowerWindow(s, PowerWindowTest.STANDARD_WINDOW_SIZE);
            w.advanceBy(PowerWindowTest.BATCH_SIZE - firstBatchSamples);
            for (int i = 0; STANDARD_WINDOW_SIZE > i; i += 1)
                assertEquals(PowerComputerTest.POWER_SAMPLES[i], w.get(i));
        }
    }

    private static byte[] bytesForZeroSamples(final int batchesCount) {
        final var bytes = new byte[PowerWindowTest.BATCH_SIZE_BYTES * batchesCount];

        final var msbBias = PowerWindowTest.BIAS >> Byte.SIZE;
        final var lsbBias = PowerWindowTest.BIAS & ((1 << Byte.SIZE) - 1);
        for (var i = 0; i < bytes.length; i += 2) {
            bytes[i] = (byte) lsbBias;
            bytes[i + 1] = (byte) msbBias;
        }
        return bytes;
    }
}