package com.example.studylah;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Market_MainActivityStudent extends AppCompatActivity {

    private Button btnReferenceBook;
    private Button btnPastYearBook;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_activity_main_student);

        username=getIntent().getStringExtra("username");

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Marketplace");
        }

        // Drawer and Navigation setup
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Set up hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setupNavigation();

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

    private void setupNavigation() {
        // Drawer and Navigation setup
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Set up hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.tool_bar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Menu menu = navigationView.getMenu();

        // Get references to the sub-items
        MenuItem flashcardsItem = menu.findItem(R.id.side_nav_flashcards);
        MenuItem todoListItem = menu.findItem(R.id.side_nav_todo_list);
        MenuItem pomodoroTimerItem = menu.findItem(R.id.side_nav_pomodoro_timer);

        // Set an OnClickListener for the "Self Learning" item
        menu.findItem(R.id.side_nav_self_learning).setOnMenuItemClickListener(item -> {
            // Toggle visibility of sub-items
            boolean areSubItemsVisible = flashcardsItem.isVisible();
            flashcardsItem.setVisible(!areSubItemsVisible);
            todoListItem.setVisible(!areSubItemsVisible);
            pomodoroTimerItem.setVisible(!areSubItemsVisible);

            return true; // Indicate the event has been handled
        });

        // Side Navigation Item Click Handling
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Intent intent;

            if (id == R.id.side_nav_find_tutor) {
                intent = new Intent(this, Mentoring_Tutors_List.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_home) {
                intent = new Intent(this, StudentHome.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_events) {
                intent = new Intent(this, Mentoring_Tutors_List.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_marketplace) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.side_nav_flashcards) {
            intent = new Intent(this, FlashcardMainActivity.class);
            startActivity(intent);
            } else if (id == R.id.side_nav_todo_list) {
            intent = new Intent(this, TodoMainActivity.class);
            startActivity(intent);
            } else if (id == R.id.side_nav_pomodoro_timer) {
            intent = new Intent(this, Pomodoro.class);
            startActivity(intent);
        }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Bottom Navigation Item Click Handling
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_marketplace);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;

            if (id == R.id.nav_home) {
                intent = new Intent(this, StudentHome.class);
                startActivity(intent);
            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(this, Market_MainActivityStudent.class);
                startActivity(intent);
            } else if (id == R.id.nav_mentoring) {
                intent = new Intent(this, Mentoring_Tutors_List.class);
                startActivity(intent);
            } else if (id == R.id.nav_events) {
                intent = new Intent(this, Mentoring_Tutors_List.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
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