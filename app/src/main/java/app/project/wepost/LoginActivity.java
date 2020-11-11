package app.project.wepost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private TextView recoverPassword;

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
        recoverPassword = findViewById(R.id.recover_password);
        loginBtn = findViewById(R.id.sign_in_btn);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegisterActivity();
            }
        });

        recoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPasswordInterface();
            }
        });

        loginBtn.setOnClickListener(this);

    }

    private void recoverPasswordInterface() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Réinitialisation de mot de passe");

        //On crée un conteneur
        LinearLayout linearLayout = new LinearLayout(this);
        //les champs du conteneur
        final EditText emailField = new EditText(this);
        emailField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailField.setHint("Email");
        emailField.setMinEms(16);

        linearLayout.addView(emailField);
        linearLayout.setPadding(10, 10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Réinitialiser", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = emailField.getText().toString();
                if (TextUtils.isEmpty(mail)){
                    Toast.makeText(LoginActivity.this, "Ce champ est obligatoire", Toast.LENGTH_SHORT).show();
                } else {
                    sendResetEmail(mail);
                    dialog.dismiss();

                }
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void sendResetEmail(String emailAdress) {
        loadingBar.setMessage("Envoie du mailde réinitialisation en cours...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        mAuth.sendPasswordResetEmail(emailAdress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                   loadingBar.dismiss();
                   Toast.makeText(LoginActivity.this, "Email de réinitialisation envoyé", Toast.LENGTH_SHORT).show();

                } else {
                    final String message = task.getException().getMessage().toString();
                    loadingBar.dismiss();
                    Log.d("Réinitialisation de password", "onComplete: Erreur" + message);
                }
            }
        });
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
                    if(task.isSuccessful()) {
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