package app.project.wepost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import app.project.wepost.Models.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText username;
    private CircleImageView profileImage;
    private Button saveBtn ;

    private  FirebaseAuth mAuth;
    private DatabaseReference myDatabase;
    ProgressDialog loadingBar;

    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        username = findViewById(R.id.setup_username);
        profileImage = findViewById(R.id.setup_profile_image);
        saveBtn = findViewById(R.id.sav_btn);
        email = getIntent().getStringExtra("userEmail");

        mAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSetupInformations();
            }
        });
    }

    private void saveSetupInformations() {
        String uName = username.getText().toString();
        if (TextUtils.isEmpty(uName)) {
            Toast.makeText(SetupActivity.this, "Pseudo obligatoire", Toast.LENGTH_SHORT).show();
        } else {
            final String currentUserId = mAuth.getCurrentUser().getUid();
            writeNewUser(currentUserId, uName, email);
        }
    }

    private void writeNewUser(String userId, String name, String email) {

        loadingBar.setTitle("Sauvegarde en cours...");
        loadingBar.setMessage("Patientez un instant");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        User user = new User(name, email);
        myDatabase.child("users").child(userId).setValue(user).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "onComplete: Success");
                    sendUserToMainActivity();
                    Toast.makeText(SetupActivity.this, "Données bien enregistrées", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
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