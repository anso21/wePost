package app.project.wepost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.project.wepost.Models.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String  TAG = "Création de compte";

    private EditText emailAddress;
    private EditText password;
    private EditText confirmPassword;
    private Button registerBtn;

    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Créer un compte");


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        emailAddress = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confirmPassword = findViewById(R.id.confirm_password);
        registerBtn = findViewById(R.id.sign_up_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            sendUserLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("TAG", "onStart: "+ currentUser);

        if (currentUser != null) {
            sendUserToMainActivity();
        }
    }


    private void createNewAccount() {
        String userEmail = emailAddress.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(RegisterActivity.this, "Veuillez renseigner votre adresse email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(RegisterActivity.this, "Définissez votre mot de passe", Toast.LENGTH_SHORT).show();
        }   else if (TextUtils.isEmpty(userConfirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Confirmez votre mot de passe", Toast.LENGTH_SHORT).show();
        } else if (!userPassword.equals(userConfirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setMessage("Création de compte en cours...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendUserToSetupActivity();
                        Log.d(TAG, "Succès");
                        Toast.makeText(RegisterActivity.this, "Votre compte a été créé av   ec succès.fournissez vos informations de profil",
                                Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        Log.d(TAG, "Erreur de création");
                        String message = task.getException().getMessage().toString();
                        Log.d(TAG, "onComplete: Erreur ->" + message);
                        Toast.makeText(RegisterActivity.this, message,
                                Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void sendUserLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}