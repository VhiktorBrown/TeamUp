package com.theelitedevelopers.teamup.modules.main.group_chat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ActivityStartNewGroupBinding;
import com.theelitedevelopers.teamup.modules.data.models.Chat;
import com.theelitedevelopers.teamup.modules.data.models.Task;
import com.theelitedevelopers.teamup.modules.main.task.CreateNewTaskActivity;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StartNewGroupActivity extends AppCompatActivity {
    ActivityStartNewGroupBinding binding;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartNewGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.createNewGroupButton.setOnClickListener(v -> {
            String title = binding.groupName.getText().toString();
            String description = binding.groupDescription.getText().toString();
            if (title.length() > 0 && description.length() > 0) {
                Chat chat = new Chat();
                chat.setName(title);
                chat.setDescription(description);

                binding.progressBar.setVisibility(View.VISIBLE);
                saveGroupChatToDB(chat);
            } else {
                AppUtils.Companion.showToastMessage(StartNewGroupActivity.this, "Please, set the title and description of this group before you proceed.");
            }
        });
    }

    private Timestamp getTimeStamp() {
        String sourceFormat = "EEE MMM d HH:mm:ss z yyyy";
        String destinationFormat = "EEE, d MMM yyyy HH:mm:ss";
        Date date = null;
        try {
            date = AppUtils.Companion.convertToDateFormat(destinationFormat,
                    Objects.requireNonNull(AppUtils.Companion.convertDateFromOneFormatToAnother(
                            sourceFormat, destinationFormat, new Date().toString())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert date != null;
        return new Timestamp(date);
    }

    private void saveGroupChatToDB(Chat chat) {

        Map<String, Object> groupChatMap = new HashMap<>();
        groupChatMap.put("title", chat.getName());
        groupChatMap.put("description", chat.getDescription());
        groupChatMap.put("createdBy", SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
        groupChatMap.put("createdByUid", SharedPref.getInstance(getApplicationContext()).getString(Constants.UID));
        groupChatMap.put("dateCreated", getTimeStamp());
        groupChatMap.put("lastMessage", "");
        groupChatMap.put("messagedLast", "");
        groupChatMap.put("date", null);

        // Add a new document with a generated ID
        database.collection(Constants.GROUPS)
                .add(groupChatMap)
                .addOnSuccessListener(documentReference -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    AppUtils.Companion
                            .showToastMessage(StartNewGroupActivity.this, "Group added to DB successfully");
                    finish();
                    //setUpNotificationData(assignment);
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Log.w(TAG, "Error adding document", e);
                });
    }
}