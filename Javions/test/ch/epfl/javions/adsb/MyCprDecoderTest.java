package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyCprDecoderTest {

    private final List<String> positions = List.of(        "(5.620176717638969°, 45.71530147455633°)",
            "(5.621292097494006°, 45.715926848351955°)",        "(5.62225341796875°, 45.71644593961537°)",
            "(5.623420681804419°, 45.71704415604472°)",        "(5.624397089704871°, 45.71759032085538°)",
            "(5.625617997720838°, 45.71820789948106°)",        "(5.626741759479046°, 45.718826316297054°)",
            "(5.627952609211206°, 45.71946484968066°)",        "(5.629119873046875°, 45.72007002308965°)",
            "(5.630081193521619°, 45.7205820735544°)",        "(5.631163045763969°, 45.72120669297874°)",
            "(5.633909627795219°, 45.722671514377°)",        "(5.634819064289331°, 45.72314249351621°)"
    );

    @Test
    void decodePosition() throws IOException {
        try (InputStream s = new FileInputStream("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\samples_20230304_1442.bin")) {
        int tc;
        RawMessage m;
        AirbornePositionMessage apm;
        List<String> o = new java.util.ArrayList<>();
        IcaoAddress expectedAddress = new IcaoAddress("4D2228");
        AdsbDemodulator adsbDemodulator = new AdsbDemodulator(s);
        Map<String, AirbornePositionMessage[]> hashMap = new HashMap<>();
        while ((m = adsbDemodulator.nextMessage()) != null) {
            tc = m.typeCode();
            if ((9 <= tc && tc <= 18) || (20 <= tc && tc <= 22)) {
                String icaoAddress = m.icaoAddress().string();
                if (!m.icaoAddress().equals(expectedAddress)) continue;
                apm = AirbornePositionMessage.of(m);
                if (apm == null) continue;
                if (hashMap.containsKey(icaoAddress)) {
                    AirbornePositionMessage[] apmm = hashMap.get(icaoAddress);
                    apmm[apm.parity()] = apm;
                    if ((apmm[(apm.parity()+ 1) & 1] != null)) {
                        GeoPos geoPos = CprDecoder.decodePosition(apmm[0].x(), apmm[0].y(), apmm[1].x(), apmm[1].y(), apm.parity());
                        if (geoPos != null) o.add(geoPos.toString());
                    }
                } else {
                    AirbornePositionMessage[] apmm = new AirbornePositionMessage[2];
                    apmm[apm.parity()] = apm;
                    hashMap.put(icaoAddress, apmm);                }

            }
        }
        assertArrayEquals(positions.toArray(), o.toArray());
        }
    }
}
