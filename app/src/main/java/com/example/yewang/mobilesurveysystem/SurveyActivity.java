package com.example.yewang.mobilesurveysystem;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SurveyActivity extends AppCompatActivity {
    private static final String TAG = "Group-project";
    ArrayList<String> questions;
    Button nextBtn, backBtn;
    int currentQuestion = 0;
    RadioGroup rg;
    int[] answers;
    boolean back = true;
    String surveyName;
    String surveyCoords;
    String user;
    ArrayList<DataSnapshot> numUsers;
    ArrayList<DataSnapshot> scores;
    boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_activitiy);


        surveyCoords = getIntent().getStringExtra("location");
        Log.i(TAG, "testing location from QR scan: " + surveyCoords);
        //surveyCoords = "123456789 , 123456789";
        scores = new ArrayList<>();
        numUsers = new ArrayList<>();
        questions = new ArrayList<String>();
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setText("Next");
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText("Back");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentQuestion > 0) {
                    nextBtn.setText("Next");
                    currentQuestion--;
                    back = true;
                    updateQuestion(currentQuestion);
                }
            }
        });

        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("New Survey");
        dbr.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i(TAG ,"count "+snapshot.getChildrenCount());
                Log.i(TAG, "user from intent: " + getIntent().getStringExtra("user"));

                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    if (userSnapshot.getKey().equals(getIntent().getStringExtra("user"))) {
                        DataSnapshot coordSnapshot = userSnapshot.child(surveyCoords);
                        for (DataSnapshot surv : coordSnapshot.getChildren()) {
                            surveyName = surv.getKey();
                            getSupportActionBar().setTitle(surveyName);
                        }
                        DataSnapshot surv = coordSnapshot.child(surveyName);
                        user = userSnapshot.getKey();

                        for (DataSnapshot nums : surv.getChildren()) {
                            for (DataSnapshot ques : nums.getChildren()) {
                                if (first) {
                                    scores.add(ques);
                                    String post = ques.getKey();
                                    Log.i(TAG, post);
                                    questions.add(post);
                                    Log.i(TAG, Integer.toString(questions.size()));
                                }
                            }
                        }
                        break;
                    }
                }

                answers = new int[questions.size()];
                TextView quesView = (TextView) findViewById(R.id.questionView);
                quesView.setText(questions.get(0));
                Log.i(TAG,"Updating");
                if(first) {
                    updateQuestion(0);
                    first = false;
                }
                //create an int array for the number of questions
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.i("The read failed: " ,firebaseError.getMessage());
            }
        });

        rg = findViewById(R.id.optionsGroup);

        for(int i =0; i < 5; i++){
            RadioButton button = new RadioButton(this);
            button.setId(i+1);
            button.setText(Integer.toString(i+1));
            rg.addView(button);
        }
    }

    public void updateQuestion(int i){
       if(rg.getCheckedRadioButtonId() == -1 && !back){
            Toast.makeText(getApplicationContext(), "Please select an option",
                    Toast.LENGTH_LONG).show();
            currentQuestion--;
            return;
        }else{
           if(!back)
               answers[i-1] = rg.getCheckedRadioButtonId();
           else
               rg.check(answers[i]);
            TextView quesView = (TextView) findViewById(R.id.questionView);
            quesView.setText(questions.get(i));
            if(!back)
                rg.clearCheck();
        }

        if(i == questions.size()-1){
            nextBtn.setText("Submit");
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rg.getCheckedRadioButtonId() == -1 && !back){
                        Toast.makeText(getApplicationContext(), "Please select an option",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    Log.i(TAG, "thanks for answers");
                    answers[currentQuestion] = rg.getCheckedRadioButtonId();
//                    for(int x:answers){
//                        Log.i("Finish", Integer.toString(x));
//                    }
                    back = false;
                    //submit answers to firebase
                    firebaseSubmit();
                    //TODO exit the activity, return to wherever we need to go
                    //Since we launched this activity from the main, once we call finish() we
                    //will return to the main activity
                    //Intent myIntent = new Intent();
                    Toast.makeText(getApplicationContext(), "Thank you for taking this survey",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        else{
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentQuestion++;
                    back = false;
                    updateQuestion(currentQuestion);
                }
            });
        }

    }

    public void firebaseSubmit(){
        DatabaseReference thr = FirebaseDatabase.getInstance().getReference("New Survey")
                .child(user).child(surveyCoords).child(surveyName);
        DatabaseReference thr2 = FirebaseDatabase.getInstance().getReference("Answers")
                .child(user).child(surveyCoords).child(surveyName);
        thr2 = thr2.push();
        int i =1;
        String curr;
        for(int x:answers){
            curr = scores.get(i-1).getValue().toString();
            double y = Double.parseDouble(curr.substring(0,curr.indexOf(",")));
            int z = Integer.parseInt(curr.substring(curr.indexOf(",")+1));
            Log.i(TAG,y + " " + z);
            //this calculates the average and then rounds it up
            double a = ((y*z)+x)/(++z) + 0.5;

            thr.child(Integer.toString(i)).child(questions.get(i-1)).setValue(Integer.toString(((int)a))
                + "," + Integer.toString(z));
            thr2.child(Integer.toString(i)).child(questions.get(i-1)).setValue(x);
            //thr.child(Integer.toString(i)).setValue(x);
            i++;
        }
    }
}
