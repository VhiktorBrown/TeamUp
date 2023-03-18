package com.theelitedevelopers.teamup.modules.authentication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;
import com.theelitedevelopers.teamup.modules.main.home.MainActivity;
import com.theelitedevelopers.teamup.databinding.ActivityLoginBinding;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    UserDetails employee;
    String firebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(!SharedPref.getInstance(getApplicationContext()).getBoolean(Constants.SUBSCRIBED)){
            subscribeToTopics();
        }

        binding.logInButton.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();

            if(email.length() > 0 && password.length() > 0){
                UserDetails userDetails = new UserDetails();
                userDetails.setEmail(email);
                userDetails.setPassword(password);

                binding.progressBar.setVisibility(View.VISIBLE);
                binding.logInButton.setEnabled(false);
                loginEmployee(userDetails);
            }
        });
    }

    private void loginEmployee(UserDetails employee){
        getToken();

        firebaseAuth.signInWithEmailAndPassword(employee.getEmail(), employee.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //fetch Student details from Database
                            getEmployeeDetails(user.getUid());

                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.logInButton.setEnabled(true);

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getEmployeeDetails(String uid){
        database.collection(Constants.EMPLOYEES)
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(!task.getResult().isEmpty()){
                            employee = task.getResult().getDocuments().get(0).toObject(UserDetails.class);
                            if(employee != null){
                                employee.setId(task.getResult().getDocuments().get(0).getId());

                                updateEmployeeDetailsWithToken(employee);
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.logInButton.setEnabled(true);
            }
        });
    }

    private void updateEmployeeDetailsWithToken(UserDetails employee){
        Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put("token", firebaseToken);

        database.collection(Constants.EMPLOYEES)
                .document(employee.getId())
                .set(employeeMap, SetOptions.merge())
                .addOnSuccessListener(documentReference ->{
                    binding.progressBar.setVisibility(View.GONE);
                    binding.logInButton.setEnabled(true);

                    AppUtils.Companion.saveDataToSharedPref(LoginActivity.this, employee);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void getToken(){
        final String[] token = {""};
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    token[0] = task.getResult();
                    firebaseToken = token[0];

                    Log.d(TAG, token[0]);
                }).addOnFailureListener(e -> getToken());
    }

    private void subscribeToTopics(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.GROUP_CHATS).addOnSuccessListener(aVoid -> {
            SharedPref.getInstance(getApplicationContext()).saveBoolean(Constants.SUBSCRIBED, true);
        });

    }

    @Override
    protected void onStart() {
        if(firebaseAuth.getCurrentUser() != null){
            if(!SharedPref.getInstance(getApplicationContext()).getString(Constants.UID).equals("")){
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finishAffinity();
            }
        }
        super.onStart();
    }
}