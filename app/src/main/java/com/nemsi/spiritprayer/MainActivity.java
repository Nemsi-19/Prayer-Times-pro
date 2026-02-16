package com.nemsi.spiritprayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

// استيراد مكتبات Google Play Services للموقع
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

// استيراد مكتبة Adhan (المواقيت) مع تحديد الفئات بدقة
import com.batoulapps.adhan.CalculationMethod;
import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.Prayer;
import com.batoulapps.adhan.PrayerTimes;
import com.batoulapps.adhan.data.DateComponents;

// استيراد أدوات الوقت والتاريخ
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        updateHijriDate();
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    calculateAndDisplay(location.getLatitude(), location.getLongitude());
                } else {
                    // إحداثيات افتراضية في حال تعذر جلب الموقع (تونس العاصمة كمثال)
                    calculateAndDisplay(36.8065, 10.1815);
                }
            });
        }
    }

    private void calculateAndDisplay(double lat, double lon) {
        Coordinates coordinates = new Coordinates(lat, lon);
        
        // الحل للمشكلة السابقة: تحويل التاريخ الحالي إلى DateComponents
        DateComponents dateComponents = DateComponents.from(new Date());
        
        PrayerTimes prayerTimes = new PrayerTimes(coordinates, dateComponents, CalculationMethod.MUSLIM_WORLD_LEAGUE);
        
        updateCityName(lat, lon);
        displayAllPrayerTimes(prayerTimes);
        
        // جلب الصلاة القادمة والتحقق من الوقت المتبقي
        Prayer next = prayerTimes.nextPrayer();
        Date nextDate = prayerTimes.timeForPrayer(next);
        
        // معالجة حالة ما بعد صلاة العشاء (الانتقال لفجر اليوم التالي)
        if (next == Prayer.NONE || nextDate == null) {
            nextDate = new Date(System.currentTimeMillis() + 8 * 3600000); 
        }
        
        startCountdown(nextDate);
    }

    private void startCountdown(Date nextDate) {
        if (countDownTimer != null) countDownTimer.cancel();
        
        long diff = nextDate.getTime() - System.currentTimeMillis();

        countDownTimer = new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millis) {
                String timeText = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
                
                TextView timerTxt = findViewById(R.id.countdown_timer_text);
                if (timerTxt != null) timerTxt.setText(timeText);
            }
            @Override
            public void onFinish() { 
                getLastLocation(); // إعادة التحديث عند انتهاء العد
            }
        }.start();
    }

    private void updateCityName(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                if (city == null) city = addresses.get(0).getAdminArea();
                TextView locTxt = findViewById(R.id.location_text);
                if (locTxt != null) locTxt.setText(city);
            }
        } catch (Exception e) {
            TextView locTxt = findViewById(R.id.location_text);
            if (locTxt != null) locTxt.setText("الموقع الحالي");
        }
    }

    private void updateHijriDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                HijrahDate hDate = HijrahDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ar"));
                TextView hijriTxt = findViewById(R.id.hijri_date_text);
                if (hijriTxt != null) hijriTxt.setText(hDate.format(formatter) + " هـ");
            } catch (Exception e) {
                // معالجة صامتة في حال فشل التاريخ الهجري
            }
        }
    }

    private void displayAllPrayerTimes(PrayerTimes pt) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        if (findViewById(R.id.fajr_time) != null) {
            ((TextView) findViewById(R.id.fajr_time)).setText(sdf.format(pt.fajr));
            ((TextView) findViewById(R.id.dhuhr_time)).setText(sdf.format(pt.dhuhr));
            ((TextView) findViewById(R.id.asr_time)).setText(sdf.format(pt.asr));
            ((TextView) findViewById(R.id.maghrib_time)).setText(sdf.format(pt.maghrib));
            ((TextView) findViewById(R.id.isha_time)).setText(sdf.format(pt.isha));
        }
    }
}
