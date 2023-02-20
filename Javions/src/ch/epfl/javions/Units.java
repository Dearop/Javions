package ch.epfl.javions;

// this class contains CONSTANTS that will be used throughout the project, they can't be changed or instantiated
public final class Units {
    private Units() {
    }

    public static final double CENTI = 1e-2;
    public static final double KILO = 1e-3;

    public static class Angle {
        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI;
        public static final double DEGREE = TURN / 360;
        public static final double T32 = TURN / (2 ^ 32);

        private Angle() {
        }
    }

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

    public static class Time {
        public static final double SECOND = 1;
        public static final double MINUTE = 60;
        public static final double HOUR = 60 * MINUTE;

        private Time() {
        }
    }

    public static class Speed {
        public static final double KNOT = 1.944;
        public static final double KILOMETER_PER_HOUR = 3.6;

        private Speed() {
        }
    }


    /**
     * these next three functions are defined in (3.4)
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
    public static double convertFrom(double value, double fromUnit) {
        return value * fromUnit;
    }

    // this function gives you the calculation from the base unit to the input unit that we want to reach.
    public static double convertTo(double value, double toUnit) {
        return value / toUnit;
    }
}
