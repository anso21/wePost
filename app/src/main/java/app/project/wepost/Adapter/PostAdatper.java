package app.project.wepost.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import app.project.wepost.Models.Posts;
import app.project.wepost.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdatper extends RecyclerView.Adapter<PostAdatper.PostsHolder> {

    Context context;
    List<Posts> postsList;

    public PostAdatper(Context context, List<Posts> postsList) {
        this.context = context;
        this.postsList = postsList;
    }


    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_users_posts, parent, false);

        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsHolder holder, int position) {

        String body = postsList.get(position).getBody();
        String uId = postsList.get(position).getUserId();
        String postImage = postsList.get(position).getPostImage();
        String createdTime = postsList.get(position).getcreatedTime();
        String createdDate = postsList.get(position).getcreatedDate();
        
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class PostsHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView createdTime;
        private TextView createdDate;
        private TextView postBody;
        private CircleImageView profileImage;
        private ImageView postImage;

        public PostsHolder(@NonNull View itemView) {

            super(itemView);

            userName = itemView.findViewById(R.id.author_name);
            createdDate = itemView.findViewById(R.id.post_date);
            createdTime = itemView.findViewById(R.id.post_time);
            profileImage = itemView.findViewById(R.id.author_image);
            postBody = itemView.findViewById(R.id.post_body);
            postImage = itemView.findViewById(R.id.post_image);

        }
    }
}
