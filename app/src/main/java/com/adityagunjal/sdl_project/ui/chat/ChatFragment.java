package com.adityagunjal.sdl_project.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.SplashActivity;
import com.adityagunjal.sdl_project.adapters.AdapterChatUser;
import com.adityagunjal.sdl_project.models.ModelChat;
import com.adityagunjal.sdl_project.models.ModelChatUser;
import com.adityagunjal.sdl_project.models.ModelMessage;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ModelChatUser> modelChatUserArrayList = new ArrayList<>();
    AdapterChatUser adapterChatUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.chat_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterChatUser = new AdapterChatUser(getActivity(), modelChatUserArrayList);

        recyclerView.setAdapter(adapterChatUser);
        populateRecyclerView();

        return  view;
    }

    public void populateRecyclerView(){

        final ArrayList<String> chats = SplashActivity.userInfo.getModelUser().getChatsList();
        //Toast.makeText(getActivity(), String.valueOf(chats), Toast.LENGTH_SHORT).show();
        if(chats != null){
            for(int i = 0; i < chats.size(); i++){
                final String chatID = chats.get(i);
                FirebaseDatabase.getInstance().getReference("Chats")
                        .child(chatID)
                        .orderByChild("timestamp")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final ModelChat modelChat = dataSnapshot.getValue(ModelChat.class);
                                final ModelChatUser modelChatUser = new ModelChatUser();
                                dataSnapshot.getRef().child("Messages").orderByChild("timestamp")
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ModelMessage message = new ModelMessage();
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    message = ds.getValue(ModelMessage.class);
                                                }
                                                modelChatUser.setLastMessage(message.getText());
                                                modelChatUser.setChatID(chatID);
                                                modelChatUser.setLastUpdated(message.getDate());
                                                if(modelChat.getParticipants().get(0).equals(SplashActivity.userInfo.getUserID())){
                                                    modelChatUser.setUserID(modelChat.getParticipants().get(1));
                                                    FirebaseDatabase.getInstance().getReference("Users/" + modelChat.getParticipants().get(1))
                                                            .child("username")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    modelChatUser.setUsername(dataSnapshot.getValue(String.class));
                                                                    adapterChatUser.addNewUser(modelChatUser);
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                }else{
                                                    modelChatUser.setUserID(modelChat.getParticipants().get(0));
                                                    FirebaseDatabase.getInstance().getReference("Users/" + modelChat.getParticipants().get(0))
                                                            .child("username")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    modelChatUser.setUsername(dataSnapshot.getValue(String.class));
                                                                    adapterChatUser.addNewUser(modelChatUser);
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
        else{

        }
    }

    public void getChats(){

    }

}
