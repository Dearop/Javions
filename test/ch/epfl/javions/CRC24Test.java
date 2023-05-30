package ch.epfl.javions;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

/** This class tests Crc24 and the values we get from it
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public class CRC24Test {
    @Test
    void Crc24ValueTest1(){
        final Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        final String mS = "8D392AE499107FB5C00439";
        final String cS = "035DB8";
        final int c = Integer.parseInt(cS, 16); // == 0x035DB8

        final byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        final byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }
    @Test
    void Crc24ValueTest2(){
        final Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        final String mS = "8D4D2286EA428867291C08";
        final String cS = "EE2EC6";
        final int c = Integer.parseInt(cS, 16); // == 0x035DB8

        final byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        final byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }
    @Test
    void Crc24ValueTest3(){
        final Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        final String mS = "8D3950C69914B232880436";
        final String cS = "BC63D3";
        final int c = Integer.parseInt(cS, 16); // == 0x035DB8

        final byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        final byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }
    @Test
    void Crc24ValueTest4(){
        final Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        final String mS = "8D4B17E399893E15C09C21";
        final String cS = "9FC014";
        final int c = Integer.parseInt(cS, 16); // == 0x035DB8

        final byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        final byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }
    @Test
    void Crc24ValueTest5(){
        final Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        final String mS = "8D4B18F4231445F2DB63A0";
        final String cS = "DEEB82";
        final int c = Integer.parseInt(cS, 16); // == 0x035DB8

        final byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        final byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }

    @Test
    void Crc24ValueTest6(){
        final Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        final String mS = "8D495293F82300020049B8";
        final String cS = "111203";
        final int c = Integer.parseInt(cS, 16); // == 0x035DB8

        final byte[] mAndC = HexFormat.of().parseHex(mS + cS);
        assertEquals(0, crc24.crc(mAndC));

        final byte[] mOnly = HexFormat.of().parseHex(mS);
        assertEquals(c, crc24.crc(mOnly));
    }
}
