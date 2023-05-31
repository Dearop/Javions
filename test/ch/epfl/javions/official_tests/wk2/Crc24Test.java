package ch.epfl.javions.official_tests.wk2;

import ch.epfl.javions.Crc24;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Crc24Test {

    private static final HexFormat HEX_FORMAT = HexFormat.of();
    private static final List<String> ADSB_MESSAGES = List.of(
            "8D392AE499107FB5C00439035DB8",
            "8D39DD4158B511FDC118E1A835FE",
            "8D346083F8230006004BB862B42C",
            "8D506CA358B982DBAD9595A23761",
            "8D3CDD2158AF85CA4125E4620E46",
            "8D39CE6B990D91126808450C6A94",
            "8D49411499113AA890044A80894B",
            "8D4CA4EEEA466867791C0845193E",
            "8D484C5058353646A147292758A9",
            "8D47BA78EA4C4864013C084ABCAA",
            "8D0A009C9908673B1808408A5B0D");

    @Test
    void crc24CrcWorksOnAdsbMessages() {
        final var crc24 = new Crc24(Crc24.GENERATOR);

        // Pass full messages with valid CRCs, then check that result is 0.
        for (final var m : Crc24Test.ADSB_MESSAGES) {
            final var bs = Crc24Test.HEX_FORMAT.parseHex(m);
            Assertions.assertEquals(0, crc24.crc(bs));
        }

        // Pass messages without CRC, then check for equality.
        for (final var m : Crc24Test.ADSB_MESSAGES) {
            final var actualCrc = crc24.crc(Crc24Test.HEX_FORMAT.parseHex(m.substring(0, m.length() - 6)));
            final var expectedCrc = HexFormat.fromHexDigits(m.substring(m.length() - 6));
            Assertions.assertEquals(expectedCrc, actualCrc);
        }
    }

    @Test
    void crc24CrcWorksWithDifferentGenerator() {
        final var crc24_FACE51 = new Crc24(0xFACE51);
        final var actual_FACE51 = crc24_FACE51.crc(Crc24Test.HEX_FORMAT.parseHex(Crc24Test.ADSB_MESSAGES.get(0)));
        final var expected_FACE51 = 3677528;
        Assertions.assertEquals(expected_FACE51, actual_FACE51);

        final var crc24_F00DAB = new Crc24(0xF00DAB);
        final var actual_F00DAB = crc24_F00DAB.crc(Crc24Test.HEX_FORMAT.parseHex(Crc24Test.ADSB_MESSAGES.get(0)));
        final var expected_F00DAB = 16093840;
        Assertions.assertEquals(expected_F00DAB, actual_F00DAB);
    }

    @Test
    void crc24CrcWorksWithZeroOnlyMessage() {
        final var crc24 = new Crc24(Crc24.GENERATOR);
        for (int i = 0; 10 > i; i += 1) {
            final var m = new byte[i];
            Assertions.assertEquals(0, crc24.crc(m));
        }
    }
}