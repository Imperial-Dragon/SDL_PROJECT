package com.adityagunjal.sdl_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.adityagunjal.sdl_project.adapters.AdapterChat;
import com.adityagunjal.sdl_project.models.ModelChat;
import com.adityagunjal.sdl_project.models.ModelChatUser;
import com.adityagunjal.sdl_project.models.ModelMessage;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.security.Timestamp;
import java.sql.Time;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ModelMessage> modelMessageArrayList = new ArrayList<>();
    AdapterChat adapterChat;

    String userID, chatID;
    ModelChatUser modelChatUser;
    ModelUser modelUser;
    ModelChat modelChat;

    TextView chatUserName;
    CircleImageView userProfilePic;

    EditText messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatUserName = findViewById(R.id.toolbar_username);
        userProfilePic = findViewById(R.id.user_profile_pic);

        Intent i = getIntent();

        if(i.getIntExtra("EXTRA_FLAG", 0) == 1){
            modelUser = (ModelUser)i.getSerializableExtra("EXTRA_USER");
            userID = i.getStringExtra("EXTRA_USER_ID");
            modelChat = (ModelChat) i.getSerializableExtra("EXTRA_CHAT");
            chatID = modelChat.getChatID();

            chatUserName.setText(modelUser.getUsername());
            if(!modelUser.getImagePath().equals("default")){
                FirebaseStorage.getInstance().getReference(modelUser.getImagePath())
                        .getBytes(1024 * 1024)
                        .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> task) {
                                userProfilePic.setImageBitmap(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length));
                            }
                        });
            }

        }else{
            modelChatUser = (ModelChatUser) i.getSerializableExtra("EXTRA_CHAT_USER");
            chatUserName.setText(modelChatUser.getUsername());
            chatID = modelChatUser.getChatID();

        }

        recyclerView = findViewById(R.id.chat_recycler_view);
        adapterChat = new AdapterChat(this, modelMessageArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterChat);

        messageText = findViewById(R.id.entered_message);
        populateRecyclerView();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void populateRecyclerView(){
       FirebaseDatabase.getInstance().getReference("Chats/" + chatID)
                .child("Messages")
                .orderByChild("timestamp")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        ModelMessage modelMessage = dataSnapshot.getValue(ModelMessage.class);

                        adapterChat.addItem(modelMessage);

                        recyclerView.scrollToPosition(adapterChat.getItemCount() - 1);
                        messageText.setText("");
                        messageText.clearFocus();

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        try {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }catch (NullPointerException e){
                            
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void onSendMessageClicked(View view){
        String message = messageText.getText().toString();
        if(message.length() == 0)
            return;

        final ModelMessage modelMessage = new ModelMessage(messageText.getText().toString(), SplashActivity.userInfo.getUserID(), SplashActivity.userInfo.getModelUser().getUsername());
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats/" + chatID).child("Messages").push();
        modelMessage.setMessageID(chatRef.getKey());


        chatRef.setValue(modelMessage);/*.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    adapterChat.addItem(modelMessage);
                }
            }
        });*/

    }

}
