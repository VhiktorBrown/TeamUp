package com.theelitedevelopers.teamup.modules.main.group_chat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.data.remote.ServiceGenerator;
import com.theelitedevelopers.teamup.core.data.request.Notification;
import com.theelitedevelopers.teamup.core.data.request.NotificationBody;
import com.theelitedevelopers.teamup.core.data.request.NotificationMessage;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ActivityGroupChatBinding;
import com.theelitedevelopers.teamup.modules.data.models.Chat;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;
import com.theelitedevelopers.teamup.modules.main.chat.adapter.ChatAdapter;
import com.theelitedevelopers.teamup.modules.main.group_chat.adapter.GroupChatAdapter;
import com.theelitedevelopers.teamup.modules.main.group_chat.adapter.GroupChatListAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatBinding binding;
    GroupChatAdapter adapter;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    String groupChatId, groupChatName;
    Chat chat;
    String[] tokens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        groupChatId = getIntent().getStringExtra(Constants.GROUP_CHAT_ID);
        groupChatName = getIntent().getStringExtra(Constants.GROUP_CHAT_NAME);

        binding.inboxGroupName.setText(groupChatName);

        getTokens();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.inboxRecyclerView.setLayoutManager(layoutManager);
        binding.inboxRecyclerView.setHasFixedSize(true);
        adapter = new GroupChatAdapter(this, chatArrayList);
        binding.inboxRecyclerView.setAdapter(adapter);

        binding.goBack.setOnClickListener(v -> onBackPressed());

        database.collection(Constants.GROUP_CHATS).document(groupChatId).collection("messages")
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    if(!value.getDocuments().isEmpty()) {
                        binding.noDataLayout.setVisibility(View.GONE);
                        chatArrayList.clear();
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            chatArrayList.add(chat);
                        }
                        adapter.setList(chatArrayList);
                        if(chatArrayList.size() > 1){
                            binding.inboxRecyclerView.smoothScrollToPosition(chatArrayList.size()-1);
                        }
                    }else {
                        binding.noDataLayout.setVisibility(View.VISIBLE);
                    }
                });

        binding.send.setOnClickListener(v -> {
            if(binding.sendAMessageEditText.getText().length() > 0){
                chat = new Chat();
                chat.setUid(SharedPref.getInstance(getApplicationContext()).getString(Constants.UID));
                chat.setName(SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
                chat.setDate(Timestamp.now());
                chat.setMessage(binding.sendAMessageEditText.getText().toString());

                saveGroupMessages(chat);
            }
            binding.sendAMessageEditText.getText().clear();
        });
    }

    private void saveGroupMessages(Chat chat){

        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("uid", chat.getUid());
        chatMap.put("name", chat.getName());
        chatMap.put("date", chat.getDate());
        chatMap.put("message", chat.getMessage());

        // Add a new document with a generated ID
        database.collection(Constants.GROUP_CHATS)
                .document(groupChatId)
                .collection("messages")
                .add(chatMap)
                .addOnSuccessListener(documentReference -> {
                    //Next, update the person and last message sent
                    updateGroupDetails(chat);

                    setUpNotificationData();
                        })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void updateGroupDetails(Chat chat){
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put("lastMessage", chat.getMessage());
        groupMap.put("messagedLast", SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
        groupMap.put("date", chat.getDate());
        groupMap.put("uid", SharedPref.getInstance(getApplicationContext()).getString(Constants.UID));

        database.collection(Constants.GROUPS)
                .document(groupChatId)
                .set(groupMap, SetOptions.merge())
                .addOnSuccessListener(documentReference1 -> {
                    AppUtils.Companion.showToastMessage(GroupChatActivity.this,
                            "Group messages added successfully");
                });
    }

    private void setUpNotificationData(){
        Notification notification = new Notification();
        notification.setRegistration_ids(tokens);
        NotificationBody notificationBody = new NotificationBody();
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setTitle(groupChatName +" has a new message from "+SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
        notificationMessage.setBody(chat.getMessage());

        notificationBody.setTitle(groupChatName +" has a new message from "+SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
        notificationBody.setBody(chat.getMessage());

        notification.setData(notificationBody);
        notification.setNotification(notificationMessage);

        notification.setPriority("high");

        sendNotification(notification);
    }

    private void sendNotification(Notification notification){
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
                            Toast.makeText(getApplicationContext(), "Push Notification sent", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        sendNotification(notification);
                    }
                });
    }

    private void getTokens(){
        database.collection(Constants.EMPLOYEES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(!task.getResult().isEmpty()){
                            tokens = new String[task.getResult().size()];
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!document.toObject(UserDetails.class).getUid().equals(SharedPref.getInstance(getApplicationContext()).getString(Constants.UID))){
                                    UserDetails userDetails = document.toObject(UserDetails.class);
                                    tokens[i] = userDetails.getToken();
                                    i++;
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    getTokens();
                });
    }

}