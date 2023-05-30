package ch.epfl.javions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UnitsTest {
    private static final double DELTA = 1.0e-7;


    @Test
    void unitConvertWorksOnSomeUnits() {
        final var actual1 = Units.convert(2.34, Units.Angle.TURN, Units.Angle.DEGREE);
        final var expected1 = 842.4;
        assertEquals(expected1, actual1, UnitsTest.DELTA);

        final var actual2 = Units.convert(2.34, Units.Angle.DEGREE, Units.Angle.T32);
        final var expected2 = 2.7917287424E7;
        assertEquals(expected2, actual2, UnitsTest.DELTA);

        final var actual3 = Units.convert(2.34, Units.Length.KILOMETER, Units.Length.INCH);
        final var expected3 = 92125.98425196849;
        assertEquals(expected3, actual3, UnitsTest.DELTA);

        final var actual4 = Units.convert(2.34, Units.Length.INCH, Units.Length.FOOT);
        final var expected4 = 0.195;
        assertEquals(expected4, actual4, UnitsTest.DELTA);

        final var actual5 = Units.convert(2.34, Units.Length.FOOT, Units.Length.NAUTICAL_MILE);
        final var expected5 = 3.851144708423326E-4;
        assertEquals(expected5, actual5, UnitsTest.DELTA);

        final var actual6 = Units.convert(2.34, Units.Time.MINUTE, Units.Time.HOUR);
        final var expected6 = 0.039;
        assertEquals(expected6, actual6, UnitsTest.DELTA);

        final var actual7 = Units.convert(2.34, Units.Speed.KNOT, Units.Speed.KILOMETER_PER_HOUR);
        final var expected7 = 4.33368;
        assertEquals(expected7, actual7, UnitsTest.DELTA);
    }

    @Test
    void unitConvertFromWorksOnSomeUnits() {
        final var actual1 = Units.convertFrom(2.34, Units.Angle.TURN);
        final var expected1 = 14.70265361880023;
        assertEquals(expected1, actual1, UnitsTest.DELTA);

        final var actual2 = Units.convertFrom(2.34, Units.Angle.DEGREE);
        final var expected2 = 0.04084070449666731;
        assertEquals(expected2, actual2, UnitsTest.DELTA);

        final var actual3 = Units.convertFrom(2.34, Units.Length.CENTIMETER);
        final var expected3 = 0.0234;
        assertEquals(expected3, actual3, UnitsTest.DELTA);

        final var actual4 = Units.convertFrom(2.34, Units.Length.INCH);
        final var expected4 = 0.059436;
        assertEquals(expected4, actual4, UnitsTest.DELTA);

        final var actual5 = Units.convertFrom(2.34, Units.Length.FOOT);
        final var expected5 = 0.713232;
        assertEquals(expected5, actual5, UnitsTest.DELTA);

        final var actual6 = Units.convertFrom(2.34, Units.Time.MINUTE);
        final var expected6 = 140.39999999999998;
        assertEquals(expected6, actual6, UnitsTest.DELTA);

        final var actual7 = Units.convertFrom(2.34, Units.Speed.KNOT);
        final var expected7 = 1.2038;
        assertEquals(expected7, actual7, UnitsTest.DELTA);
    }

    @Test
    void unitConvertToWorksOnSomeUnits() {
        final var actual1 = Units.convertTo(2.34, Units.Angle.TURN);
        final var expected1 = 0.3724225668350351;
        assertEquals(expected1, actual1, UnitsTest.DELTA);

        final var actual2 = Units.convertTo(2.34, Units.Angle.DEGREE);
        final var expected2 = 134.07212406061262;
        assertEquals(expected2, actual2, UnitsTest.DELTA);

        final var actual3 = Units.convertTo(2.34, Units.Length.KILOMETER);
        final var expected3 = 0.00234;
        assertEquals(expected3, actual3, UnitsTest.DELTA);

        final var actual4 = Units.convertTo(2.34, Units.Length.INCH);
        final var expected4 = 92.12598425196849;
        assertEquals(expected4, actual4, UnitsTest.DELTA);

        final var actual5 = Units.convertTo(2.34, Units.Length.FOOT);
        final var expected5 = 7.6771653543307075;
        assertEquals(expected5, actual5, UnitsTest.DELTA);

        final var actual6 = Units.convertTo(2.34, Units.Time.MINUTE);
        final var expected6 = 0.039;
        assertEquals(expected6, actual6, UnitsTest.DELTA);

        final var actual7 = Units.convertTo(2.34, Units.Speed.KNOT);
        final var expected7 = 4.548596112311015;
        assertEquals(expected7, actual7, UnitsTest.DELTA);
    }
}