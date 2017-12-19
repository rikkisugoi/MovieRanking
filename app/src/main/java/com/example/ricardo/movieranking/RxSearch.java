package com.example.ricardo.movieranking;

import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by Ricardo on 19/12/2017.
 *
 * Essa classe tem como função escutar o campo
 * do elemento SearchView, para disparar
 * as chamadas com Debounce
 */

public class RxSearch {

    public static Observable<String> fromSearchView(@NonNull final SearchView searchView) {
        final BehaviorSubject<String> subject = BehaviorSubject.create("");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                subject.onCompleted();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                subject.onNext(newText);
                return true;
            }
        });

        return subject;
    }
}

