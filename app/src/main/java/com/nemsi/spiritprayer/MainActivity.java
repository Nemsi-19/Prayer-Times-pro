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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

// استيراد مكتبة Adhan بشكل كامل وصحيح
import com.batoulapps.adhan.CalculationMethod;
import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.Prayer;
import com.batoulapps.adhan.PrayerTimes;

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
                    calculateAndDisplay(34.4311, 8.7757); // إحداثيات قفصة/الرديف كمثال احتياطي
                }
            });
        }
    }

    private void calculateAndDisplay(double lat, double lon) {
        Coordinates coordinates = new Coordinates(lat, lon);
        Date now = new Date();
        PrayerTimes prayerTimes = new PrayerTimes(coordinates, now, CalculationMethod.MUSLIM_WORLD_LEAGUE);
        
        updateCityName(lat, lon);
        displayAllPrayerTimes(prayerTimes);
        
        Prayer next = prayerTimes.nextPrayer();
        Date nextDate = prayerTimes.timeForPrayer(next);
        
        // إذا كانت صلاة العشاء قد فاتت، فالصلاة القادمة هي فجر الغد
        if (next == Prayer.NONE) {
            // حساب فجر الغد (تبسيطاً سنعيد الحساب في المحاولة القادمة)
            nextDate = new Date(System.currentTimeMillis() + 8 * 3600000); 
        }
        
        if (nextDate != null) startCountdown(nextDate);
    }

    private void startCountdown(Date nextDate) {
        if (countDownTimer != null) countDownTimer.cancel();
        long diff = nextDate.getTime() - System.currentTimeMillis();

        countDownTimer = new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millis) {
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
                
                TextView timerTxt = findViewById(R.id.countdown_timer_text);
                if (timerTxt != null) timerTxt.setText(time);
            }
            @Override
            public void onFinish() { getLastLocation(); }
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
        } catch (Exception e) {}
    }

    private void updateHijriDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                HijrahDate hDate = HijrahDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ar"));
                TextView hijriTxt = findViewById(R.id.hijri_date_text);
                if (hijriTxt != null) hijriTxt.setText(hDate.format(formatter) + " هـ");
            } catch (Exception e) {}
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
