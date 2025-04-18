package com.shysoftware.h20tracker.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.repository.ChallengesAIRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChallengesAIViewModel extends ViewModel {
    private ChallengesAIRepository challengesAIRepository = new ChallengesAIRepository();

    public void callGemini(String prompt){
        challengesAIRepository.testGemini(prompt, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("CHALLLENGES", "Network Error!");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    String json = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray candidates = jsonObject.getJSONArray("candidates");

                        if (candidates.length() > 0) {
                            JSONObject firstCandidate = candidates.getJSONObject(0);
                            JSONObject content = firstCandidate.getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");

                            if (parts.length() > 0) {
                                String text = parts.getJSONObject(0).getString("text");
                                Log.d("CHALLENGES_RESPONSE", text);
                            }
                        }

                    } catch (Exception e) {
                        Log.d("CHALLENGES", "An error has occurred");
                    }

                }
            }
        });
    }
}
