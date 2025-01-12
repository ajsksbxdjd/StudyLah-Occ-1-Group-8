package com.example.studylah;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Activity to create a new task.
 * Users can input task details like name, category, due date, time, and priority.
 */
public class TodoCreateTaskActivity extends AppCompatActivity {

    private EditText taskNameField;
    private String selectedDate = "";
    private String selectedTime = "";
    private String selectedPriority = "";
    private Calendar reminderCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_activity_create_task);
        ImageButton backButton = findViewById(R.id.back_button); // ✅ Get back button

        // ✅ Set Back Button to return to MainActivity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(TodoCreateTaskActivity.this, TodoMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // ✅ Prevents creating multiple MainActivity instances
            startActivity(intent);
            finish(); // ✅ Close AllTasksActivity
        });

        taskNameField = findViewById(R.id.taskNameField);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        TextView setDateButton = findViewById(R.id.setDateButton);
        TextView setTimeButton = findViewById(R.id.setTimeButton);
        ToggleButton highPriority = findViewById(R.id.highPriority);
        ToggleButton mediumPriority = findViewById(R.id.mediumPriority);
        ToggleButton lowPriority = findViewById(R.id.lowPriority);
        Button addTaskButton = findViewById(R.id.addTaskButton);
        Button cancelTaskButton = findViewById(R.id.cancelTaskButton);

        // Fetch categories
        ArrayList<String> categories = getIntent().getStringArrayListExtra("categories");
        if (categories == null || categories.isEmpty()) {
            categories = new ArrayList<>();
            categories.add("Uncategorized"); // Default category
        }

        // Populate Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Date Picker Logic
        setDateButton.setOnClickListener(v -> showDatePickerDialog(setDateButton, false));

        // Time Picker Logic
        setTimeButton.setOnClickListener(v -> showTimePickerDialog(setTimeButton, false));

        // Priority Selection (Toggle Button)
        highPriority.setOnClickListener(v -> setPriority(highPriority, mediumPriority, lowPriority, "High"));
        mediumPriority.setOnClickListener(v -> setPriority(mediumPriority, highPriority, lowPriority, "Medium"));
        lowPriority.setOnClickListener(v -> setPriority(lowPriority, highPriority, mediumPriority, "Low"));

        // Add Task Button Logic
        addTaskButton.setOnClickListener(view -> {
            String taskName = taskNameField.getText().toString().trim();
            String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "Uncategorized";

            if (taskName.isEmpty()) {
                Toast.makeText(this, "Task name cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPriority.isEmpty()) {
                Toast.makeText(this, "Please select a priority!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("taskName", taskName);
            resultIntent.putExtra("category", category);
            resultIntent.putExtra("date", selectedDate);
            resultIntent.putExtra("time", selectedTime);
            resultIntent.putExtra("priority", selectedPriority);

            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Cancel Button Logic
        cancelTaskButton.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    // Show Date Picker
    private void showDatePickerDialog(TextView targetView, boolean isReminder) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    if (isReminder) {
                        reminderCalendar.set(Calendar.YEAR, year);
                        reminderCalendar.set(Calendar.MONTH, month);
                        reminderCalendar.set(Calendar.DAY_OF_MONTH, day);
                        showTimePickerDialog(targetView, true);
                    } else {
                        selectedDate = day + "/" + (month + 1) + "/" + year;
                        targetView.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePickerDialog(TextView targetView, boolean isReminder) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(
                this,
                (view, hour, minute) -> {
                    String formattedTime = String.format("%02d:%02d", hour, minute);
                    if (isReminder) {
                        reminderCalendar.set(Calendar.HOUR_OF_DAY, hour);
                        reminderCalendar.set(Calendar.MINUTE, minute);
                        reminderCalendar.set(Calendar.SECOND, 0);
                        targetView.setText("Reminder set for: " + reminderCalendar.get(Calendar.DAY_OF_MONTH)
                                + "/" + (reminderCalendar.get(Calendar.MONTH) + 1)
                                + " " + formattedTime);
                    } else {
                        selectedTime = formattedTime;
                        targetView.setText(selectedTime);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    // Set Priority (Only one can be selected)
    private void setPriority(ToggleButton selected, ToggleButton unselected1, ToggleButton unselected2, String priority) {
        selected.setChecked(true);
        unselected1.setChecked(false);
        unselected2.setChecked(false);
        selectedPriority = priority;
    }
}
