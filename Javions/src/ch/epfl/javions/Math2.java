package ch.epfl.javions;

/**
 * Add-on to the java Math class
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */

public final class Math2 {
    private Math2() {
    }

    /**
     * This method returns the minimum if v is smaller than min or returns the maximum if v is bigger than max and
     * returns v otherwise (3.5)
     *
     * @param min int signifying greatest lower bound
     * @param v   int the value
     * @param max int signifying smallest upper bound
     * @return int that only gets changed from the initial v if it is bigger than max or smaller than min
     * @throws IllegalArgumentException if min is bigger than max
     */
    public static int clamp(int min, int v, int max) {
        if (min > max) throw new IllegalArgumentException();

        else if (v < min) return min;
        else if (v > max) return max;

        return v;
    }


    /**
     * @param x double to which we want to apply the function f(x) = arcsinh(x)
     * @return double value of arcsinh(x)
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + Math.pow(x, 2)));
    }
}
