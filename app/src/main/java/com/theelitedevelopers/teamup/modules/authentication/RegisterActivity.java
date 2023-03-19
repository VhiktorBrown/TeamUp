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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.ActivityRegisterBinding;
import com.theelitedevelopers.teamup.modules.data.models.UserDetails;
import com.theelitedevelopers.teamup.modules.main.home.MainActivity;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    UserDetails userDetails = new UserDetails();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerButton.setOnClickListener(v -> {
            if(validateUserInput()){
                if(userDetails != null){
                    binding.progressBar.setVisibility(View.VISIBLE);
                    createEmployee(userDetails);
                }
            }
        });


    }

    private void createEmployee(UserDetails employee){
        firebaseAuth.createUserWithEmailAndPassword(employee.getEmail(), employee.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            binding.progressBar.setVisibility(View.GONE);

                            showToastMessage("Employee successfully registered");
                            saveEmployeeToDB(employee, user);
                        } else {
                            binding.progressBar.setVisibility(View.GONE);

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateUserInput() {
        String name = binding.name.getText().toString();
        String email = binding.email.getText().toString();
        String role = binding.role.getText().toString();
        String password = binding.password.getText().toString();
        String phone = binding.phone.getText().toString();
        String address = binding.address.getText().toString();
        if(name.isEmpty()){
            showToastMessage("Please, fill in employee's name before you continue");
            return false;
        }
        if(email.isEmpty()){
            showToastMessage("Please, fill in company mail of employee before you continue");
            return false;
        }
        if(role.isEmpty()){
            showToastMessage("Please, fill in current position of employee before you continue");
            return false;
        }
        if(password.isEmpty()){
            showToastMessage("Please, fill in password before you continue");
            return false;
        }
        if(phone.isEmpty()){
            showToastMessage("Please, fill in phone number of employee before you continue");
            return false;
        }
        if(address.isEmpty()){
            showToastMessage("Please, fill in address of employee before you continue");
            return false;
        }
        userDetails.setName(name);
        userDetails.setEmail(email);
        userDetails.setRole(role);
        userDetails.setPassword(password);
        userDetails.setAdmin(false);
        try {
            userDetails.setDateJoined(AppUtils.Companion.getTimeStamp());
            userDetails.setDateOfBirth(AppUtils.Companion.getTimeStamp());
        }catch (ParseException e){
            e.printStackTrace();;
        }
        userDetails.setPhone(phone);
        userDetails.setAddress(address);
        return true;
    }

    private void saveEmployeeToDB(UserDetails employee, FirebaseUser firebaseUser){
        employee.setUid(firebaseUser.getUid());

        Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put("name", employee.getName());
        employeeMap.put("email", employee.getEmail());
        employeeMap.put("role", employee.getRole());
        employeeMap.put("password", employee.getPassword());
        employeeMap.put("phone", employee.getPhone());
        employeeMap.put("address", employee.getAddress());
        employeeMap.put("admin", employee.getAdmin());
        employeeMap.put("dateJoined", employee.getDateJoined());
        employeeMap.put("dateOfBirth", employee.getDateOfBirth());
        employeeMap.put("uid", employee.getUid());
        employeeMap.put("token", "");

        // Add a new document with a generated ID
        database.collection(Constants.EMPLOYEES)
                .add(employeeMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        //Task<DocumentSnapshot> snapshot = documentReference.get();
                        employee.setId(documentReference.getId());
                        //AppUtils.Companion.saveDataToSharedPref(RegisterActivity.this, employee);
                        finish();

                        Toast.makeText(RegisterActivity.this, "Employee data saved to DB", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    private void showToastMessage(String message){
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}