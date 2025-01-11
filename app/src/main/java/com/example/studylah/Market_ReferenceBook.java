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
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Market_ReferenceBook extends Fragment {

    private RecyclerView recyclerView;
    private Market_BookAdapter adapter;
    private List<Market_BookItem> referenceBookList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_fragment_reference_book, container, false);

        recyclerView = view.findViewById(R.id.recycle_reference);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Set 2 columns

        referenceBookList = new ArrayList<>();
        adapter = new Market_BookAdapter(referenceBookList);
        recyclerView.setAdapter(adapter);

        new FetchReferenceBooksTask().execute();

        return view;
    }

    private class FetchReferenceBooksTask extends AsyncTask<Void, Void, String> {

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

                    List<JSONObject> itemList = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        if ("Reference Book".equals(item.getString("item_category"))) {
                            itemList.add(item);
                        }
                    }

                    // Sort items by publish_date in descending order
                    itemList.sort((item1, item2) -> {
                        try {
                            String date1 = item1.getString("publish_date");
                            String date2 = item2.getString("publish_date");
                            return date2.compareTo(date1); // Descending order
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    for (JSONObject item : itemList) {
                        try {
                            String id = item.getString("item_id");
                            String name = item.getString("item_name");
                            String price = item.getString("item_price");
                            String imageBase64 = item.getString("item_picture");

                            // Decode Base64 string to Bitmap
                            byte[] decodedString = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT);
                            Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            referenceBookList.add(new Market_BookItem(decodedByte, name, price, id));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}