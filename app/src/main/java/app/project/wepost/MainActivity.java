package app.project.wepost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private ImageView addPost;
    private ImageView home;
    private Toolbar hToolbar;
    private FloatingActionButton toCreatePostBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(hToolbar);
        getSupportActionBar().setTitle("Accueil");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        addPost = findViewById(R.id.add_post);
        home = findViewById(R.id.home);

        toCreatePostBtn = findViewById(R.id.toCreatePostfloatingButton);
        addPost.setOnClickListener(this);
        home.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        toCreatePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToCreateNewPostActivity();
            }
        });

        String newPostContent = getIntent().getStringExtra("New_Post");
        TextView showNewPostContent = findViewById(R.id.show_new_post);
        showNewPostContent.setText(newPostContent);
    }

    private void GoToCreateNewPostActivity() {
        Intent gotToCreateNewPostIntent = new Intent(this, NewPost.class);
        startActivity(gotToCreateNewPostIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home :
                Toast.makeText(this, "Acceuil", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile :
                Toast.makeText(this, "Profil", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_add_post :
                Toast.makeText(this, "Ajouter une publicatioin", Toast.LENGTH_SHORT).show();
                GoToCreateNewPostActivity();
                break;
            case R.id.nav_log_out :
                Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            break;
            case R.id.add_post:
                Toast.makeText(this, "Post", Toast.LENGTH_SHORT).show();
                break;
            case R.id.toCreatePostfloatingButton:
                Toast.makeText(this, "Créer un nouveau post", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_home:
                Toast.makeText(this, "Accueil !", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}