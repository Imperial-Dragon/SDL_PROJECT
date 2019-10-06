package com.adityagunjal.sdl_project.ui.recent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.adapters.AdapterQuestion;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class RecentFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ModelQuestion> modelQuestionArrayList = new ArrayList<>();
    AdapterQuestion adapterQuestion;
    LinearLayoutManager layoutManager;

    String userID;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    long offset = 0;
    final int pageLimit = 20;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = view.findViewById(R.id.recent_recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        adapterQuestion = new AdapterQuestion(getActivity(), modelQuestionArrayList);
        recyclerView.setAdapter(adapterQuestion);

        recyclerView.setOnScrollListener(onScrollListener);

        populateRecyclerView();

        return view;
    }

    public void populateRecyclerView(){

        Query getQuestions = FirebaseDatabase.getInstance().getReference("questions")
                .orderByChild("timestamp")
                .limitToFirst(pageLimit);

        getQuestions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String qID = ds.getKey();
                    final ModelQuestion modelQuestion = ds.getValue(ModelQuestion.class);
                    modelQuestion.setqID(qID);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(modelQuestion.getUserID())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.child("username").getValue(String.class);
                                    modelQuestion.setUsername(userName);
                                    adapterQuestion.addNewItem(modelQuestion);
                                    offset = (long) modelQuestion.getTimestamp();

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

    public void loadMoreQuestions(){
        Query getQuestions = FirebaseDatabase.getInstance().getReference("questions")
                .orderByChild("timestamp")
                .limitToFirst(pageLimit)
                .startAt(offset);

        getQuestions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    if(i == 0){
                        i++;
                        continue;
                    }

                    String qID = ds.getKey();
                    final ModelQuestion modelQuestion = ds.getValue(ModelQuestion.class);
                    modelQuestion.setqID(qID);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(modelQuestion.getUserID())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.child("username").getValue(String.class);
                                    modelQuestion.setUsername(userName);
                                    adapterQuestion.addNewItem(modelQuestion);
                                    offset = (long) modelQuestion.getTimestamp();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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
                        loadMoreQuestions();
                    }
                }
            }

        }
    };
}
