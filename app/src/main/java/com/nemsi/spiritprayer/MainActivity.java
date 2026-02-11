package com.nemsi.spiritprayer;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // واجهة ترحيبية بسيطة للتأكد من عمل التطبيق
        TextView textView = new TextView(this);
        textView.setText("مرحباً بك في مواقيت الصلاة برو\n\nتطبيقك قيد الإنشاء بنجاح!");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(22);
        
        setContentView(textView);
    }
}
