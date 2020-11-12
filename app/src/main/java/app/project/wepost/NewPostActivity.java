package app.project.wepost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView userNewPostImg;
    private TextView newPostUserName;
    private EditText newCreatedPostContent;
    private TextView newPostAddImgBtn;
    private Button publishNewCreatedPost;
    private static final int Gallery_Pick = 1;
    private Uri imageUri;
    private ImageView newPostImgAdded;
    private String newPostContent;

    private ProgressDialog loadingBar;
    private Toolbar toolbar;

    private StorageReference postsImagesReference;
    private DatabaseReference userPostContentDataBase;
    private FirebaseAuth mAuth;

    private String userId,saveCurrentDate,saveCurrentTime,postRandomName, postImageSavedUrl;

    public NewPostActivity() {
    }

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
        postsImagesReference = FirebaseStorage.getInstance().getReference();
        userPostContentDataBase = FirebaseDatabase.getInstance().getReference().child("PostContent");

        //Récupération des éléments du formulaire de création de post NewPost

        newCreatedPostContent = findViewById(R.id.new_created_post_content);

        publishNewCreatedPost = findViewById(R.id.publish_new_post_btn);
        publishNewCreatedPost.setOnClickListener(this);

        newPostUserName = findViewById(R.id.new_post_user_name);
        newPostAddImgBtn = findViewById(R.id.new_post_add_img_btn);
        newPostAddImgBtn.setOnClickListener(this);
        newPostImgAdded = findViewById(R.id.new_post_img_added);

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
        Intent goToGalleryIntent = new Intent();
        goToGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        goToGalleryIntent.setType("image/*");
        startActivityForResult(goToGalleryIntent, Gallery_Pick);
    }

    //Récupération et affichage de l'image choisie
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            newPostImgAdded.setImageURI(imageUri);
        }
    }

    //Méthode pour vérifier s'il y a au mmoins quelque à publier
    private void validateNewPostContent(){
        newPostContent = newCreatedPostContent.getText().toString();
        if(newPostContent.isEmpty() && imageUri == null){
            Toast.makeText(this, "Choisissez quelque chose à publier!", Toast.LENGTH_SHORT).show();
        }
        if (!newPostContent.isEmpty()){
            saveNewPostContent();
        }
        if (imageUri != null){
            saveNewPostContent();
        }
    }

    //Méthode pour récupérer le contenu du nouveau post et le sauvegarder dans la base de données
    //TODO sauvegarder le contenu textuel du post
    public void saveNewPostContent(){
        loadingBar.setTitle("En cours de publication...");
        loadingBar.setMessage("Veuillez patientez !");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        Calendar forCurentDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(forCurentDate.getTime());

        Calendar forCurentTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(forCurentTime.getTime());

        postRandomName = saveCurrentDate + "-" + saveCurrentTime;

        StorageReference imagePath = postsImagesReference.child("Posts_Images").child(imageUri.getLastPathSegment() + "-" + postRandomName + ".jpg");

        imagePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(NewPostActivity.this,"Image enregistré avec succès!",Toast.LENGTH_SHORT).show();
                    savePostInformationToDataBase();
                    loadingBar.dismiss();
                    sendUserToMainActivity();
                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(NewPostActivity.this,"Error occured:" + message,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void savePostInformationToDataBase() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.new_post_add_img_btn:
                OpenGallery();//Accès à la gallerie de l'utilisateur afin qu'il puisse choir la photo à publier
                Toast.makeText(this, "Choisir une image !", Toast.LENGTH_SHORT).show();
                break;

            case R.id.publish_new_post_btn:
                validateNewPostContent();
                break;
        }
    }
}