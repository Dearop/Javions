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
        computer.readBatch(batch);
        assertEquals(73, batch[0]);
        assertEquals(292, batch[1]);
        assertEquals(65, batch[2]);
        assertEquals(745, batch[3]);
        assertEquals(98, batch[4]);
        assertEquals(4226,batch[5]);
        assertEquals(12244, batch[6]);
        assertEquals(25722,batch[7]);
        assertEquals(36818, batch[8]);
        assertEquals(23825, batch[9]);
    }
}
