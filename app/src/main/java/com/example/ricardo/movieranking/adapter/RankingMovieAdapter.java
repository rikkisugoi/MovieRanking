package com.example.ricardo.movieranking.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.ricardo.movieranking.R;
import com.example.ricardo.movieranking.activity.DetailsActivity;
import com.example.ricardo.movieranking.model.Configuration;
import com.example.ricardo.movieranking.model.DetailsMovieRanking;

import java.util.ArrayList;

/**
 * Created by Ricardo on 15/12/2017.
 */

public class RankingMovieAdapter extends RecyclerView.Adapter<RankingMovieAdapter.ViewHolder>{

    private final ArrayList<DetailsMovieRanking> listMovieRanking;
    private final Context context;
    private Configuration configuration;
    public View link;
    private int movieId;

    public RankingMovieAdapter(Context context) {
        super();
        this.context = context;
        listMovieRanking = new ArrayList<DetailsMovieRanking>();
    }

    public void setConfiguration(Configuration configuration){
        this.configuration = configuration;
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
        DetailsMovieRanking detailsMovieRanking = listMovieRanking.get(position);

        String imageLoader = configuration.getImages().getBaseUrl()
                + configuration.getImages().getPosterSizes().get(2)
                + detailsMovieRanking.getPosterPath();

        Glide.with(context)
                .load(imageLoader)
                .placeholder(R.drawable.clickbus_logo)
                .error(R.drawable.image_not_found)
                .centerCrop()
                .override(400, 500)
                .animate(android.R.anim.fade_in)
                .into(holder.poster);

        holder.title.setText(detailsMovieRanking.getTitle());
        holder.score.setText("Nota: " + String.valueOf(detailsMovieRanking.getVoteAverage()));
//        holder.genre.getText(detailsMovieRanking.)
        holder.releaseYear.setText("Ano de lançamento: " + detailsMovieRanking.getReleaseDate().substring(0,4));

        final CharSequence text = "Clique aqui para saber mais";
        final SpannableString spannableString = new SpannableString( text );
        spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.moreInfo.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    @Override
    public int getItemCount() {
        return listMovieRanking.size();
    }

    public void addData(DetailsMovieRanking response) {
        listMovieRanking.add(response);
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
