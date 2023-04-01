package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_5 {
    private SignatureChecks_5() {}

    void checkCprDecoder() throws Exception {
        this.v03 = ch.epfl.javions.adsb.CprDecoder.decodePosition(this.v01, this.v01, this.v01, this.v01, this.v02);
    }

    void checkAircraftIdentificationMessage() throws Exception {
        this.v04 = new ch.epfl.javions.adsb.AircraftIdentificationMessage(this.v05, this.v06, this.v02, this.v07);
        this.v04 = ch.epfl.javions.adsb.AircraftIdentificationMessage.of(this.v08);
        this.v07 = this.v04.callSign();
        this.v02 = this.v04.category();
        this.v10 = this.v04.equals(this.v09);
        this.v02 = this.v04.hashCode();
        this.v06 = this.v04.icaoAddress();
        this.v05 = this.v04.timeStampNs();
        this.v11 = this.v04.toString();
    }

    void checkAirbornePositionMessage() throws Exception {
        this.v12 = new ch.epfl.javions.adsb.AirbornePositionMessage(this.v05, this.v06, this.v01, this.v02, this.v01, this.v01);
        this.v12 = ch.epfl.javions.adsb.AirbornePositionMessage.of(this.v08);
        this.v01 = this.v12.altitude();
        this.v10 = this.v12.equals(this.v09);
        this.v02 = this.v12.hashCode();
        this.v06 = this.v12.icaoAddress();
        this.v02 = this.v12.parity();
        this.v05 = this.v12.timeStampNs();
        this.v11 = this.v12.toString();
        this.v01 = this.v12.x();
        this.v01 = this.v12.y();
    }

    double v01;
    int v02;
    ch.epfl.javions.GeoPos v03;
    ch.epfl.javions.adsb.AircraftIdentificationMessage v04;
    long v05;
    ch.epfl.javions.aircraft.IcaoAddress v06;
    ch.epfl.javions.adsb.CallSign v07;
    ch.epfl.javions.adsb.RawMessage v08;
    Object v09;
    boolean v10;
    String v11;
    ch.epfl.javions.adsb.AirbornePositionMessage v12;
}
