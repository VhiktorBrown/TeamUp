package com.theelitedevelopers.teamup.modules.main.task;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ActivityCreateNewTaskBinding;
import com.theelitedevelopers.teamup.databinding.DropDownBottomSheetLayoutBinding;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateNewTaskActivity extends AppCompatActivity {
    ActivityCreateNewTaskBinding binding;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ArrayList<UserDetails> employees = new ArrayList<>();
    String selectedEmployee = "Select Employee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNewTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getAllEmployees();
    }

    private void showBottomSheetDialog(int id) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        DropDownBottomSheetLayoutBinding dialogBinding
                = DropDownBottomSheetLayoutBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        bottomSheetDialog.show();

        dialogBinding.title.setText("Employees");

        dialogBinding.dropDownRv.setLayoutManager(new LinearLayoutManager(this));
        dialogBinding.dropDownRv.setHasFixedSize(true);


        BottomSheetDialogRvAdapter bottomSheetDialogRvAdapter = new BottomSheetDialogRvAdapter(getListOfEmployees());
        bottomSheetDialogRvAdapter.setOnItemCLickListener(item -> {
            selectedEmployee = item.getName();
            binding.taskAssignedTo.setText(selectedEmployee);
            bottomSheetDialog.dismiss();
        });
        dialogBinding.dropDownRv.setAdapter(bottomSheetDialogRvAdapter);
    }

    private ArrayList<UserDetails> getAllEmployees(){
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
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        return employees;
    }

    private ArrayList<UserDetails> getListOfEmployees() {
        if(employees != null && !employees.isEmpty()){
            return employees;
        }else {
            return getAllEmployees();
        }
    }



}