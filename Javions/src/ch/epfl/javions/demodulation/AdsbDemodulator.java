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
    private static final int[] sumP_Values = {0, 10, 35, 45};
    private static final int[] sumV_Values = {5, 15, 20, 25, 30, 40};
    private static final int[] nextSumP_Values = {1, 11, 36, 46};
    private static final int BYTE_SIZE_FOR_SHIFTING = 7;
    private static byte[] table;
    private static final int BYTE_SIZE = 8;
    private static final int START_POS = 0;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        window = new PowerWindow(samplesStream, WINDOW_SIZE);
    }

    /**
     * @return RawMessage from the stream
     * @throws IOException when there is a stream error
     */
    public RawMessage nextMessage() throws IOException {

        // 0, 10, 35, 45 are the initial positions in the window for the sum of P
        int sumP = window.get(sumP_Values[0]) +
                window.get(sumP_Values[1]) + window.get(sumP_Values[2]) + window.get(sumP_Values[3]);
        int sumV;
        int sumPNext;
        int beforeP = 0;

        while (window.isFull()) {
            // 1, 11, 36, 46 are the positions in the window for the next sum of P, all the values got shifted by one.
            sumPNext = window.get(nextSumP_Values[0]) +
                    window.get(nextSumP_Values[1]) + window.get(nextSumP_Values[2]) + window.get(nextSumP_Values[3]);

            // 5, 15, 20, 25, 30, 40 are the positions in the window for the sum of V
            sumV = window.get(sumV_Values[0]) + window.get(sumV_Values[1]) + window.get(sumV_Values[2])
                    + window.get(sumV_Values[3]) + window.get(sumV_Values[4]) + window.get(sumV_Values[5]);

            if ((beforeP < sumP) && (sumPNext < sumP) && (sumP >= (2 * sumV))) {
                table = new byte[MESSAGE_SIZE];

                fillTable(window, START_POS);

                if (EXPECTED_DF == RawMessage.size(table[0])) {

                    for (int byteUsed = 1; 14 > byteUsed; byteUsed++) {
                        fillTable(window, byteUsed);
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

    public static void fillTable(PowerWindow window, int pos) {


        for (int byteIterator = 0; BYTE_SIZE > byteIterator; byteIterator++) {
            if (window.get(START_OF_BYTE + (START_OF_BYTE * pos) + (BIT_TIME_INTERVAL * byteIterator))
                    >= window.get(START_OF_BYTE_CHECK + (START_OF_BYTE * pos) +  (BIT_TIME_INTERVAL * byteIterator))) {

                table[pos] |= (byte) (1 << (BYTE_SIZE_FOR_SHIFTING - byteIterator));
            }
        }
    }

}
