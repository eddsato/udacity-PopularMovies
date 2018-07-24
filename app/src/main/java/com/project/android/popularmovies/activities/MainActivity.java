package com.project.android.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.project.android.popularmovies.models.Movie;
import com.project.android.popularmovies.adapters.MovieAdapter;
import com.project.android.popularmovies.network.QueryUtils;
import com.project.android.popularmovies.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String POPULARITY_PARAM = "popular";
    private static final String TOP_RATED_PARAM = "top_rated";
    private static final String LIST_STATE = "state";
    private static final int NUM_COLUMNS_PORTRAIT = 2;
    private static final int NUM_COLUMNS_LANDSCAPE = 4;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingIndicator;
    private List<Movie> moviesList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movies);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            gridLayoutManager = new GridLayoutManager(this, NUM_COLUMNS_LANDSCAPE);
        } else {
            gridLayoutManager = new GridLayoutManager(this, NUM_COLUMNS_PORTRAIT);
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(moviesList, MainActivity.this, MainActivity.this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if (isOnline()){
            new FetchMovieTask().execute(POPULARITY_PARAM);
        }
    }

    public boolean isOnline(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Parcelable savedState = savedInstanceState.getParcelable(LIST_STATE);
                gridLayoutManager.onRestoreInstanceState(savedState);
            }
        }, 200);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popularity) {
            moviesList.clear();
            new FetchMovieTask().execute(POPULARITY_PARAM);
            return true;
        }

        if (id == R.id.action_sort_top_rated) {
            moviesList.clear();
            new FetchMovieTask().execute(TOP_RATED_PARAM);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movieDetail) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie_item", movieDetail);
        startActivity(intentToStartDetailActivity);
    }

    private class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length < 1 || params[0] == null) {
                return null;
            }

            String sortBy = params[0];
            URL movieRequestUrl = QueryUtils.buildUrl(sortBy);

            String jsonMovieResponse = null;
            try {
                jsonMovieResponse = QueryUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                return QueryUtils.extractMovieResultsFromJson(jsonMovieResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mMovieAdapter.setMovieData(movies);
        }
    }
}
