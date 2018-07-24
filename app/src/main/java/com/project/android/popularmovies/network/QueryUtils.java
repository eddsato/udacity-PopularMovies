package com.project.android.popularmovies.network;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.project.android.popularmovies.BuildConfig;
import com.project.android.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final String api_key = BuildConfig.api_key;
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String API_KEY = "api_key";

    public static URL buildUrl (String sortBy){
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(API_KEY, api_key)
                .build();

        URL url = null;

        try {
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl (URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<Movie> extractMovieResultsFromJson (String movieJSON){
        if (TextUtils.isEmpty(movieJSON)){
            return null;
        }

        List<Movie> movies = new ArrayList<>();

        try{
            JSONObject baseJsonResponse = new JSONObject(movieJSON);
            JSONArray movieArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < movieArray.length(); i++){
                JSONObject currentMovie = movieArray.getJSONObject(i);
                String mTitle = currentMovie.getString("title");
                String mPosterPath = currentMovie.getString("poster_path");
                String mOverview = currentMovie.getString("overview");
                Double mVoteAverage = currentMovie.getDouble("vote_average");
                String mReleaseDate = currentMovie.getString("release_date");

                Movie movie = new Movie(mTitle, mPosterPath, mOverview, mVoteAverage, mReleaseDate);
                movies.add(movie);
            }
        } catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing the movie JSON", e);
        }

        return movies;
    }
}
