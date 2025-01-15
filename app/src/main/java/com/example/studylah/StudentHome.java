package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentHome extends AppCompatActivity {
    private String username, displayName, email;
    private int bm, bi, math, add_math, physics, chemistry, biology, sejarah, moral, islam, economy, accounting;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        
        Toolbar toolbar = findViewById(R.id.tool_bar);
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
        Home_Panel_Adapter adapter = new Home_Panel_Adapter(this, panelList, bottomNavigationView);
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
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.side_nav_events) {
                intent = new Intent(StudentHome.this, EventActivityStudent.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_flashcards) {
                intent = new Intent(StudentHome.this, FlashcardMainActivity.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_pomodoro_timer) {
                intent = new Intent(StudentHome.this, Pomodoro.class);
                startActivity(intent);
            } else if (id == R.id.side_nav_todo_list) {
                    intent = new Intent(StudentHome.this, TodoMainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.side_nav_marketplace) {
                intent = new Intent(StudentHome.this, Market_MainActivityStudent.class);
                intent.putExtra("username", username);
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
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(StudentHome.this, Market_MainActivityStudent.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else if (id == R.id.nav_mentoring) {
                intent = new Intent(StudentHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);
            } else if (id == R.id.nav_events) {
                intent = new Intent(StudentHome.this, EventActivityStudent.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after selection
            return true;


        });
        // Initialize data from Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        displayName = intent.getStringExtra("display_name");
        email = intent.getStringExtra("email");
        bm = intent.getIntExtra("bm", 0);
        bi = intent.getIntExtra("bi", 0);
        math = intent.getIntExtra("math", 0);
        add_math = intent.getIntExtra("add_math", 0);
        physics = intent.getIntExtra("physics", 0);
        chemistry = intent.getIntExtra("chemistry", 0);
        biology = intent.getIntExtra("biology", 0);
        sejarah = intent.getIntExtra("sejarah", 0);
        moral = intent.getIntExtra("moral", 0);
        islam = intent.getIntExtra("islam", 0);
        economy = intent.getIntExtra("economy", 0);
        accounting = intent.getIntExtra("accounting", 0);

        settingsBtn = findViewById(R.id.settings_button);

        // Set up PopupMenu
        settingsBtn.setOnClickListener(v -> {
            // Create PopupMenu
            PopupMenu popupMenu = new PopupMenu(StudentHome.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());

            // Handle menu item clicks
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_profile) {
                    // Navigate to Edit Profile page
                    openEditProfilePage();
                    return true;
                } else if (item.getItemId() == R.id.log_out) {
                    // Handle log-out logic
                    //handleLogout();
                    startActivity(new Intent(StudentHome.this, MainActivity.class));

                    return true;
                }
                return false;
            });


            // Show the PopupMenu
            popupMenu.show();
        });
    }

    // Method to navigate to Edit Profile page
    private void openEditProfilePage() {
        Intent editIntent = new Intent(StudentHome.this, editProfileStudent.class);
        editIntent.putExtra("username", username);
        editIntent.putExtra("display_name", displayName);
        editIntent.putExtra("email", email);
        editIntent.putExtra("bm", bm);
        editIntent.putExtra("bi", bi);
        editIntent.putExtra("math", math);
        editIntent.putExtra("add_math", add_math);
        editIntent.putExtra("physics", physics);
        editIntent.putExtra("chemistry", chemistry);
        editIntent.putExtra("biology", biology);
        editIntent.putExtra("sejarah", sejarah);
        editIntent.putExtra("moral", moral);
        editIntent.putExtra("islam", islam);
        editIntent.putExtra("economy", economy);
        editIntent.putExtra("accounting", accounting);
        startActivityForResult(editIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchLatestUserData(); // Fetch updated data from the server
        }
    }

    private void fetchLatestUserData() {
        String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/student/users";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("ServerResponse", "Response: " + response.toString());
                        JSONArray items = response.getJSONArray("items");

                        // Filter the user data based on the email
                        JSONObject user = null;
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject currentUser = items.getJSONObject(i);
                            if (currentUser.optString("email").equals(email)) {
                                user = currentUser;
                                break;
                            }
                        }

                        if (user != null) {
                            // Update local variables
                            username = user.optString("username", username);
                            displayName = user.optString("display_name", displayName);

                            // Log the updated values
                            Log.d("UpdatedData", "Username: " + username + ", Display Name: " + displayName);
                        } else {
                            Log.e("FetchError", "No user data found for email: " + email);
                        }
                    } catch (JSONException e) {
                        Log.e("JSONError", "Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = "Error fetching user data: ";
                    if (error.networkResponse != null) {
                        errorMessage += new String(error.networkResponse.data);
                    } else {
                        errorMessage += error.getMessage();
                    }
                    Log.e("VolleyError", errorMessage);
                });

        Volley.newRequestQueue(this).add(request);
    }
}
