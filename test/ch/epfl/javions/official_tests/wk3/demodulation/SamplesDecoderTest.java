package ch.epfl.javions.official_tests.wk3.demodulation;

import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest {
    private static final int SAMPLES_COUNT = 1 << 12;
    private static final int BIAS = 1 << 11;

    private static byte[] getSampleBytes() {
        final var sampleBytes = new byte[SamplesDecoderTest.SAMPLES_COUNT * Short.BYTES];
        final var sampleBytesBuffer = ByteBuffer.wrap(sampleBytes)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer();

        for (int i = 0; SAMPLES_COUNT > i; i += 1)
            sampleBytesBuffer.put((short) i);
        return sampleBytes;
    }

    @Test
    void samplesDecoderConstructorThrowsWithInvalidBatchSize() {
        final var stream = new ByteArrayInputStream(new byte[0]);
        assertThrows(
                IllegalArgumentException.class,
                () -> new SamplesDecoder(stream, -1));
        assertThrows(
                IllegalArgumentException.class,
                () -> new SamplesDecoder(stream, 0));
    }

    @Test
    void samplesDecoderConstructorThrowsWithNullStream() {
        assertThrows(
                NullPointerException.class,
                () -> new SamplesDecoder(null, 1));
    }

    @Test
    void samplesDecoderReadBatchThrowsOnInvalidBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            try (final var byteStream = new ByteArrayInputStream(SamplesDecoderTest.getSampleBytes())) {
                final var batchSize = 1024;
                final var actualSamples = new short[batchSize - 1];
                final var samplesDecoder = new SamplesDecoder(byteStream, batchSize);
                samplesDecoder.readBatch(actualSamples);
            }
        });
    }

    @Test
    void samplesDecoderReadBatchCorrectlyReadsSamples() throws IOException {
        try (final var byteStream = new ByteArrayInputStream(SamplesDecoderTest.getSampleBytes())) {
            final var expectedSamples = new short[SamplesDecoderTest.SAMPLES_COUNT];
            for (int i = 0; SAMPLES_COUNT > i; i += 1)
                expectedSamples[i] = (short) (i - SamplesDecoderTest.BIAS);

            final var actualSamples = new short[SamplesDecoderTest.SAMPLES_COUNT];
            final var samplesDecoder = new SamplesDecoder(byteStream, actualSamples.length);
            final var readSamples = samplesDecoder.readBatch(actualSamples);
            assertEquals(SamplesDecoderTest.SAMPLES_COUNT, readSamples);
            assertArrayEquals(expectedSamples, actualSamples);
        }
    }

    @Test
    void samplesDecoderWorksWithDifferentBatchSizes() throws IOException {
        final var expectedSamples = new short[SamplesDecoderTest.SAMPLES_COUNT];
        for (int i = 0; SAMPLES_COUNT > i; i += 1)
            expectedSamples[i] = (short) (i - SamplesDecoderTest.BIAS);

        for (var batchSize = 1; SAMPLES_COUNT > batchSize; batchSize *= 2) {
            try (final var byteStream = new ByteArrayInputStream(SamplesDecoderTest.getSampleBytes())) {
                final var samplesDecoder = new SamplesDecoder(byteStream, batchSize);
                final var actualSamples = new short[SamplesDecoderTest.SAMPLES_COUNT];
                final var batch = new short[batchSize];
                for (var i = 0; i < SamplesDecoderTest.SAMPLES_COUNT / batchSize; i += 1) {
                    final var samplesRead = samplesDecoder.readBatch(batch);
                    assertEquals(batchSize, samplesRead);
                    System.arraycopy(batch, 0, actualSamples, i * batchSize, batchSize);
                }
                assertArrayEquals(expectedSamples, actualSamples);
            }
        }
    }
}