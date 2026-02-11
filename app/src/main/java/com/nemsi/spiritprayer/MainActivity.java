package com.nemsi.spiritprayer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.CalculationParameters;
import com.batoulapps.adhan.CalculationMethod;
import com.batoulapps.adhan.PrayerTimes;
import com.batoulapps.adhan.data.DateComponents;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. تحديد إحداثيات الموقع (مثال: تونس العاصمة)
        Coordinates coordinates = new Coordinates(36.8065, 10.1815);

        // 2. ضبط طريقة الحساب (رابطة العالم الإسلامي كمثال)
        CalculationParameters params = CalculationMethod.MUSLIM_WORLD_LEAGUE.getParameters();

        // 3. تحديد التاريخ الحالي
        DateComponents date = DateComponents.from(new Date());

        // 4. تشغيل المحرك لحساب المواقيت
        PrayerTimes prayerTimes = new PrayerTimes(coordinates, date, params);

        // 5. عرض النتائج على الشاشة
        TextView welcomeText = findViewById(R.id.welcomeText);
        String results = "مواقيت الصلاة اليوم:\n\n" +
                "الفجر: " + prayerTimes.fajr + "\n" +
                "الظهر: " + prayerTimes.dhuhr + "\n" +
                "العصر: " + prayerTimes.asr + "\n" +
                "المغرب: " + prayerTimes.maghrib + "\n" +
                "العشاء: " + prayerTimes.isha;
        
        welcomeText.setText(results);
        welcomeText.setTextSize(20);
    }
}
