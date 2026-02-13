package com.nemsi.spiritprayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
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
        highlightNextPrayer(prayerTimes, now);
    }

    private void highlightNextPrayer(PrayerTimes pt, Date now) {
        resetColors();
        int highlightColor = Color.parseColor("#4CAF50"); // الأخضر

        // تحديد الصلاة القادمة يدوياً بمقارنة الوقت الحالي مع أوقات الصلوات
        if (now.before(pt.fajr)) {
            ((TextView) findViewById(R.id.fajr_time)).setTextColor(highlightColor);
        } else if (now.before(pt.dhuhr)) {
            ((TextView) findViewById(R.id.dhuhr_time)).setTextColor(highlightColor);
        } else if (now.before(pt.asr)) {
            ((TextView) findViewById(R.id.asr_time)).setTextColor(highlightColor);
        } else if (now.before(pt.maghrib)) {
            ((TextView) findViewById(R.id.maghrib_time)).setTextColor(highlightColor);
        } else if (now.before(pt.isha)) {
            ((TextView) findViewById(R.id.isha_time)).setTextColor(highlightColor);
        } else {
            // إذا فات وقت العشاء، الصلاة القادمة هي فجر اليوم التالي
            ((TextView) findViewById(R.id.fajr_time)).setTextColor(highlightColor);
        }
    }

    private void resetColors() {
        int white = Color.parseColor("#FFFFFF");
        ((TextView) findViewById(R.id.fajr_time)).setTextColor(white);
        ((TextView) findViewById(R.id.dhuhr_time)).setTextColor(white);
        ((TextView) findViewById(R.id.asr_time)).setTextColor(white);
        ((TextView) findViewById(R.id.maghrib_time)).setTextColor(white);
        ((TextView) findViewById(R.id.isha_time)).setTextColor(white);
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
 
