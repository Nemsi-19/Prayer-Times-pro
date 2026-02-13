package com.nemsi.spiritprayer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.nemsi.spiritprayer.adhan.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Coordinates coordinates = new Coordinates(34.0, 9.0);
        PrayerTimes prayerTimes = new PrayerTimes(coordinates, new Date(), CalculationMethod.MUSLIM_WORLD_LEAGUE);

        displayAllPrayerTimes(prayerTimes);
    }

    private void displayAllPrayerTimes(PrayerTimes prayerTimes) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        ((TextView) findViewById(R.id.fajr_time)).setText("الفجر: " + formatter.format(prayerTimes.fajr));
        ((TextView) findViewById(R.id.dhuhr_time)).setText("الظهر: " + formatter.format(prayerTimes.dhuhr));
        ((TextView) findViewById(R.id.asr_time)).setText("العصر: " + formatter.format(prayerTimes.asr));
        ((TextView) findViewById(R.id.maghrib_time)).setText("المغرب: " + formatter.format(prayerTimes.maghrib));
        ((TextView) findViewById(R.id.isha_time)).setText("العشاء: " + formatter.format(prayerTimes.isha));
    }
}
