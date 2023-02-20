package ch.epfl.javions;

public final class Math2 {
    private Math2(){}

    /*  throws IllegalArgumentException if min bigger than max,
        returns the minimum if v is smaller than min
        returns the maximum if v is bigger than max
        returns v otherwise (3.5)
     */
    public static int clamp(int min, int v, int max){
        if (min > max) throw new IllegalArgumentException();
        else if (v < min) return min;
        else if (v > max) return max;
        return v;
    }

    // returns arsinh of the x value (3.5)
    public static double asinh(double x){
        return Math.log(x+Math.sqrt(1+Math.pow(x,2)));
    }
}
