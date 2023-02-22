package ch.epfl.javions;

public class Bits {
    private Bits(){}

    /**
     * In this function we extract a number out of a given value. The value is given as a binary Number.
     * In the first three lines we look at the exceptions. The size can't be bigger than 31 or smaller than 0.
     *
     * @param value
     * @param start
     * @param size
     * @return
     */
    public int extractUInt(long value, int start, int size){
        if (size < 0) throw new IllegalArgumentException();
        else if (size > 32) throw new IllegalArgumentException();
        //@Todo there is no way that this is correct, for example you have size = 5 and start = 10
        //that would give an exception... i propose that it is (start >= 0) && (start + size < 64)
        // I agree
        else if (!(start >= 0) || !(start + size < 64)) throw new IndexOutOfBoundsException();
        int extractedValue = (int) value >>>(start);
        int changedValueSize = 0;
        for(int i = 0; i < size; ++i){
            changedValueSize += Math.pow(2,i);
        }
        extractedValue &= changedValueSize;
        return extractedValue;
    }


    /**
     * In this function, we test if this index is what we want and throw an Exception is it isn't
     * If the number on the index(parameter)-ed value of value is equal to 1 we return true
     * otherwise we return false.
     *
     * @param value
     * @param index
     * @return
     */
    public static boolean testBit(long value, int index){
        if (index <= 0 ^ index > 64) throw new IndexOutOfBoundsException();
        String sValue = Long.toString(value);
        if (sValue.charAt(sValue.length()-index) == 1) return true;
        /**
         * I don't understand this method do they want us to give a return true iff the given value at the given index
         * is equal to one??? but give a OutOfBoundsException if it is between 0-63 ?? /TODO (3.9)
         *
         * Basically they want you to throw the Exception if index is not in [0,64[,
         * if the number at the index of value is equal to one, then you return true, if it isn't
         * then the function sends back false.
         *
         * QUICK DISCLAIMER, IT'S WRITTEN UNDERNEATH THAT BITS START ON THE RIGHT, NOT THE LEFT LIKE STRINGS DO
         * This is why I added sValue.length()
         */
        return false;
    }
}

