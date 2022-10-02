package com.example.instagramclone;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class BlankFragment extends Fragment {
    public  static  final String TAG ="BlankFragment" ;
    RecyclerView recycler;
    List<Post> postList;
    protected  PostAdapter adapter;
    protected SwipeRefreshLayout swipeContainer;
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected Boolean isRefreshing = false;
    protected ProgressDialog pd;

    Context context;

     public BlankFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);



    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler = view.findViewById(R.id.recycler);
        postList = new ArrayList<>();

         adapter = new PostAdapter(getContext(),postList);
         recycler.setAdapter(adapter);
         recycler.setLayoutManager(new LinearLayoutManager(context));


        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"fetching new data!");
                isRefreshing = true;
                queryPost();
            }
        });

        postList=new ArrayList<>();

        adapter = new PostAdapter(getContext(),postList);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(context));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                //Load the next page
                queryPost();
            }
        };

        recycler.addOnScrollListener(scrollListener);

        // Initialize Progress Dialog
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);

//         Load timeline
         queryPost();
    }


    protected void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_KEY);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting Posts", e);
                    return;
                }


                for (Post post : posts){
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                postList.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });




}


}