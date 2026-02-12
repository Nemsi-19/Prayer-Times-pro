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
    private TextView locationStatus; // أضفنا هذا السطر

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prayerTimesText = findViewById(R.id.prayerTimesText);
        locationStatus = findViewById(R.id.locationStatus); // ربط نص حالة الموقع

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateUI(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            } else {
                locationStatus.setText("📍 جاري البحث عن إشارة GPS...");
                updateUI(36.8065, 10.1815); // تونس كافتراضي
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(double lat, double lon) {
        // تحديث نص الموقع في الأعلى
        locationStatus.setText("📍 موقعك: " + String.format("%.2f", lat) + ", " + String.format("%.2f", lon));

        Coordinates coordinates = new Coordinates(lat, lon);
        DateComponents dateComponents = DateComponents.from(new Date());
        CalculationParameters parameters = CalculationParameters.MUSLIM_WORLD_LEAGUE;
        parameters.madhab = Madhab.SHAFI;

        PrayerTimes prayerTimes = new PrayerTimes(coordinates, dateComponents, parameters);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        String result = "الفجر: " + formatter.format(prayerTimes.fajr) + "\n" +
                "الشروق: " + formatter.format(prayerTimes.sunrise) + "\n" +
                "الظهر: " + formatter.format(prayerTimes.dhuhr) + "\n" +
                "العصر: " + formatter.format(prayerTimes.asr) + "\n" +
                "المغرب: " + formatter.format(prayerTimes.maghrib) + "\n" +
                "العشاء: " + formatter.format(prayerTimes.isha);

        prayerTimesText.setText(result);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        }
    }
}
