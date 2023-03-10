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

        PowerWindow window = new PowerWindow(stream, 40);
        PowerComputer computer = new PowerComputer(stream, 2400);
        int[] batch = new int[2400];
        computer.readBatch(batch);
        //System.out.println(Arrays.toString(window.batchOne));
        int counter = 0;
        while(batch[counter] != 0)
            ++ counter;
        //System.out.println(counter);
        //System.out.print(Arrays.toString(window.computer.output));

        window.advanceBy(50);
        for (int i = 0; i < 40; i++) {
            //System.out.println(window.get(i));
            assertEquals(window.get(i),window.batchOne[i+50]);
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

    @Test
    void testIfGetWorksForTriviain1stWindow() throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);
        InputStream f = new FileInputStream(samples);
        PowerWindow powerWindow=new PowerWindow(stream,60);
        PowerComputer powerComputer = new PowerComputer(f,120);
        int[] tab = new int[120];
        int count = powerComputer.readBatch(tab);
        for(var i=0;i<60;i++) {
            assertEquals(tab[i],powerWindow.get(i));
        }
    }

    @Test
    void testIfGetWorksIfCanSwitchTable() throws IOException { //with small batchsize
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);
        InputStream f = new FileInputStream(samples);
        PowerWindow powerWindow=new PowerWindow(stream,8);
        PowerComputer powerComputer = new PowerComputer(f,1208);
        int[] tab = new int[1208];
        int count = powerComputer.readBatch(tab);
        powerWindow.advanceBy(9);
        for(var i=0;i<8;i++){
            assertEquals(tab[i+9],powerWindow.get(i));
        }
    }

    @Test
    void testIfAdvanceby1Worksfor1TableCases() throws IOException{
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);
        InputStream f=new FileInputStream(samples);

        PowerWindow powerWindow=new PowerWindow(stream,60);
        PowerComputer powerComputer = new PowerComputer(f,1208);
        int[] tab = new int[1208];
        int count = powerComputer.readBatch(tab);
        for(var i=0;i<60;i++) {
            assertEquals(tab[i],powerWindow.get(0));
            powerWindow.advance();
        }
    }

    @Test
    void testIfAdvancebyOffsetWorksfor1TableCases() throws IOException{
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);
        InputStream f=new FileInputStream(samples);
        PowerWindow powerWindow=new PowerWindow(stream,60);
        PowerComputer powerComputer = new PowerComputer(f,1208);
        int[] tab = new int[1208];
        int count = powerComputer.readBatch(tab);
        for(var i=0;i<60;i+=3) {
            assertEquals(tab[i],powerWindow.get(0));
            powerWindow.advanceBy(3);
        }
    }

    @Test
    void testIfIsFullforSmallFile()throws IOException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(samples);
        PowerWindow powerWindow = new PowerWindow(stream, 8);
        assertTrue(powerWindow.isFull());
        powerWindow.advanceBy(1193);
        assertTrue(powerWindow.isFull());
        powerWindow.advance();
        assertFalse(powerWindow.isFull());
    }

    @Test
    void testTable() throws IOException{    String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, StandardCharsets.UTF_8);    InputStream stream = new FileInputStream(samples);
        PowerWindow window = new PowerWindow(stream, 8);    int[] test = new int[8];

        System.out.println(Arrays.toString(window.batchOne));    System.out.println(Arrays.toString(window.batchTwo));
        System.out.println("advanced by 5");
        window.advanceBy(5);
        System.out.println(Arrays.toString(window.batchOne));
        System.out.println(Arrays.toString(window.batchTwo));
    }

}
