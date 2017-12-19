package com.example.ricardo.movieranking.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ricardo.movieranking.RecyclerTouchListener;
import com.example.ricardo.movieranking.interfaces.ClickListener;
import com.example.ricardo.movieranking.interfaces.OnBottomReachedListener;
import com.example.ricardo.movieranking.R;
import com.example.ricardo.movieranking.adapters.RankingMovieAdapter;
import com.example.ricardo.movieranking.models.AllGenres;
import com.example.ricardo.movieranking.models.Configuration;
import com.example.ricardo.movieranking.models.Genre;
import com.example.ricardo.movieranking.models.MovieRanking;
import com.example.ricardo.movieranking.services.MovieDBService;
import com.example.ricardo.movieranking.services.ServiceFactory;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private int page = 1;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final RankingMovieAdapter rankingMovieAdapter = new RankingMovieAdapter(getApplicationContext());
        recyclerView.setAdapter(rankingMovieAdapter);

        final MovieDBService movieDBService = ServiceFactory.createRetrofitService(MovieDBService.class, MovieDBService.SERVICE_ENDPOINT);
        callGetConfiguration(rankingMovieAdapter, movieDBService);
        callGetGenres(rankingMovieAdapter, movieDBService);
        callGetTopRatedMovies(rankingMovieAdapter, movieDBService, page);

        rankingMovieAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                page++;
                if(page <= totalPages) {
                    callGetTopRatedMovies(rankingMovieAdapter, movieDBService, page);
                }
            }
        });
    }

    private void callGetGenres(final RankingMovieAdapter rankingMovieAdapter, MovieDBService movieDBService) {
        final Observable<AllGenres> allGenres =
                movieDBService.getGenres(MovieDBService.SERVICE_API_KEY, MovieDBService.SERVICE_LANGUAGE);

        allGenres.
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AllGenres>() {
                    @Override
                    public void onCompleted() {
                        Log.e("getGenres end", "COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getGenres err", e.getMessage());
                    }

                    @Override
                    public void onNext(AllGenres allGenres) {
                        rankingMovieAdapter.setGenreList(allGenres.getGenres());
                    }
                });
    }

    private void callGetConfiguration(final RankingMovieAdapter rankingMovieAdapter, MovieDBService movieDBService) {
        final Observable<Configuration> configuration =
                movieDBService.getConfiguration(MovieDBService.SERVICE_API_KEY);

        configuration.
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Configuration>() {
                    @Override
                    public void onCompleted() {
                        Log.e("getConfiguration end", "COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getConfiguration err", e.getMessage());
                    }

                    @Override
                    public void onNext(Configuration configuration) {
                        rankingMovieAdapter.setConfiguration(configuration);
                    }
                });
    }

    private void callGetTopRatedMovies(final RankingMovieAdapter rankingMovieAdapter, MovieDBService movieDBService, int page) {
        final Observable<MovieRanking> popularRanking =
                movieDBService.getTopRatedMovies(MovieDBService.SERVICE_API_KEY, MovieDBService.SERVICE_LANGUAGE, page);

        popularRanking.
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieRanking>() {
                               @Override
                               public void onCompleted() {
                                   Log.e("getTopRatedMovies end", "COMPLETED");
                               }

                               @Override
                               public void onError(Throwable e) {
                                   Log.e("getTopRatedMovies err", e.getMessage());
                               }

                               @Override
                               public void onNext(MovieRanking response) {
                                   totalPages = response.getTotalPages();

                                   for(int i =0; i < response.getResults().size(); i++){
                                       rankingMovieAdapter.addData(response.getResults().get(i));
                                       Log.e("MovieRanking response", response.getResults().get(i).getTitle());

                                       recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                                               recyclerView, new ClickListener() {
                                           @Override
                                           public void onClick(final View view, final int position) {

                                               TextView moreInfo=(TextView)view.findViewById(R.id.more_info);
                                               moreInfo.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       int movieId = rankingMovieAdapter.getMovieIdFromClicked(position);
                                                       Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                                                       intent.putExtra("movieId", movieId);
                                                       startActivity(intent);
                                                   }
                                               });
                                           }
                                       }));
                                   }
                               }
                           }
                );
    }
}
