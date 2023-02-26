package ch.epfl.javions;

public final class Crc24 {

    public final static int GENERATOR = FFF409;
    public Crc24(int GENERATOR){

    }

    public int crc(byte[] bytes){
        crc_bitwise(GENERATOR, bytes);
        return 13121212;
    };
    private byte crc_bitwise(int GENERATOR, byte[] bytes){

    }
}
