package com.example.studylah;

import android.Manifest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.Manifest; // Import for permission constants

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

import android.util.Base64;

public class EventDetails extends AppCompatActivity {

    EditText ETEventName, ETEventDate, ETEventTime, ETPublishDate, ETLocation, ETDesc, ETWebsiteLink, ETRegLink, ETTutorName, ETTutorEmail;;
    ImageButton BtnDatePicker, BtnTimePicker, BtnPublishDatePicker;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Button BtnAddImg, BtnCreate;

    // Placeholder variables for text field values
    String eventName, eventDate, eventTime, eventLocation, eventDescription, eventWebsiteLink, publishDate, registrationLink, tutorDisplayName, tutorEmail;
    byte[] eventPicture; // Variable to store the image as byte array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        BtnDatePicker = findViewById(R.id.BtnDatePicker);
        BtnTimePicker = findViewById(R.id.BtnTimePicker);
        BtnPublishDatePicker = findViewById(R.id.BtnPublishDatePicker);
        BtnCreate = findViewById(R.id.BtnCreate);

        ETEventName = findViewById(R.id.ETEventName);
        ETEventDate = findViewById(R.id.ETEventDate);
        ETEventTime = findViewById(R.id.ETEventTime);
        ETLocation = findViewById(R.id.ETLocation);
        ETDesc = findViewById(R.id.ETDesc);
        ETWebsiteLink = findViewById(R.id.ETWebsiteLink);
        ETPublishDate = findViewById(R.id.ETPublishDate);
        ETRegLink = findViewById(R.id.ETRegLink);
        ETTutorName = findViewById(R.id.ETTutorName);
        ETTutorEmail = findViewById(R.id.ETTutorEmail);

        BtnAddImg = findViewById(R.id.BtnAddImg);

        // Set click listeners for date picker and time picker
        BtnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(ETEventDate);
            }
        });

        BtnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(ETEventTime);
            }
        });

        // Set click listeners for date picker and time picker
        BtnPublishDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(ETPublishDate);
            }
        });

        BtnAddImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Check if permission is granted
//                if (ContextCompat.checkSelfPermission(EventDetails.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(EventDetails.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
//                } else {
//                    openFileExplorer();
//                }
//            }

            @Override
            public void onClick(View v) {
                // Check if permissions are granted
                if (ContextCompat.checkSelfPermission(EventDetails.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(EventDetails.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EventDetails.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, 1);
                } else {
                    showImageSourceDialog();
                }
            }
        });

        BtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validate all fields
                if (eventPicture == null) {
                    Toast.makeText(EventDetails.this, "Event Picture is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ETEventName.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Event Name is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if event name exceeds 15 characters
                if (ETEventName.getText().toString().length() > 15) {
                    Toast.makeText(EventDetails.this, "Event Name cannot exceed 15 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ETEventDate.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Event Date is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ETEventTime.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Event Time is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ETLocation.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Event Location is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ETDesc.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Event Description is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ETWebsiteLink.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Event Website Link is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate URL format
                if (!android.util.Patterns.WEB_URL.matcher(ETWebsiteLink.getText().toString()).matches()) {
                    Toast.makeText(EventDetails.this, "Invalid Event Website Link format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ETRegLink.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Registration Link is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.WEB_URL.matcher(ETRegLink.getText().toString()).matches()) {
                    Toast.makeText(EventDetails.this, "Invalid Registration Link format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ETTutorName.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Tutor Name is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if tutor name exceeds 10 characters
                if (ETTutorName.getText().toString().length() > 10) {
                    Toast.makeText(EventDetails.this, "Tutor Name cannot exceed 10 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ETPublishDate.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Publish Date is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate that publish date is earlier than event date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date publishDate = sdf.parse(ETPublishDate.getText().toString());
                    Date eventDate = sdf.parse(ETEventDate.getText().toString());

                    if (publishDate != null && eventDate != null && !publishDate.before(eventDate)) {
                        Toast.makeText(EventDetails.this, "Publish date must be earlier than event date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(EventDetails.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ETTutorEmail.getText().toString().isEmpty()) {
                    Toast.makeText(EventDetails.this, "Tutor Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate email format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(ETTutorEmail.getText().toString()).matches()) {
                    Toast.makeText(EventDetails.this, "Invalid Tutor Email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send data to API
                sendDataToApi();
            }
        });

    }

//    //  Method to show the date picker dialog
//    private void pickDate(EditText e) {
//        final Calendar calendar = Calendar.getInstance();
//        mYear = calendar.get(Calendar.YEAR);
//        mMonth = calendar.get(Calendar.MONTH);
//        mDay = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                // Set the selected date in the EditText
//                e.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
//            }
//        }, mYear, mMonth, mDay);
//        datePickerDialog.show();
//    }

    private void pickDate(EditText e) {
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set the selected date in the EditText in yyyy-MM-dd format
                e.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth));
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

//    // Method to show the time picker dialog （24h format）
//    private void pickTime(EditText e) {
//        final Calendar calendar = Calendar.getInstance();
//        mHour = calendar.get(Calendar.HOUR_OF_DAY);
//        mMinute = calendar.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                // Set the selected time in the EditText
//                e.setText(String.format("%02d:%02d", hourOfDay, minute));
//            }
//        }, mHour, mMinute, false);
//        timePickerDialog.show();
//    }

    // Method to show the time picker dialog （12-hour format with AM/PM)
    private void pickTime(EditText e) {
        final Calendar calendar = Calendar.getInstance();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Convert to 12-hour format and determine AM/PM
                String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                int hourIn12HourFormat = (hourOfDay > 12) ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);

                // Set the selected time in the EditText
                e.setText(String.format("%02d:%02d %s", hourIn12HourFormat, minute, amPm));
            }
        }, mHour, mMinute, false); // Use 'false' to set the time picker in default 12-hour mode
        timePickerDialog.show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, allow file explorer to open
                openFileExplorer();
            } else {
                // Permission denied, show a message or disable feature
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
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
                eventPicture = byteArrayOutputStream.toByteArray();

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
                BtnAddImg.setBackground(layerDrawable);
            }
        }
    }

    private void sendDataToApi() {
        // Get values from text fields
        eventName = ETEventName.getText().toString();
        eventDate = ETEventDate.getText().toString();
        eventTime = ETEventTime.getText().toString();
        eventLocation = ETLocation.getText().toString();
        eventDescription = ETDesc.getText().toString();
        eventWebsiteLink = ETWebsiteLink.getText().toString();
        publishDate = ETPublishDate.getText().toString();
        registrationLink = ETRegLink.getText().toString();
        tutorDisplayName = ETTutorName.getText().toString();
        tutorEmail = ETTutorEmail.getText().toString();

        // Log the values to check if they are retrieved correctly
        Log.d("EventDetails", "Event Name: " + eventName);
        Log.d("EventDetails", "Event Date: " + eventDate);
        Log.d("EventDetails", "Event Time: " + eventTime);
        Log.d("EventDetails", "Event Location: " + eventLocation);
        Log.d("EventDetails", "Event Description: " + eventDescription);
        Log.d("EventDetails", "Event Website Link: " + eventWebsiteLink);
        Log.d("EventDetails", "Publish Date: " + publishDate);
        Log.d("EventDetails", "Registration Link: " + registrationLink);
        Log.d("EventDetails", "Tutor Display Name: " + tutorDisplayName);
        Log.d("EventDetails", "Tutor Email: " + tutorEmail);

        // Convert image to Base64 string
        String eventPictureBase64 = encodeImageToBase64(eventPicture);

        // Create JSON object with event data
        JSONObject eventData = new JSONObject();
        try {
            eventData.put("event_name", eventName);
            eventData.put("event_date", eventDate);
            eventData.put("event_time", eventTime);
            eventData.put("event_location", eventLocation);
            eventData.put("event_picture", eventPictureBase64);
            eventData.put("event_description", eventDescription);
            eventData.put("event_website_link", eventWebsiteLink);
            eventData.put("publish_date", publishDate);
            eventData.put("registration_link", registrationLink);
            eventData.put("tutor_display_name", tutorDisplayName);
            eventData.put("tutor_email", tutorEmail);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create JSON object", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send JSON data to API
        new Thread(() -> {
            try {
                URL url = new URL("https://apex.oracle.com/pls/apex/wia2001_database_oracle/event/users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = eventData.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> Toast.makeText(EventDetails.this, "Event data is saved successfully", Toast.LENGTH_SHORT).show());

                    // Redirect to EventCreated activity
                    Intent intent = new Intent(getApplicationContext(), EventCreated.class);
                    startActivity(intent);

                } else {
                    runOnUiThread(() -> Toast.makeText(EventDetails.this, "Failed to save data. Please try again later", Toast.LENGTH_SHORT).show());

                    // Redirect to EventCreated activity
                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                    startActivity(intent);
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EventDetails.this, "Failed to send data to API", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}