package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyCprDecoderTest {
    @Test
    public void CprDecoderWorksWithGivenExample(){
        double x0 = 111600/Math.pow(2,17);
        double x1 = 108865/Math.pow(2,17);
        double y0 = 94445/Math.pow(2,17);
        double y1 = 77558/Math.pow(2,17);
        assertEquals(CprDecoder.decodePosition(x0, y0, x1, y1, 0),
                new GeoPos((int) Units.convert(7.476062, Units.Angle.DEGREE, Units.Angle.T32),
                        (int) Units.convert(46.323349, Units.Angle.DEGREE, Units.Angle.T32)));
    }

    @Test
    public void CprDecoderWorksWithGivenExample2() {
        double x0 = 111600 / Math.pow(2, 17);
        double x1 = 108865 / Math.pow(2, 17);
        double y0 = 94445 / Math.pow(2, 17);
        double y1 = 77558 / Math.pow(2, 17);
        GeoPos expected = new GeoPos((int) Units.convert(7.476062, Units.Angle.DEGREE, Units.Angle.T32),
                (int) Units.convert(46.323349, Units.Angle.DEGREE, Units.Angle.T32));
        GeoPos actual = CprDecoder.decodePosition(x0, y0, x1, y1, 0);
        assertEquals(expected, actual);
    }
}
