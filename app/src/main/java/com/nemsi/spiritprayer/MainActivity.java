package com.nemsi.spiritprayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.nemsi.spiritprayer.adhan.*;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private CountDownTimer countDownTimer;
    private TextView timerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // سنستخدم التاريخ الهجري ليكون مكاناً لعرض العداد أيضاً أو تحت اسم المدينة
        updateHijriDate();
        calculateAndDisplay(36.8065, 10.1815); 
    }

    private void startCountdown(Date nextPrayerDate) {
        if (countDownTimer != null) countDownTimer.cancel();

        long diffInMs = nextPrayerDate.getTime() - System.currentTimeMillis();

        countDownTimer = new CountDownTimer(diffInMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                String timeText = String.format(Locale.getDefault(), "باقي على الصلاة القادمة: %02d:%02d:%02d", hours, minutes, seconds);
                // تحديث نص الموقع ليظهر تحته العداد أو استبداله مؤقتاً
                ((TextView) findViewById(R.id.location_text)).setText(timeText);
            }

            @Override
            public void onFinish() {
                // إعادة الحساب عند انتهاء الوقت لدخول الصلاة الجديدة
                getLastLocation();
            }
        }.start();
    }

    private void calculateAndDisplay(double lat, double lon) {
        Coordinates coordinates = new Coordinates(lat, lon);
        Date now = new Date();
        PrayerTimes prayerTimes = new PrayerTimes(coordinates, now, CalculationMethod.MUSLIM_WORLD_LEAGUE);
        
        displayAllPrayerTimes(prayerTimes);
        
        // جلب الصلاة القادمة لتشغيل العداد عليها
        Prayer nextPrayer = prayerTimes.nextPrayer();
        Date nextPrayerDate = prayerTimes.timeForPrayer(nextPrayer);
        if (nextPrayerDate != null) {
            startCountdown(nextPrayerDate);
        }

        updateCityName(lat, lon);
    }

    // (بقية الدوال: updateCityName, updateHijriDate, checkLocationPermission إلخ تبقى كما هي في الكود السابق)
    // سأختصر الكود هنا لسهولة النسخ، لكن تأكد من وجود الدوال السابقة لديك
    
    private void updateCityName(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                if (city == null) city = addresses.get(0).getAdminArea();
                // سيتم عرض اسم المدينة في العنوان أو بجانب العداد
            }
        } catch (Exception e) {}
    }

    private void updateHijriDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            HijrahDate hDate = HijrahDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ar"));
            ((TextView) findViewById(R.id.hijri_date_text)).setText(hDate.format(formatter) + " هـ");
        }
    }

    private void displayAllPrayerTimes(PrayerTimes prayerTimes) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        ((TextView) findViewById(R.id.fajr_time)).setText(formatter.format(prayerTimes.fajr));
        ((TextView) findViewById(R.id.dhuhr_time)).setText(formatter.format(prayerTimes.dhuhr));
        ((TextView) findViewById(R.id.asr_time)).setText(formatter.format(prayerTimes.asr));
        ((TextView) findViewById(R.id.maghrib_time)).setText(formatter.format(prayerTimes.maghrib));
        ((TextView) findViewById(R.id.isha_time)).setText(formatter.format(prayerTimes.isha));
    }
}
