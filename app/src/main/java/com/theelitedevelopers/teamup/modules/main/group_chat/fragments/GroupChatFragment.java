package com.theelitedevelopers.teamup.modules.main.group_chat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.theelitedevelopers.teamup.R;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.FragmentGroupChatBinding;
import com.theelitedevelopers.teamup.modules.data.models.Chat;
import com.theelitedevelopers.teamup.modules.main.chat.adapter.ChatListAdapter;
import com.theelitedevelopers.teamup.modules.main.group_chat.StartNewGroupActivity;
import com.theelitedevelopers.teamup.modules.main.group_chat.adapter.GroupChatListAdapter;

import java.util.ArrayList;

public class GroupChatFragment extends Fragment {
    FragmentGroupChatBinding binding;
    GroupChatListAdapter adapter;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGroupChatBinding.inflate(inflater, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.inboxRecyclerView.setLayoutManager(linearLayoutManager);

        binding.inboxRecyclerView.setHasFixedSize(true);
        adapter = new GroupChatListAdapter(requireContext(), chatArrayList);
        binding.inboxRecyclerView.setAdapter(adapter);

        initViews();
        fetchGroupChatHistory();
        return binding.getRoot();
    }

    private void initViews(){
        if(SharedPref.getInstance(requireActivity()).getBoolean(Constants.ADMIN)){
            binding.newGroup.setVisibility(View.VISIBLE);
        }else {
            binding.newGroup.setVisibility(View.GONE);
        }

        binding.newGroup.setOnClickListener(v -> {
            if(SharedPref.getInstance(requireActivity()).getBoolean(Constants.ADMIN)){
                startActivity(new Intent(requireActivity(), StartNewGroupActivity.class));
            }else {
                AppUtils.Companion.showToastMessage(requireActivity(), "You are not an admin, so you can't create a group.");
            }
        });
    }

    private void fetchGroupChatHistory(){
        database.collection(Constants.GROUPS)
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if(value != null){
                        if(!value.getDocuments().isEmpty()) {
                            binding.noDataLayout.setVisibility(View.GONE);
                            chatArrayList.clear();
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                Chat chat = documentSnapshot.toObject(Chat.class);
                                if(chat != null){
                                    chat.setId(documentSnapshot.getId());
                                    chatArrayList.add(chat);
                                }
                            }
                            adapter.setList(chatArrayList);
                        }else {
                            binding.noDataLayout.setVisibility(View.VISIBLE);
                        }
                    }else {
                        binding.noDataLayout.setVisibility(View.VISIBLE);
                    }
                });
    }
}