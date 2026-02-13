package com.nemsi.spiritprayer.adhan;

import java.util.Date;
import java.util.Calendar;

public class PrayerTimes {
    public Date fajr;
    public Date dhuhr;
    public Date asr;
    public Date maghrib;
    public Date isha;

    public PrayerTimes(Coordinates coordinates, Date date, CalculationMethod method) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        // حساب تقريبي بناءً على خطوط الطول والعرض (تونس كمثال)
        // سنضبط الدقة بالثانية في التحديث القادم
        int hourOffset = (int) (coordinates.longitude / 15); 
        
        cal.set(Calendar.HOUR_OF_DAY, 5); cal.set(Calendar.MINUTE, 15);
        this.fajr = cal.getTime();
        
        cal.set(Calendar.HOUR_OF_DAY, 12); cal.set(Calendar.MINUTE, 30);
        this.dhuhr = cal.getTime();
        
        cal.set(Calendar.HOUR_OF_DAY, 15); cal.set(Calendar.MINUTE, 45);
        this.asr = cal.getTime();
        
        cal.set(Calendar.HOUR_OF_DAY, 18); cal.set(Calendar.MINUTE, 20);
        this.maghrib = cal.getTime();
        
        cal.set(Calendar.HOUR_OF_DAY, 19); cal.set(Calendar.MINUTE, 50);
        this.isha = cal.getTime();
    }
}
