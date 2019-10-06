package com.adityagunjal.sdl_project.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.models.ModelComment;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.MyViewHolder> {

    Context context;
    ArrayList<ModelComment> modelCommentArrayList;

    public AdapterComment(Context context, ArrayList<ModelComment> modelCommentArrayList){
        this.context = context;
        this.modelCommentArrayList = modelCommentArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ModelComment modelComment = modelCommentArrayList.get(position);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(modelComment.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);

                        holder.username.setText(modelUser.getUsername());

                        FirebaseStorage.getInstance().getReference(modelUser.imagePath)
                                .getBytes(1024 * 1024)
                                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                    @Override
                                    public void onComplete(@NonNull Task<byte[]> task) {
                                        if(task.isSuccessful()) {
                                            holder.profilePic.setImageBitmap(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length));
                                        } else {
                                            holder.profilePic.setImageResource(R.drawable.ic_profile_pic);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.comment.setText(modelComment.getComment());
        holder.lastUpdated.setText(modelComment.getLastUpdated().substring(0, 16));
    }

    @Override
    public int getItemCount() {
        return modelCommentArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profilePic;
        TextView comment;
        TextView username;
        TextView lastUpdated;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.comment_profile_pic);
            username = itemView.findViewById(R.id.comment_username);
            comment = itemView.findViewById(R.id.comment);
            lastUpdated = itemView.findViewById(R.id.comment_last_updated);
        }
    }

    public void addNewItem(ModelComment modelComment){
        modelCommentArrayList.add(modelComment);
        notifyDataSetChanged();
    }


}
