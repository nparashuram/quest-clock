package com.nparashuram.quest.clock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class NavigationBarView extends LinearLayout {
    
    private static final String PREFS_NAME = "AlarmPrefs";
    private static final String KEY_ALARM_SET = "alarm_set";
    private static final String KEY_ALARM_TRIGGERED = "alarm_triggered";
    
    private ImageButton digitalClockButton;
    private ImageButton stopwatchButton;
    private ImageButton timerButton;
    private ImageButton alarmButton;
    private ImageButton closeButton;
    
    private AppCompatActivity currentActivity;
    private String currentScreen;

    public NavigationBarView(Context context) {
        super(context);
        init(context);
    }

    public NavigationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.navigation_bar, this, true);
        
        // Initialize buttons
        digitalClockButton = findViewById(R.id.digitalClockButton);
        stopwatchButton = findViewById(R.id.stopwatchButton);
        timerButton = findViewById(R.id.timerButton);
        alarmButton = findViewById(R.id.alarmButton);
        closeButton = findViewById(R.id.closeButton);
        
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Digital Clock button
        digitalClockButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivity != null && !"clock".equals(currentScreen)) {
                    Intent intent = new Intent(currentActivity, DigitalClockActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    currentActivity.startActivity(intent);
                }
            }
        });

        // Stopwatch button
        stopwatchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivity != null && !"stopwatch".equals(currentScreen)) {
                    Intent intent = new Intent(currentActivity, StopwatchActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    currentActivity.startActivity(intent);
                }
            }
        });

        // Timer button
        timerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivity != null && !"timer".equals(currentScreen)) {
                    Intent intent = new Intent(currentActivity, TimerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    currentActivity.startActivity(intent);
                }
            }
        });

        // Alarm button
        alarmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivity != null && !"alarm".equals(currentScreen)) {
                    Intent intent = new Intent(currentActivity, AlarmActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    currentActivity.startActivity(intent);
                }
            }
        });

        // Close button
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentActivity != null) {
                    currentActivity.finish();
                }
            }
        });
    }

    public void setup(AppCompatActivity activity, String screenName) {
        this.currentActivity = activity;
        this.currentScreen = screenName;
        
        // Hide the button for the current screen
        switch (screenName) {
            case "clock":
                digitalClockButton.setVisibility(View.GONE);
                break;
            case "stopwatch":
                stopwatchButton.setVisibility(View.GONE);
                break;
            case "timer":
                timerButton.setVisibility(View.GONE);
                break;
            case "alarm":
                alarmButton.setVisibility(View.GONE);
                break;
        }
        
        // Update alarm icon based on state
        updateAlarmIcon();
    }

    private void updateAlarmIcon() {
        if (currentActivity != null) {
            SharedPreferences prefs = currentActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean alarmSet = prefs.getBoolean(KEY_ALARM_SET, false);
            boolean alarmTriggered = prefs.getBoolean(KEY_ALARM_TRIGGERED, false);
            
            // Show filled icon if alarm is set and not triggered
            if (alarmSet && !alarmTriggered) {
                alarmButton.setImageResource(R.drawable.ic_alarm_filled);
            } else {
                alarmButton.setImageResource(R.drawable.ic_alarm);
            }
        }
    }

    public void refreshAlarmIcon() {
        updateAlarmIcon();
    }
} 