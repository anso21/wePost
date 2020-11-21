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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

    DatabaseReference likesRef;
    DatabaseReference postsRef;
    DatabaseReference userRef;
    String currentUserId;
    String authorId;
    boolean likesChecker;
    
    public MyPostAdatper(@NonNull FirebaseRecyclerOptions<Posts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull final Posts model) {


        final String postsId = getRef(position).getKey();
        String postPicture = model.getPostImage();
        String postDatetime = model.getcreatedDate() + " " +model.getcreatedTime();;

        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //Récupération de l'id du posteur
        getAuthorInformations(postsId, model);
        //Récupération du nom et de l'image de l'auteur du post
        //getAuthorImageAndFullname(model,authorId);

        //Si le post contient une image , le champ d'image est visible
        if (!TextUtils.isEmpty(postPicture)){
            holder.postImage.setVisibility(View.VISIBLE);
        }

        //Chargement des données de posts
        Picasso.get().load(model.getPostImage()).into(holder.postImage);
        Picasso.get().load(model.getAuthorProfile()).placeholder(R.drawable.profile).into(holder.authorImage);
        holder.authorName.setText(model.getauthorName());

        holder.postDate.setText(postDatetime);
        holder.postContent.setText(model.getBody());

        checkLikeStatus(model, holder, postsId);

        holder.likesField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost(postsId);
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
    }

    private void getAuthorInformations(String postsId, final Posts model) {
        //Recupération de l'id de l'auteur
        postsRef.child(postsId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("userId")) {
                    authorId = snapshot.child("userId").getValue().toString();
                    Log.d("TAG", "onDataChange: =>" + authorId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Récupération du nom et de l'image de l'auteur du post
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (authorId != null) {
                    if (snapshot.child(authorId).hasChild("profilePicture")) {
                        String profilePicture = (String) snapshot.child(authorId).child("profilePicture").getValue();
                        model.setAuthorProfile(profilePicture);
                    }
                    if (snapshot.child(authorId).hasChild("fullname")){
                        String fullname = snapshot.child(authorId).child("fullname").getValue().toString();
                        model.setauthorName(fullname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void checkLikeStatus(final Posts model, final PostViewHolder holder, final String postsId) {

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int likes = (int) snapshot.child(postsId).getChildrenCount();

                //conditions sur les text de likes
                if (likes > 1) {
                    model.setLikes(likes + " j'aimes");
                } else {
                    model.setLikes(likes + " j'aime");
                }

                //condition sur le champ de likes

                if (snapshot.child(postsId).hasChild(currentUserId)) {
                    holder.likesField.setText("Déjà aimé");

                } else {
                    holder.likesField.setText("J'aime");
                }

                //Affichage des likes
                holder.likeCount.setText(model.getLikes());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void likePost(final String postId) {
        likesChecker = true;
        likesRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (likesChecker) {
                    if (snapshot.hasChild(currentUserId)) {
                        //dejà liké
                        likesRef.child(postId).child(currentUserId).removeValue();
                        likesChecker = false;
                    } else {
                        // pas encore liké
                        likesRef.child(postId).child(currentUserId).setValue(true);
                        likesChecker = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        private TextView likeCount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            authorImage = itemView.findViewById(R.id.author_image);
            authorName = itemView.findViewById(R.id.author_name);
            postContent = itemView.findViewById(R.id.post_body);
            postImage = itemView.findViewById(R.id.post_image);
            postDate = itemView.findViewById(R.id.post_date);
            likeCount = itemView.findViewById(R.id.like_count);

            likesField = itemView.findViewById(R.id.likes_field);
            shareField = itemView.findViewById(R.id.share_field);
        }
    }
}
