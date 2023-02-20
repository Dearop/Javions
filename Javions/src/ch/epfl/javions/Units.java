package ch.epfl.javions;

public final class Units {
    public static final double CENTI = 10^-2;
    public static final double KILO = 10^3;

    public static class Angle{
        public static final double RADIAN = 1;
        public static final double TURN = 2*Math.PI;
        public static final double DEGREE = TURN/360;
        public static final double T32 = TURN/(2^32);
        private Angle(){}
    }

    public static class Length{
        public static final double METER = 1;
        public static final double CENTIMETER = 10^-2;
        public static final double KILOMETER = 10^3;
        public static final double INCH = 2.54*CENTIMETER;
        public static final double FOOT = 12*INCH;
        public static final double NAUTICAL_MILE = 1852;
        private Length(){}
    }

    public static class Time{
        public static final double SECOND = 1;
        public static final double MINUTE = 60;
        public static final double HOUR = 60*MINUTE;
        private Time(){}
    }

    public static class Speed{
        public static final double KNOT = 1/1.944;
        public static final double KILOMETER_PER_HOUR = 1/3.6;
        private Speed(){}
    }
}
