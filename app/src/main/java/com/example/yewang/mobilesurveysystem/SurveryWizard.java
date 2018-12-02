package com.example.yewang.mobilesurveysystem;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SurveryWizard extends ListActivity {

    private static final String TAG = "Group-project";
    private static final int ADD_TODO_ITEM_REQUEST = 0;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    WizardItemListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_survery_wizard);

        mAdapter = new WizardItemListAdapter(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

//        set footer and header
        getListView().setHeaderDividersEnabled(true);
        getListView().setFooterDividersEnabled(true);

        LinearLayout footView = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.wizard_footer,null);
        RelativeLayout headView = (RelativeLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.wizard_header,null);
//        RelativeLayout
        getListView().addHeaderView(headView);
        getListView().addFooterView(footView);


//        set action TO DO

        final Button addbtn = findViewById(R.id.addWizBtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SurveryWizard.this,AddWizardItem.class);

                startActivityForResult(intent,ADD_TODO_ITEM_REQUEST);

            }
        });

        final Button submitbtn = findViewById(R.id.submitWizBtn);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



//            Use reference as key a like structure
//            then we can set the value to different values
//            as right now I am using location as key and rest as value
                database = FirebaseDatabase.getInstance();
                final DatabaseReference databasebRef = database.getReference("New Survey");

                List<WizardItem> alldata = mAdapter.getAllItemsData();
                //            TODO check if there is nothing to submit then pop something

                if(alldata.size()!=0){
                    EditText title =  findViewById(R.id.titleinput);
                    EditText lat = findViewById(R.id.latitude_input);
                    EditText log = findViewById(R.id.longtitude_input);

                    final String lat_string = lat.getText().toString();
                    final String log_string = log.getText().toString();
                    //
                    Intent intent = getIntent();
                    String user = intent.getStringExtra("user");

                    String tmp_title = title.getText().toString();
                    if(lat_string.isEmpty() || log_string.isEmpty()){
                        Toast.makeText(getApplicationContext(),"Missing Coordinate(s)!",Toast.LENGTH_SHORT).show();
                    }else{
                        if(tmp_title.isEmpty()){
                            Toast.makeText(getApplicationContext(),"Missing Survey Name!",Toast.LENGTH_SHORT).show();
                        }else{
                            int question_number =1;
                            final FirebaseUser test = mAuth.getCurrentUser();
                            databasebRef.child(test.getUid()) //user
                                    .child(lat_string+" , "+log_string)
                                    .setValue(null);

                            for (WizardItem x : alldata ){
                                Log.i(TAG, "the user is: " + user);

                                databasebRef.child(test.getUid()) //user
                                        .child(lat_string+" , "+log_string) //coordinates
                                        .child(tmp_title).child(question_number+"") //survy name
                                        .child(x.getQuestion()) //question
                                        .setValue("0,0"); //defalt rate


                                Log.i(TAG, "data: "+x.getQuestion());
                                question_number++;
                            }

                            finish();
                        }
                    }
//                int question_number =1;
//
//
//                for (WizardItem x : alldata ){
//                    Log.i(TAG, "the user is: " + user);
//                    FirebaseUser test = mAuth.getCurrentUser();
//                    databasebRef.child(test.getUid()).child(lat_string+" , "+log_string).child(tmp_title).child(question_number+"").child(x.getQuestion())
//                            .setValue("0,0");
//
//
//                    Log.i(TAG, "data: "+x.getQuestion());
//                    question_number++;
//                }
//
//                finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Please Add a Question before you submit the survey",Toast.LENGTH_SHORT).show();
                }

            }
        });

        getListView().setAdapter(mAdapter);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(ADD_TODO_ITEM_REQUEST== requestCode && RESULT_OK== resultCode){
            WizardItem toDoItem = new WizardItem(data);
            mAdapter.add(toDoItem);
        }
    }

}
