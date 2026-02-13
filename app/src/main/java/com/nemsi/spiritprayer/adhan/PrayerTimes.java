package com.nemsi.spiritprayer.adhan;

import java.util.Date;
import java.util.Calendar;

public class PrayerTimes {
    public Date fajr;
    public Date sunrise;
    public Date dhuhr;
    public Date asr;
    public Date maghrib;
    public Date isha;

    public PrayerTimes(Coordinates coordinates, Date date, CalculationMethod method) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        // أوقات افتراضية مختلفة للتجربة
        cal.set(Calendar.HOUR_OF_DAY, 5); this.fajr = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 12); this.dhuhr = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 15); this.asr = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 18); this.maghrib = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 20); this.isha = cal.getTime();
    }
}
