package com.nemsi.spiritprayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
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

    private TextView prayerTimesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ربط النص الموجود في الواجهة بالبرمجة
        prayerTimesText = findViewById(R.id.prayerTimesText);

        // بدء عملية التحقق من إذن الموقع
        checkLocationPermission();
    }

    // دالة التحقق من الإذن
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // طلب الإذن إذا لم يكن موجوداً
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // جلب الموقع إذا كان الإذن ممنوحاً مسبقاً
            getUserLocation();
        }
    }

    // دالة جلب إحداثيات الموقع
    private void getUserLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        try {
            // محاولة جلب آخر موقع معروف لسرعة الاستجابة
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                displayPrayerTimes(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            } else {
                // إظهار تنبيه في حال عدم وجود إشارة GPS حالياً
                Toast.makeText(this, "جاري البحث عن إشارة GPS...", Toast.LENGTH_SHORT).show();
                displayPrayerTimes(36.8065, 10.1815); // موقع افتراضي (تونس)
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // دالة حساب وعرض المواقيت بناءً على الإحداثيات
    private void displayPrayerTimes(double lat, double lon) {
        Coordinates coordinates = new Coordinates(lat, lon);
        DateComponents dateComponents = DateComponents.from(new Date());
        CalculationParameters parameters = CalculationParameters.MUSLIM_WORLD_LEAGUE;
        parameters.madhab = Madhab.SHAFI;

        PrayerTimes prayerTimes = new PrayerTimes(coordinates, dateComponents, parameters);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        String result = "🕌 Prayer Times Pro\n" +
                "📍 موقعك الحالي: " + String.format("%.2f", lat) + ", " + String.format("%.2f", lon) + "\n\n" +
                "الفجر: " + formatter.format(prayerTimes.fajr) + "\n" +
                "الشروق: " + formatter.format(prayerTimes.sunrise) + "\n" +
                "الظهر: " + formatter.format(prayerTimes.dhuhr) + "\n" +
                "العصر: " + formatter.format(prayerTimes.asr) + "\n" +
                "المغرب: " + formatter.format(prayerTimes.maghrib) + "\n" +
                "العشاء: " + formatter.format(prayerTimes.isha);

        prayerTimesText.setText(result);
    }

    // التعامل مع رد فعل المستخدم بعد ظهور نافذة طلب الإذن
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // إذا وافق المستخدم، نجلب الموقع فوراً
                getUserLocation();
            } else {
                // إذا رفض، نستخدم الموقع الافتراضي مع تنبيه
                Toast.makeText(this, "تم رفض الإذن، سيتم استخدام موقع افتراضي", Toast.LENGTH_LONG).show();
                displayPrayerTimes(36.8065, 10.1815);
            }
        }
    }
}
