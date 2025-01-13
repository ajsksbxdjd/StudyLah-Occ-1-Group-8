package com.example.studylah;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.gridlayout.widget.GridLayout;

import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TodoMainActivity extends AppCompatActivity {
    private GridLayout categoryGrid;
    private ArrayList<String> categories = new ArrayList<>(); // ✅ List of category names
    private ArrayList<TodoTask> filteredTodoTasks; // ✅ Filtered list of tasks (used for searching)
    private EditText searchBar;
    private LinearLayout taskListLayout;
    private ArrayList<TodoTask> todoTasks; // ✅ List of all tasks
    private ImageButton addTaskButton;
    private Typeface ralewayFont;
    private boolean isTyping = false; // ✅ Indicates if the user is typing in the search bar

    // ✅ Launcher to receive updated tasks from AllTasksActivity
    private final ActivityResultLauncher<Intent> allTasksLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        // ✅ Receive updated tasks from AllTasksActivity
                        todoTasks = data.getParcelableArrayListExtra("updatedTasks");
                        updateTaskList(); // ✅ Refresh the task list display
                    }
                }
            });

    // ✅ Launcher to receive updated category-specific tasks
    private final ActivityResultLauncher<Intent> categoryTasksLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String category = data.getStringExtra("categoryName"); // ✅ Get category name
                        ArrayList<TodoTask> updatedTodoTasks = data.getParcelableArrayListExtra("updatedTasks"); // ✅ Get updated tasks
                        if (category != null && updatedTodoTasks != null) {
                            System.out.println("Before updating tasks:");
                            for (TodoTask t : todoTasks) {
                                System.out.println("Task: " + t.getName() + " | Completed: " + t.isCompleted());
                            }
                            // ✅ Remove old tasks of this category from the main task list
                            todoTasks.removeIf(todoTask -> todoTask.getCategory().equals(category));
                            // ✅ Add updated tasks for this category
                            todoTasks.addAll(updatedTodoTasks);
                            System.out.println("After updating tasks:");
                            for (TodoTask t : todoTasks) {
                                System.out.println("Task: " + t.getName() + " | Completed: " + t.isCompleted());
                            }
                        }
                        System.out.println("Received category: " + category);
                        System.out.println("Received tasks:");
                        for (TodoTask t : updatedTodoTasks) {
                            System.out.println("Task: " + t.getName() + " | Completed: " + t.isCompleted());
                        }
                        updateTaskList(); // ✅ Now updates properly without wiping out data
                    }
                }
            });

    // ✅ Launcher to handle the creation of new categories
    private final ActivityResultLauncher<Intent> createCategoryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String newCategoryName = data.getStringExtra("categoryName");
                        categories.add(newCategoryName); // Add the new category name to the list
                        updateCategoryGrid(); // Refresh the category grid
                    }
                }
            });

    @Override
    protected void onResume() {
        super.onResume();
        updateCategoryGrid(); // ✅ Refresh Categories display when returning
        updateTaskList(); // ✅ Refresh Tasks list
    }

    // ✅ Launcher to handle the creation of new tasks
    private final ActivityResultLauncher<Intent> createTaskLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String taskName = data.getStringExtra("taskName");
                        String category = data.getStringExtra("category");
                        String date = data.getStringExtra("date");
                        String time = data.getStringExtra("time");
                        String priority = data.getStringExtra("priority");
                        // ✅ Create new Task object with reminder details
                        TodoTask newTodoTask = new TodoTask(taskName, category, date, time, priority);
                        todoTasks.add(newTodoTask);
                        updateTaskList();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_activity_main);

        ImageButton back_Button = findViewById(R.id.back_button);
        back_Button.setOnClickListener(view -> sendUpdatedTasksBack());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                sendUpdatedTasksBack();
            }
        });

        // ✅ Initialize UI components
        categoryGrid = findViewById(R.id.categoryGrid);
        taskListLayout = findViewById(R.id.taskListLayout);
        addTaskButton = findViewById(R.id.add_task_button);
        ImageButton addCategoryButton = findViewById(R.id.add_category_button);
        Button seeAllButton = findViewById(R.id.viewAllButton);
        searchBar = findViewById(R.id.search_bar); // ✅ Initialize search bar
        ralewayFont = ResourcesCompat.getFont(this, R.font.raleway_semibold);
        categories = new ArrayList<>();
        todoTasks = getIntent().getParcelableArrayListExtra("tasks");
        if (todoTasks == null) {
            todoTasks = new ArrayList<>();
        }
        filteredTodoTasks = new ArrayList<>(todoTasks);

        // ✅ Event listener for adding a new category
        addCategoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(TodoMainActivity.this, TodoCreateCategoryActivity.class);
            createCategoryLauncher.launch(intent);
        });

        // ✅ Event listener for adding a new task
        addTaskButton.setOnClickListener(view -> {
            Intent intent = new Intent(TodoMainActivity.this, TodoCreateTaskActivity.class);
            intent.putStringArrayListExtra("categories", new ArrayList<>(categories)); // Pass the updated category list
            createTaskLauncher.launch(intent);
        });

        // ✅ Event listener for viewing all tasks
        seeAllButton.setOnClickListener(view -> {
            Intent intent = new Intent(TodoMainActivity.this, TodoAllTasksActivity.class);
            intent.putParcelableArrayListExtra("tasks", todoTasks);
            allTasksLauncher.launch(intent); // ✅ Launch AllTasksActivity
        });

        // ✅ Implement search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isTyping = !s.toString().isEmpty();
                filterTasks(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        updateCategoryGrid();
        updateTaskList();
    }

    //✅ Filters tasks based on user input in the search bar
    private void filterTasks(String query) {
        filteredTodoTasks.clear();
        if (query.isEmpty()) {
            isTyping = false;
            filteredTodoTasks.addAll(todoTasks); // ✅ Show all tasks when cleared
        } else {
            isTyping = true;
            for (TodoTask todoTask : todoTasks) {
                if (todoTask.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredTodoTasks.add(todoTask);
                }
            }
        }
        updateTaskList();
    }

    /**
     * Updates the category grid by displaying all available categories.
     * Each category is shown as a MaterialCardView that users can click
     * to view tasks specific to that category.
     */
    private void updateCategoryGrid() {
        Typeface ralewayFont = ResourcesCompat.getFont(this, R.font.raleway_semibold);

        categoryGrid.removeAllViews(); // ✅ Clear previous views before adding new ones
        if (categories.isEmpty()) {
            return; // ✅ Exit if no categories exist
        }

        // ✅ Iterate through the category list and create a CardView for each
        for (String categoryName : categories) {
            // ✅ Create a MaterialCardView for each category
            MaterialCardView cardView = new MaterialCardView(this);
            cardView.setRadius(24); // ✅ Rounded corners
            cardView.setCardElevation(8); // ✅ Add shadow effect
            cardView.setStrokeColor(Color.parseColor("#4A683B"));
            cardView.setStrokeWidth(2);
            cardView.setCardBackgroundColor(Color.WHITE);
            cardView.setLayoutParams(new GridLayout.LayoutParams());

            // ✅ Create a TextView to display the category name
            TextView categoryText = new TextView(this);
            categoryText.setText(categoryName);
            categoryText.setTypeface(ralewayFont);
            categoryText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            categoryText.setGravity(Gravity.CENTER);
            categoryText.setTextColor(Color.parseColor("#4A683B"));
            categoryText.setTextSize(22);
            categoryText.setPadding(20, 20, 20, 20);

            cardView.addView(categoryText); // ✅ Add category name to the CardView

            // ✅ Set click listener to open TodoCategoryActivity with tasks from that category
            cardView.setOnClickListener(view -> {
                ArrayList<TodoTask> filteredTodoTasks = new ArrayList<>();
                for (TodoTask todoTask : todoTasks) {
                    if (todoTask.getCategory().equals(categoryName)) {
                        filteredTodoTasks.add(todoTask); // ✅ Filter tasks by category
                    }
                }

                // ✅ Launch category-specific activity
                Intent intent = new Intent(TodoMainActivity.this, TodoCategoryActivity.class);
                intent.putExtra("categoryName", categoryName);
                intent.putParcelableArrayListExtra("tasks", filteredTodoTasks);
                categoryTasksLauncher.launch(intent);
            });

            // ✅ Set layout parameters for the category card
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(16, 16, 16, 16);
            params.width = 300;
            params.height = 300;
            cardView.setLayoutParams(params);

            // Add to GridLayout
            categoryGrid.addView(cardView); // ✅ Add category card to the grid
        }
    }

    /**
     * Updates the task list to display today's tasks, separated into:
     * - Overdue tasks
     * - Remaining tasks (not yet completed)
     * - Completed tasks
     */
    private void updateTaskList() {
        taskListLayout.removeAllViews(); // ✅ Clear previous views before updating

        if (!isTyping) {
            filteredTodoTasks = new ArrayList<>(todoTasks); // ✅ Restore all tasks if not searching
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String todayDate = dateFormat.format(new Date()); // ✅ Get today's date

        // ✅ Separate tasks into categories
        ArrayList<TodoTask> overdueTodoTasks = new ArrayList<>();
        ArrayList<TodoTask> remainingTodoTasks = new ArrayList<>();
        ArrayList<TodoTask> completedTodoTasks = new ArrayList<>(); // ✅ Store completed tasks separately

        // ✅ Iterate through the tasks and categorize them
        for (TodoTask todoTask : filteredTodoTasks) {
            try {
                Date dueDate = dateFormat.parse(todoTask.getDate()); // ✅ Extract date part
                String formattedDueDate = dateFormat.format(dueDate); // ✅ Format for comparison

                if (formattedDueDate.equals(todayDate)) { // ✅ Show only today's tasks
                    Date dueDateTime = dateTimeFormat.parse(todoTask.getDate() + " " + todoTask.getTime());

                    if (todoTask.isCompleted()) {
                        completedTodoTasks.add(todoTask); // ✅ Completed tasks go at the bottom
                    } else if (dueDateTime != null && dueDateTime.before(new Date())) {
                        overdueTodoTasks.add(todoTask); // ✅ Overdue tasks are placed first
                    } else {
                        remainingTodoTasks.add(todoTask); // ✅ Upcoming tasks
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // ✅ Add overdue tasks section
        if (!overdueTodoTasks.isEmpty()) {
            TextView overdueHeader = createSectionHeader("Overdue Tasks", getResources().getColor(R.color.red));
            taskListLayout.addView(overdueHeader);
            for (TodoTask todoTask : overdueTodoTasks) {
                addTaskCard(todoTask, taskListLayout, true, false);
            }
        }

        // ✅ Add remaining tasks section
        if (!remainingTodoTasks.isEmpty()) {
            TextView remainingHeader = createSectionHeader("Remaining Tasks", getResources().getColor(R.color.green));
            taskListLayout.addView(remainingHeader);
            for (TodoTask todoTask : remainingTodoTasks) {
                addTaskCard(todoTask, taskListLayout, false, false);
            }
        }

        // ✅ Add completed tasks section
        if (!completedTodoTasks.isEmpty()) {
            TextView completedHeader = createSectionHeader("Completed Tasks", getResources().getColor(R.color.green_completed));
            taskListLayout.addView(completedHeader);
            for (TodoTask todoTask : completedTodoTasks) {
                addTaskCard(todoTask, taskListLayout, false, true);
            }
        }

        // ✅ Show message if no tasks exist
        if (filteredTodoTasks.isEmpty()) {
            TextView noTasksMessage = new TextView(this);
            noTasksMessage.setText("No tasks found.");
            noTasksMessage.setTextSize(16);
            noTasksMessage.setTextColor(getResources().getColor(R.color.gray));
            noTasksMessage.setTypeface(ralewayFont);
            noTasksMessage.setPadding(20, 20, 20, 20);
            taskListLayout.addView(noTasksMessage);
        }
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

        // ✅ Populate task details
        taskName.setText(todoTask.getName());
        taskCategory.setText(todoTask.getCategory());
        taskPriority.setText(todoTask.getPriority() + " Priority");
        taskTime.setText(todoTask.getTime());
        taskDueDate.setText("Due: " + todoTask.getDate());

        // ✅ Highlight overdue tasks in red
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

        // ✅ Handle task completion
        if (isCompleted) {
            taskCompletionCircle.setImageResource(R.drawable.circle_checked);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.green_completed)); // ✅ Dark green for completed
        } else {
            taskCompletionCircle.setImageResource(R.drawable.circle_unchecked);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
        }

        taskView.setOnClickListener(view -> {
            todoTask.setCompleted(!todoTask.isCompleted()); // ✅ Toggle completion status

            // ✅ Update task in the main list
            for (TodoTask t : todoTasks) {
                if (t.getName().equals(todoTask.getName()) && t.getCategory().equals(todoTask.getCategory())) {
                    t.setCompleted(todoTask.isCompleted());
                    break; // Stop loop once we update the correct task
                }
            }
            updateTaskList(); // ✅ Refresh UI after updating
        });

        menuButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, menuButton);
            popupMenu.getMenuInflater().inflate(R.menu.task_options_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.delete_task) {
                    todoTasks.remove(todoTask);
                    updateTaskList();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

        parentLayout.addView(taskView);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<TodoTask> updatedTodoTasks = data.getParcelableArrayListExtra("updatedTasks");
            if (updatedTodoTasks != null) {
                todoTasks = updatedTodoTasks;
                updateTaskList();
            }
            String taskName = data.getStringExtra("taskName");
            String category = data.getStringExtra("category");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            String priority = data.getStringExtra("priority");

            if (taskName != null && !taskName.isEmpty()) {
                // ✅ Debugging: Print received task
                System.out.println("Received Task: " + taskName);

                TodoTask newTodoTask = new TodoTask(taskName, category, date, time, priority);
                todoTasks.add(newTodoTask);
                updateTaskList();
            } else {
                System.out.println("No task received.");
            }
        }
    }

    private void sendUpdatedTasksBack() {
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("updatedTasks", todoTasks);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
