package com.project.android.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.android.popularmovies.R;
import com.project.android.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.tv_title) TextView mTitleTextView;
    @BindView(R.id.tv_release_date) TextView mReleaseDateTextView;
    @BindView(R.id.tv_vote_average) TextView mVoteAverageTextView;
    @BindView(R.id.tv_overview) TextView mOverviewTextView;
    @BindView(R.id.im_poster) ImageView mPosterImageView;
    private Movie movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        movies = bundle.getParcelable("movie_item");

        mTitleTextView.setText(movies.getTitle());
        mReleaseDateTextView.setText(getString(R.string.details_release) + " "  + movies.getReleaseDate());
        mVoteAverageTextView.setText(getString(R.string.details_vote) + " " + String.valueOf(movies.getVoteAverage()));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w342//" + movies.getPoster()).into(mPosterImageView);
        mOverviewTextView.setText(movies.getOverview());

    }
}
