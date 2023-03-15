package com.theelitedevelopers.teamup.modules.main.chat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ActivityStartNewChatBinding;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;
import com.theelitedevelopers.teamup.modules.main.chat.adapter.StartNewChatAdapter;

import java.util.ArrayList;

public class StartNewChatActivity extends AppCompatActivity {
    ActivityStartNewChatBinding binding;
    StartNewChatAdapter adapter;
    ArrayList<UserDetails> employees = new ArrayList<>();
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartNewChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    private void initViews(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.newChatRecyclerView.setLayoutManager(layoutManager);
        binding.newChatRecyclerView.setHasFixedSize(true);

        adapter = new StartNewChatAdapter(this, employees);
        binding.newChatRecyclerView.setAdapter(adapter);

        binding.goBack.setOnClickListener(v -> onBackPressed());

        database.collection(Constants.EMPLOYEES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(!task.getResult().isEmpty()){
                            employees.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!document.toObject(UserDetails.class).getUid().equals(SharedPref.getInstance(getApplicationContext()).getString(Constants.UID))){
                                    employees.add(document.toObject(UserDetails.class));
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            }
                            adapter.setList(employees);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}