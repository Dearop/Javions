package ch.epfl.javions;

import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        SamplesDecoder decoder = new SamplesDecoder(stream, 2402);
        short[] batch = new short[2402];
        assertEquals(1201, decoder.readBatch(batch));
    }

    @Test
    void testValidSampleDecoder() throws IOException {
        String stream2 = getClass().getResource("/samples.bin").getFile();
        stream2 = URLDecoder.decode(stream2, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(stream2);
        SamplesDecoder test = new SamplesDecoder(stream, 2402);
        //  PowerComputer test2 = new PowerComputer(stream , 9608);

        short[] batch = new short[2402];
        test.readBatch(batch);
        //  int [] batch2 = new int[9608];
        // int a = test2.readBatch(batch2);
        for (int i = 0; i < 10; ++i) {
            System.out.println(batch[i]);
        }

    }
}
