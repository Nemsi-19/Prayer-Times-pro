package com.nemsi.spiritprayer;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // هذه السطر هو السحر الذي يربط الجافا بالتصميم XML
        setContentView(R.layout.activity_main);
    }
}
