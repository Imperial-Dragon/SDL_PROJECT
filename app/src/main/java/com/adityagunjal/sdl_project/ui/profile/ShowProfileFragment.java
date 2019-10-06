package com.adityagunjal.sdl_project.ui.profile;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.adityagunjal.sdl_project.ChatActivity;
import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.SplashActivity;
import com.adityagunjal.sdl_project.helpers.Helpers;
import com.adityagunjal.sdl_project.interfaces.CustomeOnBackPressed;
import com.adityagunjal.sdl_project.interfaces.DataChanged;
import com.adityagunjal.sdl_project.models.ModelChat;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.bumptech.glide.load.model.Headers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ShowProfileFragment extends Fragment implements CustomeOnBackPressed {

    public static final int GET_FROM_GALLERY = 3;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    ModelUser modelUser;
    Bitmap profilePic;
    String userID;

    CircleImageView circleImageView;
    ImageView editProfile;
    TextView username, bio, answerCount, questionCount, name, emailID, registrationID, editProfileText;
    Button saveButton, chatButton;
    ImageButton addPhoto;

    LinearLayout editLayout;

    View rootView;

    boolean isEditEnabled = true;
    boolean isEditModeOn = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Intent i = getActivity().getIntent();

        ModelUser user = (ModelUser)i.getSerializableExtra("EXTRA_USER");
        String uid = i.getStringExtra("EXTRA_USER_ID");

        rootView = inflater.inflate(R.layout.fragment_show_profile, container, false);
        circleImageView = rootView.findViewById(R.id.show_profile_circle_image);
        username = rootView.findViewById(R.id.tv_name);
        bio = rootView.findViewById(R.id.tv_bio);
        questionCount = rootView.findViewById(R.id.question_count);
        answerCount = rootView.findViewById(R.id.answer_count);
        name = rootView.findViewById(R.id.full_name);
        emailID = rootView.findViewById(R.id.user_email);
        registrationID = rootView.findViewById(R.id.user_registration_id);
        editProfile = rootView.findViewById(R.id.edit_profile_button);
        editProfileText = rootView.findViewById(R.id.edit_profile_text);
        chatButton = rootView.findViewById(R.id.chat_button);

        editLayout = rootView.findViewById(R.id.edit_layout);

        editProfile.setOnClickListener(onEditProfileClicked);
        editProfileText.setOnClickListener(onEditProfileClicked);

        circleImageView.setOnClickListener(onProfilePicClicked);

        saveButton = rootView.findViewById(R.id.edit_save_button);
        saveButton.setOnClickListener(onSaveButtonPressed);

        addPhoto = rootView.findViewById(R.id.add_photo_button);
        addPhoto.setOnClickListener(onAddImageClicked);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        if(user == null || uid.equals(SplashActivity.userInfo.getUserID())){
            editLayout.setVisibility(View.VISIBLE);
            isEditEnabled = true;
            editProfile.setVisibility(View.VISIBLE);
            getUserInfo();
        }else{
            isEditEnabled = false;
            editLayout.setVisibility(View.GONE);
            chatButton.setVisibility(View.VISIBLE);
            setUserInfo(user, uid, null);
            chatButton.setOnClickListener(onSendMessageClicked);
        }

        return rootView;
    }

    private void startGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        if (galleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(galleryIntent, 1000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try{
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                }catch (Exception e){

                }
                circleImageView.setImageBitmap(bitmapImage);
            }
        }
    }

    public void getUserInfo() {

        SplashActivity.userInfo.setDataChangedListener(new DataChanged() {
            @Override
            public void onDataChanged(ModelUser modelUser, String userID, Bitmap profilePic) {
                setUserInfo(modelUser, userID, profilePic);
            }
        });

    }

    public void setUserInfo(ModelUser modelUser, String userID, Bitmap profilePic){

        this.modelUser = modelUser;
        this.userID = userID;
        this.profilePic = profilePic;

        if (profilePic == null) {
            if(modelUser.getImagePath().equals("default")){
                circleImageView.setImageResource(R.drawable.ic_profile_icon);
            }else{
                FirebaseStorage.getInstance().getReference(modelUser.getImagePath())
                        .getBytes(1024 * 1024)
                        .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> task) {
                                if(task.isSuccessful())
                                    circleImageView.setImageBitmap(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length));
                                else
                                    circleImageView.setImageResource(R.drawable.ic_profile_icon);
                            }
                        });
            }
        } else {
            circleImageView.setImageBitmap(profilePic);
        }
        username.setText(modelUser.getUsername());
        emailID.setText(modelUser.getEmail());
        name.setText(modelUser.getFirstName() + " " + modelUser.getLastName());
        bio.setText(modelUser.getBio());
        registrationID.setText(modelUser.getRegistrationID());
        questionCount.setText(Integer.toString(modelUser.getQuestionCount()));
        answerCount.setText(Integer.toString(modelUser.getAnswerCount()));

    }

    View.OnClickListener onEditProfileClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            TextInputEditText firstName = rootView.findViewById(R.id.edit_first_name);
            TextInputEditText lastName = rootView.findViewById(R.id.edit_last_name);
            TextInputEditText bio = rootView.findViewById(R.id.edit_bio);
            TextInputEditText editUsername = rootView.findViewById(R.id.edit_username);

            firstName.setText(modelUser.getFirstName());
            lastName.setText(modelUser.getLastName());
            if (!modelUser.getBio().equals("-")) bio.setText(modelUser.getBio());
            editUsername.setText(modelUser.getUsername());

            toggleView();
        }
    };

    View.OnClickListener onSaveButtonPressed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            saveButton.setClickable(false);

            final TextInputEditText firstName = rootView.findViewById(R.id.edit_first_name);
            final TextInputEditText lastName = rootView.findViewById(R.id.edit_last_name);
            final TextInputEditText bio = rootView.findViewById(R.id.edit_bio);
            TextInputEditText editUsername = rootView.findViewById(R.id.edit_username);

            FirebaseDatabase.getInstance().getReference("Users").child(userID).child("firstName").setValue(firstName.getText().toString());
            FirebaseDatabase.getInstance().getReference("Users").child(userID).child("lastName").setValue(lastName.getText().toString());
            FirebaseDatabase.getInstance().getReference("Users").child(userID).child("bio").setValue(bio.getText().toString());

            if(profilePic != null){
                storageReference.child(modelUser.getImagePath()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        uploadImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e){

                    }
                });
            }else{
                uploadImage();
            }

        }
    };

    @Override
    public boolean onBackPressed() {
        if (isEditModeOn) {
            toggleView();
            return true;
        } else {
            return false;
        }
    }

    public View.OnClickListener onAddImageClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
            } else {
                startGallery();
            }
        }
    };

    public void uploadImage(){
        Bitmap bitmap = null;
        try{
             bitmap = ((BitmapDrawable)circleImageView.getDrawable()).getBitmap();
        }catch (Exception e){
            toggleView();
            saveButton.setClickable(true);
            return;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] data = byteArrayOutputStream.toByteArray();

        final String randomUUID = UUID.randomUUID().toString();

        UploadTask uploadTask = storageReference.child("images/users/" + randomUUID).putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(userID)
                        .child("imagePath")
                        .setValue("images/users/"+ randomUUID)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Image uploaded Successfully", Toast.LENGTH_SHORT).show();
                                saveButton.setClickable(true);
                                toggleView();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to upload Image", Toast.LENGTH_SHORT).show();
                saveButton.setClickable(true);
            }
        });
    }

    View.OnClickListener onProfilePicClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Drawable drawable = circleImageView.getDrawable();

            Dialog imageDialogue = new Dialog(getActivity());
            View contentView = getLayoutInflater().inflate(R.layout.image_layout, null);
            ((ImageView)contentView.findViewById(R.id.image_dialogue)).setImageDrawable(drawable);
            imageDialogue.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            imageDialogue.setContentView(contentView);

            imageDialogue.show();

        }
    };

    View.OnClickListener onSendMessageClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String temp;
            if(Helpers.stringCompare(userID, SplashActivity.userInfo.getUserID()) > 0){
                temp = SplashActivity.userInfo.getUserID() + "_" + userID;
            }else{
                temp = userID + "_" + SplashActivity.userInfo.getUserID();
            }

            final String chatID = temp;

            FirebaseDatabase.getInstance().getReference("Chats/" + chatID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue(ModelChat.class) == null){
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(SplashActivity.userInfo.getUserID())
                                        .child("chatsList")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ArrayList<String> chatsList = (ArrayList<String>) dataSnapshot.getValue();
                                                if(chatsList == null){
                                                    chatsList = new ArrayList<>();
                                                }
                                                if(!chatsList.contains(chatID)){
                                                    chatsList.add(chatID);
                                                    dataSnapshot.getRef().setValue(chatsList);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(userID)
                                        .child("chatsList")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ArrayList<String> chatsList = (ArrayList<String>) dataSnapshot.getValue();
                                                if(chatsList == null){
                                                    chatsList = new ArrayList<>();
                                                }
                                                if(!chatsList.contains(chatID)){
                                                    chatsList.add(chatID);
                                                    dataSnapshot.getRef().setValue(chatsList);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                ArrayList<String> participants = new ArrayList<>();
                                participants.add(userID);
                                participants.add(SplashActivity.userInfo.getUserID());

                                final ModelChat chat = new ModelChat(chatID, participants);

                                FirebaseDatabase.getInstance().getReference("Chats")
                                        .child(chatID)
                                        .setValue(chat)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                                    i.putExtra("EXTRA_FLAG", 1);
                                                    i.putExtra("EXTRA_USER_ID", userID);
                                                    i.putExtra("EXTRA_USER", modelUser);
                                                    i.putExtra("EXTRA_CHAT", chat);
                                                    startActivity(i);
                                                    getActivity().finish();

                                                }else{
                                                    Toast.makeText(getActivity(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                ModelChat chat = dataSnapshot.getValue(ModelChat.class);
                                Intent i = new Intent(getActivity(), ChatActivity.class);
                                i.putExtra("EXTRA_FLAG", 1);
                                i.putExtra("EXTRA_USER_ID", userID);
                                i.putExtra("EXTRA_USER", modelUser);
                                i.putExtra("EXTRA_CHAT", chat);
                                startActivity(i);
                                getActivity().finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    };

    public void toggleView(){
        if(isEditModeOn == true){
            addPhoto.setVisibility(View.GONE);

            TextInputLayout editUsernameLayout = rootView.findViewById(R.id.edit_username_layout);
            editUsernameLayout.setVisibility(View.GONE);

            LinearLayout linearLayout2 = rootView.findViewById(R.id.edit_profile_linear_layout);
            linearLayout2.setVisibility(View.GONE);

            LinearLayout linearLayout = rootView.findViewById(R.id.linlay1);
            LinearLayout linearLayout1 = rootView.findViewById(R.id.profile_info_linear_layout);
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            bio.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            editProfileText.setVisibility(View.VISIBLE);

            isEditModeOn = false;
        }else{
            LinearLayout linearLayout = rootView.findViewById(R.id.linlay1);
            LinearLayout linearLayout1 = rootView.findViewById(R.id.profile_info_linear_layout);
            linearLayout.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            bio.setVisibility(View.GONE);
            editProfile.setVisibility(View.GONE);
            editProfileText.setVisibility(View.GONE);

            addPhoto.setVisibility(View.VISIBLE);

            TextInputLayout editUsernameLayout = rootView.findViewById(R.id.edit_username_layout);
            editUsernameLayout.setVisibility(View.VISIBLE);

            LinearLayout linearLayout2 = rootView.findViewById(R.id.edit_profile_linear_layout);
            linearLayout2.setVisibility(View.VISIBLE);

            isEditModeOn = true;
        }
    }


}
