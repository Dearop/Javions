package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class AdsbDemodulator {
    private final PowerWindow window;
    private final static int windowSize = 1200;
    private final static int ExpectedDF = 14;
    private final static int messageSize = 14;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.window = new PowerWindow(samplesStream, windowSize);
    }

    /**
     *
     * @return RawMessage from the stream
     * @throws IOException when there is a stream error
     */
    public RawMessage nextMessage() throws IOException {
        int sumP = window.get(0) + window.get(10) + window.get(35) + window.get(45);
        int sumV;
        int sumPNext;
        int beforeP = 0;

        while (window.isFull()) {
            sumPNext = window.get(1) + window.get(11) + window.get(36) + window.get(46);
            sumV = window.get(5) + window.get(15) + window.get(20) + window.get(25) + window.get(30) + window.get(40);

            if ((beforeP < sumP) && (sumPNext < sumP) && (sumP >= (2 * sumV))) {
                byte[] table = new byte[messageSize];
                for (int j = 0; j < 8; j++) {
                    if (window.get(80 + (10 * j)) >= window.get(85 + (10 * j))) {
                        table[0]|=(byte) (1<< (7-j));
                    }
                }
                if (RawMessage.size(table[0]) == ExpectedDF) {

                    for (int i = 1; i < 14; i++) {
                        for (int j = 0; j < 8; j++) {
                            if (window.get(80 + (80 * i) + (10 * j)) >= window.get(85 + (80 * i) + (10 * j))) {
                                table[i] |=(byte) (1 << (7-j));
                            }
                        }
                    }
                    RawMessage checker = RawMessage.of(window.position() * 100, table);

                    if (checker != null) {
                        window.advanceBy(window.size());
                        return checker;
                    }
                }
            }
            beforeP = sumP;
            sumP = sumPNext;
            window.advance();
        }
        return null;
    }

}
