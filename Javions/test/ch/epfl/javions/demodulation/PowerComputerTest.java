package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class PowerComputerTest {
    @Test
    public void PowerComputerTest() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);
        PowerComputer computer = new PowerComputer(stream, 2402);
        int[] batch = new int[10];

    }
}
