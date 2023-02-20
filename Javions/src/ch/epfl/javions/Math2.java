package ch.epfl.javions;

public final class Math2 {
    private Math2(){}

    public static int clamp(int min, int v, int max){
        if (min > max) throw new IllegalArgumentException();
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    public static double asinh(double x){

    }
}
