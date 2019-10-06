package com.adityagunjal.sdl_project.adapters;
import android.content.Context;
import android.content.Intent;
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
import com.adityagunjal.sdl_project.models.ModelDraft;
import com.adityagunjal.sdl_project.models.ModelFeed;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AdapterDraft extends RecyclerView.Adapter<AdapterDraft.MyViewHolder>{

    String qText = "a";
    ModelUser user;
    Context context;
    ArrayList<ModelDraft> modelDraftArrayList;

    public AdapterDraft(Context context, ArrayList<ModelDraft> modelDraftArrayList){
        this.context = context;
        this.modelDraftArrayList = modelDraftArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.draft_card, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ModelDraft modelDraft = modelDraftArrayList.get(position);
        String uID  = modelDraft.getUserID();
        String qID = modelDraft.getQuestionID();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("questions").child(qID).child("text");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                qText = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


        holder.question.setText(qText);





        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return modelDraftArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        final Context itemViewContext;

        TextView question;
        public MyViewHolder(View itemView) {
            super(itemView);

            itemViewContext = itemView.getContext();


            question = (TextView) itemView.findViewById(R.id.draft_card_question);


        }
    }
    public void addNewItem(ModelDraft modelDraft){
        modelDraftArrayList.add(modelDraft);
      notifyDataSetChanged();
    }

    public void deleteItem(ModelDraft modelDraft)
    {
        modelDraftArrayList.remove(modelDraft);
        notifyDataSetChanged();
    }

    public void deleteAll()
    {
        modelDraftArrayList.clear();
    }
}
