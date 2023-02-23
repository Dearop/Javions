package ch.epfl.javions;

/**
 * this class contains CONSTANTS that will be used throughout the project, they can't be changed or instantiated
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class Units {
    private Units() {
    }

    /**
     * Constant for the CENTI conversion, so 100*CENTI = 1 because CENTI = 0.01
     */
    public static final double CENTI = 1e-2;
    /**
     * Constant for the KILO conversion, so 20*KILO = 20_000 because KILO = 1000
     */
    public static final double KILO = 1e3;

    /**
     * Class that uses all the constants that are needed for doing the angle conversions
     */
    public static class Angle {
        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI;
        public static final double DEGREE = TURN / 360;
        public static final double T32 = TURN / Math.scalb(1, 32);

        private Angle() {
        }
    }

    /**
     * Class that uses all the constants that are needed for doing the length conversions
     */
    public static class Length {
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54 * CENTIMETER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE = 1852;

        private Length() {
        }
    }

    /**
     * Class that uses all the constants that are needed for doing the time conversions
     */
    public static class Time {
        public static final double SECOND = 1;
        public static final double MINUTE = 60;
        public static final double HOUR = 60 * MINUTE;

        private Time() {
        }
    }

    /**
     * Class that uses all the constants that are needed for doing the speed conversions, the base speed is
     * meters per second
     */
    public static class Speed {
        public static final double KNOT = 0.51444444444;
        public static final double KILOMETER_PER_HOUR = 1/3.6;

        private Speed() {
        }
    }


    /**
     * this function converts from one unit system to another unit system
     * for example convert(2, FOOT, CENTI) gives you the value 60.96 so we change from 2 foot to 60.96cm
     *
     * @param value    a
     * @param fromUnit b
     * @param toUnit   c
     * @return
     */
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * (fromUnit / toUnit);
    }

    // this function gives you the calculation from the given unit to the base unit

    /**
     * This function takes a value from one unit system and converts it to the base system.
     *
     * @param value    a
     * @param fromUnit b
     * @return The value in the base, for example meters, radians, etc.
     */
    public static double convertFrom(double value, double fromUnit) {
        return value * fromUnit;
    }

    // this function gives you the calculation from the base unit to the input unit that we want to reach.

    /**
     * This function takes a value from a base unit and then converts it to the new unit.
     *
     * @param value  a
     * @param toUnit b
     * @return The value will be returned in the given unit system, for example from meters to foot
     */
    public static double convertTo(double value, double toUnit) {
        return value / toUnit;
    }
}
