package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerWindowTest {

    @Test
    public void PowerWindowConstructorTest() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        PowerWindow window = new PowerWindow(stream, 16);

    }
    @Test
    public void PowerWindowAdvanceMethodWorks() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        PowerWindow window = new PowerWindow(stream, 16);
        window.advance();
        for(int i = 0; i < window.batchOne.length; ++i) {
            assertEquals(window.batchOne[i], window.computer.output[i]);
        }
        assertEquals(window.batchOne[1], window.get(0));
    }
}
