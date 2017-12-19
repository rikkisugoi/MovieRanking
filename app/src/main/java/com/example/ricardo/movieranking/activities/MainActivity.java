package com.example.ricardo.movieranking.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ricardo.movieranking.RecyclerTouchListener;
import com.example.ricardo.movieranking.RxSearch;
import com.example.ricardo.movieranking.interfaces.ClickListener;
import com.example.ricardo.movieranking.interfaces.OnBottomReachedListener;
import com.example.ricardo.movieranking.R;
import com.example.ricardo.movieranking.adapters.RankingMovieAdapter;
import com.example.ricardo.movieranking.models.AllGenres;
import com.example.ricardo.movieranking.models.Configuration;
import com.example.ricardo.movieranking.models.MovieList;
import com.example.ricardo.movieranking.services.MovieDBService;
import com.example.ricardo.movieranking.services.ServiceFactory;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RankingMovieAdapter rankingMovieAdapter;
    private MovieDBService movieDBService;
    private int page = 1;
    private int totalPages = 1;
    boolean isFirstSearch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Criando a RecyclerView,
        configurando Layout e Adapter
         */
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rankingMovieAdapter = new RankingMovieAdapter(getApplicationContext());
        recyclerView.setAdapter(rankingMovieAdapter);

        /*
        Criando objeto de Retrofit
        e realizando as chamadas
         */
        movieDBService = ServiceFactory.createRetrofitService(MovieDBService.class, MovieDBService.SERVICE_ENDPOINT);
        callGetConfiguration(rankingMovieAdapter, movieDBService);
        callGetGenres(rankingMovieAdapter, movieDBService);
        callGetTopRatedMovies(rankingMovieAdapter, movieDBService, page);

        /*
        Ao atingir o fim da RecyclerView,
        buscar a próxima página de filmes
        gerando um scroll contínuo
         */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        /*
        Criando o mecanismo de pesquisa
         */
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        /*
        Pesquisa utilizando Debounce
        com intervalo de 1 segundo
         */
        RxSearch.fromSearchView(searchView)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {

                    @Override
                    public void onCompleted() {
                        Log.e("searchView end", "COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("searchView err", e.getMessage());
                    }

                    @Override
                    public void onNext(String query) {
                        rankingMovieAdapter.clear();
                        page = 1;

                        if(query.length() > 0) {
                            isFirstSearch = false;
                            callGetMovieSearch(query);
                        } else {
                            /*
                            ao abrir a busca, não é necessário efetuar
                            essa chamada pois os elementos já estão
                            carregados na tela.
                            Se o campo de busca estiver vazia,
                            carrego a lista normal de filmes
                             */
                            if(!isFirstSearch) {
                                callGetTopRatedMovies(rankingMovieAdapter, movieDBService, page);
                            }
                        }
                    }
                });
        return true;
    }

    /*
    Pesquisar por filmes na API
     */
    private void callGetMovieSearch(String query) {
        final Observable<MovieList> movieSearch =
                movieDBService.getSearchMovie(MovieDBService.SERVICE_API_KEY, MovieDBService.SERVICE_LANGUAGE, query, page);

        movieSearch.
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieList>() {
                    @Override
                    public void onCompleted() {
                        Log.e("getMovieSearch end", "COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getMovieSearch err", e.getMessage());
                    }

                    @Override
                    public void onNext(MovieList movieList) {
                        totalPages = movieList.getTotalPages();

                        for (int i = 0; i < movieList.getResults().size(); i++) {
                            rankingMovieAdapter.addData(movieList.getResults().get(i));
                            Log.e("MovieList response", movieList.getResults().get(i).getTitle());

                            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                                    recyclerView, new ClickListener() {
                                @Override
                                public void onClick(final View view, final int position) {

                                    TextView moreInfo = (TextView) view.findViewById(R.id.more_info);
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
                });
    }

    /*
    Consultar na API a lista de gêneros
     */
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

    /*
    Obtendo as configurações.
    Esse objeto terá a URL base para
    buscar as imagens
     */
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

    /*
    Buscando lista de filmes
    ordenadas por maior nota
     */
    private void callGetTopRatedMovies(final RankingMovieAdapter rankingMovieAdapter, MovieDBService movieDBService, int page) {
        rankingMovieAdapter.clear();
        final Observable<MovieList> popularRanking =
                movieDBService.getTopRatedMovies(MovieDBService.SERVICE_API_KEY, MovieDBService.SERVICE_LANGUAGE, page);

        popularRanking.
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieList>() {
                               @Override
                               public void onCompleted() {
                                   Log.e("getTopRatedMovies end", "COMPLETED");
                               }

                               @Override
                               public void onError(Throwable e) {
                                   Log.e("getTopRatedMovies err", e.getMessage());
                               }

                               @Override
                               public void onNext(MovieList response) {
                                   totalPages = response.getTotalPages();

                                   for(int i =0; i < response.getResults().size(); i++){
                                       rankingMovieAdapter.addData(response.getResults().get(i));
                                       Log.e("MovieList response", response.getResults().get(i).getTitle());

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
