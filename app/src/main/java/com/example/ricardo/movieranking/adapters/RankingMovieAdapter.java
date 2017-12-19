package com.example.ricardo.movieranking.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ricardo.movieranking.interfaces.OnBottomReachedListener;
import com.example.ricardo.movieranking.R;
import com.example.ricardo.movieranking.models.Configuration;
import com.example.ricardo.movieranking.models.MovieListResults;
import com.example.ricardo.movieranking.models.Genre;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo on 15/12/2017.
 */

public class RankingMovieAdapter extends RecyclerView.Adapter<RankingMovieAdapter.ViewHolder>{

    private final ArrayList<MovieListResults> listMovieRanking;
    private final Context context;
    private Configuration configuration;
    OnBottomReachedListener onBottomReachedListener;
    private List<Genre> genresList;

    public RankingMovieAdapter(Context context) {
        super();
        this.context = context;
        listMovieRanking = new ArrayList<>();
    }

    public void setConfiguration(Configuration configuration){
        this.configuration = configuration;
    }

    public void setGenreList(List<Genre> genres) {
        this.genresList = genres;
    }

    public int getMovieIdFromClicked(int position){
        return listMovieRanking.get(position).getId();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_filmes,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieListResults movieListResults = listMovieRanking.get(position);

        if(configuration != null) {
            String imageLoader = configuration.getImages().getBaseUrl()
                    + configuration.getImages().getPosterSizes().get(2)
                    + movieListResults.getPosterPath();

            Glide.with(context)
                    .load(imageLoader)
                    .placeholder(R.drawable.clickbus_logo)
                    .error(R.drawable.image_not_found)
                    .centerCrop()
                    .override(400, 500)
                    .animate(android.R.anim.fade_in)
                    .into(holder.poster);
        }

        holder.title.setText(movieListResults.getTitle());
        holder.score.setText("Nota: " + String.valueOf(movieListResults.getVoteAverage()));

        if(genresList != null) {
            String genreText = "Gêneros: ";
            for (int i = 0; i < movieListResults.getGenreIds().size(); i++) {
                int genre = movieListResults.getGenreIds().get(i);
                if (genreText != "Gêneros: ") {
                    genreText += ", ";
                }
                for (int j = 0; i < this.genresList.size(); j++) {
                    if (genre == this.genresList.get(j).getId()) {
                        genreText += this.genresList.get(j).getName();
                        break;
                    }
                }
            }
            holder.genre.setText(genreText);
        }

        if( movieListResults.getReleaseDate() != null
                &&  movieListResults.getReleaseDate().length() > 0){
            holder.releaseYear.setText("Ano de lançamento: " + movieListResults.getReleaseDate().substring(0,4));
        }

        final CharSequence text = "Clique aqui para saber mais";
        final SpannableString spannableString = new SpannableString( text );
        spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.moreInfo.setText(spannableString, TextView.BufferType.SPANNABLE);


        if (position == listMovieRanking.size() - 1){
            onBottomReachedListener.onBottomReached(position);
        }
    }

    @Override
    public int getItemCount() {
        return listMovieRanking.size();
    }

    public void addData(MovieListResults response) {
        listMovieRanking.add(response);
        notifyDataSetChanged();
    }

    public void clear() {
        listMovieRanking.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView poster;
        public TextView title;
        public TextView score;
        public TextView genre;
        public TextView releaseYear;
        public TextView moreInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.movie_poster);
            title = (TextView) itemView.findViewById(R.id.movie_title);
            score = (TextView) itemView.findViewById(R.id.movie_score);
            genre = (TextView) itemView.findViewById(R.id.movie_genre);
            releaseYear = (TextView) itemView.findViewById(R.id.movie_release);
            moreInfo = (TextView) itemView.findViewById(R.id.more_info);
        }
    }
}
