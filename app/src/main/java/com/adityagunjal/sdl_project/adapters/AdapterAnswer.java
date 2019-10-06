package com.adityagunjal.sdl_project.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.ShowAnswerActivity;
import com.adityagunjal.sdl_project.models.ModelAnswer;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAnswer extends RecyclerView.Adapter<AdapterAnswer.MyViewHolder> {

    Context context;
    ArrayList<ModelAnswer> modelAnswerArrayList;
    ArrayList<ModelUser> modelUserArrayList = new ArrayList<>();

    ModelUser user;

    public AdapterAnswer(Context context, ArrayList<ModelAnswer> modelAnswerArrayList){
        this.context = context;
        this.modelAnswerArrayList = modelAnswerArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_card_2, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return  myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ModelAnswer modelAnswer = modelAnswerArrayList.get(position);

        holder.likes.setText(Integer.toString(modelAnswer.getUpvotes()));
        holder.dislikes.setText(Integer.toString(modelAnswer.getDownvotes()));
        holder.comments.setText(Integer.toString(modelAnswer.getComments()));
        holder.lastUpdated.setText(modelAnswer.getDate().substring(0, 16));

        float factor = holder.answer.getContext().getResources().getDisplayMetrics().density;

        HashMap<String, String> answerMap = modelAnswer.getAnswer();

        LinkedHashMap<Integer, String> sortedAnswerMap = new LinkedHashMap<>();

        ArrayList<Integer> sortedKeys = new ArrayList<>();

        for(String key : answerMap.keySet()){
            sortedKeys.add(Integer.parseInt(key.substring(1)));
        }

        Collections.sort(sortedKeys);

        for(int i : sortedKeys){
            sortedAnswerMap.put(i, answerMap.get("k" + Integer.toString(i)));
        }

        Iterator answerIterator = sortedAnswerMap.entrySet().iterator();

        String answerText = "";
        int flag0 = 0, flag1 = 0;
        while(answerIterator.hasNext()){
            Map.Entry<Integer, String> answerElement = (Map.Entry) answerIterator.next();
            int key = answerElement.getKey();
            if(key % 2 == 0){
                String text = answerElement.getValue();
                for(int i = 0; i < text.length() && answerText.length() < 100; i++){
                    answerText += text.charAt(i);
                }
            }else {
                if(flag1 == 0){
                    FirebaseStorage.getInstance().getReference(answerElement.getValue())
                            .getBytes(1024 * 1024)
                            .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                @Override
                                public void onComplete(@NonNull Task<byte[]> task) {
                                    holder.answerImage.setImageBitmap(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length));
                                    holder.answerImage.setVisibility(View.VISIBLE);
                                }
                            });
                    flag1 = 1;
                }
            }
            if(flag0 == 1 && flag1 == 1){
                break;
            }
        }

        if(flag1 == 1){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = (int)(factor * 10);
            holder.answer.setLayoutParams(layoutParams);
        }

        holder.answer.setText(answerText + " ...");

        final String userID = modelAnswer.getUserID();

        FirebaseDatabase.getInstance().getReference("Users/"+userID+"/username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.name.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("Users/"+userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        modelUserArrayList.add(user);
                        FirebaseStorage.getInstance().getReference(user.getImagePath())
                                .getBytes(1024 * 1024)
                                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                    @Override
                                    public void onComplete(@NonNull Task<byte[]> task) {
                                        if(task.isSuccessful()) {
                                            holder.profilePic.setImageBitmap(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length));
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ShowAnswerActivity)context).showAnswer(modelAnswer, modelUserArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelAnswerArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        final Context itemViewContext;

        TextView name, answer, lastUpdated, likes, dislikes, comments;
        CircleImageView profilePic;
        ImageView answerImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemViewContext = itemView.getContext();

            profilePic = itemView.findViewById(R.id.all_answer_profile_pic);
            answerImage = itemView.findViewById(R.id.imageView);

            name = itemView.findViewById(R.id.all_answer_user_name);
            answer = itemView.findViewById(R.id.all_answer_text);
            lastUpdated = itemView.findViewById(R.id.all_answer_update_info);
            likes = itemView.findViewById(R.id.all_answer_like);
            dislikes = itemView.findViewById(R.id.all_answer_dislike);
            comments = itemView.findViewById(R.id.all_answer_comment);
        }
    }

    public void addNewItem(ModelAnswer modelAnswer){
        modelAnswerArrayList.add(modelAnswer);
        notifyDataSetChanged();
    }

    public void clearList(){
        modelAnswerArrayList.clear();
        notifyDataSetChanged();
    }


}
