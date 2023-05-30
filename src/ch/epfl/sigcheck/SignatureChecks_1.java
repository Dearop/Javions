package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_1 {
    private SignatureChecks_1() {}

    void checkPreconditions() throws Exception {
        ch.epfl.javions.Preconditions.checkArgument(this.v01);
    }

    void checkMath2() throws Exception {
        this.v02 = ch.epfl.javions.Math2.asinh(this.v02);
        this.v03 = ch.epfl.javions.Math2.clamp(this.v03, this.v03, this.v03);
    }

    void checkUnits() throws Exception {
        this.v02 = ch.epfl.javions.Units.CENTI;
        this.v02 = ch.epfl.javions.Units.KILO;
        this.v02 = ch.epfl.javions.Units.convert(this.v02, this.v02, this.v02);
        this.v02 = ch.epfl.javions.Units.convertFrom(this.v02, this.v02);
        this.v02 = ch.epfl.javions.Units.convertTo(this.v02, this.v02);
    }

    void checkSpeed() throws Exception {
        this.v02 = ch.epfl.javions.Units.Speed.KILOMETER_PER_HOUR;
        this.v02 = ch.epfl.javions.Units.Speed.KNOT;
    }

    void checkTime() throws Exception {
        this.v02 = ch.epfl.javions.Units.Time.HOUR;
        this.v02 = ch.epfl.javions.Units.Time.MINUTE;
        this.v02 = ch.epfl.javions.Units.Time.SECOND;
    }

    void checkLength() throws Exception {
        this.v02 = ch.epfl.javions.Units.Length.CENTIMETER;
        this.v02 = ch.epfl.javions.Units.Length.FOOT;
        this.v02 = ch.epfl.javions.Units.Length.INCH;
        this.v02 = ch.epfl.javions.Units.Length.KILOMETER;
        this.v02 = ch.epfl.javions.Units.Length.METER;
        this.v02 = ch.epfl.javions.Units.Length.NAUTICAL_MILE;
    }

    void checkAngle() throws Exception {
        this.v02 = ch.epfl.javions.Units.Angle.DEGREE;
        this.v02 = ch.epfl.javions.Units.Angle.RADIAN;
        this.v02 = ch.epfl.javions.Units.Angle.T32;
        this.v02 = ch.epfl.javions.Units.Angle.TURN;
    }

    void checkBits() throws Exception {
        this.v03 = ch.epfl.javions.Bits.extractUInt(this.v04, this.v03, this.v03);
        this.v01 = ch.epfl.javions.Bits.testBit(this.v04, this.v03);
    }

    void checkByteString() throws Exception {
        this.v05 = new ch.epfl.javions.ByteString(this.v06);
        this.v05 = ch.epfl.javions.ByteString.ofHexadecimalString(this.v07);
        this.v03 = this.v05.byteAt(this.v03);
        this.v04 = this.v05.bytesInRange(this.v03, this.v03);
        this.v01 = this.v05.equals(this.v08);
        this.v03 = this.v05.hashCode();
        this.v03 = this.v05.size();
        this.v07 = this.v05.toString();
    }

    void checkWebMercator() throws Exception {
        this.v02 = ch.epfl.javions.WebMercator.x(this.v03, this.v02);
        this.v02 = ch.epfl.javions.WebMercator.y(this.v03, this.v02);
    }

    void checkGeoPos() throws Exception {
        this.v09 = new ch.epfl.javions.GeoPos(this.v03, this.v03);
        this.v01 = ch.epfl.javions.GeoPos.isValidLatitudeT32(this.v03);
        this.v01 = this.v09.equals(this.v08);
        this.v03 = this.v09.hashCode();
        this.v02 = this.v09.latitude();
        this.v03 = this.v09.latitudeT32();
        this.v02 = this.v09.longitude();
        this.v03 = this.v09.longitudeT32();
        this.v07 = this.v09.toString();
    }

    boolean v01;
    double v02;
    int v03;
    long v04;
    ch.epfl.javions.ByteString v05;
    byte[] v06;
    java.lang.String v07;
    java.lang.Object v08;
    ch.epfl.javions.GeoPos v09;
}
