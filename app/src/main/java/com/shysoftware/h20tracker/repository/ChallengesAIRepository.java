package com.shysoftware.h20tracker.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.shysoftware.h20tracker.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ChallengesAIRepository {
    private final OkHttpClient client;
    private final Gson gson;
    public ChallengesAIRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public void testGemini(String prompt, Callback callback){
        Log.d("CHALLENGES", "Test");

        try {
            JSONObject text = new JSONObject();
            text.put("text", prompt);

            JSONArray parts = new JSONArray();
            parts.put(text);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject payload = new JSONObject();
            payload.put("contents", contents);

            RequestBody body = RequestBody.create(
                    payload.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(BuildConfig.GEMINI_URL + "?key=" + BuildConfig.GEMINI_API_KEY)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
