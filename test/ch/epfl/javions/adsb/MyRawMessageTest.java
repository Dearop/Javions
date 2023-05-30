package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class MyRawMessageTest {
    private final String f = "C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test\\ch\\epfl\\javions\\adsb\\MyRawMessageTest.java";
        @Test
        public void RawMessagesTest1() throws IOException{

            try (final InputStream s = new FileInputStream(this.f)) {
                final AdsbDemodulator d = new AdsbDemodulator(s);
                RawMessage m;
                while (null != (m = d.nextMessage())) {
                    System.out.println(m);
                }
            }
        }
        @Test
        public void RawMessageTest2() throws IOException{
            try (InputStream s = new FileInputStream(this.f)) {
                AdsbDemodulator d = new AdsbDemodulator(s);
                RawMessage m;
                m = d.nextMessage();
                System.out.println(m);
                assert null != m;
                assertEquals(m.downLinkFormat(), 17);
                assertEquals(m.icaoAddress(), new IcaoAddress("4B17E5"));
                assertEquals(m.typeCode(), 0xF8 >> 3);
            }
        }

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
            "8D0A009C9908673B1808408A5B0D"
    );

    private static final List<String> ADSB_ICAO = List.of(
            "392AE4",
            "39DD41",
            "346083",
            "506CA3",
            "3CDD21",
            "39CE6B",
            "494114",
            "4CA4EE",
            "484C50",
            "47BA78",
            "0A009C"
    );

    private static final int[] typeCodes = {19, 11, 31, 11, 11, 19, 19, 29, 11, 29, 19};

    @Test
    void of() {
        for (final String message : MyRawMessageTest.ADSB_MESSAGES) {
            assertNotNull(RawMessage.of(0L, HexFormat.of().withUpperCase().parseHex(message)));
        }
    }

    @Test
    void size() {
        System.out.println(0x0L);
        for (final String message : MyRawMessageTest.ADSB_MESSAGES) {
            assertEquals(RawMessage.LENGTH, RawMessage.size((byte) ByteString.ofHexadecimalString(message).byteAt(0)));
        }
        assertEquals(RawMessage.LENGTH, RawMessage.size((byte)0b10001000));
    }

    @Test
    void typeCode() {
        for (int i = 0; i < MyRawMessageTest.ADSB_MESSAGES.size(); i++) {
            assertEquals(MyRawMessageTest.typeCodes[i], new RawMessage(0L, ByteString.ofHexadecimalString(MyRawMessageTest.ADSB_MESSAGES.get(i))).typeCode());
        }
    }

    @Test
    void typeCodeWithGivenMe() {
        for (int i = 0; i < MyRawMessageTest.ADSB_MESSAGES.size(); i++) {
            System.out.println(new RawMessage(0L, ByteString.ofHexadecimalString(MyRawMessageTest.ADSB_MESSAGES.get(i))).payload());
            assertEquals(MyRawMessageTest.typeCodes[i], RawMessage.typeCode(new RawMessage(0L, ByteString.ofHexadecimalString(MyRawMessageTest.ADSB_MESSAGES.get(i))).payload()));
        }
    }

    @Test
    void downLinkFormat() {
        for (final String message : MyRawMessageTest.ADSB_MESSAGES) {
            assertEquals(17, new RawMessage(0L, ByteString.ofHexadecimalString(message)).downLinkFormat());
        }
    }

    @Test
    void icaoAddress() {
        for (int i = 0; i < MyRawMessageTest.ADSB_MESSAGES.size(); i++) {
            assertEquals(MyRawMessageTest.ADSB_ICAO.get(i), new RawMessage(0L, ByteString.ofHexadecimalString(MyRawMessageTest.ADSB_MESSAGES.get(i))).icaoAddress().string());
        }
    }
    }
