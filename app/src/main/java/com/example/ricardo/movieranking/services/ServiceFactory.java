package com.example.ricardo.movieranking.services;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ricardo on 18/12/2017.
 *
 * Classe criada para gerar
 * Builders de Retrofit
 * que serão utilizados nas
 * chamadas de serviços REST
 */

public class ServiceFactory {
    public static <T> T createRetrofitService(final Class<T> paramClass, final String endPoint) {
        final Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(endPoint)
                .build();

        T createdRetrofit = retrofit.create(paramClass);

        return createdRetrofit;
    }
}
