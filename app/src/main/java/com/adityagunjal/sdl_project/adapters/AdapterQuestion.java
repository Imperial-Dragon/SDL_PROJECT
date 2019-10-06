package com.adityagunjal.sdl_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adityagunjal.sdl_project.AnswerQuestionActivity;
import com.adityagunjal.sdl_project.R;
import com.adityagunjal.sdl_project.ShowAnswerActivity;
import com.adityagunjal.sdl_project.models.ModelQuestion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterQuestion extends RecyclerView.Adapter<AdapterQuestion.MyViewHolder>{

    Context context;
    ArrayList<ModelQuestion> modelQuestionArrayList;

    public AdapterQuestion(Context context, ArrayList<ModelQuestion> modelQuestionArrayList){
        this.context = context;
        this.modelQuestionArrayList = modelQuestionArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_card, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(view, new MyClickListener() {
            @Override
            public void answerQuestion(int position) {
                String userID = modelQuestionArrayList.get(position).getUserID();
                String qID = modelQuestionArrayList.get(position).getqID();
                String questionText = modelQuestionArrayList.get(position).getText();
                String username = modelQuestionArrayList.get(position).getUsername();
                String answers = Integer.toString(modelQuestionArrayList.get(position).getAnswers());

                Intent i = new Intent(context, AnswerQuestionActivity.class);
                i.putExtra("EXTRA_QUESTION_ID", qID);
                i.putExtra("EXTRA_QUESTION_TEXT", questionText);
                i.putExtra("EXTRA_USER_ID", userID);
                i.putExtra("EXTRA_USERNAME", username);
                i.putExtra("EXTRA_ANSWERS_COUNT",answers);

                context.startActivity(i);
            }

            @Override
            public void showAnswers(int position) {
                ModelQuestion modelQuestion = modelQuestionArrayList.get(position);

                if(modelQuestion.getAnswers() > 0){
                    Intent i = new Intent(context, ShowAnswerActivity.class);
                    i.putExtra("EXTRA_QUESTION", modelQuestion);
                    i.putExtra("EXTRA_FLAG", "ALL");

                    context.startActivity(i);
                }
            }
        });

        return  myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ModelQuestion modelQuestion = modelQuestionArrayList.get(position);
        holder.user.setText("Asked by " + modelQuestion.getUsername());
        holder.question.setText(modelQuestion.getText());
        String answerString = (modelQuestion.getAnswers() == 0) ? "Not Answers yet": Integer.toString(modelQuestion.getAnswers()) + " Answers";
        holder.answers.setText(answerString);
        holder.askDate.setText(modelQuestion.getDate().substring(0, 16));
    }

    @Override
    public int getItemCount() {
        return modelQuestionArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final Context itemViewContext;
        MyClickListener answerQuestionClickedListener;

        ImageView answerQuestionImage;
        TextView question, user, askDate, answers, answerHere;

        public MyViewHolder(@NonNull View itemView, MyClickListener listener) {
            super(itemView);

            itemViewContext = itemView.getContext();

            answerHere = itemView.findViewById(R.id.recent_answer_text);
            question = itemView.findViewById(R.id.question);
            user = itemView.findViewById(R.id.username);
            askDate = itemView.findViewById(R.id.question_date);
            answers = itemView.findViewById(R.id.no_of_answers);
            answerQuestionImage = itemView.findViewById(R.id.answer_question_symbol);

            this.answerQuestionClickedListener = listener;
            answerQuestionImage.setOnClickListener(this);
            answerHere.setOnClickListener(this);
            answers.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.answer_question_symbol)
                answerQuestionClickedListener.answerQuestion(this.getLayoutPosition());
            if(view.getId() == R.id.no_of_answers)
                answerQuestionClickedListener.showAnswers(this.getLayoutPosition());
        }

    }

    public void addNewItem(ModelQuestion modelQuestion){
        modelQuestionArrayList.add(modelQuestion);
        notifyDataSetChanged();
    }

    public interface MyClickListener{
        void answerQuestion(int position);
        void showAnswers(int position);
    }
}
