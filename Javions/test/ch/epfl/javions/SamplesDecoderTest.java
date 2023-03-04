package ch.epfl.javions;

import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplesDecoderTest {
    @Test
    public void SamplesDecodeThrowsExceptions() {
        InputStream stream = null;
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 3));
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(stream, -3));
    }

    @Test
    public void SamplesDecoderDoesWhatsExpected() throws IOException {

        String samples = getClass().getResource("/samples.bin").getFile();
        InputStream stream = new FileInputStream(samples);

        SamplesDecoder decoder = new SamplesDecoder(stream, 10);
        short[] batch = new short[10];
        assertEquals(10, decoder.readBatch(batch));


    }

}
