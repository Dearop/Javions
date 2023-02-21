package ch.epfl.javions;


/**
 * @author Paul Quesnot (347572)
 */
public final class Preconditions {
    private Preconditions() {
    }


    /**
     * this checks if a boolean has a true value, if not it throws an error (3.3)
     *
     * @param shouldBeTrue
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }

}
