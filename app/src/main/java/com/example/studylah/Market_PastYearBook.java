package com.example.studylah;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Market_PastYearBook extends Fragment {

    private RecyclerView recyclerView;
    private Market_BookAdapter adapter;
    private List<Market_BookItem> pastYearBookList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_fragment_past_year_book, container, false);

        recyclerView = view.findViewById(R.id.recycle_past_year);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Set 2 columns

        pastYearBookList = new ArrayList<>();
        adapter = new Market_BookAdapter(pastYearBookList);
        recyclerView.setAdapter(adapter);

        new FetchPastYearBooksTask().execute();

        return view;
    }

    private class FetchPastYearBooksTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
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

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        if ("Past Year Book".equals(item.getString("item_category"))) {
                            String id = item.getString("item_id");
                            String name = item.getString("item_name");
                            String price = item.getString("item_price");
                            String imageBase64 = item.getString("item_picture");

                            // Decode Base64 string to Bitmap
                            byte[] decodedString = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT);
                            Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            pastYearBookList.add(new Market_BookItem(decodedByte, name, price, id));
                        }
                    }

                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}