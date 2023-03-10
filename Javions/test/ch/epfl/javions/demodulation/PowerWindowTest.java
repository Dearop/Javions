package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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
                //System.out.println(i+j*16);
                //System.out.println(window.computer.output[i+j*16]);
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

    // Should return true because we fill the window at the beginning
    @Test
    void isFullReturnsTrueAtTheBeginning() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        PowerWindow window = new PowerWindow(stream, 16);
        assertTrue(window.isFull());
    }

    @Test
    void getMethodReturnsRightIndex() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        PowerWindow window = new PowerWindow(stream, 160);
        PowerComputer computer = new PowerComputer(stream, 2400);
        int[] batch = new int[2400];
        computer.readBatch(batch);
        System.out.println(Arrays.toString(window.batchOne));
        int counter = 0;
        while(batch[counter] != 0)
            ++ counter;
        System.out.println(counter);
        //System.out.print(Arrays.toString(window.computer.output));

        window.advanceBy(100);
        for (int i = 0; i < 100; i++) {
            //System.out.println(window.get(i));
            assertEquals(window.get(i),window.batchOne[i+100]);
        }
    }

    @Test
    void sizeMethodReturnsRightValue() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);

        PowerWindow window = new PowerWindow(stream, 16);
        assertEquals(16, window.size());
    }
}
