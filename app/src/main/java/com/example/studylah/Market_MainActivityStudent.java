package com.example.studylah;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Market_MainActivityStudent extends AppCompatActivity {

    private Button btnReferenceBook;
    private Button btnPastYearBook;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_activity_main_student);

        username=getIntent().getStringExtra("username");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnReferenceBook = findViewById(R.id.btnReferenceBook);
        btnPastYearBook = findViewById(R.id.btnPastYearBook);
        Button btnAddItem = findViewById(R.id.AddItem);

        btnReferenceBook.setOnClickListener(v -> {
            Market_ReferenceBook referenceBookFragment = new Market_ReferenceBook();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, referenceBookFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        btnPastYearBook.setOnClickListener(v -> {
            Market_PastYearBook pastYearBookFragment = new Market_PastYearBook();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, pastYearBookFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(Market_MainActivityStudent.this, Market_UploadItemStudent.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        Button viewMap = this.findViewById(R.id.viewMap);
        viewMap.setOnClickListener( view -> {
            Intent intent = new Intent(Market_MainActivityStudent.this, Market_MapsActivity.class);
            startActivity(intent);
        });

        if (findViewById(R.id.fragmentContainer) != null) {
            if (savedInstanceState != null) {
                return;
            }

            Market_ReferenceBook referenceBookFragment = new Market_ReferenceBook();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, referenceBookFragment);
            fragmentTransaction.commit();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateButtonStyles);
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
            btnReferenceBook.setTypeface(null, Typeface.BOLD);
            btnPastYearBook.setTypeface(null, Typeface.NORMAL);
            if (currentFragment instanceof Market_PastYearBook) {
                btnReferenceBook.setTypeface(null, Typeface.NORMAL);
                btnPastYearBook.setTypeface(null, Typeface.BOLD);
            }
        }
    }
}