package com.adityagunjal.sdl_project.ui.one_answer;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adityagunjal.sdl_project.ProfileActivity;
import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.SplashActivity;
import com.adityagunjal.sdl_project.models.ModelAnswer;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

public class OneAnswerFragment extends Fragment implements View.OnClickListener {

    ModelUser modelUser;
    ModelQuestion modelQuestion;
    ModelAnswer modelAnswer;

    TextView questionText, username, date, upvoteCount, downvoteCount, commentCount, allAnswersCount;
    LinearLayout answerLinearLayout;

    RelativeLayout userInfo;

    ImageView upvoteImage, downvoteImage;
    CircleImageView profilePic;

    DatabaseReference answerRef;

    boolean isAnswerUpvoted = false;
    boolean isAnswerDownvoted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_one_answer, container, false);

        Bundle bundle = this.getArguments();

        modelUser = (ModelUser) bundle.getSerializable("ModelUser");
        modelQuestion = (ModelQuestion) bundle.getSerializable("ModelQuestion");
        modelAnswer = (ModelAnswer) bundle.getSerializable("ModelAnswer");

        questionText = rootView.findViewById(R.id.one_answer_question);
        username = rootView.findViewById(R.id.one_answer_user_name);
        date = rootView.findViewById(R.id.one_answer_date);
        upvoteCount = rootView.findViewById(R.id.one_answer_upvote_count);
        downvoteCount = rootView.findViewById(R.id.one_answer_downvote_count);
        commentCount = rootView.findViewById(R.id.one_answer_comment_count);
        allAnswersCount = rootView.findViewById(R.id.total_answers_count);

        answerLinearLayout = rootView.findViewById(R.id.one_answer_linear_layout);
        userInfo = rootView.findViewById(R.id.one_answer_rel);

        upvoteImage = rootView.findViewById(R.id.one_answer_upvote_image);
        downvoteImage = rootView.findViewById(R.id.one_answer_downvote_image);
        profilePic = rootView.findViewById(R.id.one_answer_profile_pic);

        upvoteImage.setOnClickListener(this);
        downvoteImage.setOnClickListener(this);

        userInfo.setOnClickListener(showProfile);

        setInfo();

        return rootView;
    }

    public void setInfo(){
        questionText.setText(modelQuestion.getText());
        username.setText(modelUser.getUsername());
        date.setText(modelAnswer.getDate());
        upvoteCount.setText(Integer.toString(modelAnswer.getUpvotes()));
        downvoteCount.setText(Integer.toString(modelAnswer.getDownvotes()));
        commentCount.setText(Integer.toString(modelAnswer.getComments()));

        FirebaseDatabase.getInstance().getReference("Likes")
                .child(modelAnswer.getAnswerID())
                .child(SplashActivity.userInfo.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if((dataSnapshot.getValue(Boolean.class))) {
                                upvoteImage.setImageResource(R.drawable.ic_upvote_active);
                                isAnswerUpvoted = true;
                            }else{
                                downvoteImage.setImageResource(R.drawable.ic_downvote_active);
                                isAnswerDownvoted = true;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        float factor =  getContext().getResources().getDisplayMetrics().density;

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

        while(answerIterator.hasNext()){
            Map.Entry<Integer, String> answerElement = (Map.Entry) answerIterator.next();
            int key = answerElement.getKey();

            if(key % 2 == 0){
                TextView textView = new TextView(answerLinearLayout.getContext());
                textView.setText(answerElement.getValue());
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(17);
                answerLinearLayout.addView(textView);
            }else{
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = (int)(factor * 7);
                layoutParams.bottomMargin = (int)(factor * 7);
                final ImageView imageView = new ImageView(answerLinearLayout.getContext());
                imageView.setLayoutParams(layoutParams);
                answerLinearLayout.addView(imageView);

                FirebaseStorage.getInstance().getReference(answerElement.getValue())
                        .getBytes(1024 * 1024)
                        .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> task) {
                                if(task.isSuccessful()){
                                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length));
                                }
                            }
                        });
            }
        }

        FirebaseStorage.getInstance().getReference(modelUser.getImagePath())
                .getBytes(1024 * 1024)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if(task.isSuccessful()){
                            profilePic.setImageBitmap(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length));
                        }
                    }
                });

        answerRef = FirebaseDatabase.getInstance().getReference("Answers/" + modelAnswer.getAnswerID());

        answerRef.addValueEventListener(listener);
    }

    View.OnClickListener showProfile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(getActivity(), ProfileActivity.class);
            i.putExtra("EXTRA_USER", modelUser);
            i.putExtra("EXTRA_USER_ID", modelAnswer.getUserID());
            startActivity(i);
        }
    };

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ModelAnswer currentAnswer = dataSnapshot.getValue(ModelAnswer.class);
            try{
                currentAnswer.setAnswerID(dataSnapshot.getKey());
                modelAnswer = currentAnswer;

                upvoteCount.setText(Integer.toString(currentAnswer.getUpvotes()));
                downvoteCount.setText(Integer.toString(currentAnswer.getDownvotes()));
                commentCount.setText(Integer.toString(currentAnswer.getComments()) + " comments");
            }catch (NullPointerException e){

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.one_answer_upvote_image && !isAnswerDownvoted){

            if(isAnswerUpvoted){
                isAnswerUpvoted = false;
                upvoteImage.setImageResource(R.drawable.ic_upvote_icon);
                modelAnswer.setUpvotes(modelAnswer.getUpvotes() - 1);
            }else{
                isAnswerUpvoted = true;
                upvoteImage.setImageResource(R.drawable.ic_upvote_active);
                modelAnswer.setUpvotes(modelAnswer.getUpvotes() + 1);
            }

            upvoteCount.setText(Integer.toString(modelAnswer.getUpvotes()));

            upvoteImage.setClickable(false);

            FirebaseDatabase.getInstance().getReference("Likes")
                    .child(modelAnswer.getAnswerID())
                    .child(SplashActivity.userInfo.getUserID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                dataSnapshot.getRef().removeValue();
                            }else{
                                dataSnapshot.getRef().setValue(true);
                            }
                            FirebaseDatabase.getInstance().getReference("Answers")
                                    .child(modelAnswer.getAnswerID())
                                    .child("upvotes")
                                    .setValue(modelAnswer.getUpvotes());
                            upvoteImage.setClickable(true);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            upvoteImage.setClickable(true);
                        }
                    });
        }
        if(view.getId() == R.id.one_answer_downvote_image && !isAnswerUpvoted){

            if(isAnswerDownvoted){
                isAnswerDownvoted = false;
                downvoteImage.setImageResource(R.drawable.ic_downvote_icon);
                modelAnswer.setDownvotes(modelAnswer.getDownvotes() - 1);
            } else {
                isAnswerDownvoted = true;
                downvoteImage.setImageResource(R.drawable.ic_downvote_active);
                modelAnswer.setDownvotes(modelAnswer.getDownvotes() + 1);
            }

            downvoteCount.setText(Integer.toString(modelAnswer.getDownvotes()));

            downvoteImage.setClickable(false);

            FirebaseDatabase.getInstance().getReference("Likes")
                    .child(modelAnswer.getAnswerID())
                    .child(SplashActivity.userInfo.getUserID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                dataSnapshot.getRef().removeValue();
                            }else{
                                dataSnapshot.getRef().setValue(false);
                            }
                            FirebaseDatabase.getInstance().getReference("Answers")
                                    .child(modelAnswer.getAnswerID())
                                    .child("downvotes")
                                    .setValue(modelAnswer.getDownvotes());
                            downvoteImage.setClickable(true);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            downvoteImage.setClickable(true);
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        answerRef.removeEventListener(listener);
    }
}
