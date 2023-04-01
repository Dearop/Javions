package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MySamplesDecoderTest {
    @Test
    public void SamplesDecodeThrowsExceptions() throws FileNotFoundException {
        String samples = this.getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        final InputStream stream = new FileInputStream(samples);
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 3));
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(stream, -3));
    }


    @Test
    public void SamplesDecoderReturnsExpectedValue() throws IOException {
        String samples = this.getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        final InputStream stream = new FileInputStream(samples);
        final SamplesDecoder decoder = new SamplesDecoder(stream, 2402);
        final short[] batch = new short[2402];
        assertEquals(2402, decoder.readBatch(batch));
    }

    @Test
    void testValidSampleDecoder() throws IOException {
        String stream2 = this.getClass().getResource("/samples.bin").getFile();
        stream2 = URLDecoder.decode(stream2, StandardCharsets.UTF_8);
        final InputStream stream = new FileInputStream(stream2);
        final SamplesDecoder test = new SamplesDecoder(stream, 2402);


        final short[] batch = new short[2402];
        test.readBatch(batch);
        assertEquals(-3, batch[0]);
        assertEquals(8, batch[1]);
        assertEquals(-9, batch[2]);
        assertEquals(-8, batch[3]);
        assertEquals(-5, batch[4]);
        assertEquals(-8, batch[5]);
        assertEquals(-12, batch[6]);
        assertEquals(-16, batch[7]);
        assertEquals(-23, batch[8]);
        assertEquals(-9, batch[9]);

    }

}
