package app.project.wepost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class NewPost extends AppCompatActivity implements View.OnClickListener {

    private Button toCreateNewPostBtn;
    private TextView addImg;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private EditText newCreatedPostContent;
    private ImageButton goBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        /*
        * Récupération des éléments du formulaire de création de post NewPost
        */
        toCreateNewPostBtn = findViewById(R.id.new_post_btn); //Récupération du bouton "Publier"
        toCreateNewPostBtn.setOnClickListener(this);
        addImg = findViewById(R.id.new_post_add_img); //Récupération de la photo de l'utilisateur
        addImg.setOnClickListener(this);
        newCreatedPostContent = findViewById(R.id.new_post_content); //Récupération du contenu du nouveau post de l'utilateur
        goBackBtn = findViewById(R.id.go_back_btn);
        goBackBtn.setOnClickListener(this);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

    }

    /*
    * Méthode pour contrôler l'action sur l'item acueil de la barre de navition
    * Quand click dessus on doit pouvoir ramener l'utilisateur à la page d'acueil
    */
    private void UserMenuSelector(MenuItem item) {
        if (item.getItemId() == R.id.nav_home ){
            Toast.makeText(this, "Aller à l'acueil !", Toast.LENGTH_SHORT).show();
            GoToHomeActivity(); //Appellle à la fonction pour le retour à la page d'acueil
        }
    }

    /*
    * Méthode pour pouvoir aller à la page d'acueil
    */
    private void GoToHomeActivity() {
        Intent goToHomeActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToHomeActivityIntent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_post_btn:
                //Passage du contenu de la nouvelle publication à la page d'acueil par un Intent pour pouvoir l'afficher
                Toast.makeText(this, "Publication du post !", Toast.LENGTH_SHORT).show();
                Intent toPublishNewPostIntent = new Intent(this, MainActivity.class);
                final String nPostContent = newCreatedPostContent.getText().toString();
                toPublishNewPostIntent.putExtra("New_Post", nPostContent);
                startActivity(toPublishNewPostIntent);
                break;
            case R.id.new_post_add_img:
                Toast.makeText(this, "Ajouter une photo !", Toast.LENGTH_SHORT).show();
                break;
            case R.id.go_back_btn: // L'action sur la flèche de retour de la barre supéreure pour retourner à l'acueil
                Toast.makeText(this, "Retour", Toast.LENGTH_SHORT).show();
                GoToHomeActivity();
                break;
        }

    }
}