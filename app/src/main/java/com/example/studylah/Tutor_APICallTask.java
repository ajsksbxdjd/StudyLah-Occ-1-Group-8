package com.example.studylah;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tutor_APICallTask extends AsyncTask<Void, Void, String> {

    private APICallback callback;

    public Tutor_APICallTask(APICallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        try {
            URL url = new URL("https://apex.oracle.com/pls/apex/wia2001_database_oracle/tutor/users");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
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
        if (callback != null) {
            callback.onResult(result);
        }
    }

    public interface APICallback {
        void onResult(String result);
    }
}
