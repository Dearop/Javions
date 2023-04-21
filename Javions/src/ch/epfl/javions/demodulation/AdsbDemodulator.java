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
    private static final int WINDOW_SIZE = 1200;
    private static final int EXPECTED_DF = 14;
    private static final int MESSAGE_SIZE = 14;
    private static final int BIT_TIME_INTERVAL = 10;
    private static final int START_OF_BYTE = 80;
    private static final int START_OF_BYTE_CHECK = 85;
    private static final int TIMESTAMP_CONVERTER = 100;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        window = new PowerWindow(samplesStream, AdsbDemodulator.WINDOW_SIZE);
    }

    /**
     * @return RawMessage from the stream
     * @throws IOException when there is a stream error
     */
    public RawMessage nextMessage() throws IOException {

        // 0, 10, 35, 45 are the initial positions in the window for the sum of P
        int sumP = window.get(0) + window.get(10) + window.get(35) + window.get(45);
        int sumV;
        int sumPNext;
        int beforeP = 0;

        while (window.isFull()) {
            // 1, 11, 36, 46 are the positions in the window for the next sum of P, all the values got shifted by one.
            sumPNext = window.get(1) + window.get(11) + window.get(36) + window.get(46);

            // 5, 15, 20, 25, 30, 40 are the positions in the window for the sum of V
            sumV = window.get(5) + window.get(15) + window.get(20)
                    + window.get(25) + window.get(30) + window.get(40);

            if ((beforeP < sumP) && (sumPNext < sumP) && (sumP >= (2 * sumV))) {
                byte[] table = new byte[MESSAGE_SIZE];

                for (int byteIterator = 0; 8 > byteIterator; byteIterator++) {
                    if (window.get(START_OF_BYTE + (BIT_TIME_INTERVAL * byteIterator))
                            >= window.get(START_OF_BYTE_CHECK + (BIT_TIME_INTERVAL * byteIterator))) {

                        table[0] |= (byte) (1 << (7 - byteIterator));
                    }
                }

                if (EXPECTED_DF == RawMessage.size(table[0])) {

                    for (int byteUsed = 1; 14 > byteUsed; byteUsed++) {
                        for (int posInsideByte = 0; 8 > posInsideByte; posInsideByte++) {

                            if (this.window.get(START_OF_BYTE +
                                    (START_OF_BYTE * byteUsed) + (BIT_TIME_INTERVAL * posInsideByte))
                                    >= this.window.get(START_OF_BYTE_CHECK +
                                    (START_OF_BYTE * byteUsed) + (BIT_TIME_INTERVAL * posInsideByte))) {

                                table[byteUsed] |= (byte) (1 << (7 - posInsideByte));
                            }
                        }
                    }

                    RawMessage checker = RawMessage.of(window.position() * TIMESTAMP_CONVERTER, table);

                    if (null != checker) {
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
