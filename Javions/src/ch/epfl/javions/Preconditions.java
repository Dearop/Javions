package ch.epfl.javions;

/**
 * This class allows the code to test as a precondition to see if a certain boolean is true when it should be true.
 *
 * @author Paul Quesnot (347572)
 * @author Henri Antal (339444)
 */
public enum Preconditions {
    ;

    /**
     * Checks if a boolean has a true value, if not it throws an error
     *
     * @param shouldBeTrue boolean which should have the truth value true
     * @throws IllegalArgumentException if shouldBeTrue is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}

