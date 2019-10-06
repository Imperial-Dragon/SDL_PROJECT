package com.adityagunjal.sdl_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.ChatActivity;
import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.models.ModelChatUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChatUser extends RecyclerView.Adapter<AdapterChatUser.MyViewHolder> {

    Context context;
    ArrayList<ModelChatUser> modelChatUserArrayList;

    public AdapterChatUser(Context context, ArrayList<ModelChatUser> modelChatUserArrayList){
        this.context = context;
        this.modelChatUserArrayList = modelChatUserArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chat, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ModelChatUser modelChatUser = modelChatUserArrayList.get(position);

        holder.username.setText(modelChatUser.getUsername());

        try{
            if(modelChatUser.getLastMessage().length() > 26){
                holder.lastMessage.setText(modelChatUser.getLastMessage().substring(0, 25).trim() + " ...");
            }else{
                holder.lastMessage.setText(modelChatUser.getLastMessage());
            }
            holder.lastUpdated.setText(modelChatUser.getLastUpdated().substring(3, 10).trim());
        }catch (NullPointerException e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("EXTRA_CHAT_USER", modelChatUser);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  modelChatUserArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView username, lastMessage, lastUpdated;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.chat_profile_icon);
            username = itemView.findViewById(R.id.chat_username);
            lastMessage = itemView.findViewById(R.id.chat_last_message);
            lastUpdated = itemView.findViewById(R.id.chat_last_updated);
        }
    }

    public void addNewUser(ModelChatUser modelChatUser){
        modelChatUserArrayList.add(modelChatUser);
        notifyDataSetChanged();
    }
}
