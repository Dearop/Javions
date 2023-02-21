package ch.epfl.javions;

public class Bits {
    private Bits(){}

    /**
     * In this function we extract a number out of a given value. The value is given as a binary Number.
     * In the first three lines we look at the exceptions. The size can't be bigger than 31 or smaller than 0.
     * Also the difference b
     * @param value
     * @param start
     * @param size
     * @return
     */
    public int extractUInt(long value, int start, int size){
        if (size < 0) throw new IllegalArgumentException();
        else if (size > 32) throw new IllegalArgumentException();
        //Todo there is no way that this is correct, for example you have size = 5 an d start = 10
        //that would give an exception... i propose that it is (start >= 0) && (start + size < 64)
        else if (0 <= size-start && size-start < 64) throw new IndexOutOfBoundsException();

        String sValue = Long.toString(value);
        String isolatedValue = "";
        for(int i = start; i < start+size; ++i){
            isolatedValue += sValue.charAt(i);
        }

        return Integer.parseInt(isolatedValue,2);
    }

    public static boolean testBit(long value, int index){

        /**
         * I don't understand this method do they want us to give a return true iff the given value at the given index
         * is equal to one??? but give a OutOfBoundsException if it is between 0-63 ?? /TODO (3.9)
         */
        return false;
    }
}

