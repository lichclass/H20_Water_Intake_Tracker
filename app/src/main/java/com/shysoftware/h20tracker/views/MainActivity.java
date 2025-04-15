package com.shysoftware.h20tracker;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SupabaseFetch";
    private static final String SUPABASE_URL = "https://ffgblqadfbyecgyibxzh.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZmZ2JscWFkZmJ5ZWNneWlieHpoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDQ0ODMzMjMsImV4cCI6MjA2MDA1OTMyM30.IHHIbtgZnmQX-eH_ewdc_7JUFK177l6Nbn0MzR-PzKM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchUsers();
    }

    private void fetchUsers() {
        OkHttpClient client = new OkHttpClient();

        // Construct full URL to the users table
        String fullUrl = SUPABASE_URL + "/rest/v1/users";

        // Build the HTTP GET request
        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Prefer", "return=representation")
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SupabaseFetch", "❌ Request Failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Log and handle successful response
                    String responseBody = response.body() != null ? response.body().string() : "No response body";
                    Log.d("SupabaseFetch", "✅ Success: " + responseBody);
                } else {
                    // Log error code and body
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    Log.e("SupabaseFetch", "❌ HTTP Code: " + response.code());
                    Log.e("SupabaseFetch", "❌ Message: " + response.message());
                    Log.e("SupabaseFetch", "❌ Body: " + errorBody);
                }
            }
        });
    }

}
