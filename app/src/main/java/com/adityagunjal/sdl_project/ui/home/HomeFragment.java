package com.adityagunjal.sdl_project.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.SplashActivity;
import com.adityagunjal.sdl_project.adapters.AdapterFeed;
import com.adityagunjal.sdl_project.models.ModelAnswer;
import com.adityagunjal.sdl_project.models.ModelFeed;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ModelFeed> modelFeedArrayList = new ArrayList<>();
    AdapterFeed adapterFeed;

    HashSet<String> currentFeedQuestions = new HashSet<>();

    LinearLayoutManager layoutManager;

    int pageLimit = 8;
    long offset = 0;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean self = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapterFeed = new AdapterFeed(getActivity(), modelFeedArrayList);
        recyclerView.setAdapter(adapterFeed);

        recyclerView.setOnScrollListener(onScrollListener);

        populateRecyclerView();

        return  view;
    }

    public void populateRecyclerView() {

        self = true;

        Query query = FirebaseDatabase.getInstance().getReference("Answers").orderByChild("timestamp").limitToFirst(pageLimit);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String answerID = ds.getKey();
                    final ModelAnswer modelAnswer = ds.getValue(ModelAnswer.class);
                    modelAnswer.setAnswerID(answerID);

                    if(!currentFeedQuestions.contains(modelAnswer.getQuestionID())){

                        currentFeedQuestions.add(modelAnswer.getQuestionID());

                        final String questionID = modelAnswer.getQuestionID();

                        if(!modelAnswer.getUserID().equals(SplashActivity.userInfo.getUserID())){
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(modelAnswer.getUserID())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            final ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                                            FirebaseDatabase.getInstance().getReference("questions")
                                                    .child(questionID)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            ModelQuestion modelQuestion = dataSnapshot.getValue(ModelQuestion.class);
                                                            modelQuestion.setqID(questionID);

                                                            ModelFeed modelFeed = new ModelFeed(modelQuestion, modelUser, modelAnswer);

                                                            adapterFeed.addNewItem(modelFeed);
                                                            offset = (long) modelAnswer.getTimestamp();
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void loadMoreFeed(){
        Query query = FirebaseDatabase.getInstance().getReference("Answers")
                .orderByChild("timestamp")
                .limitToFirst(pageLimit)
                .startAt(offset);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    if(i == 0){
                        i++;
                        continue;
                    }

                    String answerID = ds.getKey();
                    final ModelAnswer modelAnswer = ds.getValue(ModelAnswer.class);
                    modelAnswer.setAnswerID(answerID);

                    if(!currentFeedQuestions.contains(modelAnswer.getQuestionID())){

                        currentFeedQuestions.add(modelAnswer.getQuestionID());

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(modelAnswer.getUserID())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                                        FirebaseDatabase.getInstance().getReference("questions")
                                                .child(modelAnswer.getQuestionID())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        ModelQuestion modelQuestion = dataSnapshot.getValue(ModelQuestion.class);

                                                        ModelFeed modelFeed = new ModelFeed(modelQuestion, modelUser, modelAnswer);

                                                        adapterFeed.addNewItem(modelFeed);
                                                        offset = (long) modelAnswer.getTimestamp();
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
                loading = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(dy > 0)
            {
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                if (loading)
                {
                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                    {
                        loading = false;
                        loadMoreFeed();
                    }
                }
            }

        }
    };
}
