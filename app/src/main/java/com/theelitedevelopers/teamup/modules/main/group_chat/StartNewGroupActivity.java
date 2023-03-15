package com.theelitedevelopers.teamup.modules.main.group_chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.theelitedevelopers.teamup.databinding.ActivityStartNewGroupBinding;

public class StartNewGroupActivity extends AppCompatActivity {
    ActivityStartNewGroupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartNewGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}