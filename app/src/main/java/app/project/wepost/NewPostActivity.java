package app.project.wepost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import app.project.wepost.Models.Posts;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewPostActivity extends AppCompatActivity {

    private static final int Gallery_Pick = 1;

    private EditText postContent;
    private TextView addImage;
    private ImageView uploadedImage;
    private Button publishPostBtn;

    private Uri imageUri;
    private String newPostContent;
    private String currentUserId;
    private String postId;

    private ProgressDialog loadingBar;
    private Toolbar toolbar;

    private StorageReference postsImagesReference;
    private DatabaseReference userPostContentDatabase;
    private FirebaseAuth mAuth;

    private String saveCurrentDate,saveCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        toolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Ajouter une publication");

        loadingBar = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        postsImagesReference = FirebaseStorage.getInstance().getReference().child("Posts images");
        userPostContentDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");

        //Récupération des éléments du formulaire de création de post NewPost

        postContent = findViewById(R.id.post_content);

        publishPostBtn = findViewById(R.id.publish_post_btn);
        publishPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateNewPostContent();
            }
        });

        addImage = findViewById(R.id.add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        uploadedImage = findViewById(R.id.img_uploaded);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            sendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    //Méthode pour pouvoir aller à la page d'acueil
    private void sendUserToMainActivity() {
        Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //Methode pour accéder à la galerie de l'utilisateur afin qu'il puisse choisir une photo à publier
    public void OpenGallery() {
        Intent goToGalleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(goToGalleryIntent, Gallery_Pick);
    }

    //Récupération et affichage de l'image choisie
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            uploadedImage.setImageURI(imageUri);
            uploadedImage.setVisibility(View.VISIBLE);
        }
    }

    //Méthode pour vérifier s'il y a au mmoins quelque à publier
    private void validateNewPostContent(){
        newPostContent = postContent.getText().toString().trim();
        if (TextUtils.isEmpty(newPostContent) && imageUri == null) {
            Toast.makeText(this, "Choisissez quelque chose à publier!", Toast.LENGTH_SHORT).show();
        } else {
            Calendar forCurentDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(forCurentDate.getTime());

            Calendar forCurentTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = currentTime.format(forCurentTime.getTime());
            postId = currentUserId + "-" + saveCurrentDate;

            if (imageUri != null){
                savePostImageIntoStorage(imageUri,postId);
            }
            if (!TextUtils.isEmpty(newPostContent)) {

                savePostIntoPostRef(postId,currentUserId,newPostContent,saveCurrentDate, saveCurrentTime);
            }
            Toast.makeText(NewPostActivity.this,"Publication réussie",Toast.LENGTH_SHORT).show();
            sendUserToMainActivity();
        }
    }

    private void savePostIntoPostRef(String postid,String userId, String body,String createdDate, String createdTime) {
        Posts post = new Posts(postid,userId, body, createdDate,createdTime);
        HashMap postMap = post.postsMap();
        userPostContentDatabase.child(currentUserId).child(postid).updateChildren(postMap).addOnCompleteListener(this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
               if (!task.isSuccessful()){
                    String message = task.getException().getMessage();
                    Log.d("PostTag", "onComplete: Post => " + message);
                }
            }
        });
    }

    private void savePostImageIntoStorage(Uri imageUri, final String postId) {
        final StorageReference imagePath = postsImagesReference.child(postId +".jpg");
        imagePath.putFile(imageUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                long progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("Image Download", "onProgress: " + progress +"%");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String postImageUri = uri.toString();
                        userPostContentDatabase.child(currentUserId).child(postId).child("postImage").setValue(postImageUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Saving ImageUri", "onComplete: Success");
                                } else {
                                    String message = task.getException().getMessage().toString();
                                    Log.d("Error", "onComplete: " + message);
                                }
                            }
                        });
                        Log.d("image uri", "onSuccess: " + postImageUri);
                    }
                });
            }
        });
    }
}
