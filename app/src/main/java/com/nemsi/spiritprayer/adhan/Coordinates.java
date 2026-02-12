package com.nemsi.spiritprayer.adhan;

/**
 * تمثيل لإحداثيات الموقع الجغرافي
 */
public class Coordinates {
    public final double latitude;
    public final double longitude;

    /**
     * @param latitude خط العرض
     * @param longitude خط الطول
     */
    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
