package ch.epfl.javions;

public final class Units {
    public static final double CENTI = 10^-3;
    public static final double KILO = 10^3;

    public static class Angle{
        public static final double RADIAN = 1;
        public static final double TURN = 2*Math.PI;
        public static final double DEGREE = TURN/360;
        public static final double T32 = TURN/(2^32);
        private Angle(){}
    }

    public static class Length{
        public static final double METER;
        public static final double CENTIMETER;
        public static final double KILOMETER;
        public static final double INCH;
        public static final double FOOT;
        public static final double 
        private Length(){}
    }

    public static class Time{
        private Time(){}
    }

    public static class Speed{
        private Speed(){}
    }
}
