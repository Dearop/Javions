package ch.epfl.javions;

/**
 * This class is an addon to the java Math class
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */

public final class Math2 {
    private Math2() {
    }

    /**
     * This method throws IllegalArgumentException if min bigger than max,
     * - returns the minimum if v is smaller than min
     * - returns the maximum if v is bigger than max
     * - returns v otherwise (3.5)
     *
     * @param min minimum value that int v can have
     * @param v   initial value
     * @param max maximum value that int v can have
     * @return v a int that only gets changed from the initial v if it is bigger than max or smaller than min
     */
    public static int clamp(int min, int v, int max) {
        if (min > max) throw new IllegalArgumentException();
        else if (v < min) return min;
        else if (v > max) return max;
        return v;
    }

    // returns arsinh of the x value (3.5)

    /**
     * this method makes it easier
     *
     * @param x for the input in arcsinh
     * @return returns arcsinh of the x value (3.5)
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + Math.pow(x, 2)));
    }
}
