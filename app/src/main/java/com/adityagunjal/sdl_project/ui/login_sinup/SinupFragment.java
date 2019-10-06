package com.adityagunjal.sdl_project.ui.login_sinup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.SplashActivity;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.adityagunjal.sdl_project.models.ModelUsernameEmail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class SinupFragment extends Fragment implements Button.OnClickListener{
    FirebaseAuth firebaseAuth;

    TextInputEditText firstName, lastName, username, email, registrationID, password, confirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sinup, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        Button signupButton = view.findViewById(R.id.signup_button);
        firstName = view.findViewById(R.id.signup_first_name);
        lastName = view.findViewById(R.id.signup_last_name);
        username = view.findViewById(R.id.signup_username);
        email = view.findViewById(R.id.signup_email);
        registrationID = view.findViewById(R.id.signup_registration_id);
        password = view.findViewById(R.id.signup_password);
        confirmPassword = view.findViewById(R.id.signup_confirm_password);

        signupButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signup_button){
            registerUser();
        }
    }

    public void registerUser(){
        final String fname = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        final String uname = username.getText().toString().trim();
        final String eMail = email.getText().toString().trim();
        final String rid = registrationID.getText().toString().trim();
        final String pword = password.getText().toString().trim();
        String cpword = confirmPassword.getText().toString().trim();

        if(fname.isEmpty()){
            firstName.setError("First Name is required !");
            firstName.requestFocus();
            return;
        }
        if(lname.isEmpty()){
            lastName.setError("Last Name is required !");
            lastName.requestFocus();
            return;
        }
        if(uname.isEmpty()){
            username.setError("Username is required !");
            username.requestFocus();
            return;
        }
        if(eMail.isEmpty()){
            email.setError("Email is required !");
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()){
            email.setError("Please enter valid email address!");
            email.requestFocus();
            return;
        }
        if(rid.isEmpty()){
            registrationID.setError("Registration ID is required !");
            registrationID.requestFocus();
            return;
        }
        if(pword.isEmpty() || pword.length() < 6){
            password.setError("Password should be at least 6 characters long !");
            password.requestFocus();
            return;
        }
        if(!pword.equals(cpword)){
            confirmPassword.setError("Password field do not match with Confirm Password field !");
            password.requestFocus();
            return;
        }


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Signing Up ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(eMail, pword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            ModelUser user = new ModelUser(fname, lname, uname, eMail, rid);
                            final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userID)
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FirebaseDatabase.getInstance().getReference("Usernames")
                                                        .child(uname)
                                                        .setValue(new ModelUsernameEmail(eMail, userID))
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){

                                                                    SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("user_login", MODE_PRIVATE);
                                                                    SharedPreferences.Editor editor = preferences.edit();
                                                                    editor.putString("username", uname);
                                                                    editor.putString("email" ,eMail);
                                                                    editor.putString("password", pword);
                                                                    editor.apply();
                                                                    editor.commit();

                                                                    Intent i = new Intent(getActivity(), SplashActivity.class);
                                                                    startActivity(i);
                                                                    getActivity().finish();
                                                                    progressDialog.dismiss();
                                                                }
                                                            }
                                                        });
                                            }else{
                                                Toast.makeText(getActivity(), "Something went wrong ! Check your Internet Connection", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Registration Failed ! Check your Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
