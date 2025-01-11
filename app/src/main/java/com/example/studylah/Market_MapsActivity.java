package com.example.studylah;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Market_MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<String> sellerLocations = new ArrayList<>();
    private static final String API_URL = "https://apex.oracle.com/pls/apex/wia2001_database_oracle/market/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new APICallTask().execute();
    }

    private class APICallTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                result = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONArray items = jsonResponse.getJSONArray("items");

                    if (items.length() > 0) {
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            String location = item.getString("seller_location");
                            String itemName = item.getString("item_name");
                            String itemPrice = item.getString("item_price");
                            String itemId = item.getString("item_id"); // Ensure this key matches your JSON response

                            sellerLocations.add(location + "|" + itemName + "|" + itemPrice + "|" + itemId);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Market_MapsActivity.this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Market_MapsActivity.this, "Failed to connect or retrieve data.", Toast.LENGTH_SHORT).show();
            }

            updateMapWithLocations();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(marker -> {
            String itemId = (String) marker.getTag();
            if (itemId != null) {
                Intent intent = new Intent(Market_MapsActivity.this, Market_ItemDetails.class);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
            }
        });
    }

    private void updateMapWithLocations() {
        if (sellerLocations.size() > 0) {
            for (String locationInfo : sellerLocations) {
                String[] parts = locationInfo.split("\\|");
                String location = parts[0];
                String itemName = parts[1];
                String itemPrice = parts[2];
                String itemId = parts[3]; // Ensure this matches the position in the string

                LatLng latLng = getLatLngFromLocation(location);

                if (latLng != null) {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(location)
                            .snippet("Item: " + itemName + "  Price: RM" + itemPrice));
                    marker.setTag(itemId); // Store the item ID in the marker's tag
                }
            }
        } else {
            Toast.makeText(this, "No locations to display", Toast.LENGTH_SHORT).show();
        }

        // Focus camera on Malaysia
        LatLng malaysiaCenter = new LatLng(4.2105, 101.9758);  // Approximate center of Malaysia
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysiaCenter, 5)); // Zoom out to show all of Malaysia
    }


    private LatLng getLatLngFromLocation(String location) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
