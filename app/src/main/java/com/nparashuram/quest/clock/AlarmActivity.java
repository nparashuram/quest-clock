package com.nparashuram.quest.clock;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AlarmPrefs";
    private static final String KEY_ALARM_HOUR = "alarm_hour";
    private static final String KEY_ALARM_MINUTE = "alarm_minute";
    private static final String KEY_ALARM_SET = "alarm_set";
    private static final String KEY_ALARM_TRIGGERED = "alarm_triggered";
    
    private TextView currentTimeText;
    private Button alarmButton;
    private NavigationBarView navigationBar;

    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;
    private boolean alarmSet = false;
    private int selectedHour = 7;
    private int selectedMinute = 0;
    private SharedPreferences prefs;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Initialize views
        currentTimeText = findViewById(R.id.currentTimeText);
        alarmButton = findViewById(R.id.alarmButton);
        navigationBar = findViewById(R.id.navigationBar);

        // Setup navigation bar
        navigationBar.setup(this, "alarm");

        // Initialize AlarmManager
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Create notification channel for Android 8.0+
        createNotificationChannel();

        // Initialize time format
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Load saved alarm time
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        selectedHour = prefs.getInt(KEY_ALARM_HOUR, 7);
        selectedMinute = prefs.getInt(KEY_ALARM_MINUTE, 0);
        alarmSet = prefs.getBoolean(KEY_ALARM_SET, false);
        
        updateTimeDisplay();

        // Set up button click listeners
        currentTimeText.setOnClickListener(v -> showTimePickerDialog());
        
        alarmButton.setOnClickListener(v -> {
            if (alarmSet) {
                cancelAlarm();
            } else {
                setAlarm();
            }
        });

        // Update button state based on saved alarm state
        updateButtonState();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                updateTimeDisplay();
                
                // Save the selected time
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(KEY_ALARM_HOUR, selectedHour);
                editor.putInt(KEY_ALARM_MINUTE, selectedMinute);
                editor.apply();
            },
            selectedHour,
            selectedMinute,
            false // 24-hour format
        );
        
        timePickerDialog.show();
    }

    private void updateTimeDisplay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);
        
        String timeString = timeFormat.format(calendar.getTime());
        currentTimeText.setText(timeString);
    }

    private void setAlarm() {
        try {
            // Check if we have permission to set exact alarms
            if (!hasExactAlarmPermission()) {
                requestExactAlarmPermission();
                return;
            }
            
            // Calculate alarm time
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            calendar.set(Calendar.SECOND, 0);
            
            // If the time has already passed today, set it for tomorrow
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            
            // For testing: set alarm to 5 seconds after current time
            //calendar.setTimeInMillis(System.currentTimeMillis() + 5000);
            
            // Create intent for alarm
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            
            // Create PendingIntent with proper flags
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            flags |= PendingIntent.FLAG_IMMUTABLE;

            alarmPendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, flags);
            
            // Set the alarm
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);

            alarmSet = true;
            updateButtonState();
            
            // Save alarm state
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_ALARM_SET, true);
            editor.apply();

            // Refresh navigation alarm icon
            navigationBar.refreshAlarmIcon();

            // Show confirmation message with seconds using DateFormat
            Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e("AlarmActivity", "Failed to set alarm: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to set alarm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return alarmManager.canScheduleExactAlarms();
        }
        return true; // Permission not required on older versions
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app needs permission to set exact alarms. Please grant this permission in the system settings.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
        }
    }

    private void cancelAlarm() {
        if (alarmSet && alarmPendingIntent != null) {
            alarmManager.cancel(alarmPendingIntent);
            Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show();

            // Clear saved alarm state
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_ALARM_SET, false);
            editor.apply();

            // Refresh navigation alarm icon
            navigationBar.refreshAlarmIcon();
        }
        alarmSet = false;
        updateButtonState();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm Channel";
            String description = "Channel for alarm notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ALARM_CHANNEL", name, importance);
            channel.setDescription(description);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't cancel alarm when activity is destroyed, let it ring
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Check if alarm was triggered (notification was tapped)
        if (prefs.getBoolean(KEY_ALARM_TRIGGERED, false)) {
            // Mark alarm as triggered and clear the flag
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_ALARM_TRIGGERED, false);
            editor.putBoolean(KEY_ALARM_SET, false);
            editor.apply();
            
            // Update alarm state and button
            alarmSet = false;
            updateButtonState();
        }
    }

    private void updateButtonState() {
        alarmButton.setText(alarmSet ? "Cancel Alarm" : "Set Alarm");
    }

    public static void markAlarmTriggered(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ALARM_TRIGGERED, true);
        editor.apply();
    }
} 