package app.project.wepost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private  EditText password;
    private Button loginBtn ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.sign_in_btn);

        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String uName = username.getText().toString();
        String pass = password.getText().toString();

        if (uName.isEmpty()) {
            Toast.makeText(this, "Renseigner votre nom", Toast.LENGTH_SHORT).show();
        } else if (pass.isEmpty()) {
            Toast.makeText(this, "Resnseigner mot de passe", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
            //TODO vérification des données
        }

    }
}