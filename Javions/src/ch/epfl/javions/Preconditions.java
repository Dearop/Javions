package ch.epfl.javions;


/**
 * @author Paul Quesnot (347572)
 * @author Henri Antal (339444)
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Checks if a boolean has a true value, if not it throws an error (3.3)
     *
     * @param shouldBeTrue boolean which should have the truth value true
     * @throws IllegalArgumentException if shouldBeTrue is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }

}
