package com.nemsi.spiritprayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.nemsi.spiritprayer.adhan.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        calculateAndDisplay(36.8065, 10.1815); 
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGPSService();
    }

    private void checkGPSService() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsEnabled = (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        if (!isGpsEnabled) {
            if (!isDialogShowing) showGPSDisabledDialog();
        } else {
            checkLocationPermission();
        }
    }

    private void showGPSDisabledDialog() {
        isDialogShowing = true;
        new AlertDialog.Builder(this)
            .setTitle("تفعيل الموقع")
            .setMessage("الـ GPS مغلق حالياً. يرجى تفعيله للحصول على مواقيت دقيقة.")
            .setPositiveButton("الإعدادات", (dialog, which) -> {
                isDialogShowing = false;
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            })
            .setNegativeButton("لاحقاً", (dialog, which) -> {
                isDialogShowing = false;
                dialog.dismiss();
            })
            .setCancelable(false).show();
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
                if (location != null) calculateAndDisplay(location.getLatitude(), location.getLongitude());
            });
        }
    }

    private void calculateAndDisplay(double lat, double lon) {
        Coordinates coordinates = new Coordinates(lat, lon);
        Date now = new Date();
        PrayerTimes prayerTimes = new PrayerTimes(coordinates, now, CalculationMethod.MUSLIM_WORLD_LEAGUE);
        
        displayAllPrayerTimes(prayerTimes);
        highlightNextPrayer(prayerTimes, now); // تمييز الصلاة القادمة
    }

    private void highlightNextPrayer(PrayerTimes prayerTimes, Date now) {
        // نحدد الصلاة القادمة
        Prayer nextPrayer = prayerTimes.nextPrayer(now);
        
        // إعادة الألوان للوضع الطبيعي أولاً (أبيض)
        resetColors();

        // تلوين الصلاة القادمة بلون مميز (أخضر فاتح)
        switch (nextPrayer) {
            case FAJR: ((TextView) findViewById(R.id.fajr_time)).setTextColor(Color.parseColor("#4CAF50")); break;
            case DHUHR: ((TextView) findViewById(R.id.dhuhr_time)).setTextColor(Color.parseColor("#4CAF50")); break;
            case ASR: ((TextView) findViewById(R.id.asr_time)).setTextColor(Color.parseColor("#4CAF50")); break;
            case MAGHRIB: ((TextView) findViewById(R.id.maghrib_time)).setTextColor(Color.parseColor("#4CAF50")); break;
            case ISHA: ((TextView) findViewById(R.id.isha_time)).setTextColor(Color.parseColor("#4CAF50")); break;
        }
    }

    private void resetColors() {
        int normalColor = Color.parseColor("#FFFFFF");
        ((TextView) findViewById(R.id.fajr_time)).setTextColor(normalColor);
        ((TextView) findViewById(R.id.dhuhr_time)).setTextColor(normalColor);
        ((TextView) findViewById(R.id.asr_time)).setTextColor(normalColor);
        ((TextView) findViewById(R.id.maghrib_time)).setTextColor(normalColor);
        ((TextView) findViewById(R.id.isha_time)).setTextColor(normalColor);
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
