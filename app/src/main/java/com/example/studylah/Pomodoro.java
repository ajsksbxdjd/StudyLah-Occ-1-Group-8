package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Pomodoro extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView timerText, sessionCountText, endTimeText, taskTitle;
    private EditText editTaskTitle, editTimer;
    private Button focusButton, breakButton, continueButton, endButton;
    private CountDownTimer countDownTimer;
    private TextView wellDone;

    private boolean isFocusMode = true;
    private long focusTime = 25 * 60 * 1000; // 25 minutes
    private long breakTime = 5 * 60 * 1000; // 5 minutes
    private long timeLeft;
    private int progress = 0;
    private int sessionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pomodoro);

        // Initialize views
        progressBar = findViewById(R.id.progress_bar);
        timerText = findViewById(R.id.timer_text);
        sessionCountText = findViewById(R.id.session_count);
        endTimeText = findViewById(R.id.end_time);
        taskTitle = findViewById(R.id.task_title);
        editTaskTitle = findViewById(R.id.edit_task_title);
        editTimer = findViewById(R.id.edit_timer);
        continueButton = findViewById(R.id.continue_button);
        wellDone = findViewById(R.id.welldone);

        focusButton = findViewById(R.id.btn_focus);
        breakButton = findViewById(R.id.btn_break);
        endButton = findViewById(R.id.end_button);

        // Set initial button text
        continueButton.setText("Start");

        // Set default times
        focusTime = 25 * 60 * 1000; // 25 minutes
        breakTime = 5 * 60 * 1000;  // 5 minutes
        timeLeft = focusTime;       // Initialize timeLeft with focusTime

        updateTimerText();
        updateEndTime();

        // Timer click to edit
        timerText.setOnClickListener(v -> {
            timerText.setVisibility(View.GONE);
            editTimer.setVisibility(View.VISIBLE);
            editTimer.setText(timerText.getText().toString()); // Pre-fill the current timer value
            updateConstraintsForTimerEdit(true);
        });

        editTimer.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String input = editTimer.getText().toString().trim();

                // Validate and parse input as MM:SS
                if (input.matches("\\d{1,2}:\\d{2}")) {
                    String[] timeParts = input.split(":");
                    int minutes = Integer.parseInt(timeParts[0]);
                    int seconds = Integer.parseInt(timeParts[1]);

                    // Convert to milliseconds
                    timeLeft = (minutes * 60 + seconds) * 1000;

                    // Update the timer display and "Finished At" time
                    updateTimerText();
                    updateEndTime();

                    // Reset the timer to reflect the new value
                    if (isFocusMode) {
                        focusTime = timeLeft;
                    } else {
                        breakTime = timeLeft;
                    }
                } else {
                    // If invalid input, reset to the current timer value
                    editTimer.setText(timerText.getText().toString());
                }

                editTimer.setVisibility(View.GONE); // Hide the edit timer field
                timerText.setVisibility(View.VISIBLE); // Show the timer text
                updateConstraintsForTimerEdit(false); // Revert constraints

                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTimer.getWindowToken(), 0);

                return true; // Indicate the action was handled
            }
            return false; // Indicate the action was not handled
        });

        // Button click listeners
        focusButton.setOnClickListener(v -> switchToFocusMode());
        breakButton.setOnClickListener(v -> switchToBreakMode());
        continueButton.setOnClickListener(v -> startTimer());
        endButton.setOnClickListener(v -> endSession());

        // Task title click to edit
        taskTitle.setOnClickListener(v -> {
            taskTitle.setVisibility(View.GONE);
            editTaskTitle.setVisibility(View.VISIBLE);
            editTaskTitle.setText(taskTitle.getText().toString());
            updateProgressBarConstraints(true);
        });

        // Save task title input
        editTaskTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                taskTitle.setText(editTaskTitle.getText().toString());
                editTaskTitle.setVisibility(View.GONE);
                taskTitle.setVisibility(View.VISIBLE);
                updateProgressBarConstraints(false);
                return true;
            }
            return false;
        });

        // Set up the back button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Pomodoro.this, StudentHome.class);
            startActivity(intent);
            finish(); // Optional: Close current activity to prevent going back to it
        });
    }

    private boolean isTimerRunning = false;

    private void startTimer() {
        if (isTimerRunning) {
            // Pause the timer
            countDownTimer.cancel();
            isTimerRunning = false;
            continueButton.setText("Resume"); // Change button text to "Resume"
        } else {
            // Resume or start the timer
            isTimerRunning = true;
            continueButton.setText("Pause"); // Change button text to "Pause"

            // Use the remaining timeLeft when resuming
            final long totalTime = isFocusMode ? focusTime : breakTime; // Dynamically set total time
            progressBar.setMax(1000);
            countDownTimer = new CountDownTimer(timeLeft, 1000) { // Tick every second
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeft = millisUntilFinished;

                    // Calculate progress as a value between 0 and 1000
                    int progress = (int) ((totalTime - timeLeft) * 1000 / totalTime);
                    progressBar.setProgress(progress); // Update progress bar smoothly

                    // Update timer display
                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    progressBar.setProgress(1000); // Ensure full progress on finish
                    isTimerRunning = false; // Reset state
                    continueButton.setText("Start"); // Change button back to "Start"

                    if (isFocusMode) {
                        sessionCount++;
                        switchToBreakMode();
                    } else {
                        switchToFocusMode();
                    }
                }
            }.start();
        }
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void updateEndTime() {
        long endTimeMillis = System.currentTimeMillis() + timeLeft;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        endTimeText.setText("FINISHED AT: " + sdf.format(endTimeMillis));
    }

    private void switchToFocusMode() {
        isFocusMode = true;
        timeLeft = focusTime;
        progressBar.setProgress(0);
        updateTimerText();
        updateEndTime();

        // Update TextView for Focus mode
        wellDone.setText("Well done!\nHow about a rest?");

        // Update UI for focus mode
        sessionCountText.setText("SESSION " + sessionCount);
        continueButton.setText("Start");
        endButton.setText("End Session");
        endButton.setOnClickListener(v -> endSession());

        // Set button colors
        focusButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#698c56"))); // Green
        focusButton.setTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFFFFF")));
        breakButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E1E1E1"))); // Grey
        breakButton.setTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#698c56")));
    }

    private void switchToBreakMode() {
        isFocusMode = false;
        timeLeft = breakTime;
        progressBar.setProgress(0);
        updateTimerText();
        updateEndTime();

        wellDone.setText("Well done!\nYou deserve this rest!");

        // Update UI for break mode
        sessionCountText.setText("On Break");
        continueButton.setText("Start");
        endButton.setText("Skip Break");
        endButton.setOnClickListener(v -> {
            switchToFocusMode();
            startTimer(); // Start focus timer automatically
        });

        // Set button colors
        focusButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E1E1E1"))); // Grey
        focusButton.setTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#698c56")));
        breakButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#698c56"))); // Green
        breakButton.setTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFFFFF")));
    }

    private void endSession() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeft = focusTime;
        progressBar.setProgress(0);
        updateTimerText();
        updateEndTime();
    }

    private void updateProgressBarConstraints(boolean isEditingTitle) {
        ConstraintLayout constraintLayout = findViewById(R.id.ConstraintLayout); // Your root layout ID
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        if (isEditingTitle) {
            // Constrain ProgressBar to edit_task_title when editing
            constraintSet.connect(R.id.progress_bar, ConstraintSet.TOP, R.id.edit_task_title, ConstraintSet.BOTTOM, 20);
        } else {
            // Constrain ProgressBar to task_title when not editing
            constraintSet.connect(R.id.progress_bar, ConstraintSet.TOP, R.id.task_title, ConstraintSet.BOTTOM, 20);
        }

        // Apply changes
        constraintSet.applyTo(constraintLayout);
    }

    // Method to update constraints dynamically
    private void updateConstraintsForTimerEdit(boolean isEditing) {
        ConstraintLayout constraintLayout = findViewById(R.id.ConstraintLayout); // Replace with your actual root layout ID
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        if (isEditing) {
            // Constrain ProgressBar to editTimer when editing
            constraintSet.connect(R.id.end_time, ConstraintSet.TOP, R.id.edit_timer, ConstraintSet.BOTTOM, 20);
        } else {
            // Constrain ProgressBar to timerText when not editing
            constraintSet.connect(R.id.end_time, ConstraintSet.TOP, R.id.timer_text, ConstraintSet.BOTTOM, 20);
        }

        // Apply the constraints
        constraintSet.applyTo(constraintLayout);
    }
}
