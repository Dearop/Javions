package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private final PowerWindow window;
    private final static int windowSize = 1200;
    private long timeStamp;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.window = new PowerWindow(samplesStream, windowSize);
        timeStamp = 0;
    }

    public RawMessage nextMessage() throws IOException{
        int sumP;
        int sumV;
        int sumPNext;
        int beforeP = 0;
        boolean condition = true;
        while(condition) {
            if(!window.isFull()) return null;
            sumP = window.get(0) + window.get(10) + window.get(35) + window.get(45);
            sumPNext = window.get(1) + window.get(11) + window.get(36) + window.get(46);
            sumV = window.get(5) + window.get(15) + window.get(20) + window.get(25) + window.get(30) + window.get(40);
            if(beforeP < sumP && sumPNext < sumP && sumP >= 2*sumV){
                condition = false;
                timeStamp+= 8000;
            } else{
                window.advance();
                timeStamp += 100;
                beforeP = sumP;
            }
        }
        byte[] table = new byte[14];
        byte b = 0;
        //Bit decoding
        for (int i = 0; i < 14; i++) {
            b = 0;
            for (int j = 0; j < 8; j++) {
                if (window.get(80 * (i + 1) + 10 * j) >= window.get(85 + 80 * i + 10 * j)) {
                    //to be tested
                    b += Math.pow(2, j);
                }
            }
            table[13 - i]= b;
        }
        long currentTimeStamp = timeStamp;
        timeStamp += 112000;
        return RawMessage.of(currentTimeStamp, table);
    }
}
