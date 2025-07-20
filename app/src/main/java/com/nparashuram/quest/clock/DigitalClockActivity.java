package com.nparashuram.quest.clock;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DigitalClockActivity extends AppCompatActivity {
    private TextView timeTextView;
    private TextView dateTextView;
    private NavigationBarView navigationBar;
    private Handler handler;
    private Runnable updateTimeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_clock);

        // Initialize views
        timeTextView = findViewById(R.id.timeTextView);
        dateTextView = findViewById(R.id.dateTextView);
        navigationBar = findViewById(R.id.navigationBar);

        // Setup navigation bar
        navigationBar.setup(this, "clock");

        // Initialize time update
        handler = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 1000);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTime();
        handler.postDelayed(updateTimeRunnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTimeRunnable);
    }

    private void updateTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        
        String currentTime = timeFormat.format(new Date());
        String currentDate = dateFormat.format(new Date());
        
        timeTextView.setText(currentTime);
        dateTextView.setText(currentDate);
    }
} 