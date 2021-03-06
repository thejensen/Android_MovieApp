package com.epicodus.movieapp.services;

import android.util.Log;

import com.epicodus.movieapp.Constants;
import com.epicodus.movieapp.models.Actor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Guest on 12/1/16.
 */
public class MovieService {
    public static final String TAG = MovieService.class.getSimpleName();

    public static void findActorId(String actorString, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.MOVIEDB_ACTOR_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.API_KEY_QUERY_PARAMETER, Constants.API_KEY);
        urlBuilder.addQueryParameter(Constants.QUERY_PARAMETER, actorString);
        String url = urlBuilder.build().toString();

        Log.v(TAG, "Actor Id URL: " + url);
        Request request = new Request.Builder().url(url).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public String processActorId(Response response) {
        String actorId = null;
        try {
            String jsonData = response.body().string();

            if (response.isSuccessful()) {
                JSONObject actorJSON = new JSONObject(jsonData);
                JSONArray resultsJSON = actorJSON.getJSONArray("results");

                actorId = resultsJSON.getJSONObject(0).getString("id");
                Log.v(TAG, "ActorId: " + actorId);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return actorId;
    }

    public static void findActorMovies(String actorId, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.MOVIEDB_ACTOR_MOVIES_BASE_URL).newBuilder();
        urlBuilder.addPathSegment(actorId);
        urlBuilder.addQueryParameter(Constants.API_KEY_QUERY_PARAMETER, Constants.API_KEY);
        urlBuilder.addQueryParameter(Constants.APPEND_TO_RESPONSE_QUERY_PARAMETER, Constants.MOVIE_CREDITS_AND_IMAGES);
        String url = urlBuilder.build().toString();

        Log.v(TAG, "Movie URL: " + url);

        Request request = new Request.Builder().url(url).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public ArrayList<Actor> processActorMovies (Response response) {
        ArrayList<Actor> actorMovies = new ArrayList<>();
        try {
            String jsonData = response.body().string();

            if (response.isSuccessful()) {
                JSONObject actorMoviesObject = new JSONObject(jsonData);
                JSONObject resultsJSON = actorMoviesObject.getJSONObject("movie_credits");
                JSONArray resultsArrayJSON = actorMoviesObject.getJSONObject("movie_credits").getJSONArray("cast");

                for (int i=0; i < resultsArrayJSON.length(); i++) {
                    String character = resultsArrayJSON.getJSONObject(i).getString("character");
                    String title = resultsArrayJSON.getJSONObject(i).getString("title");
                    String posterPath = resultsArrayJSON.getJSONObject(i).getString("poster_path");
                    String releaseDate = resultsArrayJSON.getJSONObject(i).getString("release_date");

                    Actor actor = new Actor(character, title, posterPath, releaseDate);
                    actorMovies.add(actor);
                }
            for (Object object : actorMovies) {
                Log.v(TAG, "Actor movies: " + object);
            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
        e.printStackTrace();
        }

        return actorMovies;
    }


}
