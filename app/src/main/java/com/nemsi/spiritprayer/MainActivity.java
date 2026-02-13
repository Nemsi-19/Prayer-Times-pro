package com.nemsi.spiritprayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // 1. عرض أوقات تونس العاصمة فوراً (خطة بديلة لضمان ظهور بيانات)
        calculateAndDisplay(36.8065, 10.1815); 

        // 2. التحقق من حالة الـ GPS وطلب الصلاحيات من المستخدم
        checkGPSService();
    }

    private void checkGPSService() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsEnabled = false;
        if (locationManager != null) {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        if (!isGpsEnabled) {
            // إظهار تنبيه للمستخدم يطلب منه تفعيل الـ GPS
            new AlertDialog.Builder(this)
                .setTitle("تفعيل الموقع")
                .setMessage("الـ GPS مغلق حالياً. يرجى تفعيله للحصول على مواقيت دقيقة لموقعك الحالي.")
                .setPositiveButton("الإعدادات", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("إلغاء", (dialog, which) -> {
                    dialog.dismiss();
                    checkLocationPermission(); // محاولة طلب الصلاحية حتى لو الـ GPS مغلق
                })
                .setCancelable(false)
                .show();
        } else {
            checkLocationPermission();
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    // تحديث الأوقات بناءً على الموقع الدقيق للمستخدم فور توفره
                    calculateAndDisplay(location.getLatitude(), location.getLongitude());
                }
            });
        }
    }

    private void calculateAndDisplay(double lat, double lon) {
        Coordinates coordinates = new Coordinates(lat, lon);
        // استخدام إعدادات الحساب (يمكن تطويرها لاحقاً لتشمل طرق حساب مخصصة)
        PrayerTimes prayerTimes = new PrayerTimes(coordinates, new Date(), CalculationMethod.MUSLIM_WORLD_LEAGUE);
        displayAllPrayerTimes(prayerTimes);
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
