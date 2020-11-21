package app.project.wepost.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.FileOutputStream;

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
    Context context;

    public MyPostAdatper(@NonNull FirebaseRecyclerOptions<Posts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull final Posts model) {

        String profileImage = model.getAuthorProfile();
        String postPicture = model.getPostImage();
        String postDatetime = model.getcreatedDate() + " " +model.getcreatedTime();;

        String postsId = getRef(position).getKey();
        Log.d("TAG", "onBindViewHolder: => " + postsId);

        if (!TextUtils.isEmpty(postPicture)){
            holder.postImage.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(model.getPostImage()).into(holder.postImage);
        Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(holder.authorImage);
        holder.authorName.setText(model.getauthorName());
        holder.postDate.setText(postDatetime);

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
//                BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.postImage.getDrawable();
//
//                if (bitmapDrawable == null) {
//                    //post sans image
//                    shareOnlyText(model.getBody());
//                } else {
//                    //post avec image
//                    Bitmap bitmap = bitmapDrawable.getBitmap();
//                    shareTextAndImage(model.getBody(), bitmap);
//                }
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

//    private void shareTextAndImage(String body, Bitmap bitmap) {
//        //on stock l'image dans le cache et prendre l'uri du l'image
//        Uri imageUri = saveImageToShare(bitmap);
//
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Destinataire"); //cas de partage vers un mail app
//        shareIntent.setType("image/png");
//        context.startActivity(Intent.createChooser(shareIntent, "Partage via"));
//    }
//
//    private Uri saveImageToShare(Bitmap bitmap) {
//        File imageFolder = new File(context.getCacheDir(),"images");
//        Uri uri = null;
//        try {
//            // si le dossier n'existe pas, on en crée
//             imageFolder.mkdir();
//
//             File file = new File(imageFolder, "shared_image.png");
//             FileOutputStream stream = new FileOutputStream(file);
//             bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//             stream.flush();
//             stream.close();
//
//             uri = FileProvider.getUriForFile(context,"app.project.wepost.fileprovider", file);
//
//        } catch (Exception e) {
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//        return uri;
//    }
//
//    private void shareOnlyText(String body) {
//        //Share intent
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.setType("text/plain");
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Destinataire"); //cas de partage vers un mail app
//        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
//        context.startActivity(Intent.createChooser(shareIntent, "Partage via"));
//
//    }
//
//


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
        private TextView postContent;
        private ImageView postImage;
        private TextView likesField;
        private TextView shareField;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            authorImage = itemView.findViewById(R.id.author_image);
            authorName = itemView.findViewById(R.id.author_name);
            postContent = itemView.findViewById(R.id.post_body);
            postImage = itemView.findViewById(R.id.post_image);
            postDate = itemView.findViewById(R.id.post_date);

            likesField = itemView.findViewById(R.id.likes_field);
            shareField = itemView.findViewById(R.id.share_field);
        }
    }
}
