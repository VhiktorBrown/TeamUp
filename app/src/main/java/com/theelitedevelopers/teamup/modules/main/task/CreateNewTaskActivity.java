package com.theelitedevelopers.teamup.modules.main.task;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.theelitedevelopers.teamup.R;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.data.remote.ServiceGenerator;
import com.theelitedevelopers.teamup.core.data.request.Notification;
import com.theelitedevelopers.teamup.core.data.request.NotificationBody;
import com.theelitedevelopers.teamup.core.data.request.NotificationMessage;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ActivityCreateNewTaskBinding;
import com.theelitedevelopers.teamup.databinding.DropDownBottomSheetLayoutBinding;
import com.theelitedevelopers.teamup.modules.data.models.Task;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;
import com.theelitedevelopers.teamup.modules.main.home.MainActivity;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class CreateNewTaskActivity extends AppCompatActivity {
    ActivityCreateNewTaskBinding binding;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ArrayList<UserDetails> employees = new ArrayList<>();
    String selectedEmployee = "Select Employee";
    String selectedEmployeeUid = "";
    String selectedEmployeeToken ="";
    int progressStatus =0;
    Calendar assDueDate = Calendar.getInstance();
    String date="", dateToday="";
    Timestamp dateDue, datePosted;
    SimpleDateFormat simpleDateFormat, simpleTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNewTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getAllEmployees();

        binding.goBack.setOnClickListener(v -> onBackPressed());

        binding.taskAssignedTo.setOnClickListener(v -> {
            showBottomSheetDialog(binding.taskAssignedTo.getId());
        });

        binding.progressStatus.setOnClickListener(view -> {
            showBottomSheetDialog(binding.progressStatus.getId());
        });

        binding.selectDate.setOnClickListener(v -> {
            showDateTimePicker();
        });

        binding.createNewTaskButton.setOnClickListener(v -> {
            String title = binding.taskTitle.getText().toString();
            String description = binding.taskDescription.getText().toString();

            if(title.length() > 0 && description.length() > 0){
                if(!selectedEmployee.equals("Select Employee")){
                    if(!date.equals("")){
                        Task task = new Task();
                        task.setTitle(title);
                        task.setDescription(description);
                        task.setTaskStatus(progressStatus);
                        task.setUid(selectedEmployeeUid);
                        task.setEmployee(selectedEmployee);
                        task.setDatePosted(datePosted);
                        task.setDeadLine(dateDue);
                        task.setPostedBy(SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
                        task.setPostedByUid(SharedPref.getInstance(getApplicationContext()).getString(Constants.UID));

                        binding.progressBar.setVisibility(View.VISIBLE);
                        saveTaskToDB(task);
                    }else {
                        AppUtils.Companion.showToastMessage(
                                CreateNewTaskActivity.this, "Please, select the deadline for this task"
                        );
                    }
                }else {
                    AppUtils.Companion.showToastMessage(
                            CreateNewTaskActivity.this, "Please, assign this task to an employee before you proceed"
                    );
                }
            }else {
                AppUtils.Companion.showToastMessage(
                        CreateNewTaskActivity.this, "Please, fill in title and description of task.");
            }
        });
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
                    selectedEmployeeToken = item.getToken();
                    binding.taskAssignedTo.setText(selectedEmployee);
                    bottomSheetDialog.dismiss();
                    });
                dialogBinding.dropDownRv.setAdapter(bottomSheetDialogRvAdapter);
                break;
                }
            }

        }

    private void setUpNotificationData(Task task){
        Notification notification = new Notification();
        notification.setTo(selectedEmployeeToken);
        NotificationBody notificationBody = new NotificationBody();
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setTitle(AppUtils.Companion.getFirstNameOnly(SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME))+
                " just assigned you a task");
        notificationMessage.setBody(task.getTitle()+" is to be turned in "+
                AppUtils.Companion.getTimeInDaysOrWeeksForNotification(
                        AppUtils.Companion.fromTimeStampToString(
                                task.getDeadLine().getSeconds()))+ ". Hurry up, carry it out and submit before the deadline.");

        notificationBody.setTitle(AppUtils.Companion.getFirstNameOnly(SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME))+
                " just assigned you a task");
        notificationBody.setBody(task.getTitle()+" is to be turned in "+
                AppUtils.Companion.getTimeInDaysOrWeeksForNotification(
                        AppUtils.Companion.fromTimeStampToString(task.getDeadLine().getSeconds()))+ ". Hurry up, do it and submit before the deadline.");
        notification.setData(notificationBody);
        notification.setNotification(notificationMessage);

        notification.setPriority("high");

        sendNotification(notification, task);
    }


    private void saveTaskToDB(Task task){

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("title", task.getTitle());
        taskMap.put("description", task.getDescription());
        taskMap.put("taskStatus", progressStatus);
        taskMap.put("employee", selectedEmployee);
        taskMap.put("uid", selectedEmployeeUid);
        taskMap.put("postedBy", SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
        taskMap.put("postedByUid", SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
        taskMap.put("datePosted", task.getDatePosted());
        taskMap.put("deadLine", task.getDeadLine());

        // Add a new document with a generated ID
        database.collection(Constants.TASKS)
                .add(taskMap)
                .addOnSuccessListener(documentReference -> {
                    setUpNotificationData(task);
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void sendNotification(Notification notification, Task task){
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "key=" + Constants.PUSH_NOT_KEY);

        Single<Response<JSONObject>> sendNotification = ServiceGenerator.getInstance()
                .getApi().sendNotification(headers, notification);
        sendNotification.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<JSONObject>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Response<JSONObject> sentNotificationResponse) {
                        if(sentNotificationResponse.isSuccessful()){
                            binding.progressBar.setVisibility(View.GONE);
                            AppUtils.Companion
                                    .showToastMessage(CreateNewTaskActivity.this, "Task added to DB successfully");
                            finish();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        sendNotification(notification, task);
                    }
                });
    }


    private String getTextViewNameFromId(int id) {
        if (id == R.id.progress_status) {
            return Constants.TASK_STATUS;
        } else if (id == R.id.task_assigned_to) {
            return Constants.EMPLOYEES_TEXT;
        }
        return null;
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
            dateToday = AppUtils.Companion.convertDateFromOneFormatToAnother(sourceFormat, destinationFormat, new Date().toString());

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

            try {
                getTimeStamp(date, dateToday);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
    }

    private void getTimeStamp(String date, String dateToday) throws ParseException {
        String destinationFormat = "EEE, d MMM yyyy HH:mm:ss";

        dateDue = new Timestamp(Objects.requireNonNull(
                AppUtils.Companion.convertToDateFormat(destinationFormat, date)));
        datePosted = new Timestamp(Objects.requireNonNull(
                AppUtils.Companion.convertToDateFormat(destinationFormat, dateToday)));
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