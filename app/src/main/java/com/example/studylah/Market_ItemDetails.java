package com.example.studylah;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Market_ItemDetails extends AppCompatActivity {

    private TextView titleTextView;
    private TextView categoryTextView;
    private TextView idTextView;
    private TextView descriptionTextView;
    private TextView priceTextView;
    private TextView sellerTextView;
    private TextView contactTextView;
    private ImageView itemImageView;

    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_activity_item_details);

        titleTextView = findViewById(R.id.title);
        categoryTextView = findViewById(R.id.category);
        idTextView = findViewById(R.id.id);
        descriptionTextView = findViewById(R.id.description);
        priceTextView = findViewById(R.id.price);
        sellerTextView = findViewById(R.id.seller);
        contactTextView = findViewById(R.id.contact);
        itemImageView = findViewById(R.id.image);

        itemId = getIntent().getStringExtra("itemId");

        new ItemDetailsTask().execute();
    }

    private class ItemDetailsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                URL url = new URL("https://apex.oracle.com/pls/apex/wia2001_database_oracle/market/users");
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
                    boolean itemFound = false;

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        if (itemId.equals(item.getString("item_id"))) {
                            itemFound = true;

                            String id = item.getString("item_id");
                            String name = item.getString("item_name");
                            String category = item.getString("item_category");
                            String price = item.getString("item_price");
                            String description = item.getString("item_description");
                            String location = item.getString("seller_location");
                            String publishDate = item.getString("publish_date");
                            String sellerName = item.getString("seller_display_name");
                            String sellerEmail = item.getString("seller_email");
                            String imageBase64 = item.getString("item_picture");

                            titleTextView.setText(name);
                            categoryTextView.setText(category);
                            idTextView.setText("Item id: " + id + "\nPublish date: " + publishDate);
                            descriptionTextView.setText("Description:\n" + description);
                            priceTextView.setText("Price\nRM" + price);
                            sellerTextView.setText("Seller's name: " + sellerName + "\nSeller's email: " + sellerEmail + "\nSeller's location: " + location);
                            contactTextView.setText("Please contact " + sellerName + " to learn more about the purchasing process");

                            // Decode Base64 string and set the image
                            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            itemImageView.setImageBitmap(decodedByte);
                            break;
                        }
                    }

                    if (!itemFound) {
                        titleTextView.setText("Item ID not found.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    titleTextView.setText("Error parsing response.");
                }
            } else {
                titleTextView.setText("Failed to connect or retrieve data.");
            }
        }
    }
}