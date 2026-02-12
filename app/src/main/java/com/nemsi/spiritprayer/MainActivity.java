package com.nemsi.spiritprayer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.nemsi.spiritprayer.adhan.*; // استيراد مكتبتنا المحلية الجديدة
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. تحديد موقع جغرافي افتراضي (مثال: تونس)
        Coordinates coordinates = new Coordinates(34.0, 9.0);

        // 2. حساب أوقات الصلاة باستخدام المكتبة التي زرعناها يدوياً
        PrayerTimes prayerTimes = new PrayerTimes(
            coordinates, 
            new Date(), 
            CalculationMethod.MUSLIM_WORLD_LEAGUE
        );

        // 3. عرض النتائج في الواجهة (تأكد أن ID النص موجود في layout الخاص بك)
        displayPrayerTimes(prayerTimes);
    }

    private void displayPrayerTimes(PrayerTimes prayerTimes) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // ملاحظة: تأكد أن ملف activity_main.xml يحتوي على TextView بهذا الـ ID
        TextView fajrTextView = findViewById(R.id.fajr_time);
        if (fajrTextView != null) {
            fajrTextView.setText("الفجر: " + formatter.format(prayerTimes.fajr));
        }
        
        // يمكنك إضافة بقية الصلوات بنفس الطريقة (الظهر، العصر، إلخ)
    }
}
