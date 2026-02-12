package com.nemsi.spiritprayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.CalculationParameters;
import com.batoulapps.adhan.Madhab;
import com.batoulapps.adhan.PrayerTimes;
import com.batoulapps.adhan.data.DateComponents;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. تعريف واجهة العرض
        TextView prayerTimesText = findViewById(R.id.prayerTimesText);

        // 2. التحقق من إذن الموقع (هذا هو الجزء الجديد)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // إذا لم نملك الإذن، نطلبه من المستخدم
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // إذا كان لدينا الإذن، نقوم بحساب المواقيت (مؤقتاً بإحداثيات تونس)
            displayPrayerTimes(prayerTimesText);
        }
    }

    // دالة حساب وعرض المواقيت
    private void displayPrayerTimes(TextView textView) {
        // إحداثيات افتراضية لتونس العاصمة
        Coordinates coordinates = new Coordinates(36.8065, 10.1815);
        DateComponents dateComponents = DateComponents.from(new Date());
        CalculationParameters parameters = CalculationParameters.MUSLIM_WORLD_LEAGUE;
        parameters.madhab = Madhab.SHAFI;

        PrayerTimes prayerTimes = new PrayerTimes(coordinates, dateComponents, parameters);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        String result = "🕌 مواقيت الصلاة برو\n\n" +
                "الفجر: " + formatter.format(prayerTimes.fajr) + "\n" +
                "الشروق: " + formatter.format(prayerTimes.sunrise) + "\n" +
                "الظهر: " + formatter.format(prayerTimes.dhuhr) + "\n" +
                "العصر: " + formatter.format(prayerTimes.asr) + "\n" +
                "المغرب: " + formatter.format(prayerTimes.maghrib) + "\n" +
                "العشاء: " + formatter.format(prayerTimes.isha);

        textView.setText(result);
    }
}
