package com.project.android.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.android.popularmovies.R;
import com.project.android.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    public static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> result;
    private Context context;
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342//";

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movieDetail);
    }

    public MovieAdapter(List<Movie> movies, Context context, MovieAdapterOnClickHandler clickHandler) {
        this.result = movies;
        this.context = context;
        mClickHandler = clickHandler;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mMoviePoster;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mMoviePoster = itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = result.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Movie movie = result.get(position);
        Picasso.with(context).load(IMAGE_BASE_URL + movie.getPoster()).into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public void setMovieData (List<Movie> movie){
        result.addAll(movie);
        notifyDataSetChanged();
    }
}
