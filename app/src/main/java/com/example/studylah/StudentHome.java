package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class StudentHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        // Set up hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
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

        RecyclerView recyclerView = findViewById(R.id.tutors_list);

        // Set LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create Panel Data
        List<HomePanel> panelList = new ArrayList<>();
        panelList.add(new HomePanel("Find a Tutor", "Learn better, faster with expert tutors!", R.drawable.find_a_tutor_icon));
        panelList.add(new HomePanel("Flashcards", "Review key concepts and master any subject!", R.drawable.flashcards_icon));
        panelList.add(new HomePanel("Pomodoro Timer", "Stay focused and productive!", R.drawable.pomodoro_icon));
        panelList.add(new HomePanel("Upcoming Events", "Don't miss opportunities to learn and grow!", R.drawable.upcoming_event_icon));

        // Set Adapter
        Home_Panel_Adapter adapter = new Home_Panel_Adapter(this, panelList);
        recyclerView.setAdapter(adapter);

        ImageButton settingsBtn = findViewById(R.id.settings_button);

        settingsBtn.setOnClickListener(v -> {
            // Create PopupMenu
            PopupMenu popupMenu = new PopupMenu(StudentHome.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());

            // Show the PopupMenu
            popupMenu.show();
        });

        // Handle Side Navigation Item Clicks
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Intent intent;

            if (id == R.id.side_nav_find_tutor) {
                intent = new Intent(StudentHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_home) {
                intent = new Intent(StudentHome.this, StudentHome.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_events) {
                intent = new Intent(StudentHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_flashcards) {
                intent = new Intent(StudentHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_pomodoro_timer) {
                intent = new Intent(StudentHome.this, Pomodoro.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after selection
            return true;
        });

        // Bottom Navigation Item Click Handling
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;

            if (id == R.id.nav_home) {
                intent = new Intent(StudentHome.this, StudentHome.class);
                startActivity(intent);
            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(StudentHome.this, StudentHome.class);
                startActivity(intent);
            } else if (id == R.id.nav_mentoring) {
                intent = new Intent(StudentHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);
                finish();
                // Set the mentoring tab as selected
                bottomNavigationView.setSelectedItemId(R.id.nav_mentoring);
            } else if (id == R.id.nav_events) {
                intent = new Intent(StudentHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after selection
            return true;
        });

    }
}
