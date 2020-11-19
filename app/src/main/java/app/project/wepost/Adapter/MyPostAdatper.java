package app.project.wepost.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import app.project.wepost.EditProfileActivity;
import app.project.wepost.MainActivity;
import app.project.wepost.Models.Posts;
import app.project.wepost.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostAdatper extends FirebaseRecyclerAdapter<Posts, MyPostAdatper.PostViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    DatabaseReference userRef, postRef;
    String userId;
    public MyPostAdatper(@NonNull FirebaseRecyclerOptions<Posts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull Posts model) {


        String profileImage = model.getAuthorProfile();
        String postPicture = model.getPostImage();
        String postsId = getRef(position).getKey();
        Log.d("TAG", "onBindViewHolder: => " + postsId);

        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postsId);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("userId")) {
                    userId = snapshot.child("userId").getValue().toString();
                    Log.d("TAG", "onDataChange: => " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        if (!TextUtils.isEmpty(postPicture)){
            holder.postImage.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(model.getPostImage()).into(holder.postImage);
        Picasso.get().load(model.getAuthorProfile()).placeholder(R.drawable.profile).into(holder.authorImage);
        holder.authorName.setText(model.getauthorName());
        holder.postDate.setText(model.getcreatedDate());
        holder.postTime.setText(model.getcreatedTime());
        holder.postContent.setText(model.getBody());

        holder.likesField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Aimer", Toast.LENGTH_SHORT).show();
            }
        });

        holder.shareField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Partager", Toast.LENGTH_SHORT).show();
            }
        });

//        holder.authorName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendUserToEditActivity(v.getContext());
//            }
//        });
//
//        holder.authorImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendUserToEditActivity(v.getContext());
//            }
//        });
    }

    private void sendUserToEditActivity(Context context) {
        Intent profileIntent = new Intent(context, EditProfileActivity.class);
        context.startActivity(profileIntent);
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_posts,parent,false);
        return new PostViewHolder(view);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView authorImage;
        private TextView authorName ;
        private TextView postDate;
        private TextView postTime;
        private TextView postContent;
        private ImageView postImage;
        private TextView likesField;
        private  TextView shareField;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            authorImage = itemView.findViewById(R.id.author_image);
            authorName = itemView.findViewById(R.id.author_name);
            postContent = itemView.findViewById(R.id.post_body);
            postImage = itemView.findViewById(R.id.post_image);
            postDate = itemView.findViewById(R.id.post_date);
            postTime = itemView.findViewById(R.id.post_time);

            likesField = itemView.findViewById(R.id.likes_field);
            shareField = itemView.findViewById(R.id.share_field);
        }
    }
}
