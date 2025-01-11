package com.example.studylah;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TodoCategoryActivity extends AppCompatActivity {

    private ArrayList<TodoTask> todoTasks;
    private ArrayList<TodoTask> filteredTodoTasks;
    private LinearLayout taskListLayout;
    private String categoryName;
    private EditText searchBar;
    private Typeface ralewayFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_activity_category);

        taskListLayout = findViewById(R.id.taskListLayout);
        ImageButton backButton = findViewById(R.id.backButton);
        searchBar = findViewById(R.id.search_bar);
        TextView categoryHeader = findViewById(R.id.categoryHeader);
        ralewayFont = ResourcesCompat.getFont(this, R.font.raleway_semibold);

        categoryName = getIntent().getStringExtra("categoryName");
        todoTasks = getIntent().getParcelableArrayListExtra("tasks");
        if (todoTasks == null) {
            todoTasks = new ArrayList<>();
        }
        filteredTodoTasks = new ArrayList<>(todoTasks);

        categoryHeader.setText("Tasks in \"" + categoryName + "\"");

        backButton.setOnClickListener(view -> sendUpdatedTasksBack());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                sendUpdatedTasksBack();
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        updateTaskList();
    }

    private void filterTasks(String query) {
        filteredTodoTasks.clear();
        for (TodoTask todoTask : todoTasks) {
            if (todoTask.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredTodoTasks.add(todoTask);
            }
        }
        updateTaskList();
    }

    private void updateTaskList() {
        taskListLayout.removeAllViews();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Calendar currentCalendar = Calendar.getInstance();
        long currentTimeMillis = currentCalendar.getTimeInMillis();

        ArrayList<TodoTask> overdueTodoTasks = new ArrayList<>();
        ArrayList<TodoTask> remainingTodoTasks = new ArrayList<>();
        ArrayList<TodoTask> completedTodoTasks = new ArrayList<>(); // ✅ Store completed tasks separately

        for (TodoTask todoTask : filteredTodoTasks) { // ✅ Use filteredTasks instead of tasks
            if (todoTask.getCategory().equals(categoryName)) { // ✅ Ensure it belongs to the category
                try {
                    Date taskDateTime = dateFormat.parse(todoTask.getDate() + " " + todoTask.getTime());
                    if (todoTask.isCompleted()) {
                        completedTodoTasks.add(todoTask);
                    } else if (taskDateTime != null && taskDateTime.getTime() < currentTimeMillis) {
                        overdueTodoTasks.add(todoTask);
                    } else {
                        remainingTodoTasks.add(todoTask);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!overdueTodoTasks.isEmpty()) {
            TextView overdueHeader = createSectionHeader("Overdue Tasks", getResources().getColor(R.color.red));
            taskListLayout.addView(overdueHeader);
            for (TodoTask todoTask : overdueTodoTasks) {
                addTaskCard(todoTask, taskListLayout, true, false);
            }
        }

        if (!remainingTodoTasks.isEmpty()) {
            TextView remainingHeader = createSectionHeader("Remaining Tasks", getResources().getColor(R.color.green));
            taskListLayout.addView(remainingHeader);
            for (TodoTask todoTask : remainingTodoTasks) {
                addTaskCard(todoTask, taskListLayout, false, false);
            }
        }

        if (!completedTodoTasks.isEmpty()) {
            TextView completedHeader = createSectionHeader("Completed Tasks", getResources().getColor(R.color.green_completed));
            taskListLayout.addView(completedHeader);
            for (TodoTask todoTask : completedTodoTasks) {
                addTaskCard(todoTask, taskListLayout, false, true); // ✅ Mark as completed
            }
        }

        if (filteredTodoTasks.isEmpty()) {
            TextView noTasksMessage = new TextView(this);
            noTasksMessage.setText("No tasks found in this category.");
            noTasksMessage.setTextSize(16);
            noTasksMessage.setTextColor(getResources().getColor(R.color.gray));
            noTasksMessage.setTypeface(ralewayFont); // ✅ Apply Raleway font
            noTasksMessage.setPadding(20, 20, 20, 20);
            taskListLayout.addView(noTasksMessage);
        }
    }

    private TextView createSectionHeader(String text, int color) {
        TextView header = new TextView(this);
        header.setText(text);
        header.setTextSize(20);
        header.setTextColor(color);
        header.setTypeface(ralewayFont);
        header.setPadding(20, 20, 20, 10);
        return header;
    }

    private void addTaskCard(TodoTask todoTask, LinearLayout parentLayout, boolean isOverdue, boolean isCompleted) {
        View taskView = getLayoutInflater().inflate(R.layout.todo_item_task, parentLayout, false);

        MaterialCardView cardView = taskView.findViewById(R.id.taskCardView);
        ImageView taskCompletionCircle = taskView.findViewById(R.id.taskCompletionCircle);
        TextView taskName = taskView.findViewById(R.id.taskName);
        TextView taskCategory = taskView.findViewById(R.id.taskCategory);
        TextView taskTime = taskView.findViewById(R.id.taskTime);
        TextView taskPriority = taskView.findViewById(R.id.taskPriority);
        TextView taskDueDate = taskView.findViewById(R.id.taskDueDate);
        ImageView menuButton = taskView.findViewById(R.id.taskOptionsMenu);

        taskName.setText(todoTask.getName());
        taskCategory.setText(todoTask.getCategory());
        taskPriority.setText(todoTask.getPriority() + " Priority");
        taskTime.setText(todoTask.getTime());
        taskDueDate.setText("Due: " + todoTask.getDate());

        if (isOverdue) {
            int redColor = getResources().getColor(R.color.red);
            taskName.setTextColor(redColor);
            taskTime.setTextColor(redColor);
            taskCategory.setTextColor(redColor);
            taskPriority.setTextColor(redColor);
            taskDueDate.setTextColor(redColor);
            menuButton.setColorFilter(redColor);
            taskCategory.setBackgroundResource(R.drawable.category_outline_red);
            taskPriority.setBackgroundResource(R.drawable.category_outline_red);
            taskDueDate.setBackgroundResource(R.drawable.category_outline_red);
        }

        if (isCompleted) {
            taskCompletionCircle.setImageResource(R.drawable.circle_checked);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.green_completed));
        } else {
            taskCompletionCircle.setImageResource(R.drawable.circle_unchecked);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
        }

        taskView.setOnClickListener(view -> {
            todoTask.setCompleted(!todoTask.isCompleted());
            updateTaskList();
        });

        menuButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, menuButton);
            popupMenu.getMenuInflater().inflate(R.menu.task_options_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.delete_task) {
                    todoTasks.remove(todoTask);
                    filteredTodoTasks.remove(todoTask); // ✅ Ensure it's removed from filtered list too
                    updateTaskList(); // ✅ Immediately refresh the UI
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
        parentLayout.addView(taskView);
    }

    private void sendUpdatedTasksBack() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("categoryName", categoryName); // ✅ Send category name
        resultIntent.putParcelableArrayListExtra("updatedTasks", filteredTodoTasks); // ✅ Send updated tasks
        setResult(RESULT_OK, resultIntent);
        finish(); // ✅ Close activity while preserving data
    }

}
