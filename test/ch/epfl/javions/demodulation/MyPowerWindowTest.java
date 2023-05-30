package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class MyPowerWindowTest {

    @Test
    public void PowerWindowConstructorTest() throws IOException {
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);
        final PowerWindow window = new PowerWindow(stream, 16);
    }

    @Test
    public void PowerWindowAdvanceMethodWorks() throws IOException {
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);

        final PowerWindow window = new PowerWindow(stream, 16);

        for (int j = 0; 3000 > j; j++) {
            for (int i = 0; 16 > i; i++) {
                //System.out.println(i+j*16);
                //System.out.println(window.computer.output[i+j*16]);
                window.advance();
            }
        }
    }

    @Test
    public void PowerWindowAdvanceByTest() throws IOException{
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);

        final PowerWindow window = new PowerWindow(stream, 8);

        window.advanceBy(5);
    }

    // Should return true because we fill the window at the beginning
    @Test
    void isFullReturnsTrueAtTheBeginning() throws IOException {
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);

        final PowerWindow window = new PowerWindow(stream, 16);
        assertTrue(window.isFull());
    }

    @Test
    void getMethodReturnsRightIndex() throws IOException {
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);

        final PowerWindow window = new PowerWindow(stream, 40);
        final PowerComputer computer = new PowerComputer(stream, 2400);
        final int[] batch = new int[2400];
        computer.readBatch(batch);
        //System.out.println(Arrays.toString(window.batchOne));
        int counter = 0;
        while(0 != batch[counter])
            ++ counter;
        //System.out.println(counter);
        //System.out.print(Arrays.toString(window.computer.output));

        window.advanceBy(50);
        for (int i = 0; 40 > i; i++) {
            //System.out.println(window.get(i));
        }
    }

    @Test
    void sizeMethodReturnsRightValue() throws IOException {
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);

        final PowerWindow window = new PowerWindow(stream, 16);
        assertEquals(16, window.size());
    }

    @Test
    void testIfGetWorksForTriviain1stWindow() throws IOException {
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);
        final InputStream f = new FileInputStream(samples);
        final PowerWindow powerWindow=new PowerWindow(stream,60);
        final PowerComputer powerComputer = new PowerComputer(f,120);
        final int[] tab = new int[120];
        final int count = powerComputer.readBatch(tab);
        for(var i = 0; 60 > i; i++) {
            assertEquals(tab[i],powerWindow.get(i));
        }
    }

    @Test
    void testIfGetWorksIfCanSwitchTable() throws IOException { //with small batchsize
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);
        final InputStream f = new FileInputStream(samples);
        final PowerWindow powerWindow=new PowerWindow(stream,8);
        final PowerComputer powerComputer = new PowerComputer(f,1208);
        final int[] tab = new int[1208];
        final int count = powerComputer.readBatch(tab);
        powerWindow.advanceBy(9);
        for(var i = 0; 8 > i; i++){
            assertEquals(tab[i+9],powerWindow.get(i));
        }
    }

    @Test
    void testIfAdvanceby1Worksfor1TableCases() throws IOException{
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);
        final InputStream f=new FileInputStream(samples);

        final PowerWindow powerWindow=new PowerWindow(stream,60);
        final PowerComputer powerComputer = new PowerComputer(f,1208);
        final int[] tab = new int[1208];
        final int count = powerComputer.readBatch(tab);
        for(var i = 0; 60 > i; i++) {
            assertEquals(tab[i],powerWindow.get(0));
            powerWindow.advance();
        }
    }

    @Test
    void testIfAdvancebyOffsetWorksfor1TableCases() throws IOException{
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);
        final InputStream f=new FileInputStream(samples);
        final PowerWindow powerWindow=new PowerWindow(stream,60);
        final PowerComputer powerComputer = new PowerComputer(f,1208);
        final int[] tab = new int[1208];
        final int count = powerComputer.readBatch(tab);
        for(var i = 0; 60 > i; i+=3) {
            assertEquals(tab[i],powerWindow.get(0));
            powerWindow.advanceBy(3);
        }
    }

    @Test
    void testIfIsFullforSmallFile()throws IOException {
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);
        final PowerWindow powerWindow = new PowerWindow(stream, 8);
        assertTrue(powerWindow.isFull());
        powerWindow.advanceBy(1193);
        assertTrue(powerWindow.isFull());
        powerWindow.advance();
        assertFalse(powerWindow.isFull());
    }

    @Test
    void testTable() throws IOException{
        String samples = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\samples.bin";
        final InputStream stream = new FileInputStream(samples);
        final PowerWindow window = new PowerWindow(stream, 4);

    }
}
