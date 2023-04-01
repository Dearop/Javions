package ch.epfl.javions.official_tests.wk4;

import ch.epfl.javions.Crc24;
import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AirbornePositionMessageTest {
    @Test
    void airbornePositionMessageConstructorThrowsWhenTimeStampIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AirbornePositionMessage(-1, new IcaoAddress("ABCDEF"), 1000, 0, 0, 0);
        });
        assertDoesNotThrow(() -> {
            new AirbornePositionMessage(0, new IcaoAddress("ABCDEF"), 1000, 0, 0, 0);
        });
    }

    @Test
    void airbornePositionMessageConstructorThrowsWhenIcaoAddressIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new AirbornePositionMessage(100, null, 1000, 0, 0, 0);
        });
    }

    @Test
    void airbornePositionMessageConstructorThrowsWhenParityIsInvalid() {
        final var icaoAddress = new IcaoAddress("ABCDEF");
        for (int i = -100; 100 >= i; i += 1) {
            if (0 == i || 1 == i) continue;
            final var invalidParity = i;
            assertThrows(IllegalArgumentException.class, () -> {
                new AirbornePositionMessage(100, icaoAddress, 100, invalidParity, 0.5, 0.5);
            });
        }
    }

    @Test
    void airbornePositionMessageConstructorThrowsWhenXYAreInvalid() {
        final var icaoAddress = new IcaoAddress("ABCDEF");
        for (var invalidXY = 1.0d; 5.0d > invalidXY; invalidXY += 0.1) {
            final var xy = invalidXY;
            assertThrows(IllegalArgumentException.class, () -> {
                new AirbornePositionMessage(100, icaoAddress, 100, 0, xy, 0.5);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                new AirbornePositionMessage(100, icaoAddress, 100, 0, -xy, 0.5);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                new AirbornePositionMessage(100, icaoAddress, 100, 0, 0.5, xy);
            });
            assertThrows(IllegalArgumentException.class, () -> {
                new AirbornePositionMessage(100, icaoAddress, 100, 0, 0.5, -xy);
            });
        }
    }

    @Test
    void airbornePositionMessageOfCorrectlyDecodesAltitudeWhenQIs0() {
        record MessageAndAltitude(String message, double altitude) {
        }
        final var testValues = List.of(
                new MessageAndAltitude("8D4B1BB5598486491F4BDBF44FC6", 1584.96),
                new MessageAndAltitude("8D4B1BB5592C22D2A155F49835EF", 1798.32),
                new MessageAndAltitude("8D4B1BB5592422D2BB55FD991FA4", 1828.80),
                new MessageAndAltitude("8D4B1BB559A4264FDB4DDDC058EA", 1859.28),
                new MessageAndAltitude("8D4B1BB5598426509F4E1F032D5D", 1889.76),
                new MessageAndAltitude("8D4B1BB5598406514D4E5FEC1AC3", 1920.24),
                new MessageAndAltitude("8D4B1BB559A40653594F35F9A08F", 1950.72),
                new MessageAndAltitude("8D4B1BB55924065661506DA3728A", 1981.20),
                new MessageAndAltitude("8D4B1BB5592C02EE175E9AF78185", 2011.68),
                new MessageAndAltitude("8D4B1BB5590C02FAED63A05783E1", 2042.16),
                new MessageAndAltitude("8DADA2FD593682D9D99C2643E7DA", 2743.20));
        for (final var testValue : testValues) {
            final var message = RawMessage.of(100, HexFormat.of().parseHex(testValue.message));
            assertNotNull(message);
            final var airbornePositionMessage = AirbornePositionMessage.of(message);
            assertNotNull(airbornePositionMessage);
            assertEquals(testValue.altitude, airbornePositionMessage.altitude(), 0.005);
        }
    }

    @Test
    void airbornePositionMessageOfCorrectlyDecodesAltitudeWhenQIs1() {
        record MessageAndAltitude(String message, double altitude) {
        }
        final var testValues = List.of(
                new MessageAndAltitude("8D406666580D1652395CBE0A4D3E", 434.34),
                new MessageAndAltitude("8D4B1BDD5911A68127785A8F1273", 746.76),
                new MessageAndAltitude("8D344645584592A80D5BC637ED82", 3909.06),
                new MessageAndAltitude("8F405B66585915E28714229EFD13", 5067.30),
                new MessageAndAltitude("8D4B1A23586B8307F5B26CB39D00", 6217.92),
                new MessageAndAltitude("8D4402F2587563156B9880D4D855", 6812.28),
                new MessageAndAltitude("8D4402F25887D6AFD7A1A3769B45", 7962.90),
                new MessageAndAltitude("8D347307589B66396B69C91DD7B1", 9128.76),
                new MessageAndAltitude("8D4CA24558ADE68009DEF6E531E5", 10287.00),
                new MessageAndAltitude("8D49328A59CB16A537939E3B583D", 12016.74),
                new MessageAndAltitude("8D3B754358D311E57545B2100575", 12504.42));
        for (final var testValue : testValues) {
            final var message = RawMessage.of(100, HexFormat.of().parseHex(testValue.message));
            assertNotNull(message);
            final var airbornePositionMessage = AirbornePositionMessage.of(message);
            assertNotNull(airbornePositionMessage);
            assertEquals(testValue.altitude, airbornePositionMessage.altitude(), 0.005);
        }
    }

    // Code to generate the invalid messages used by the test below.
    List<String> airbornePositionMessagesWithInvalidAltitude() {
        final var crcComputer = new Crc24(Crc24.GENERATOR);
        final var messages = new ArrayList<String>();

        final var byte0 = "8D";
        final var icaoAddress = "406666";
        final var payload = 0x580D1652395CBEL;
        final var altMask = ((1L << 12) - 1) << 36;
        final var invalidAlts = new long[]{0b000000000000, 0b101010000000, 0b100010000000};
        for (final var alt : invalidAlts) {
            final var corruptedPayload = payload & ~altMask | (alt & 0xFFF) << 36;
            final var messageWithoutCRC = byte0 + icaoAddress + "%014X".formatted(corruptedPayload);
            final var messageBytes = HexFormat.of().parseHex(messageWithoutCRC);
            final var crc = crcComputer.crc(messageBytes);
            final var message = messageWithoutCRC + "%06X".formatted(crc);
            messages.add(message);
        }
        return messages;
    }

    @Test
    void airbornePositionMessageOfReturnsNullWhenAltitudeIsInvalid() {
        final var messages = List.of(
                "8D40666658000652395CBEB25722",
                "8D40666658A80652395CBED10630",
                "8D40666658880652395CBE7570E9");
        for (final var testValue : messages) {
            final var message = RawMessage.of(100, HexFormat.of().parseHex(testValue));
            assertNotNull(message);
            final var airbornePositionMessage = AirbornePositionMessage.of(message);
            assertNull(airbornePositionMessage);
        }
    }
}