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

import static org.junit.jupiter.api.Assertions.*;

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
    @Test
    void decodePoistionTrowsIfMostRecentNotValid(){
        assertThrows(IllegalArgumentException.class, () -> CprDecoder.decodePosition(1/2, 1/2, 1/2, 1/2, 3));
        assertThrows(IllegalArgumentException.class, () -> CprDecoder.decodePosition(1/2, 1/2, 1/2, 1/2, -1));
    }

    @Test
    void decodePositionWorksForBasicCase(){
        double x0 = Math.scalb(111600d, -17);
        double y0 = Math.scalb(94445d, -17);
        double x1 = Math.scalb(108865d, -17);
        double y1 = Math.scalb(77558d, -17);
        GeoPos p = CprDecoder.decodePosition(x0, y0, x1, y1, 0);
        assertEquals(89192898, p.longitudeT32());
        assertEquals(552659081, p.latitudeT32());
        // p = (7.476062 , 46.323349) degree
        GeoPos r = CprDecoder.decodePosition(x0, y0, x1, y1, 1);
        assertEquals(89182208, r.longitudeT32());
        assertEquals(552647316, r.latitudeT32());
        // r = (7.7475166, 46.322363) degree
    }

    @Test
    void decodePositionWorksForNegativeCoord(){
        GeoPos p = CprDecoder.decodePosition(0.1, 0.91, 0.1, 0.01, 0);
        assertEquals(8947849, p.longitudeT32());
        assertEquals(-435939181, p.latitudeT32());
    }

    @Test
    void decodePositionReturnsNullIfLatitudeNotValid(){
        double[] wrongY0 = new double[]{0.9};
        double[] wrongY1 = new double[]{0.3};
        GeoPos p = CprDecoder.decodePosition(0.1 , wrongY0[0], 0.1 , wrongY1[0], 0);
        assertNull(p);

        GeoPos r = CprDecoder.decodePosition(0, 0.3, 0, 0, 0);
        assertNull(r);
    }

    @Test
    void decodePositionReturnsNullIfChangeOfZone(){
        double[] wrongY0 = new double[] {0.4, 0.4, 0.4, 0.4, 0.6, 0.6, 0.6, 0.6};
        double[] wrongY1 = new double[] {0.2, 0.30000000000000004, 0.7, 0.7999999999999999, 0.2, 0.30000000000000004,
                0.7, 0.7999999999999999};
        for(int i=0; i<8; i++){
            assertNull(CprDecoder.decodePosition(0.1, wrongY0[i], 0.1, wrongY1[i], 0 ));
        }
    }

    @Test
    void decodePositionWorksForValuesFromEd(){
        GeoPos a = CprDecoder.decodePosition(0.3, 0.3, 0.3, 0.3, 0);
        GeoPos b = CprDecoder.decodePosition(0.3, 0.3, 0.3, 0.3, 1);
        //System.out.println(a);
        //System.out.println(b);

        double lon0InDegrees = 1.8305084947496653;
        double lat0InDegrees = 1.7999999597668648;
        double lon1InDegrees = 1.862068958580494;
        double lat1InDegrees = 1.8305084947496653;

        //System.out.println(Units.convertFrom(lat1InDegrees, Units.Angle.DEGREE));
        //System.out.println(Units.convertTo(Units.convertFrom(lat1InDegrees, Units.Angle.DEGREE), Units.Angle.DEGREE));
        assertEquals(Units.convertFrom(lon0InDegrees, Units.Angle.DEGREE), a.longitude());
        assertEquals(lat0InDegrees, Units.convertTo(a.latitude(), Units.Angle.DEGREE));
        assertEquals(lon1InDegrees, Units.convertTo(b.longitude(), Units.Angle.DEGREE));
        assertEquals(Units.convertFrom(lat1InDegrees, Units.Angle.DEGREE), b.latitude());
        // if i convert from radians to degree it loses precision
    }

    @Test
    void valuesFromAFriend(){
        GeoPos a = CprDecoder.decodePosition(0.62,0.42,0.6200000000000000001,0.4200000000000000001,0);
        assertEquals(-2.3186440486460924, Units.convertTo(a.longitude(), Units.Angle.DEGREE));
        assertEquals(Units.convertFrom(2.5199999939650297, Units.Angle.DEGREE), a.latitude());
        // (-2.3186440486460924°, 2.5199999939650297°)
    }
}
