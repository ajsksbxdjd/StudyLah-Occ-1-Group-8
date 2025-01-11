package com.example.studylah;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Mentoring_Tutors_List extends AppCompatActivity {

    private RecyclerView RVTutorsList;
    private Tutor_List_Adapter tutorAdapter;
    private List<Tutor_List_Data> tutorList = new ArrayList<>();
    private List<Tutor_List_Data> filteredTutorList = new ArrayList<>(); // To store filtered tutors
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentoring_tutors_list);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set selected item to 'Mentoring'
        bottomNavigationView.setSelectedItemId(R.id.nav_mentoring);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;

            if (id == R.id.nav_home) {
                intent = new Intent(this, StudentHome.class);
                startActivity(intent);
            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(this, StudentHome.class);
                startActivity(intent);
            } else if (id == R.id.nav_events) {
                intent = new Intent(this, StudentHome.class);
                startActivity(intent);
            }

            return true;
        });

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tutors");
        }

        // Initialize RecyclerView
        RVTutorsList = findViewById(R.id.tutors_list);
        RVTutorsList.setLayoutManager(new LinearLayoutManager(this));
        tutorAdapter = new Tutor_List_Adapter(this, filteredTutorList); // Use filtered list for the adapter
        RVTutorsList.setAdapter(tutorAdapter);

        // SearchView setup
        searchView = findViewById(R.id.search_tutors);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTutors(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTutors(newText);
                return true;
            }
        });

        // Fetch tutors
        fetchTutors();
        setupNavigation();
    }
    private void setupNavigation() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Set up hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
            } else if (id == R.id.side_nav_flashcards) {
                intent = new Intent(this, Mentoring_Tutors_List.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Bottom Navigation Item Click Handling
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;

            if (id == R.id.nav_home) {
                intent = new Intent(this, StudentHome.class);
                startActivity(intent);
            } else if (id == R.id.nav_marketplace) {
                intent = new Intent(this, StudentHome.class);
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

    private void fetchTutors() {
        new Tutor_APICallTask(result -> {
            Log.d("API_RESPONSE", result); // Log the API response
            if (result != null && !result.isEmpty()) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONArray tutorsArray = jsonResponse.getJSONArray("items");

                    if (tutorsArray.length() == 0) {
                        Log.d("API_RESPONSE", "No tutors found in the response.");
                        Toast.makeText(Mentoring_Tutors_List.this, "No tutors available", Toast.LENGTH_SHORT).show();
                    }

                    tutorList.clear();
                    for (int i = 0; i < tutorsArray.length(); i++) {
                        JSONObject tutorJson = tutorsArray.getJSONObject(i);


                        Map<String, Integer> subjectsMap = new HashMap<>();
                        subjectsMap.put("Malay", tutorJson.getInt("bm"));
                        subjectsMap.put("Englis", tutorJson.getInt("bi"));
                        subjectsMap.put("Math", tutorJson.getInt("math"));
                        subjectsMap.put("Add Math", tutorJson.getInt("add_math"));
                        subjectsMap.put("Physics", tutorJson.getInt("physics"));
                        subjectsMap.put("Chemistry", tutorJson.getInt("chemistry"));
                        subjectsMap.put("Biology", tutorJson.getInt("biology"));
                        subjectsMap.put("Sejarah", tutorJson.getInt("sejarah"));
                        subjectsMap.put("Moral", tutorJson.getInt("moral"));
                        subjectsMap.put("Islam", tutorJson.getInt("islam"));

                        String profilePictureBase64 = tutorJson.optString("profile_picture", null);

                        Tutor_List_Data tutorData = new Tutor_List_Data(
                                tutorJson.getString("username"),
                                tutorJson.getString("display_name"),
                                tutorJson.getString("email"),
                                tutorJson.getString("registration_date"),
                                tutorJson.getString("university"),
                                subjectsMap,
                                tutorJson.getString("tutor_description"),
                                profilePictureBase64, // Add Base64 string
                                tutorJson.getString("gc_link")
                        );

                        tutorList.add(tutorData);
                    }
                    // Save tutorList to the Singleton
                    TutorDataSingleton.getInstance().setTutorList(tutorList);

                    filteredTutorList.clear();
                    filteredTutorList.addAll(tutorList);
                    tutorAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Mentoring_Tutors_List.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    Log.e("JSON Error", e.getMessage());
                }
            } else {
                Toast.makeText(Mentoring_Tutors_List.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


    private void filterTutors(String query) {
        filteredTutorList.clear();
        if (query == null || query.isEmpty()) {
            filteredTutorList.addAll(tutorList); // Show all tutors when query is empty
        } else {
            String queryLower = query.toLowerCase(); // Convert query to lower case once for efficiency
            for (Tutor_List_Data tutor : tutorList) {
                // Case-insensitive check for tutor name
                boolean nameMatches = tutor.getDisplayName().toLowerCase().contains(queryLower);

                // Case-insensitive check for subjects
                boolean subjectMatches = false;
                for (Map.Entry<String, Integer> entry : tutor.getSubjects().entrySet()) {
                    if (entry.getKey().toLowerCase().contains(queryLower) && entry.getValue() > 0) {
                        subjectMatches = true;
                        break;
                    }
                }

                if (nameMatches || subjectMatches) {
                    filteredTutorList.add(tutor);
                }
            }
        }
        tutorAdapter.notifyDataSetChanged();
    }
}
