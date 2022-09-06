package com.example.instagramclone;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.ViewHolder>{
    public static Context context;
    List<Post> posts;



    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);


        holder.bind(post);
    }



    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ettImage,imageView;
        TextView tName ,twStar , description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ettImage = itemView.findViewById(R.id.ettImage);
            imageView = itemView.findViewById(R.id.imageView);
            tName = itemView.findViewById(R.id.tName);
            twStar = itemView.findViewById(R.id.twStar);
            description = itemView.findViewById(R.id.description);


        }

        public void bind(Post post) {
            tName.setText(post.getUser().getUsername());
            description.setText(post.getDescription());
            ParseFile imaj = post.getImage();

            if (imaj != null ) {

                Glide.with(context)
                        .load(imaj)
                        .fitCenter()
                        .into(ettImage);


            }

        }
    }


}