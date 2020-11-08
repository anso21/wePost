package app.project.wepost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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

import java.net.URI;
import java.util.Date;
import java.util.HashMap;

import app.project.wepost.Models.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText username;
    private EditText fullName;
    private CircleImageView profileImage;
    private String email;
    private Button saveBtn ;

    private final String TAG = "Debug";

    private ProgressDialog loadingBar;

    private DatabaseReference userDatabase;
    private StorageReference userProfileRef;
    private  FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        username = findViewById(R.id.setup_username);
        fullName = findViewById(R.id.setup_fullname);
        profileImage = findViewById(R.id.setup_profile_image);

        saveBtn = findViewById(R.id.sav_btn);

        //innitialisation et prise de l'ID de l'utilisateur courant
        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();
        currentUserId = mAuth.getCurrentUser().getUid();

        //innitialisation de la base de données de l'user
        userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        //innitalisation du storage tout en créant le dossier qui contiendra les images des users
        userProfileRef = FirebaseStorage.getInstance().getReference().child("Profile image");

        loadingBar = new ProgressDialog(this);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        //ecoute de la base de données
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profilePicture")) {
                        //récupération du lien du profile
                        String image = dataSnapshot.child("profilePicture").getValue().toString();
                        //displaying de l'image
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImage);;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSetupInformations();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null ) {
            //Récupération de l'URI de l'image uploadée
            Uri imageUri = data.getData();

            //définition du chemin de d'accès à l'image
            final StorageReference filepath = userProfileRef.child(currentUserId + ".jpg");

            //chargement de l'image et surveillance du sauvegarde
            filepath.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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
                            userDatabase.child("profilePicture").setValue(profileImageUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SetupActivity.this, "Profile bien défini", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onComplete: Saugarde dans la bdd avec succès");
                                    } else {
                                        String message = task.getException().getMessage().toString();
                                        Log.d(TAG, "onComplete: " + message);
                                    }
                                }
                            });
                            Log.d(TAG, "onSuccess: " + profileImageUri);
                        }
                    });
                }
            });
        }
    }

    private void saveSetupInformations() {

        String uName = username.getText().toString();
        String uFullname = fullName.getText().toString();

        if (TextUtils.isEmpty(uName)) {
            Toast.makeText(SetupActivity.this, "Définissez un pseudo", Toast.LENGTH_SHORT).show();
        } if (TextUtils.isEmpty(uFullname)) {
            Toast.makeText(SetupActivity.this, "Votre nom complet s'il vout plait", Toast.LENGTH_SHORT).show();
        } else {
            writeNewUser(uName, uFullname , email);
        }
    }

    private void writeNewUser(String name, String fullname, String email) {

        loadingBar.setTitle("Sauvegarde en cours...");
        loadingBar.setMessage("Patientez un instant");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        //intanciation de User
        User user = new User(currentUserId, name, fullname, email);
        HashMap userMap = user.toMap();
        userDatabase.updateChildren(userMap).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Success");
                    Toast.makeText(SetupActivity.this, "Données bien enregistrées", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    sendUserToMainActivity();
                } else {
                    String message = task.getException().getMessage();
                    Log.d("TAG", "onComplete: Error " + message);
                    Toast.makeText(SetupActivity.this, "Erreur d'enregistrement", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}