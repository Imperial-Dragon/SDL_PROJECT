package com.adityagunjal.sdl_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.MainActivity;
import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.ShowAnswerActivity;
import com.adityagunjal.sdl_project.SplashActivity;
import com.adityagunjal.sdl_project.models.ModelAnswer;
import com.adityagunjal.sdl_project.models.ModelFeed;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {

    Context context;
    ArrayList<ModelFeed> modelFeedArrayList;

    String userID;

    public AdapterFeed(Context context, ArrayList<ModelFeed> modelFeedArrayList){
        this.context = context;
        this.modelFeedArrayList = modelFeedArrayList;
        this.userID = SplashActivity.userInfo.getUserID();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_card, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ModelFeed modelFeed = modelFeedArrayList.get(position);

        final ModelUser modelUser = modelFeed.getUser();
        final ModelQuestion modelQuestion = modelFeed.getQuestion();
        final ModelAnswer modelAnswer = modelFeed.getAnswer();

        float factor = holder.answer.getContext().getResources().getDisplayMetrics().density;

        holder.name.setText(modelUser.getUsername());
        holder.question.setText(modelQuestion.getText());
        holder.lastUpdated.setText(modelAnswer.getDate().substring(0, 16));
        holder.likes.setText(Integer.toString(modelAnswer.getUpvotes()));
        holder.dislikes.setText(Integer.toString(modelAnswer.getDownvotes()));
        holder.comments.setText(Integer.toString(modelAnswer.getComments()));

        FirebaseDatabase.getInstance().getReference("Answers")
                .child(modelAnswer.getAnswerID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        modelAnswer.setUpvotes(dataSnapshot.child("upvotes").getValue(Integer.class));
                        modelAnswer.setDownvotes(dataSnapshot.child("downvotes").getValue(Integer.class));
                        modelAnswer.setComments(dataSnapshot.child("comments").getValue(Integer.class));

                        holder.likes.setText(Integer.toString(modelAnswer.getUpvotes()));
                        holder.dislikes.setText(Integer.toString(modelAnswer.getDownvotes()));
                        holder.comments.setText(Integer.toString(modelAnswer.getComments()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("Likes")
                .child(modelAnswer.getAnswerID())
                .child(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.getValue(Boolean.class)){
                                holder.upvoteImage.setImageResource(R.drawable.ic_upvote_active);
                            }else{
                                holder.downvoteImage.setImageResource(R.drawable.ic_downvote_active);
                            }
                        }
                        else{
                            holder.upvoteImage.setImageResource(R.drawable.ic_upvote_icon);
                            holder.downvoteImage.setImageResource(R.drawable.ic_downvote_icon);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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

        FirebaseStorage.getInstance().getReference(modelUser.getImagePath())
                .getBytes(1024 * 1024)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if(task.isSuccessful()) {
                            Bitmap image = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                            holder.profilePic.setImageBitmap(image);
                        }else{
                            holder.profilePic.setImageResource(R.drawable.ic_profile_pic);
                        }
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ShowAnswerActivity.class);
                i.putExtra("EXTRA_USER", modelUser);
                i.putExtra("EXTRA_QUESTION", modelQuestion);
                i.putExtra("EXTRA_ANSWER",  modelAnswer);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelFeedArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        final Context itemViewContext;

        TextView name, answer, question, lastUpdated, likes, dislikes, comments;
        CircleImageView profilePic;
        ImageView answerImage, upvoteImage, downvoteImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemViewContext = itemView.getContext();

            profilePic = itemView.findViewById(R.id.card_profile_pic);
            answerImage = itemView.findViewById(R.id.card_answer_image);

            name = itemView.findViewById(R.id.card_user_name);
            answer = itemView.findViewById(R.id.card_answer_text);
            question = itemView.findViewById(R.id.card_question);
            lastUpdated = itemView.findViewById(R.id.card_update_info);
            likes = itemView.findViewById(R.id.feed_answer_like);
            dislikes = itemView.findViewById(R.id.feed_answer_dislike);
            comments = itemView.findViewById(R.id.feed_answer_comment);
            upvoteImage =itemView.findViewById(R.id.card_upvote);
            downvoteImage = itemView.findViewById(R.id.card_downvote);

        }

    }

    public void addNewItem(ModelFeed modelFeed){
        modelFeedArrayList.add(modelFeed);
        notifyDataSetChanged();
    }
}
