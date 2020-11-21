package app.project.wepost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import app.project.wepost.ui.HomeFragment;
import app.project.wepost.ui.NotificationFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Toolbar hToolbar;

    private CircleImageView navigationProfileImage;
    private TextView navigationUserFullname;

    BottomNavigationView.OnNavigationItemSelectedListener navListiner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.bottom_notification:
                    selectedFragment = new NotificationFragment();
                    break;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
    };
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private BottomNavigationView bottomNavigationView;
    private String profileUri;
    private String currentUserId;

    @Override
    protected void onStart() {
        super.onStart();
        //récupération de l'user courant
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendUserToLoginActivity();
        } else {
            currentUserId = currentUser.getUid();
            checkUserExistence(currentUserId);
            displayUserImageAndHisName(currentUserId);
        }
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
                selfIntent();
                break;
            case R.id.nav_profile :
                sendUserToEditActivity();
                break;
            case R.id.nav_add_post :
                sendUserToPostActivity();
                break;
            case R.id.nav_log_out :
                mAuth.signOut();
                sendUserToLoginActivity();
                break;
        }
    }


    //Fonctions utilisées
    private void displayUserImageAndHisName(String uID) {
        //Il faut vérifier dabord si les infos sur l'utilisateur ont été bien enregistrées avant de prendre les valeurs et afficher
        userDatabase.child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("profilePicture")) {
                        profileUri = dataSnapshot.child("profilePicture").getValue().toString();
                        Picasso.get().load(profileUri).placeholder(R.drawable.profile).into(navigationProfileImage);
                    }

                    if (dataSnapshot.hasChild("fullname")) {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        navigationUserFullname.setText(fullname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showProfileImageOnDialog() {
        if (profileUri != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ImageView imageView =new ImageView(this);
            Picasso.get().load(profileUri).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            builder.setView(imageView);
            builder.create().show();
        }
    }

    private void checkUserExistence(final String uID) {

        userDatabase.child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("fullname")) {
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

    private void selfIntent() {
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendUserToEditActivity() {
        Intent postIntent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(postIntent);
    }

    private void sendUserToPostActivity() {
        Intent postIntent = new Intent(MainActivity.this, NewPostActivity.class);
        startActivity(postIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //innitialisation de l'user
        mAuth = FirebaseAuth.getInstance();
        Log.d("UserId", "onCreate: " + currentUserId);

        //pointage de la table user dans la base de données
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListiner);

        if (savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        hToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(hToolbar);
        getSupportActionBar().setTitle("wePost");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.my_navigation_header);

        navigationProfileImage = navView.findViewById(R.id.profile_image);
        navigationUserFullname = navView.findViewById(R.id.nav_fullname);

        navigationProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileImageOnDialog();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });
    }
}
