package com.example.ricardo.movieranking.service;

import com.example.ricardo.movieranking.model.Configuration;
import com.example.ricardo.movieranking.model.DetailsMovie;
import com.example.ricardo.movieranking.model.MovieRanking;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Ricardo on 12/12/2017.
 */

public interface MovieDBService {

    String SERVICE_ENDPOINT = "https://api.themoviedb.org/3/";
    String SERVICE_API_KEY = "2efea6668b02c34eedcbbf7d85e32607";

    @GET("/3/configuration?")
    Observable<Configuration> getConfiguration(@Query("api_key") String apiKey);

    @GET("movie/top_rated?")
    Observable<MovieRanking>  getTopRatedMovies(@Query("api_key") String apiKey,
                                                @Query("language") String language,
                                                @Query("page") int page);

    @GET("movie/{movie_id}?")
    Observable<DetailsMovie>  getMovieDetails(@Path("movie_id") int movieId,
                                              @Query("api_key") String apiKey,
                                              @Query("language") String language);

}
