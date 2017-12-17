package com.example.ricardo.movieranking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final RankingMovieAdapter rankingMovieAdapter = new RankingMovieAdapter();
        recyclerView.setAdapter(rankingMovieAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.themoviedb.org/3/")
                .build();

        final MovieDBService movieDBService = retrofit.create(MovieDBService.class);
        final Observable<MovieRanking> popularRanking =
                movieDBService.getPopularMovies("2efea6668b02c34eedcbbf7d85e32607", "pt-BR", 1);

        popularRanking.
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieRanking>() {
                               @Override
                               public void onCompleted() {
                                   Log.e("RICARDO ", "COMPLETED");
                               }

                               @Override
                               public void onError(Throwable e) {
                                   Log.e("GithubDemo", e.getMessage());
                               }

                               @Override
                               public void onNext(MovieRanking response) {
//                                   rankingMovieAdapter.addData(response.getResults());
                                   Log.e("GithubDemo", response.getResults().get(1).getTitle());
                               }
                           }

                );
    }
}
