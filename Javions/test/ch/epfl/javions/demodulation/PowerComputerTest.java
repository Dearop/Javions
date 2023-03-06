package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerComputerTest {
    @Test
    public void PowerComputerTest() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        PowerComputer computer = new PowerComputer(stream, 2400);
        int[] batch = new int[1201];

        // first is 8pow2 + 3pow2
        computer.readBatch(batch);
        for (int i = 0; i < 10; i++) {
            System.out.println(computer.output[i]);
        }
        assertEquals(73, computer.output[0]);
        assertEquals(292, computer.output[1]);
        assertEquals(65, computer.output[2]);
        assertEquals(745, computer.output[3]);
        assertEquals(98, computer.output[4]);
        assertEquals(4226,computer.output[5]);
        assertEquals(12244, computer.output[6]);
        assertEquals(25722,computer.output[7]);
        assertEquals(36818, computer.output[8]);
        assertEquals(23825, computer.output[9]);
    }

    public void PowerGraphTest() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        PowerComputer computer = new PowerComputer(stream, 2400);
        int[] batch = new int[1201];

        // first is 8pow2 + 3pow2
        computer.readBatch(batch);
        int[] firstValues = new int[160];
        for (int i = 0; i < firstValues.length; i++) {
            firstValues[i] = computer.output[i];
        }

    }
}
