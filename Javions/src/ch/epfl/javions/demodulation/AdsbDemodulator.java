package ch.epfl.javions.demodulation;

import ch.epfl.javions.Crc24;
import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public final class AdsbDemodulator {
    private final PowerWindow window;
    private final static int windowSize = 1200;
    private long timeStamp;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.window = new PowerWindow(samplesStream, windowSize);
        timeStamp = 0;
    }

    public RawMessage nextMessage() throws IOException {
        int UselessCounter = 0;
        int windowSize = 0;
        ArrayList<RawMessage> rawTable = new ArrayList<>();

        int sumP = window.get(0) + window.get(10) + window.get(35) + window.get(45);;
        int sumV;
        int sumPNext;
        int beforeP = 0;

        boolean condition = true;
        while (condition) {
            sumPNext = window.get(1) + window.get(11) + window.get(36) + window.get(46);
            sumV = window.get(5) + window.get(15) + window.get(20) + window.get(25) + window.get(30) + window.get(40);
            if (beforeP < sumP && sumPNext < sumP && sumP >= 2 * sumV) {


                byte bit5 = 0;
                // let's calculate the first 5 bits to see if the DF is correct
                for (int j = 0; j < 5; j++) {
                    if (window.get(80 + 10 * j) >= window.get(85 + 10 * j)) {
                        //to be tested
                        bit5 += Math.pow(2, j);
                    }
                }

                // we need to check for type 17 inside of this function timael told me.
                // &= the highest value because then we can have bit5 unsigned... otherwise the highest number is 15

                if (Byte.toUnsignedInt(bit5) == 17) {
                    /**
                     * TODO the way i understood it we are supposed to use the function RawMessage.size(byte0) here
                     * but that doesn't seem to work because it never goes inside the if statement
                     * BUUUT it works and it seems to me to be very efficient we don't calculate the whole byte
                     * but only the first 5 bits... defo faster
                     */
                    ++UselessCounter;

                    timeStamp = window.position() * 100;


                    //TODO up to here the code works pretty well but either the bytes get put in wrong or crc doesn't read properly
                    byte[] table = new byte[14];
                    byte b = 0;
                    //Bit decoding
                    for (int i = 0; i < 14; i++) {
                        b = 0;
                        //this INNER for loop works
                        for (int j = 0; j < 8; j++) {
                            if (window.get(80 * (i + 1) + 10 * j) >= window.get(85 + 80 * i + 10 * j)) {
                                //to be tested
                                b += Math.pow(2, j);
                            }
                        }

                        table[i] = b;
                    }

                    // We need to check if crc = 0
                    // this method correctly gets the first message, good sign!! timeStamp = 63700
                    RawMessage checker = RawMessage.of(timeStamp, table);
                    //System.out.println(Arrays.toString(table));
                    if (checker != null){
                        rawTable.add(checker);
                    }
                }


            }
            window.advance();
            beforeP = sumP;
            sumP = sumPNext;

            //basically checking here if we read through the whole stream TODO i am not sure if this actually works!! but i get correct amount of DF's!!
            if(!window.isFull()){
                windowSize++;
                if(window.size() == windowSize){
                    condition = false;
                }
            }

        }
        //Todo with this I counted DF's --> 57660 delete later!
        System.out.println(UselessCounter);
        System.out.println(rawTable);
        return null;// Michel Schinz said end we return null #523
    }
}
