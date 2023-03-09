package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        for (int j = 0; j < 3000; j++) {
            for (int i = 0; i < 16; i++) {
                System.out.println(i+j*16);
                System.out.println(window.computer.output[i+j*16]);
                assertEquals(window.batchOne[i+j*16], window.get(0));
                window.advance();
            }
        }
    }

    @Test
    public void PowerWindowAdvanceByTest() throws IOException{
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        PowerWindow window = new PowerWindow(stream, 8);

        window.advanceBy(5);
        assertEquals(window.batchOne[5], window.get(0));
    }
}
