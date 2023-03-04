package ch.epfl.javions;

import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplesDecoderTest {
    @Test
    public void SamplesDecodeThrowsExceptions() {
        InputStream stream = null;
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 3));
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(stream, -3));
    }

    @Test


    }

}
