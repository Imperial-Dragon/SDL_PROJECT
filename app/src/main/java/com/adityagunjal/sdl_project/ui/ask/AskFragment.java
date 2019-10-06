package com.adityagunjal.sdl_project.ui.ask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AskFragment extends Fragment {

    EditText editAsk;
    Button buttonPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ask, container, false);

        editAsk = view.findViewById(R.id.edit_ask);
        buttonPost = view.findViewById(R.id.button_post);

        buttonPost.setOnClickListener(onPostButtonClicked);

        return view;
    }

    public View.OnClickListener onPostButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String text = editAsk.getText().toString().trim();

            if(text == null || text.equals("")){
                return;
            }

            Calendar c = Calendar.getInstance();

            String date = c.getTime().toString();

            final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            final ModelQuestion modelQuestion = new ModelQuestion(text, date, userID, 0);

            final String questionID = UUID.randomUUID().toString();

            FirebaseDatabase.getInstance().getReference("questions")
                    .child(questionID)
                    .setValue(modelQuestion)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(userID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                                                ArrayList<String> questionsArrayList;
                                                try{
                                                    questionsArrayList = modelUser.getQuestionsArrayList();
                                                    questionsArrayList.add(questionID);
                                                }catch(NullPointerException e){
                                                    questionsArrayList = new ArrayList<>();
                                                    questionsArrayList.add(questionID);
                                                }
                                                modelUser.setQuestionCount(modelUser.getQuestionCount() + 1);
                                                modelUser.setQuestionsArrayList(questionsArrayList);
                                                FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(userID)
                                                        .setValue(modelUser)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    editAsk.setText("");
                                                                    editAsk.clearFocus();
                                                                    Toast.makeText(getActivity(), "Question Posted Successfully", Toast.LENGTH_SHORT).show();

                                                                }else{
                                                                    Toast.makeText(getActivity(), "Failed to post question", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                            }else{
                                Toast.makeText(getActivity(), "Failed to post question", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };
}
