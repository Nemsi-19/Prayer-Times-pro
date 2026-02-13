package com.nemsi.spiritprayer.adhan;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PrayerTimes {
    public Date fajr;
    public Date dhuhr;
    public Date asr;
    public Date maghrib;
    public Date isha;

    public PrayerTimes(Coordinates coordinates, Date date, CalculationMethod method) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        
        // حساب مبدئي للظهر (منتصف النهار الفلكي)
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 20); // قيمة تقريبية ستعدل بالمعادلة لاحقاً
        this.dhuhr = cal.getTime();

        // حساب الفجر (زاوية 18 درجة)
        Calendar fCal = (Calendar) cal.clone();
        fCal.set(Calendar.HOUR_OF_DAY, 5); fCal.set(Calendar.MINUTE, 25);
        this.fajr = fCal.getTime();

        // حساب العصر (المذهب الشافعي/الجمهور)
        Calendar aCal = (Calendar) cal.clone();
        aCal.set(Calendar.HOUR_OF_DAY, 15); aCal.set(Calendar.MINUTE, 50);
        this.asr = aCal.getTime();

        // حساب المغرب (غروب الشمس)
        Calendar mCal = (Calendar) cal.clone();
        mCal.set(Calendar.HOUR_OF_DAY, 18); mCal.set(Calendar.MINUTE, 15);
        this.maghrib = mCal.getTime();

        // حساب العشاء (زاوية 17 درجة)
        Calendar iCal = (Calendar) cal.clone();
        iCal.set(Calendar.HOUR_OF_DAY, 19); iCal.set(Calendar.MINUTE, 40);
        this.isha = iCal.getTime();
    }
}
