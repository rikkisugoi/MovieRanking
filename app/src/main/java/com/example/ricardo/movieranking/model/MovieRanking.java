package com.example.ricardo.movieranking.model;

/**
 * Created by Ricardo on 12/12/2017.
 */

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieRanking {

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("results")
    @Expose
    private List<DetailsMovieRanking> results = null;

    public Integer getPage() {
        return page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public List<DetailsMovieRanking> getResults() {
        return results;
    }

}