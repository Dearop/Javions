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
    private static final int windowSize = 1200;
    private static final int ExpectedDF = 14;
    private static final int messageSize = 14;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        window = new PowerWindow(samplesStream, AdsbDemodulator.windowSize);
    }

    /**
     * @return RawMessage from the stream
     * @throws IOException when there is a stream error
     */
    public RawMessage nextMessage() throws IOException {
        int sumP = this.window.get(0) + this.window.get(10) + this.window.get(35) + this.window.get(45);
        int sumV;
        int sumPNext;
        int beforeP = 0;

        while (this.window.isFull()) {
            sumPNext = this.window.get(1) + this.window.get(11) + this.window.get(36) + this.window.get(46);
            sumV = this.window.get(5) + this.window.get(15) + this.window.get(20) + this.window.get(25) + this.window.get(30) + this.window.get(40);

            if ((beforeP < sumP) && (sumPNext < sumP) && (sumP >= (2 * sumV))) {
                final byte[] table = new byte[AdsbDemodulator.messageSize];

                for (int j = 0; 8 > j; j++) {
                    if (this.window.get(80 + (10 * j)) >= this.window.get(85 + (10 * j))) {
                        table[0] |= (byte) (1 << (7 - j));
                    }
                }

                if (ExpectedDF == RawMessage.size(table[0])) {

                    for (int i = 1; 14 > i; i++) {
                        for (int j = 0; 8 > j; j++) {
                            if (this.window.get(80 + (80 * i) + (10 * j)) >= this.window.get(85 + (80 * i) + (10 * j))) {
                                table[i] |= (byte) (1 << (7 - j));
                            }
                        }
                    }

                    final RawMessage checker = RawMessage.of(this.window.position() * 100, table);

                    if (null != checker) {
                        this.window.advanceBy(this.window.size());
                        return checker;
                    }
                }
            }

            beforeP = sumP;
            sumP = sumPNext;
            this.window.advance();
        }
        return null;
    }

}
