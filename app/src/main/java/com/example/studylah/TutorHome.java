package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class TutorHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.tutor_navigation_view);

        // Set up hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        RecyclerView recyclerView = findViewById(R.id.tutors_list);

        // Set LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create Panel Data
        List<HomePanel> panelList = new ArrayList<>();
        panelList.add(new HomePanel("Upcoming Sessions", "View and manage your upcoming mentoring sessions at a glance!", R.drawable.sessions_icon));
        panelList.add(new HomePanel("Manage Marketplace", "Ready to grow your shop? Start uploading stuff to sell", R.drawable.marketplace_icon));
        panelList.add(new HomePanel("Events Overview", "View your event details and make updates anytime.", R.drawable.calendar_icon));

        // Set Adapter
        Home_Panel_Adapter.TutorPanelAdapter adapter = new Home_Panel_Adapter.TutorPanelAdapter(this, panelList);
        recyclerView.setAdapter(adapter);

        ImageButton settingsBtn = findViewById(R.id.settings_button);

        settingsBtn.setOnClickListener(v -> {
            // Create PopupMenu
            PopupMenu popupMenu = new PopupMenu(TutorHome.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());

            // Show the PopupMenu
            popupMenu.show();
        });

        // Side Navigation Item Click Handling
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Intent intent;

            if (id == R.id.side_nav_schedules) {
                intent = new Intent(TutorHome.this, Tutor_Schedules.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.side_nav_home) {
                intent = new Intent(TutorHome.this,TutorHome.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.side_nav_events) {
                intent = new Intent(TutorHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.side_nav_flashcards) {
                intent = new Intent(TutorHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);
                finish();
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
                intent = new Intent(TutorHome.this, TutorHome.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(TutorHome.this, TutorHome.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_mentoring) {
                intent = new Intent(TutorHome.this, Tutor_Schedules.class);
                startActivity(intent);
                finish();
                // Set the mentoring tab as selected
                bottomNavigationView.setSelectedItemId(R.id.nav_mentoring);
            } else if (id == R.id.nav_events) {
                intent = new Intent(TutorHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after selection
            return true;
        });

    }
}
