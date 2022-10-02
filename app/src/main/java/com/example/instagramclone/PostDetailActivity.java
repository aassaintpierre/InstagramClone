package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagramclone.R;
import com.example.instagramclone.CommentAdapter;
import com.example.instagramclone.PostAdapter;
import com.example.instagramclone.TimeFormatter;
import com.example.instagramclone.Comment;
import com.example.instagramclone.Post;
import com.example.instagramclone.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    protected ImageView ivProfileImage, ivPost;
    TextView tvUsername, tvDescription, tvDate, tvLikeDetail;
    ImageButton imgBtnSettings, imgBtnHeart, imgBtnComment, imgBtnSend, imgBtnSave;
    RecyclerView rvComment;
    protected List<String> commentsParse;
    protected List<Comment> comments;
    protected CommentAdapter adapter;
    Context context;
    public static int like;
    public static ArrayList<String> listUserLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ParseUser currentUser = ParseUser.getCurrentUser();



        ivProfileImage = findViewById(R.id.ivProfileImageDetail);
        ivPost = findViewById(R.id.ivPostDetail);
        tvUsername = findViewById(R.id.tvUsernameDetail);
        tvDate = findViewById(R.id.tvDateDetail);
        tvLikeDetail = findViewById(R.id.tvLikeDetail);
        tvDescription = findViewById(R.id.tvDescriptionDetail);
        imgBtnSettings = findViewById(R.id.imgBtnSettingsDetail);
        imgBtnHeart = findViewById(R.id.imgBtnHeartDetail);
        imgBtnComment = findViewById(R.id.imgBtnCommentDetail);
        imgBtnSend = findViewById(R.id.imgBtnSendDetail);
        imgBtnSave = findViewById(R.id.imgBtnSaveDetail);
        rvComment = findViewById(R.id.rvComment);

        Post post = Parcels.unwrap(getIntent().getParcelableExtra(MainActivity.POST));
        post.getListComment();
        try {
            commentsParse = Comment.fromJsonArray(post.getListComment());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get list user who liked this post
        try {
            listUserLike = Post.fromJsonArray(post.getListLike());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set color for heart
        try {
            if (listUserLike.contains(currentUser.getObjectId())) {
                Drawable drawable = ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.red_heart);
                imgBtnHeart.setImageDrawable(drawable);
            }

        else {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.heart);
            imgBtnHeart.setImageDrawable(drawable);
        }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        comments = new ArrayList<>();
        adapter = new CommentAdapter(context, comments);
        rvComment.setAdapter(adapter);
        rvComment.setLayoutManager(new LinearLayoutManager(context));

        tvDescription.setText(post.getDescription());
        tvLikeDetail.setText(String.valueOf(post.getNumberLike()) + " likes");
        tvDate.setText(TimeFormatter.getTimeStamp(post.getCreatedAt().toString()));


        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(PostDetailActivity.this).load(image.getUrl()).transform(new RoundedCorners(30)).into(ivPost);
        }

        imgBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PostDetailActivity.this, CommentActivity.class);
                i.putExtra("post", Parcels.wrap(post));
                startActivity(i);
            }

        });



        imgBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PostDetailActivity.this, CommentActivity.class);
                i.putExtra("post", Parcels.wrap(post));
                startActivity(i);
            }
        });

        imgBtnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like = post.getNumberLike();
                int index;

                if (!listUserLike.contains(currentUser.getObjectId())){
                    Drawable drawable = ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.red_heart);
                    imgBtnHeart.setImageDrawable(drawable);
                    like++;
                    index = -1;

                }else {
                    Drawable drawable = ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.heart);
                    imgBtnHeart.setImageDrawable(drawable);
                    like--;
                    index = listUserLike.indexOf(currentUser.getObjectId());
                }

                tvLikeDetail.setText(String.valueOf(like) + " likes");
                saveLike(post, like, index, currentUser);
            }
        });

        queryPost();
    }

    protected void queryPost() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereContainedIn("objectId", commentsParse);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> commentList, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting Posts", e);
                    Toast.makeText(context, "Issue with getting Posts", Toast.LENGTH_SHORT).show();
                    return;
                }

                comments.addAll(commentList);
                adapter.notifyDataSetChanged();

            }
        });
    }


    private void saveLike(Post post, int like, int index, ParseUser currentUser) {
        post.setNumberLike(like);

        if (index == -1){
            post.setListLike(currentUser);
            listUserLike.add(currentUser.getObjectId());
        }else {
            listUserLike.remove(index);
            post.removeItemListLike(listUserLike);
        }

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(context, "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, listUserLike.toString());
            }
        });


    }

}








