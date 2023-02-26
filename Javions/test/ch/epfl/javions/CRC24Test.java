package ch.epfl.javions;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;
public class CRC24Test {
    @Test
    void Crc24ValueTests(){
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS1 = "8D392AE499107FB5C00439";
        String cS1 = "035DB8";
        int c = Integer.parseInt(cS1, 16); // == 0x035DB8

        byte[] mAndC = HexFormat.of().parseHex(mS1 + cS1);
        assertEquals(0, crc24.crc(mAndC));

        byte[] mOnly = HexFormat.of().parseHex(mS1);
        assertEquals(c, crc24.crc(mOnly));

    }



}
