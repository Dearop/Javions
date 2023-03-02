package ch.epfl.javions;

import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WakeTurbulenceCategoryTest {
    @Test
    public void WakeTurbulenceCategoryIdentifiesUnknown(){
        assertEquals(WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of("HELLO"));
        assertEquals(WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of(""));
        assertEquals(WakeTurbulenceCategory.of("L"), WakeTurbulenceCategory.LIGHT);
        assertEquals(WakeTurbulenceCategory.of("M"), WakeTurbulenceCategory.MEDIUM);
        assertEquals(WakeTurbulenceCategory.of("H"), WakeTurbulenceCategory.HEAVY);
    }
}
