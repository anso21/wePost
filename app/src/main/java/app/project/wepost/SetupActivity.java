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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;

import app.project.wepost.Models.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText username;
    private EditText fullName;
    private CircleImageView profileImage;
    private String email;
    private Button saveBtn ;

    private ProgressDialog loadingBar;

    private  FirebaseAuth mAuth;
    private DatabaseReference myDatabase;
    private String currentUserId;
    //private StorageReference userProfileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        username = findViewById(R.id.setup_username);
        fullName = findViewById(R.id.setup_fullname);
        profileImage = findViewById(R.id.setup_profile_image);
        email = getIntent().getStringExtra("userEmail");
        saveBtn = findViewById(R.id.sav_btn);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        myDatabase = FirebaseDatabase.getInstance().getReference();
        //userProfileRef = FirebaseStorage.getInstance().getReference().child("Profile image");

        loadingBar = new ProgressDialog(this);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
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
            Uri imageUri = data.getData();

            /*StorageReference filepath = userProfileRef.child(currentUserId + ".jpg");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SetupActivity.this, "Profile bien sauvegardé", Toast.LENGTH_SHORT).show();
                        //String downloadUri = task.getResult().getDownloadUrl().toString();
                    }
                }
            });*/

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
            writeNewUser(currentUserId, uName, uFullname , email);
        }
    }

    private void writeNewUser(String userId, String name, String fullname, String email) {

        loadingBar.setTitle("Sauvegarde en cours...");
        loadingBar.setMessage("Patientez un instant");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullname);
        user.setUsername(name);
        user.setId(userId);

        myDatabase.child("users").child(userId).setValue(user).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "onComplete: Success");
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