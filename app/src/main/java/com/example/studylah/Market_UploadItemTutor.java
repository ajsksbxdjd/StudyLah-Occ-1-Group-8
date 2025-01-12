package com.example.studylah;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Market_UploadItemTutor extends FragmentActivity implements OnMapReadyCallback {

    private AutoCompleteTextView mSearchText;
    private PlacesClient placesClient;
    private ListView mSuggestionsListView;
    private List<String> placeSuggestions;
    private ArrayAdapter<String> suggestionsAdapter;

    private EditText itemName, itemPrice, itemDescription;
    private Spinner itemCategory;
    private Button uploadButton;
    private Switch switch1;
    private TextView textLocation;
    private ImageView boxLocation;

    private static final int PICK_IMAGE_REQUEST = 1;
    byte[] itemPicture;
    ImageButton imageButton;

    private String username;
    private String tutor_name;
    private String tutor_email;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_activity_upload_item_tutor);

        username = getIntent().getStringExtra("username");

        // Initialize ImageButton
        imageButton = findViewById(R.id.imageButton1);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if permissions are granted
                if (ContextCompat.checkSelfPermission(Market_UploadItemTutor.this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(Market_UploadItemTutor.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Market_UploadItemTutor.this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, 1);
                } else {
                    showImageSourceDialog();
                }
            }
        });

        // Remove focus when clicking outside the AutoCompleteTextView
        findViewById(R.id.main).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mSearchText.clearFocus();
                itemName.clearFocus();
                itemCategory.clearFocus();
                itemPrice.clearFocus();// Clear focus on touch outside
            }
            return false;// Allow other touch events to propagate
        });

        // Initialize UI elements
        itemName = findViewById(R.id.editTextItemName1);
        itemCategory = findViewById(R.id.spinner1);
        itemPrice = findViewById(R.id.editTextItemName2);
        itemDescription = findViewById(R.id.editTextItemName3);
        mSearchText = findViewById(R.id.editTextItemName4);
        uploadButton = findViewById(R.id.button);
        switch1 = findViewById(R.id.switch1);
        textLocation = findViewById(R.id.textView5);
        boxLocation = findViewById(R.id.imageView5);

        mSuggestionsListView = findViewById(R.id.suggestionsListView);
        placeSuggestions = new ArrayList<>();

        // Initially hide the location field
        mSearchText.setVisibility(View.GONE);
        textLocation.setVisibility(View.GONE);
        boxLocation.setVisibility(View.GONE);
        mSuggestionsListView.setVisibility(View.GONE);

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the location field
                mSearchText.setVisibility(View.VISIBLE);
                textLocation.setVisibility(View.VISIBLE);
                boxLocation.setVisibility(View.VISIBLE);
                mSuggestionsListView.setVisibility(View.VISIBLE);
            } else {
                // Hide the location field
                mSearchText.setVisibility(View.GONE);
                textLocation.setVisibility(View.GONE);
                boxLocation.setVisibility(View.GONE);
                mSuggestionsListView.setVisibility(View.GONE);
            }
        });

        // Set up Spinner with categories
        String[] categories = {"Reference Book", "Past Year Book"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCategory.setAdapter(adapter);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);

        // Set up ArrayAdapter for suggestions
        suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeSuggestions);
        mSuggestionsListView.setAdapter(suggestionsAdapter);

        // Fetch place predictions as the user types
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchPlacePredictions(s.toString());
                mSuggestionsListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Hide suggestions when clicking outside
        findViewById(R.id.main).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Rect outRect = new Rect();
                mSearchText.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    mSuggestionsListView.setVisibility(View.GONE);
                }
            }
            return false;
        });

        // Set up suggestion click listener
        mSuggestionsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlace = placeSuggestions.get(position);
            mSearchText.setText(selectedPlace);
            mSuggestionsListView.setVisibility(View.GONE);
        });

        // Force selection from suggestions
        mSearchText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!placeSuggestions.contains(mSearchText.getText().toString())) {
                    mSearchText.setText(""); // Clear invalid input
                    Toast.makeText(this, "Please select a location from the suggestions.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Remove focus when clicking outside the AutoCompleteTextView
        findViewById(R.id.main).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Clear focus for all fields
                mSearchText.clearFocus();
                itemName.clearFocus();
                itemPrice.clearFocus();
                itemDescription.clearFocus();
            }
            return false; // Allow other touch events to propagate
        });

        // Handle upload button click
        uploadButton.setOnClickListener(view -> uploadItem());
        new ItemDetailsTask().execute();
    }

    private void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Only allow image files
        startActivityForResult(intent, 1);
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Take Photo
                        openCamera();
                        break;
                    case 1: // Choose from Gallery
                        openFileExplorer();
                        break;
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    // Method to encode the image as a base64 string
    private String encodeImageToBase64(byte[] imageBytes) {
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }


    private class ItemDetailsTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                URL url = new URL("https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users");
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

        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONArray items = jsonResponse.getJSONArray("items");
                    boolean itemFound = false;

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        if (username.equals(item.getString("username"))) {
                            itemFound = true;

                            tutor_name = item.getString("display_name");
                            tutor_email = item.getString("email");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Handle the result from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;
            if (requestCode == 1) { // From gallery
                Uri selectedImageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 2) { // From camera
                bitmap = (Bitmap) data.getExtras().get("data");
            }

            if (bitmap != null) {
                // Convert the bitmap to byte array
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                itemPicture = byteArrayOutputStream.toByteArray();

                // Create a custom drawable that will handle both the image and the rounded corners
                class RoundedImageDrawable extends Drawable {
                    private final Paint paint;
                    private final Bitmap bitmap;
                    private final RectF rectF;
                    private final float cornerRadius;

                    RoundedImageDrawable(Bitmap bitmap, float cornerRadius) {
                        this.bitmap = bitmap;
                        this.cornerRadius = cornerRadius;
                        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        this.rectF = new RectF();
                    }

                    @Override
                    public void draw(@NonNull Canvas canvas) {
                        // Update rect to match current bounds
                        rectF.set(getBounds());

                        // Draw rounded rectangle path
                        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

                        // Set up the bitmap matrix for drawing
                        Matrix matrix = new Matrix();
                        RectF src = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        matrix.setRectToRect(src, rectF, Matrix.ScaleToFit.CENTER);

                        // Save canvas state
                        canvas.save();

                        // Apply clipping for rounded corners
                        Path clipPath = new Path();
                        clipPath.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW);
                        canvas.clipPath(clipPath);

                        // Draw the bitmap
                        canvas.drawBitmap(bitmap, matrix, paint);

                        // Restore canvas state
                        canvas.restore();
                    }

                    @Override
                    public void setAlpha(int alpha) {
                        paint.setAlpha(alpha);
                    }

                    @Override
                    public void setColorFilter(@Nullable ColorFilter colorFilter) {
                        paint.setColorFilter(colorFilter);
                    }

                    @Override
                    public int getOpacity() {
                        return PixelFormat.TRANSLUCENT;
                    }
                }

                // Create our custom drawable with the original bitmap
                RoundedImageDrawable imageDrawable = new RoundedImageDrawable(bitmap,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                                getResources().getDisplayMetrics()));

                // Create background drawable for the shadow effect
                GradientDrawable shadowDrawable = new GradientDrawable();
                shadowDrawable.setColor(Color.WHITE);
                shadowDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                        getResources().getDisplayMetrics()));

                // Combine the drawables
                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                        shadowDrawable,  // Bottom layer (shadow)
                        imageDrawable   // Top layer (image)
                });

                // Apply padding to maintain the shadow effect
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                        getResources().getDisplayMetrics());
                layerDrawable.setLayerInset(1, padding, padding, padding, padding);

                // Set the background
                imageButton.setBackground(layerDrawable);
            }
        }
    }

    private void fetchPlacePredictions(String query) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    placeSuggestions.clear();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        placeSuggestions.add(prediction.getPrimaryText(null).toString());
                    }
                    suggestionsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Error", "Place prediction error: " + e.getMessage()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with image selection
        } else {
            // Permission denied, show a message
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadItem() {
        String name = itemName.getText().toString().trim();
        String category = itemCategory.getSelectedItem().toString();
        String price = itemPrice.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        String location = mSearchText.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || price.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String itemPictureBase64 = encodeImageToBase64(itemPicture);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("item_name", name);
            jsonBody.put("item_category", category);
            jsonBody.put("item_picture", itemPictureBase64);
            jsonBody.put("item_price", price);
            jsonBody.put("item_description", description);

            if (location.isEmpty()) {
                jsonBody.put("seller_location", JSONObject.NULL);
            } else {
                jsonBody.put("seller_location", location);
            }

            jsonBody.put("publish_date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            jsonBody.put("seller_display_name", tutor_name);
            jsonBody.put("seller_email", tutor_email);

            new Thread(() -> {
                try {
                    URL url = new URL("https://apex.oracle.com/pls/apex/wia2001_database_oracle/market/users");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setDoOutput(true);

                    OutputStream os = connection.getOutputStream();
                    os.write(jsonBody.toString().getBytes("UTF-8"));
                    os.close();

                    int responseCode = connection.getResponseCode();
                    runOnUiThread(() -> {
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            Toast.makeText(this, "Item uploaded successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to upload item. Error: " + responseCode, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e("Upload Error", e.getMessage());
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        } catch (Exception e) {
            Log.e("Upload Error", e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng defaultLocation = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Default Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));

        googleMap.setOnMapClickListener(latLng -> mSuggestionsListView.setVisibility(View.GONE));
    }
}