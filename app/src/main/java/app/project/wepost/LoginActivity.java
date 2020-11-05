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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Connexion au compte";
    private EditText username;
    private  EditText password;
    private TextView registerLink;
    private Button loginBtn ;

    ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        registerLink = findViewById(R.id.goto_register_activity);
        loginBtn = findViewById(R.id.sign_in_btn);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegisterActivity();
            }
        });
        loginBtn.setOnClickListener(this);
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

    private void sendToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        //finish();
    }

    @Override
    public void onClick(View v) {
        allowUserToLigIn();
    }

    private void allowUserToLigIn() {
        String uEmail = username.getText().toString();
        String uPassword = password.getText().toString();

        if (TextUtils.isEmpty(uEmail)) {
            Toast.makeText(this, "L'email est obligatoire", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(uPassword)) {
            Toast.makeText(this, "Votre mot de passe s'il vous plait", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Connection en cours...");
            loadingBar.setMessage("Vous allez être dirigé vers la page d'acceuil.");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(uEmail, uPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        Log.d(TAG, "Succès");
                        Toast.makeText(LoginActivity.this, "Vous êtes connecté",
                                Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }  else {
                        Log.d(TAG, "Erreur de connexion");
                        Toast.makeText(LoginActivity.this, "Connection échouée",
                                Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}