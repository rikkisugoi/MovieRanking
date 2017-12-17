package com.example.ricardo.movieranking;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Ricardo on 12/12/2017.
 */

public interface MovieDBService {



    @GET("movie/popular?")
    Observable<MovieRanking>  getPopularMovies(@Query("api_key") String apiKey,
                                               @Query("language") String language,
                                               @Query("page") int page);
}
