package com.theelitedevelopers.teamup.modules.main.chat;

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
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ActivityChatBinding;
import com.theelitedevelopers.teamup.modules.data.models.Chat;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;
import com.theelitedevelopers.teamup.modules.main.chat.adapter.ChatAdapter;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    ChatAdapter adapter;
    ArrayList<Chat> chatArrayList = new ArrayList<>();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    String senderId, receiverId;
    String receiverUid, receiverName;
    Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        receiverUid = getIntent().getStringExtra(Constants.RECEIVER_UID);
        receiverName = getIntent().getStringExtra(Constants.NAME);

        senderId = SharedPref.getInstance(getApplicationContext()).getString(Constants.UID)+receiverUid;
        receiverId = receiverUid+SharedPref.getInstance(getApplicationContext()).getString(Constants.UID);

        binding.inboxName.setText(receiverName);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.inboxRecyclerView.setLayoutManager(layoutManager);
        binding.inboxRecyclerView.setHasFixedSize(true);

        adapter = new ChatAdapter(this, chatArrayList, receiverUid);
        binding.inboxRecyclerView.setAdapter(adapter);

        binding.goBack.setOnClickListener(v -> onBackPressed());

        database.collection("chats").document(senderId).collection("messages")
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

                saveMessages(chat);
            }
            binding.sendAMessageEditText.getText().clear();
        });
    }

    private void saveMessages(Chat chat){

        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("uid", chat.getUid());
        chatMap.put("name", chat.getName());
        chatMap.put("date", chat.getDate());
        chatMap.put("message", chat.getMessage());

        // Add a new document with a generated ID
        database.collection("chats")
                .document(senderId)
                .collection("messages")
                .add(chatMap)
                .addOnSuccessListener(documentReference ->
                        database.collection("chats")
                                .document(receiverId)
                                .collection("messages")
                                .add(chatMap)
                                .addOnSuccessListener(documentReference1 -> {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference1.getId());
                                    if(chatArrayList.size() > 0){
                                        binding.inboxRecyclerView.smoothScrollToPosition(chatArrayList.size()-1);
                                    }
                                    getCurrentChatHistoryItem(chat);
                                }))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }


    private void updateLastMessageAndDate(Chat chatFromDB, Chat chatJustCreated){
        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("uid", chatFromDB.getUid());
        chatMap.put("name", chatFromDB.getName());
        chatMap.put("date", chatJustCreated.getDate());
        chatMap.put("photo", "");
        chatMap.put("lastMessage", chatJustCreated.getMessage());

        database.collection(SharedPref.getInstance(getApplicationContext()).getString(Constants.UID)+"history")
                .document(chatFromDB.getId())
                .set(chatMap)
                .addOnSuccessListener(documentReference ->{
                    //Log.w(TAG, "Document added successfully in Chat History", documentReference.);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void updateLastMessageAndDateForReceiver(Chat chatFromDB, Chat chatJustCreated){
        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("uid", SharedPref.getInstance(getApplicationContext()).getString(Constants.UID));
        chatMap.put("name", SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
        chatMap.put("date", chatJustCreated.getDate());
        chatMap.put("photo", "");
        chatMap.put("lastMessage", chatJustCreated.getMessage());

        //TODO Confirm
        database.collection(receiverUid+"history")
                .document(chatFromDB.getId())
                .set(chatMap)
                .addOnSuccessListener(documentReference ->{
                    //Toast.makeText(ChatActivity.this, "Chat Updated For Receiver", Toast.LENGTH_SHORT).show();
                    //Log.w(TAG, "Document added successfully in Chat History", documentReference.);

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void createNewChatHistory(Chat chatJustCreated){
        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("uid", receiverUid);
        chatMap.put("name", receiverName);
        chatMap.put("date", chatJustCreated.getDate());
        chatMap.put("photo", "");
        chatMap.put("lastMessage", chatJustCreated.getMessage());

        database.collection(SharedPref.getInstance(getApplicationContext()).getString(Constants.UID)+"history")
                .add(chatMap)
                .addOnSuccessListener(documentReference ->{
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void createNewChatHistoryForReceiver(Chat chatJustCreated){
        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("uid", SharedPref.getInstance(getApplicationContext()).getString(Constants.UID));
        chatMap.put("name", SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME));
        chatMap.put("date", chatJustCreated.getDate());
        chatMap.put("photo", "");
        chatMap.put("lastMessage", chatJustCreated.getMessage());

        database.collection(receiverUid+"history")
                .add(chatMap)
                .addOnSuccessListener(documentReference ->{
                    //Toast.makeText(ChatActivity.this, "Created Chat For Receiver", Toast.LENGTH_SHORT).show();
                    //Log.w(TAG, "Document added successfully in Chat History", documentReference.);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void getCurrentChatHistoryItem(Chat chat){
        database.collection(SharedPref.getInstance(getApplicationContext()).getString(Constants.UID)+"history")
                .whereEqualTo("uid", receiverUid)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(!task.getResult().isEmpty()){
                            Chat chatFromDb = task.getResult().getDocuments().get(0).toObject(Chat.class);
                            if(chatFromDb != null){
                                chatFromDb.setId(task.getResult().getDocuments().get(0).getId());
                                updateLastMessageAndDate(chatFromDb, chat);

                                database.collection(receiverUid+"history")
                                        .whereEqualTo("uid", SharedPref.getInstance(getApplicationContext()).getString(Constants.UID))
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if(task1.isSuccessful()){
                                                if(!task1.getResult().isEmpty()) {
                                                    Chat currentChat = task1.getResult().getDocuments().get(0).toObject(Chat.class);
                                                    if(currentChat != null) {
                                                        currentChat.setId(task1.getResult().getDocuments().get(0).getId());
                                                        updateLastMessageAndDateForReceiver(currentChat, chat);

                                                        //send Push Notifications
                                                        //fetchStudentToken(receiverUid);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }else {
                            createNewChatHistory(chat);
                            createNewChatHistoryForReceiver(chat);
                        }
                    }
                });
    }

    private void fetchStudentToken(String uid){
        database.collection(Constants.EMPLOYEES)
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserDetails employee = task.getResult().getDocuments().get(0).toObject(UserDetails.class);
                        if(employee != null){
                            employee.setId(task.getResult().getDocuments().get(0).getId());
                            //setUpNotificationData(employee);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

//    private void setUpNotificationData(UserDetails employee){
//        Notification notification = new Notification();
//        notification.setTo(student.getToken());
//        NotificationBody notificationBody = new NotificationBody();
//        NotificationMessage notificationMessage = new NotificationMessage();
//        notificationMessage.setTitle(SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME)+" sent you a message");
//        notificationMessage.setBody(chat.getMessage());
//
//        notificationBody.setTitle(SharedPref.getInstance(getApplicationContext()).getString(Constants.NAME)+" sent you a message");
//        notificationBody.setBody(chat.getMessage());
//
//        notification.setData(notificationBody);
//        notification.setNotification(notificationMessage);
//
//        notification.setPriority("high");
//
//        sendNotification(notification, student);
//    }
//
//    private void sendNotification(Notification notification, Student student){
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", "key=" + Constants.PUSH_NOT_KEY);
//
//        Single<Response<JSONObject>> sendNotification = ServiceGenerator.getInstance()
//                .getApi().sendNotification(headers, notification);
//        sendNotification.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new SingleObserver<Response<JSONObject>>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(@NonNull Response<JSONObject> sentNotificationResponse) {
//                        if(sentNotificationResponse.isSuccessful()){
//                            Toast.makeText(getApplicationContext(), "Push Notification sent", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        sendNotification(notification, student);
//                    }
//                });
//    }

    private Date getDateTodayInAcceptableFormat() throws ParseException {
        String sourceFormat = "EEE MMM d HH:mm:ss z yyyy";
        String destinationFormat = "EEE, d MMM yyyy HH:mm:ss";

        return AppUtils.Companion.convertToDateFormat(destinationFormat, Objects.requireNonNull(AppUtils.Companion.convertDateFromOneFormatToAnother(sourceFormat, destinationFormat, new Date().toString())));

    }
}