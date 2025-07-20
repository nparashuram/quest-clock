package com.nparashuram.quest.clock;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity {
    private TextView stopwatchTime;
    private Button startStopButton;
    private NavigationBarView navigationBar;

    private Handler handler;
    private Runnable updateTimeRunnable;
    private boolean isRunning = false;
    private long startTime = 0;
    private long elapsedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        // Initialize views
        stopwatchTime = findViewById(R.id.stopwatchTime);
        startStopButton = findViewById(R.id.startStopButton);
        Button resetButton = findViewById(R.id.resetButton);
        navigationBar = findViewById(R.id.navigationBar);

        // Setup navigation bar
        navigationBar.setup(this, "stopwatch");

        // Set up button click listeners
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    stopStopwatch();
                } else {
                    startStopwatch();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetStopwatch();
            }
        });

        // Initialize handler
        handler = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateStopwatchDisplay();
                handler.postDelayed(this, 10);
            }
        };

        updateStopwatchDisplay();
    }

    private void startStopwatch() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            isRunning = true;
            startStopButton.setText(R.string.stop);
            handler.postDelayed(updateTimeRunnable, 10);
        }
    }

    private void stopStopwatch() {
        if (isRunning) {
            elapsedTime = System.currentTimeMillis() - startTime;
            isRunning = false;
            startStopButton.setText(R.string.start);
            handler.removeCallbacks(updateTimeRunnable);
        }
    }

    private void resetStopwatch() {
        elapsedTime = 0;
        updateStopwatchDisplay();
    }

    private void updateStopwatchDisplay() {
        long currentTime = isRunning ? System.currentTimeMillis() - startTime : elapsedTime;
        int hours = (int) (currentTime / 3600000);
        int minutes = (int) (currentTime % 3600000) / 60000;
        int seconds = (int) (currentTime % 60000) / 1000;
        int milliseconds = (int) (currentTime % 1000) / 10;
        
        String timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d.%02d", 
            hours, minutes, seconds, milliseconds);
        stopwatchTime.setText(timeString);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRunning) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRunning) {
            handler.postDelayed(updateTimeRunnable, 10);
        }
    }
} 