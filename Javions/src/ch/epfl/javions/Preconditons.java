package ch.epfl.javions;


public final class Preconditons {
    private Preconditons(){}

    // this checks if a boolean has a true value, if not it throws an error (3.3)
    void checkArgument (boolean shouldBeTrue){
        if(!shouldBeTrue) throw new IllegalArgumentException();
    }

}
