package ch.epfl.javions;

/**
 * this class contains CONSTANTS that will be used throughout the project, they can't be changed or instantiated
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class Units {
    private Units() {}

    /**
     * Constant for the CENTI conversion, so 100*CENTI = 1
     */
    public static final double CENTI = 1e-2;
    /**
     * Constant for the KILO conversion, so 20*KILO = 20_000 because KILO = 1000
     */
    public static final double KILO = 1e3;

    /**
     * Class that contains all the constants that are needed for doing the angle conversions(base unit is the radian)
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
     * Class that contains all the constants that are needed for doing the length conversions(base unit is the meter)
     */
    public static class Length {
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54 * CENTIMETER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE = METER * 1852;

        private Length() {
        }
    }

    /**
     * Class that contains all the constants that are needed for doing the time conversions (base unit is the second)
     */
    public static class Time {
        public static final double SECOND = 1;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;

        private Time() {
        }
    }

    /**
     * Class that contains all the constants that are needed for doing the speed conversions, the base speed is
     * meters per second
     */
    public static class Speed {
        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER /Time.HOUR;

        private Speed() {
        }
    }


    /**
     * this function converts from one unit to another unit
     * for example convert(2, FOOT, CENTI) gives you the value 60.96 so we change from 2 foot to 60.96cm
     *
     * @param value    double representing a value in fromUnit
     * @param fromUnit double representing the value of a unit in the base units of the appropriate unit system
     *                 and the unit in which value is expressed
     * @param toUnit   double representing the value of a unit in the base units of the appropriate unit system
     *                 and the unit in which we want value to be expressed
     * @return the value in the unit toUnit
     */
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * (fromUnit / toUnit);
    }

    /**
     * This function takes a value from one unit system and converts it to the base unit in the appropriate unit system.
     *
     * @param value    double representing a value in fromUnit
     * @param fromUnit double representing the value of a unit in the base units of the appropriate unit system
     *                 and the unit in which value is expressed
     * @return double representing the value of value in the base units of the appropriate unit system.
     */
    public static double convertFrom(double value, double fromUnit) {
        return value * fromUnit;
    }

    /**
     * This function takes a value from a base unit and then converts it to the new unit.
     *
     * @param value  double representing a value in the base units of the appropriate unit system
     * @param toUnit double representing the value of a unit in the base units of the appropriate unit system
     *               and the unit in which we want value to be expressed
     * @return double representing the value in the toUnit unit
     */
    public static double convertTo(double value, double toUnit) {
        return value / toUnit;
    }
}