package com.theelitedevelopers.teamup.modules.main.task;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.theelitedevelopers.teamup.databinding.ActivityEditTaskBinding;

public class EditTaskActivity extends AppCompatActivity {
    ActivityEditTaskBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}