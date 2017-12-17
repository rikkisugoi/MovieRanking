package com.example.ricardo.movieranking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo on 15/12/2017.
 */

public class RankingMovieAdapter extends RecyclerView.Adapter<RankingMovieAdapter.ViewHolder>{

    private final ArrayList<DetailsMovieRanking> listMovieRanking;

    public RankingMovieAdapter() {
        super();
        listMovieRanking = new ArrayList<DetailsMovieRanking>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DetailsMovieRanking detailsMovieRanking = listMovieRanking.get(position);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void addData(DetailsMovieRanking response) {
        listMovieRanking.add(response);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.movieTitle);
        }
    }
}
