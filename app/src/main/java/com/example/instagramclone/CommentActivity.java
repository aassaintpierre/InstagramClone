package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagramclone.R;
import com.example.instagramclone.Post;
import com.example.instagramclone.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class CommentActivity extends AppCompatActivity {

    public static final String TAG = "CommentActivity";
    private TextView tvUserName;
    private ImageView profileImage;
    private EditText etComment;
    private Button btnComment;
    Context context;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ImageButton btnBack = findViewById(R.id.ic_back);
        tvUserName = findViewById(R.id.tvUserName);
        profileImage = findViewById(R.id.profileImage);
        etComment = findViewById(R.id.etComment);
        btnComment = findViewById(R.id.btnComment);
        post = Parcels.unwrap(getIntent().getParcelableExtra(MainActivity.POST));
        ParseUser currentUser = ParseUser.getCurrentUser();
        tvUserName.setText(currentUser.getUsername());

//        Glide.with(CommentActivity.this)
//        .load(currentUser.getParseFile(User.KEY_PROFILE_IMAGE)
//        .getUrl()).into(profileImage);




        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CommentActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(i, 0);
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etComment.getText().toString();
                if (comment.isEmpty()){
                    Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                SaveComment(comment, currentUser);
            }
        });
    }

    private void SaveComment(String description, ParseUser currentUser) {
        ParseObject object = ParseObject.create("Comment");
        object.put("user", currentUser);
        object.put("comment", description);
        post.setListComment(object);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(context, "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }

                etComment.setText("");
            }
        });
    }
}