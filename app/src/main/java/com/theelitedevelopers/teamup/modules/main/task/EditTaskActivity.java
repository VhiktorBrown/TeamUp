package com.theelitedevelopers.teamup.modules.main.task;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.theelitedevelopers.teamup.R;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ActivityCreateNewTaskBinding;
import com.theelitedevelopers.teamup.databinding.ActivityEditTaskBinding;
import com.theelitedevelopers.teamup.databinding.DropDownBottomSheetLayoutBinding;
import com.theelitedevelopers.teamup.modules.data.models.Task;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditTaskActivity extends AppCompatActivity {
    ActivityEditTaskBinding binding;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ArrayList<UserDetails> employees = new ArrayList<>();
    String selectedEmployee = "Select Employee";
    String selectedEmployeeUid = "";
    int progressStatus =0;
    Task task;
    Calendar assDueDate = Calendar.getInstance();
    String date="";
    Timestamp dateDue;
    SimpleDateFormat simpleDateFormat, simpleTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        task = getIntent().getParcelableExtra(Constants.TASK_DETAILS);

        if(task != null){
            setTaskDetails(task);
        }
        getAllEmployees();

        binding.taskAssignedTo.setOnClickListener(v -> {
            if(SharedPref.getInstance(getApplicationContext()).getBoolean(Constants.ADMIN)){
                showBottomSheetDialog(binding.taskAssignedTo.getId());
            }else {
                AppUtils.Companion.showToastMessage(EditTaskActivity.this, "Only admins can assign tasks");
            }
        });

        binding.progressStatus.setOnClickListener(view -> {
            showBottomSheetDialog(binding.progressStatus.getId());
        });

        binding.updateTaskButton.setOnClickListener(v -> {
            String title = binding.taskTitle.getText().toString();
            String description = binding.taskDescription.getText().toString();

            if(title.length() > 0 && description.length() > 0){
                if(!selectedEmployee.equals("Select Employee")){
                    if(!date.equals("")){
                        Task task = new Task();
                        task.setId(this.task.getId());
                        task.setTitle(title);
                        task.setDescription(description);
                        task.setTaskStatus(progressStatus);
                        task.setUid(selectedEmployeeUid);
                        task.setEmployee(selectedEmployee);
                        task.setDeadLine(dateDue);

                        binding.progressBar.setVisibility(View.VISIBLE);
                        updateTaskOnDB(task);
                    }else {
                        AppUtils.Companion.showToastMessage(
                                EditTaskActivity.this, "Please, select the deadline for this task"
                        );
                    }
                }else {
                    AppUtils.Companion.showToastMessage(
                            EditTaskActivity.this, "Please, assign this task to an employee before you proceed"
                    );
                }
            }else {
                AppUtils.Companion.showToastMessage(
                        EditTaskActivity.this, "Please, fill in title and description of task.");
            }
        });

        binding.selectDate.setOnClickListener(v -> {
            if(SharedPref.getInstance(getApplicationContext()).getBoolean(Constants.ADMIN)){
                showDateTimePicker();
            }else {
                AppUtils.Companion.showToastMessage(getApplicationContext(), "Only admins can edit the deadline for a task.");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setTaskDetails(Task task) {
        binding.taskTitle.setText(task.getTitle());
        binding.taskDescription.setText(task.getDescription());
        binding.taskAssignedTo.setText(task.getEmployee());
        selectedEmployee = task.getEmployee();
        progressStatus = task.getTaskStatus();
        selectedEmployeeUid = task.getUid();
        dateDue = task.getDeadLine();

        if(task.getDeadLine() != null){
            String deadLine = AppUtils.Companion.fromTimeStampToString(task.getDeadLine().getSeconds());
            date = deadLine;
            binding.selectDate.setText(deadLine);
        }

        if(task.getTaskStatus() != null){
            switch (task.getTaskStatus()){
                case 0:
                    binding.progressStatus.setText(Constants.TODO);
                    binding.progressStatus.setBackground(getResources().getDrawable(R.drawable.to_do_bg));
                    break;
                case 1:
                    binding.progressStatus.setText(Constants.IN_PROGRESS);
                    binding.progressStatus.setBackground(getResources().getDrawable(R.drawable.in_progress_bg));
                    break;
                case 2:
                    binding.progressStatus.setText(Constants.COMPLETED);
                    binding.progressStatus.setBackground(getResources().getDrawable(R.drawable.completed_bg));
                    break;
            }
        }
    }

    private void showBottomSheetDialog(int id) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        DropDownBottomSheetLayoutBinding dialogBinding
                = DropDownBottomSheetLayoutBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        bottomSheetDialog.show();

        String editTextName = getTextViewNameFromId(id);
        dialogBinding.title.setText(editTextName);

        dialogBinding.dropDownRv.setLayoutManager(new LinearLayoutManager(this));
        dialogBinding.dropDownRv.setHasFixedSize(true);

        switch(Objects.requireNonNull(editTextName)){
            case Constants.TASK_STATUS: {
                BottomSheetDialogRvTsAdapter bottomSheetDialogRvTsAdapter = new BottomSheetDialogRvTsAdapter(getProgressStatus());
                bottomSheetDialogRvTsAdapter.setOnItemCLickListener(item -> {
                    switch (item) {
                        case Constants.TODO:
                            progressStatus = 0;
                            binding.progressStatus.setText(Constants.TODO);
                            binding.progressStatus.setBackground(getResources().getDrawable(R.drawable.to_do_bg));
                            bottomSheetDialog.dismiss();
                            break;
                        case Constants.IN_PROGRESS:
                            progressStatus = 1;
                            binding.progressStatus.setText(Constants.IN_PROGRESS);
                            binding.progressStatus.setBackground(getResources().getDrawable(R.drawable.in_progress_bg));
                            bottomSheetDialog.dismiss();
                            break;
                        case Constants.COMPLETED:
                            progressStatus = 2;
                            binding.progressStatus.setText(Constants.COMPLETED);
                            binding.progressStatus.setBackground(getResources().getDrawable(R.drawable.completed_bg));
                            bottomSheetDialog.dismiss();
                            break;
                    }
                });
                dialogBinding.dropDownRv.setAdapter(bottomSheetDialogRvTsAdapter);
                break;
            }
            case Constants.EMPLOYEES_TEXT: {
                BottomSheetDialogRvAdapter bottomSheetDialogRvAdapter = new BottomSheetDialogRvAdapter(getListOfEmployees());
                bottomSheetDialogRvAdapter.setOnItemCLickListener(item -> {
                    selectedEmployee = item.getName();
                    selectedEmployeeUid = item.getUid();
                    binding.taskAssignedTo.setText(selectedEmployee);
                    bottomSheetDialog.dismiss();
                });
                dialogBinding.dropDownRv.setAdapter(bottomSheetDialogRvAdapter);
                break;
            }
        }

    }

    private void updateTaskOnDB(Task task){

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("title", task.getTitle());
        taskMap.put("description", task.getDescription());
        taskMap.put("taskStatus", task.getTaskStatus());
        taskMap.put("employee", selectedEmployee);
        taskMap.put("uid", selectedEmployeeUid);
        taskMap.put("deadLine", task.getDeadLine());

        // Add a new document with a generated ID
        database.collection(Constants.TASKS)
                .document(task.getId())
                .set(taskMap, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    binding.progressBar.setVisibility(View.GONE);
                    AppUtils.Companion
                            .showToastMessage(EditTaskActivity.this, "Task updated on DB successfully");
                    finish();
                    //setUpNotificationData(assignment);
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Log.w(TAG, "Error adding document", e);
                });
    }

    public void showDateTimePicker(){
        final Calendar currentDate = Calendar.getInstance();
        assDueDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            assDueDate.set(year, month, dayOfMonth);
            showTimePicker();

            simpleDateFormat = new SimpleDateFormat("EEE dd MMM yyyy");

            simpleTimeFormat = new SimpleDateFormat("hh:mm aa");


            String sourceFormat = "EEE MMM d HH:mm:ss z yyyy";
            String destinationFormat = "EEE, d MMM yyyy HH:mm:ss";

            //convert date to Universal format
            date = AppUtils.Companion.convertDateFromOneFormatToAnother(sourceFormat, destinationFormat, assDueDate.getTime().toString());

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }

    private void showTimePicker(){
        Calendar currentDate = Calendar.getInstance();

        new TimePickerDialog(this, (timePicker, i, i1) -> {

            assDueDate.set(Calendar.HOUR_OF_DAY, i);
            assDueDate.set(Calendar.MINUTE, i1);

            simpleDateFormat = new SimpleDateFormat("EEE dd MMM yyyy");
            simpleTimeFormat = new SimpleDateFormat("hh:mm aa");


            String sourceFormat = "EEE MMM d HH:mm:ss z yyyy";
            String destinationFormat = "EEE, d MMM yyyy HH:mm:ss";

            //convert date to Universal format
            date = AppUtils.Companion.convertDateFromOneFormatToAnother(sourceFormat, destinationFormat, assDueDate.getTime().toString());
            binding.selectDate.setText(simpleDateFormat.format(assDueDate.getTime()) +". "+ simpleTimeFormat.format(assDueDate.getTime()));

            getTimeStamp(date);

        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
    }

    private void getTimeStamp(String date) {
        String destinationFormat = "EEE, d MMM yyyy HH:mm:ss";

        try {
            dateDue = new Timestamp(Objects.requireNonNull(
                    AppUtils.Companion.convertToDateFormat(destinationFormat, date)));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    private String getTextViewNameFromId(int id) {
        if (id == R.id.progress_status) {
            return Constants.TASK_STATUS;
        } else if (id == R.id.task_assigned_to) {
            return Constants.EMPLOYEES_TEXT;
        }
        return null;
    }

    private ArrayList<String> getProgressStatus(){
        ArrayList<String> status = new ArrayList<>();
        status.add(Constants.TODO);
        status.add(Constants.IN_PROGRESS);
        status.add(Constants.COMPLETED);

        return status;
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
                }).addOnFailureListener(e -> getAllEmployees());

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