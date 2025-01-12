package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TutorHome extends AppCompatActivity {
    private String username, displayName, email;
    private int bm, bi, math, add_math, physics, chemistry, biology, sejarah, moral, islam, economy, accounting;
    private String university;
    private String tutor_description;

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
                intent.putExtra("username",  username);
                startActivity(intent);
            } else if (id == R.id.side_nav_home) {
                intent = new Intent(TutorHome.this,TutorHome.class);
                startActivity(intent);

            } else if (id == R.id.side_nav_events) {
                intent = new Intent(TutorHome.this, Mentoring_Tutors_List.class);
                startActivity(intent);

            } else if (id == R.id.side_nav_marketplace) {
                intent = new Intent(TutorHome.this, Market_MainActivityTutor.class);
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
                intent = new Intent(TutorHome.this, TutorHome.class);
                startActivity(intent);

            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(TutorHome.this, Market_MainActivityTutor.class);
                intent.putExtra("username", username);
                startActivity(intent);

                bottomNavigationView.setSelectedItemId(R.id.nav_marketplace);
            } else if (id == R.id.nav_mentoring) {
                intent = new Intent(TutorHome.this, Tutor_Schedules.class);
                intent.putExtra("username", username);
                startActivity(intent);

                // Set the mentoring tab as selected
                bottomNavigationView.setSelectedItemId(R.id.nav_mentoring);
            } else if (id == R.id.nav_events) {
                intent = new Intent(TutorHome.this, Mentoring_Tutors_List.class);
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
        university=intent.getStringExtra("university");
        tutor_description=intent.getStringExtra("tutor_description");
        int bm=intent.getIntExtra("bm",0);
        int bi=intent.getIntExtra("bi",0);
        int math=intent.getIntExtra("math",0);
        int add_math=intent.getIntExtra("add_math",0);
        int physics=intent.getIntExtra("physics",0);
        int chemistry=intent.getIntExtra("chemistry",0);
        int biology=intent.getIntExtra("biology",0);
        int sejarah=intent.getIntExtra("sejarah",0);
        int moral=intent.getIntExtra("moral",0);
        int islam=intent.getIntExtra("islam",0);
        int economy=intent.getIntExtra("economy",0);
        int accounting=intent.getIntExtra("accounting",0);

        settingsBtn = findViewById(R.id.settings_button);

        // Set up PopupMenu
        settingsBtn.setOnClickListener(v -> {
            // Create PopupMenu
            PopupMenu popupMenu = new PopupMenu(TutorHome.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());

            // Handle menu item clicks
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_profile) {
                    // Navigate to Edit Profile page
                    openEditProfilePage();
                    return true;
                } else if (item.getItemId() == R.id.log_out) {
                    // Handle log-out logic
                    finish();
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
        Intent editIntent = new Intent(TutorHome.this, editProfileTutor.class);
        editIntent.putExtra("username", username);
        editIntent.putExtra("display_name", displayName);
        editIntent.putExtra("email", email);
        editIntent.putExtra("university", university);
        editIntent.putExtra("tutor_description", tutor_description);
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
        String url = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users";

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
                            university=user.optString("university",university);
                            tutor_description=user.optString("tutor_description",tutor_description);

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




