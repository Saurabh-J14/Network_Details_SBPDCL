package com.techLabs.nbpdcl.Utils;

public final class UTMConverter {
    private static final double WGS84_A = 6378137.0;
    private static final double WGS84_F = 1.0 / 298.257223563;
    private static final double K0 = 0.9996;

    private UTMConverter() {
    }

    public static Result fromLatLon(double latitude, double longitude) {
        int zoneNumber = (int) Math.floor((longitude + 180.0) / 6.0) + 1;

        if (latitude >= 56.0 && latitude < 64.0 && longitude >= 3.0 && longitude < 12.0) {
            zoneNumber = 32;
        }
        if (latitude >= 72.0 && latitude < 84.0) {
            if (longitude >= 0.0 && longitude < 9.0) zoneNumber = 31;
            else if (longitude >= 9.0 && longitude < 21.0) zoneNumber = 33;
            else if (longitude >= 21.0 && longitude < 33.0) zoneNumber = 35;
            else if (longitude >= 33.0 && longitude < 42.0) zoneNumber = 37;
        }

        double latRad = Math.toRadians(latitude);
        double lonRad = Math.toRadians(longitude);

        double e = Math.sqrt(WGS84_F * (2 - WGS84_F));
        double eSq = e * e;
        double ePrimeSq = eSq / (1 - eSq);

        double lonOrigin = (zoneNumber - 1) * 6 - 180 + 3;
        double lonOriginRad = Math.toRadians(lonOrigin);

        double N = WGS84_A / Math.sqrt(1 - eSq * Math.sin(latRad) * Math.sin(latRad));
        double T = Math.tan(latRad) * Math.tan(latRad);
        double C = ePrimeSq * Math.cos(latRad) * Math.cos(latRad);
        double A = Math.cos(latRad) * (lonRad - lonOriginRad);

        double M = WGS84_A * ((1 - eSq / 4 - 3 * eSq * eSq / 64 - 5 * eSq * eSq * eSq / 256) * latRad
                - (3 * eSq / 8 + 3 * eSq * eSq / 32 + 45 * eSq * eSq * eSq / 1024) * Math.sin(2 * latRad)
                + (15 * eSq * eSq / 256 + 45 * eSq * eSq * eSq / 1024) * Math.sin(4 * latRad)
                - (35 * eSq * eSq * eSq / 3072) * Math.sin(6 * latRad));

        double easting = K0 * N * (A + (1 - T + C) * Math.pow(A, 3) / 6
                + (5 - 18 * T + T * T + 72 * C - 58 * ePrimeSq) * Math.pow(A, 5) / 120)
                + 500000.0;

        double northing = K0 * (M + N * Math.tan(latRad) * (A * A / 2
                + (5 - T + 9 * C + 4 * C * C) * Math.pow(A, 4) / 24
                + (61 - 58 * T + T * T + 600 * C - 330 * ePrimeSq) * Math.pow(A, 6) / 720));

        if (latitude < 0) {
            northing += 10000000.0;
        }

        return new Result(easting, northing, zoneNumber, zoneLetter(latitude));
    }

    private static String zoneLetter(double latitude) {
        if (latitude >= 84 || latitude < -80) return "Z";
        String letters = "CDEFGHJKLMNPQRSTUVWX";
        int index = (int) Math.floor((latitude + 80) / 8);
        return String.valueOf(letters.charAt(index));
    }

    public static final class Result {
        public final double easting;
        public final double northing;
        public final int zoneNumber;
        public final String zoneLetter;

        public Result(double easting, double northing, int zoneNumber, String zoneLetter) {
            this.easting = easting;
            this.northing = northing;
            this.zoneNumber = zoneNumber;
            this.zoneLetter = zoneLetter;
        }

        public String getZone() {
            return zoneNumber + zoneLetter;
        }
    }
}
