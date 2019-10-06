package com.adityagunjal.sdl_project.ui.comment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.SplashActivity;
import com.adityagunjal.sdl_project.adapters.AdapterComment;
import com.adityagunjal.sdl_project.models.ModelComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class CommentFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    ArrayList<ModelComment> modelCommentArrayList = new ArrayList<>();
    AdapterComment adapter;
    String answerID;

    ArrayList<String> commentIDs = new ArrayList<>();

    DatabaseReference commentRef;

    int pageLimit = 10;
    long offset = 0;

    TextInputEditText textInputEditText;
    ImageView postButton;

    LinearLayoutManager layoutManager;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View commentFragmentView = inflater.inflate(R.layout.fragment_comment, container, false);

        Bundle bundle = getArguments();
        answerID = bundle.getString("EXTRA_ANSWER_ID");

        textInputEditText = commentFragmentView.findViewById(R.id.comment_text);
        postButton = commentFragmentView.findViewById(R.id.post_comment_button);
        postButton.setOnClickListener(this);

        recyclerView = commentFragmentView.findViewById(R.id.recycler_view_comments);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AdapterComment(getActivity(), modelCommentArrayList);

        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(onScrollListener);

        populateRecyclerView();
        onNewCommentAdded();

        return commentFragmentView;
    }

    private void onNewCommentAdded() {

        commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(answerID);
        commentRef.addChildEventListener(onCommentAdded);

    }

    public void populateRecyclerView(){
        FirebaseDatabase.getInstance().getReference("Comments")
                .child(answerID)
                .orderByChild("timestamp")
                .limitToFirst(pageLimit)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            ModelComment comment = ds.getValue(ModelComment.class);

                            if(!commentIDs.contains(comment.getCommentID())){
                                adapter.addNewItem(comment);
                                commentIDs.add(comment.getCommentID());
                            }

                            offset = (long) comment.getTimestamp();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void loadMoreComments(){
        FirebaseDatabase.getInstance().getReference("Comments")
                .child(answerID)
                .orderByChild("timestamp")
                .limitToFirst(pageLimit)
                .startAt(offset)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            if(i == 0){
                                i++;
                                continue;
                            }

                            ModelComment comment = ds.getValue(ModelComment.class);

                            if(!commentIDs.contains(comment.getCommentID())){
                                adapter.addNewItem(comment);
                                commentIDs.add(comment.getCommentID());
                            }

                            offset = (long) comment.getTimestamp();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.post_comment_button){
            String commentText = textInputEditText.getText().toString();
            if(commentText.equals(""))
                return;

            textInputEditText.setText("");

            postButton.setClickable(false);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(answerID).push();

            String commentID = ref.getKey();

            ModelComment comment = new ModelComment(commentID, commentText,  SplashActivity.userInfo.getUserID());

            ref.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    postButton.setClickable(true);
                }
            });

            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }catch (NullPointerException e){

            }

        }
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
                        loadMoreComments();
                    }
                }
            }

        }
    };

    ChildEventListener onCommentAdded = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            ModelComment modelComment = dataSnapshot.getValue(ModelComment.class);

            if(!commentIDs.contains(modelComment.getCommentID())){
                adapter.addNewItem(modelComment);
                commentIDs.add(modelComment.getCommentID());
            }

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        commentRef.removeEventListener(onCommentAdded);
    }
}
