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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;
import java.util.HashMap;

import app.project.wepost.Models.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {


    private final String TAG = "Update profile";
    private CircleImageView profileImage;
    private EditText username;
    private EditText fullname;
    private Button saveBtn;


    private String currentUserId;
    private DatabaseReference userDb;
    private StorageReference userProfileRef;
    private FirebaseAuth  mAuth;

    private ProgressDialog loadingBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Toolbar personnalisation
        toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Editer profil");


        loadingBar = new ProgressDialog(this);

        profileImage = findViewById(R.id.edited_profile_image);
        username = findViewById(R.id.edited_username);
        fullname = findViewById(R.id.edited_fullname);
        saveBtn = findViewById(R.id.edited_sav_btn);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userProfileRef = FirebaseStorage.getInstance().getReference().child("Profile image");
        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("username") && dataSnapshot.hasChild("fullname")) {
                    //Récupération des données
                    String uname = dataSnapshot.child("username").getValue().toString();
                    String fname = dataSnapshot.child("fullname").getValue().toString();
                    //On charge les valeurs par défaut des EditText avec les valeurs dans la bd
                    username.setText(uname);
                    fullname.setText(fname);
                }
                if (dataSnapshot.hasChild("profilePicture")) {
                    //Récupération du line
                    String pImage = dataSnapshot.child("profilePicture").getValue().toString();
                    //chargement de l'image
                    Picasso.get().load(pImage).placeholder(R.drawable.profile).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updapteUserInformation();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            sendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null ) {
            //Récupération de l'URI de l'image uploadée
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                //définition du chemin de d'accès à l'image
                final StorageReference filepath = userProfileRef.child(currentUserId + ".jpg");

                //chargement de l'image et surveillance du sauvegarde
                filepath.putFile(resultUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                long progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                Log.d(TAG, "onProgress: " + progress +"%");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String profileImageUri = uri.toString();

                                userDb.child("profilePicture").setValue(profileImageUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(EditProfileActivity.this, "Profile bien défini", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onComplete: Sauvegarde dans la bdd avec succès");
                                        } else {
                                            String message = task.getException().getMessage();
                                            Log.d(TAG, "onComplete: " + message);
                                        }
                                    }
                                });
                                Log.d(TAG, "onSuccess: " + profileImageUri);
                            }
                        });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: Erreur => " + error);
            }
        }
    }


    private void updapteUserInformation() {
        loadingBar.setMessage("Mise à jour du profil en cours");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
        String uname = username.getText().toString();
        String fname = fullname.getText().toString();

        if (TextUtils.isEmpty(uname)) {
            Toast.makeText(this, "Rensegnnez votre pseudo", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(fname)) {
            Toast.makeText(this, "Renseignez otre nom", Toast.LENGTH_SHORT).show();
        } else {
            User user = new User();
            user.setUpdateAt(new Date().toString());

            HashMap usermap = new HashMap();
            usermap.put("username", uname);
            usermap.put("fullname", fname);
            usermap.put("updateAt", user.getUpdateAt());
            userDb.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        Toast.makeText(EditProfileActivity.this, "Profil mis à jour", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Log.d(TAG, "onComplete: Erreur => " + message);
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}