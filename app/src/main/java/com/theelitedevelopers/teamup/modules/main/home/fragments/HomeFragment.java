package com.theelitedevelopers.teamup.modules.main.home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.theelitedevelopers.teamup.R;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.FragmentAdminHomeBinding;
import com.theelitedevelopers.teamup.databinding.FragmentHomeBinding;
import com.theelitedevelopers.teamup.modules.authentication.LoginActivity;
import com.theelitedevelopers.teamup.modules.authentication.RegisterActivity;
import com.theelitedevelopers.teamup.modules.data.models.Task;
import com.theelitedevelopers.teamup.modules.main.group_chat.StartNewGroupActivity;
import com.theelitedevelopers.teamup.modules.main.task.CreateNewTaskActivity;
import com.theelitedevelopers.teamup.modules.main.task.adapters.AdminTaskListAdapter;
import com.theelitedevelopers.teamup.modules.main.task.adapters.TaskListAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    TaskListAdapter adapter;
    ArrayList<Task> taskArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.userName.setText(SharedPref.getInstance(requireActivity()).getString(Constants.NAME));
        binding.userRole.setText(SharedPref.getInstance(requireActivity()).getString(Constants.ROLE));

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        binding.allTasksRecyclerView.setLayoutManager(layoutManager);
        binding.allTasksRecyclerView.setHasFixedSize(true);

        adapter = new TaskListAdapter(requireActivity(), taskArrayList);
        binding.allTasksRecyclerView.setAdapter(adapter);

        binding.logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            AppUtils.Companion.removeDataToSharedPref(requireActivity());
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            ((Activity) v.getContext()).finishAffinity();
        });

        fetchMyTasks();
        return binding.getRoot();
    }

    private void fetchMyTasks(){

        database.collection(Constants.TASKS)
                .orderBy("deadLine", Query.Direction.ASCENDING)
                .whereEqualTo("uid", SharedPref.getInstance(requireActivity()).getString(Constants.UID))
                .addSnapshotListener((value, error) -> {
                    if(value != null){
                        if(!value.getDocuments().isEmpty()) {
                            binding.noDataLayout.setVisibility(View.GONE);
                            taskArrayList.clear();
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                Task task = documentSnapshot.toObject(Task.class);
                                if(task != null){
                                    task.setId(documentSnapshot.getId());
                                    taskArrayList.add(task);
                                }
                            }
                            adapter.setList(taskArrayList);
                        }else {
                            binding.noDataLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchMyTasks();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMyTasks();
    }
}