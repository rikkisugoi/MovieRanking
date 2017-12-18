package com.example.ricardo.movieranking.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ricardo.movieranking.R;
import com.example.ricardo.movieranking.adapter.RankingMovieAdapter;
import com.example.ricardo.movieranking.model.Configuration;
import com.example.ricardo.movieranking.model.MovieRanking;
import com.example.ricardo.movieranking.service.MovieDBService;
import com.example.ricardo.movieranking.service.ServiceFactory;

import org.w3c.dom.Text;

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
    private Configuration configurationResponse;

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


        final Observable<MovieRanking> popularRanking =
                movieDBService.getTopRatedMovies(MovieDBService.SERVICE_API_KEY, "pt-BR", 1);

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

    public static interface ClickListener{
        public void onClick(View view,int position);
    }


    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
