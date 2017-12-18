package com.example.ricardo.movieranking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ricardo.movieranking.R;
import com.example.ricardo.movieranking.model.DetailsMovie;
import com.example.ricardo.movieranking.model.MovieRanking;
import com.example.ricardo.movieranking.service.MovieDBService;
import com.example.ricardo.movieranking.service.ServiceFactory;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ricardo on 18/12/2017.
 */

public class DetailsActivity extends AppCompatActivity{
    private int movieId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_filmes);

        Intent intent = getIntent();
        movieId = intent.getIntExtra("movieId", 0);

        getDetalhesFilme(movieId);
    }

    private void getDetalhesFilme(int movieId) {
        final MovieDBService movieDBService = ServiceFactory.createRetrofitService(MovieDBService.class, MovieDBService.SERVICE_ENDPOINT);

        final Observable<DetailsMovie> movieDetails =
                movieDBService.getMovieDetails(movieId, MovieDBService.SERVICE_API_KEY, "pt-BR");

        movieDetails.
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DetailsMovie>() {
                    @Override
                    public void onCompleted() {
                        Log.e("RICARDO MovieRanking", "COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("MovieRanking err", e.getMessage());
                    }

                    @Override
                    public void onNext(DetailsMovie response) {
                            Log.e("DetailsMovie response", response.getTitle());
                    }
                });
    }
}
