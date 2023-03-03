package ch.epfl.javions.demodulation;

import java.io.InputStream;

public final class PowerComputer {

    public PowerComputer(InputStream stream, int batchsize){
        if(batchsize%8 != 0) throw new IllegalArgumentException();
        if(stream == null) throw new NullPointerException();
    }
}
