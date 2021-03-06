package com.example.ricardo.movieranking.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.ricardo.movieranking.R;
import com.example.ricardo.movieranking.models.Configuration;
import com.example.ricardo.movieranking.models.MovieDetails;
import com.example.ricardo.movieranking.services.MovieDBService;
import com.example.ricardo.movieranking.services.ServiceFactory;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ricardo on 18/12/2017.
 */

public class DetailsActivity extends AppCompatActivity{
    private int movieId;
    private TextView detailsTitle;
    private TextView detailsScore;
    private TextView detailsRelease;
    private TextView detailsSinopsis;
    private TextView detailsOriginalTitle;
    private TextView detailsGenre;
    private TextView detailsStudios;
    private Configuration configuration;
    private View layoutDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_filme);

        Intent intent = getIntent();
        movieId = intent.getIntExtra("movieId", 0);

        final MovieDBService movieDBService = ServiceFactory.createRetrofitService(MovieDBService.class, MovieDBService.SERVICE_ENDPOINT);

        callGetConfiguration(movieDBService);

        getDetalhesFilme(movieDBService, movieId);
    }

    private void callGetConfiguration(MovieDBService movieDBService) {
        final Observable<Configuration> configuration =
                movieDBService.getConfiguration(MovieDBService.SERVICE_API_KEY);

        configuration.
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Configuration>() {
                    @Override
                    public void onCompleted() {
                        Log.e("getConfiguration end ", "COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getConfiguration err", e.getMessage());
                    }

                    @Override
                    public void onNext(Configuration configuration) {
                        setConfiguration(configuration);
                    }
                });
    }

    private void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    private void getDetalhesFilme(MovieDBService movieDBService, int movieId) {
        final Observable<MovieDetails> movieDetails =
                movieDBService.getMovieDetails(movieId, MovieDBService.SERVICE_API_KEY, MovieDBService.SERVICE_LANGUAGE);

        movieDetails.
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieDetails>() {
                    @Override
                    public void onCompleted() {
                        Log.e("getMovieDetails end ", "COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getMovieDetails err", e.getMessage());
                    }

                    @Override
                    public void onNext(MovieDetails response) {
                        setLayoutFields(response);
                    }
                });
    }

    private void setLayoutFields(MovieDetails response) {
        detailsTitle = (TextView) findViewById(R.id.details_title);
        detailsOriginalTitle = (TextView) findViewById(R.id.details_original_title);
        detailsScore = (TextView) findViewById(R.id.details_score);
        detailsGenre = (TextView) findViewById(R.id.details_genre);
        detailsRelease = (TextView) findViewById(R.id.details_release);
        detailsStudios = (TextView) findViewById(R.id.details_studios);
        detailsSinopsis = (TextView) findViewById(R.id.details_sinopsis);
        layoutDetails = findViewById(R.id.layout_details);

        detailsTitle.setText(response.getTitle());
        detailsOriginalTitle.setText("Título original: " + response.getOriginalTitle());
        detailsScore.setText("Nota: " + String.valueOf(response.getVoteAverage()));

        String genreText = "Gêneros: ";
        for (int i = 0; i < response.getGenres().size(); i ++) {
            if(genreText != "Gêneros: "){
              genreText += ", ";
            }
            genreText += response.getGenres().get(i).getName();
        }
        detailsGenre.setText(genreText);

        if( response.getReleaseDate() != null
                &&  response.getReleaseDate().length() > 0){
            detailsRelease.setText("Ano de lançamento: " + response.getReleaseDate().substring(0,4));
        }

        String studiosText = "Produzido por: ";
        for (int i = 0; i < response.getProductionCompanies().size(); i ++) {
            if(studiosText != "Produzido por: "){
                studiosText += ", ";
            }
            studiosText += response.getProductionCompanies().get(i).getName();
        }
        detailsStudios.setText(studiosText);

        detailsSinopsis.setText("Sinopse: " + response.getOverview());

        if(configuration != null) {
            String imageLoader = configuration.getImages().getBaseUrl()
                    + configuration.getImages().getBackdropSizes().get(3)
                    + response.getBackdropPath();

            Glide.with(this)
                    .load(imageLoader)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable drawable = new BitmapDrawable(getResources(), resource);

                            int sdk = android.os.Build.VERSION.SDK_INT;
                            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                layoutDetails.setBackgroundDrawable(drawable);
                            } else {
                                layoutDetails.setBackground(drawable);
                            }
                        }
                    });
        }
    }

}
