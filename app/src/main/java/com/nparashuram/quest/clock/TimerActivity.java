package com.nparashuram.quest.clock;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {
    private EditText timerTime;
    private Button startTimerButton;
    private NavigationBarView navigationBar;

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long timeLeftInMillis = 0;
    private long totalTimeInMillis = 0;
    private boolean isEditing = false;
    private boolean isFormatting = false; // Flag to prevent recursive calls

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Initialize views
        timerTime = findViewById(R.id.timerTime);
        startTimerButton = findViewById(R.id.startTimerButton);
        Button resetTimerButton = findViewById(R.id.resetTimerButton);
        navigationBar = findViewById(R.id.navigationBar);

        // Setup navigation bar
        navigationBar.setup(this, "timer");

        // Set up text change listeners for the editable timer
        timerTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isRunning && !isEditing && !isFormatting) {
                    isFormatting = true;
                    formatTimerInput(s.toString());
                    isFormatting = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up focus change listener to handle editing state
        timerTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isEditing = hasFocus;
                if (hasFocus && isRunning) {
                    // Pause timer when user starts editing
                    pauseTimer();
                }
            }
        });

        // Set up button click listeners
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        resetTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        // Initialize display
        timerTime.setText("00:00");
    }

    private void formatTimerInput(String input) {
        // Remove all non-digit characters
        String digitsOnly = input.replaceAll("[^0-9]", "");
        
        if (digitsOnly.length() > 4) {
            digitsOnly = digitsOnly.substring(0, 4);
        }
        
        String formattedTime;
        if (digitsOnly.length() == 0) {
            formattedTime = "00:00";
        } else if (digitsOnly.length() == 1) {
            formattedTime = "00:0" + digitsOnly;
        } else if (digitsOnly.length() == 2) {
            formattedTime = "00:" + digitsOnly;
        } else if (digitsOnly.length() == 3) {
            formattedTime = "0" + digitsOnly.charAt(0) + ":" + digitsOnly.substring(1);
        } else {
            formattedTime = digitsOnly.substring(0, 2) + ":" + digitsOnly.substring(2);
        }
        
        // Only update if the formatted time is different from current text
        if (!formattedTime.equals(timerTime.getText().toString())) {
            timerTime.setText(formattedTime);
            timerTime.setSelection(formattedTime.length()); // Move cursor to end
        }
    }

    private void startTimer() {
        if (!isRunning) {
            // Parse time from the editable timer display
            String timeString = timerTime.getText().toString();
            long timeInMillis = parseTimeString(timeString);
            
            if (timeInMillis == 0) {
                Toast.makeText(this, "Please enter a valid time (MM:SS)", Toast.LENGTH_SHORT).show();
                return;
            }
            
            timeLeftInMillis = timeInMillis;
            totalTimeInMillis = timeInMillis;
            
            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerDisplay();
                }

                @Override
                public void onFinish() {
                    isRunning = false;
                    timeLeftInMillis = 0;
                    startTimerButton.setText(R.string.start);
                    updateTimerDisplay();
                    Toast.makeText(TimerActivity.this, R.string.time_up, Toast.LENGTH_LONG).show();
                    
                    // Automatically reset the timer
                    resetTimer();
                }
            }.start();
            
            isRunning = true;
            startTimerButton.setText(R.string.pause);
            timerTime.setEnabled(false); // Disable editing while timer is running
        }
    }

    private void pauseTimer() {
        if (isRunning && countDownTimer != null) {
            countDownTimer.cancel();
            isRunning = false;
            startTimerButton.setText(R.string.start);
            timerTime.setEnabled(true); // Re-enable editing
        }
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = false;
        timeLeftInMillis = 0;
        totalTimeInMillis = 0;
        startTimerButton.setText(R.string.start);
        timerTime.setText("00:00");
        timerTime.setEnabled(true); // Re-enable editing
    }

    private void updateTimerDisplay() {
        if (timeLeftInMillis > 0) {
            int minutes = (int) (timeLeftInMillis / 1000) / 60;
            int seconds = (int) (timeLeftInMillis / 1000) % 60;
            String timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            timerTime.setText(timeString);
        }
    }

    private long parseTimeString(String timeString) {
        try {
            // Parse MM:SS format
            String[] parts = timeString.split(":");
            if (parts.length == 2) {
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                
                // Validate input
                if (minutes >= 0 && minutes <= 59 && seconds >= 0 && seconds <= 59) {
                    return (minutes * 60 + seconds) * 1000L;
                }
            }
        } catch (NumberFormatException e) {
            // Invalid format
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
} 