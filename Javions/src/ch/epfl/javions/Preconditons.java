package ch.epfl.javions;


public final class Preconditons {
    private Preconditons(){}

    void checkArgument (boolean shouldBeTrue){
        if(shouldBeTrue == false) throw new IllegalArgumentException();
    }

}
