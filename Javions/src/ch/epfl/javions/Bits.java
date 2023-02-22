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
        if (index < 0 || index > 63) throw new IndexOutOfBoundsException();
        int extractedValue = (int) value >>> index;
        if((extractedValue &= 1) == 1) return true;
        return false;
    }
}

