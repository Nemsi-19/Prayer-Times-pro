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
                    // إحداثيات افتراضية في حال فشل جلب الموقع
                    calculateAndDisplay(36.8065, 10.1815);
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
        
        // جلب الصلاة القادمة والعد التنازلي
        Prayer next = prayerTimes.nextPrayer();
        Date nextDate = prayerTimes.timeForPrayer(next);
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
                ((TextView) findViewById(R.id.countdown_timer_text)).setText(time);
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
                ((TextView) findViewById(R.id.location_text)).setText(city);
            }
        } catch (Exception e) {
            ((TextView) findViewById(R.id.location_text)).setText("موقعي الحالي");
        }
    }

    private void updateHijriDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            HijrahDate hDate = HijrahDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ar"));
            ((TextView) findViewById(R.id.hijri_date_text)).setText(hDate.format(formatter) + " هـ");
        }
    }

    private void displayAllPrayerTimes(PrayerTimes pt) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        ((TextView) findViewById(R.id.fajr_time)).setText(sdf.format(pt.fajr));
        ((TextView) findViewById(R.id.dhuhr_time)).setText(sdf.format(pt.dhuhr));
        ((TextView) findViewById(R.id.asr_time)).setText(sdf.format(pt.asr));
        ((TextView) findViewById(R.id.maghrib_time)).setText(sdf.format(pt.maghrib));
        ((TextView) findViewById(R.id.isha_time)).setText(sdf.format(pt.isha));
    }
}
