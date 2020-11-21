package app.project.wepost.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import app.project.wepost.Adapter.MyPostAdatper;
import app.project.wepost.Models.Posts;
import app.project.wepost.NewPostActivity;
import app.project.wepost.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton addNewPost;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private RecyclerView postsList;
    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private MyPostAdatper adapter;

   // public HomeFragment() {
        // Required empty public constructor
    //}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);


        postsList = view.findViewById(R.id.posts_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postsList.setLayoutManager(linearLayoutManager);
        postsList.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        //currentUserId = mAuth.getCurrentUser().getUid();
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        settUpRecycleView();
        return view;
    }

    private void settUpRecycleView() {
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postsRef, Posts.class)
                .build();

        adapter = new MyPostAdatper(options);
        postsList.setAdapter(adapter);

    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Acceuil");

        addNewPost = view.findViewById(R.id.toCreatePostfloatingButton);
        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPostActivity();
            }
        });
    }

    private void sendUserToPostActivity() {
        Intent postIntent = new Intent(this.getContext(), NewPostActivity.class);
        startActivity(postIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }

    }
}