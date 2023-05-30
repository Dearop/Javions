package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_4 {
    private SignatureChecks_4() {}

    void checkAdsbDemodulator() throws Exception {
        this.v01 = new ch.epfl.javions.demodulation.AdsbDemodulator(this.v02);
        this.v03 = this.v01.nextMessage();
    }

    void checkRawMessage() throws Exception {
        this.v03 = new ch.epfl.javions.adsb.RawMessage(this.v04, this.v05);
        this.v06 = ch.epfl.javions.adsb.RawMessage.LENGTH;
        this.v03 = ch.epfl.javions.adsb.RawMessage.of(this.v04, this.v07);
        this.v06 = ch.epfl.javions.adsb.RawMessage.size(this.v08);
        this.v06 = ch.epfl.javions.adsb.RawMessage.typeCode(this.v04);
        this.v05 = this.v03.bytes();
        this.v06 = this.v03.downLinkFormat();
        this.v10 = this.v03.equals(this.v09);
        this.v06 = this.v03.hashCode();
        this.v11 = this.v03.icaoAddress();
        this.v04 = this.v03.payload();
        this.v04 = this.v03.timeStampNs();
        this.v12 = this.v03.toString();
        this.v06 = this.v03.typeCode();
    }

    void checkAircraftStateSetter() throws Exception {
        this.v13.setAltitude(this.v14);
        this.v13.setCallSign(this.v15);
        this.v13.setCategory(this.v06);
        this.v13.setLastMessageTimeStampNs(this.v04);
        this.v13.setPosition(this.v16);
        this.v13.setTrackOrHeading(this.v14);
        this.v13.setVelocity(this.v14);
    }

    void checkMessage() throws Exception {
        this.v11 = this.v17.icaoAddress();
        this.v04 = this.v17.timeStampNs();
    }

    ch.epfl.javions.demodulation.AdsbDemodulator v01;
    java.io.InputStream v02;
    ch.epfl.javions.adsb.RawMessage v03;
    long v04;
    ch.epfl.javions.ByteString v05;
    int v06;
    byte[] v07;
    byte v08;
    Object v09;
    boolean v10;
    ch.epfl.javions.aircraft.IcaoAddress v11;
    String v12;
    ch.epfl.javions.adsb.AircraftStateSetter v13;
    double v14;
    ch.epfl.javions.adsb.CallSign v15;
    ch.epfl.javions.GeoPos v16;
    ch.epfl.javions.adsb.Message v17;
}
