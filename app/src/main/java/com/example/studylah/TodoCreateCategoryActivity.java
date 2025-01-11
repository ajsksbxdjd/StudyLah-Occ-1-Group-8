package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TodoCreateCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_activity_create_category);

        ImageButton backButton = findViewById(R.id.back_button); // ✅ Get back button

        // ✅ Set Back Button to return to MainActivity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(TodoCreateCategoryActivity.this, TodoMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // ✅ Prevents creating multiple MainActivity instances
            startActivity(intent);
            finish(); // ✅ Close AllTasksActivity
        });

        EditText categoryNameField = findViewById(R.id.category_name_input);
        Button createCategoryButton = findViewById(R.id.create_button);
        Button cancelCategoryButton = findViewById(R.id.cancel_button);

        createCategoryButton.setOnClickListener(view -> {
            String categoryName = categoryNameField.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("categoryName", categoryName);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Category name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        cancelCategoryButton.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
