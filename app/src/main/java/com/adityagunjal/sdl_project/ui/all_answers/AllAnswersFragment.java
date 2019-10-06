package com.adityagunjal.sdl_project.ui.all_answers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.AnswerQuestionActivity;
import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.ShowAnswerActivity;
import com.adityagunjal.sdl_project.adapters.AdapterAnswer;
import com.adityagunjal.sdl_project.models.ModelAnswer;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.adityagunjal.sdl_project.models.ModelUsernameEmail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllAnswersFragment extends Fragment implements View.OnClickListener {

    ShowAnswerActivity showAnswerActivity;

    RecyclerView recyclerView;
    ArrayList<ModelAnswer> modelAnswerArrayList = new ArrayList<>();
    AdapterAnswer adapterAnswer;

    ModelQuestion modelQuestion;

    TextView questionText, askedBy, answerButtonText;
    ImageView answerButton;


    int pageLimit = 10;
    String offset;

    public AllAnswersFragment(ShowAnswerActivity showAnswerActivity){
        this.showAnswerActivity = showAnswerActivity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        modelQuestion = (ModelQuestion) bundle.getSerializable("ModelQuestion");

        View view = inflater.inflate(R.layout.fragment_all_answers, container, false);

        recyclerView = view.findViewById(R.id.all_answers_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterAnswer = new AdapterAnswer(showAnswerActivity, modelAnswerArrayList);
        recyclerView.setAdapter(adapterAnswer);

        questionText = view.findViewById(R.id.all_answers_question);
        askedBy = view.findViewById(R.id.asked_by);
        answerButtonText = view.findViewById(R.id.answer_question_text);
        answerButton = view.findViewById(R.id.answer_question_symbol);

        answerButton.setOnClickListener(this);

        questionText.setText(modelQuestion.getText());

        FirebaseDatabase.getInstance().getReference("Usernames")
                .orderByChild("uid")
                .equalTo(modelQuestion.getUserID())
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            modelQuestion.setUsername(ds.getKey());
                            askedBy.setText("Asked by " + ds.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        populateRecyclerView();

        return  view;
    }

    public void populateRecyclerView(){

        Query query = FirebaseDatabase.getInstance().getReference("Answers")
                        .orderByChild("questionID")
                        .equalTo(modelQuestion.getqID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelAnswer modelAnswer = ds.getValue(ModelAnswer.class);
                    modelAnswer.setAnswerID(ds.getKey());
                    adapterAnswer.addNewItem(modelAnswer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.answer_question_symbol || view.getId() == R.id.answer_question_text){
            Intent i = new Intent(getActivity(), AnswerQuestionActivity.class);
            i.putExtra("EXTRA_FLAG","a");
            i.putExtra("EXTRA_QUESTION_ID", modelQuestion.getqID());
            i.putExtra("EXTRA_QUESTION_TEXT", modelQuestion.getText());
            i.putExtra("EXTRA_USER_ID", modelQuestion.getUserID());
            i.putExtra("EXTRA_USERNAME", modelQuestion.getUsername());
            i.putExtra("EXTRA_ANSWERS_COUNT",modelQuestion.getAnswers());

            getActivity().startActivity(i);
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapterAnswer.clearList();
    }
}
