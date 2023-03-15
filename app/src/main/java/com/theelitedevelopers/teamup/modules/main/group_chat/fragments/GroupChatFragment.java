package com.theelitedevelopers.teamup.modules.main.group_chat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theelitedevelopers.teamup.R;
import com.theelitedevelopers.teamup.databinding.FragmentGroupChatBinding;

public class GroupChatFragment extends Fragment {
    FragmentGroupChatBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGroupChatBinding.inflate(inflater, container, false);

        initViews();
        return binding.getRoot();
    }

    private void initViews(){
        binding.newGroup.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), StartNe));
        });
    }
}