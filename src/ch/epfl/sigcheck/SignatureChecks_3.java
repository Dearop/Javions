package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_3 {
    private SignatureChecks_3() {}

    void checkSamplesDecoder() throws Exception {
        this.v01 = new ch.epfl.javions.demodulation.SamplesDecoder(this.v02, this.v03);
        this.v03 = this.v01.readBatch(this.v04);
    }

    void checkPowerComputer() throws Exception {
        this.v05 = new ch.epfl.javions.demodulation.PowerComputer(this.v02, this.v03);
        this.v03 = this.v05.readBatch(this.v06);
    }
   void checkPowerWindow() throws Exception {
       this.v07 = new ch.epfl.javions.demodulation.PowerWindow(this.v02, this.v03);
       this.v07.advance();
       this.v07.advanceBy(this.v03);
       this.v03 = this.v07.get(this.v03);
       this.v08 = this.v07.isFull();
       this.v09 = this.v07.position();
       this.v03 = this.v07.size();
    }

    ch.epfl.javions.demodulation.SamplesDecoder v01;
    java.io.InputStream v02;
    int v03;
    short[] v04;
    ch.epfl.javions.demodulation.PowerComputer v05;
    int[] v06;
    ch.epfl.javions.demodulation.PowerWindow v07;
    boolean v08;
    long v09;
}
