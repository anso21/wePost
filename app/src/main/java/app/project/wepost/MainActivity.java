package app.project.wepost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar hToolbar;

    private CircleImageView navigationProfileImage;
    private TextView navigationUserFullname;

    private  FirebaseAuth mAuth;
    private  String currentUserId;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //innitialisation de l'user
        mAuth = FirebaseAuth.getInstance();
        Log.d("UserId", "onCreate: " + currentUserId);

        //pointage de la table user dans la base de données
        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        hToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(hToolbar);
        getSupportActionBar().setTitle("Accueil");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.my_navigation_header);

        navigationProfileImage = navView.findViewById(R.id.profile_image);
        navigationUserFullname = navView.findViewById(R.id.nav_fullname);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        //récupération de l'user courant
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendUserToLoginActivity();
        } else {
            currentUserId = currentUser.getUid().toString();
            checkUserExistence(currentUserId);

            //Il faut vérifier dabord si les infos sur l'utilisateur ont été bien enregistrées avant de prendre les valeurs et afficher
            userDatabase.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        if (dataSnapshot.hasChild("profilePicture")) {
                            String profileUri = dataSnapshot.child("profilePicture").getValue().toString();
                            Picasso.get().load(profileUri).placeholder(R.drawable.profile).into(navigationProfileImage);
                        }

                        if (dataSnapshot.hasChild("fullname")) {
                            Log.d("TAG", "onDataChange: Chargement du nom");
                            String fullname = dataSnapshot.child("fullname").getValue().toString();
                            Log.d("TAG", "onDataChange: Fillname = " + fullname);
                            navigationUserFullname.setText(fullname);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    private void checkUserExistence(final String uID) {

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(uID)) {
                    sendUserToSetupActivity();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
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
                break;
            case R.id.nav_log_out :
                mAuth.signOut();
                sendUserToLoginActivity();
                break;
        }
    }

    /*@Override
    public void onClick(View v) {
        int i = v.getId();
        if(i==R.id.nav_home){
            Toast.makeText(this, "Aceuil", Toast.LENGTH_SHORT).show();
        } else if(i== R.id.nav_add_post) {
            Toast.makeText(this, "Ajouter un nouveau post", Toast.LENGTH_SHORT).show();
        }else if(i== R.id.nav_profile) {
            Toast.makeText(this, "Profil", Toast.LENGTH_SHORT).show();
        }else if(i== R.id.nav_log_out) {

        }
    }*/
}