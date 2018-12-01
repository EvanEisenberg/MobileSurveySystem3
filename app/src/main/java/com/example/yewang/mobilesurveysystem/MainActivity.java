package com.example.yewang.mobilesurveysystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Group-project";
    private static final int LOG_IN_REQUEST = 0;
    private static final int QR_CODE_REQUEST = 1;
    private FirebaseDatabase database;
    private String user;
    private String location;
    private Button login;
    private Button logout;
    private Button qrScan;
    private Button viewSurveys;
    private Button createSurvey;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.log_in_button);
        logout = findViewById(R.id.log_out_button);
        qrScan = findViewById(R.id.scan_qr_button);
        viewSurveys = findViewById(R.id.view_survey_button);
        createSurvey = findViewById(R.id.create_survey_button);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "LAUNCHING LOG_IN_REQUEST");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOG_IN_REQUEST);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Logging out current user");
                mAuth.signOut();
                viewSurveys.setVisibility(View.INVISIBLE);
                createSurvey.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);
                logout.setVisibility(View.INVISIBLE);

            }
        });


        qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "launching QrActivity");
                Intent intent = new Intent(MainActivity.this, QrActivity.class);
                startActivityForResult(intent, QR_CODE_REQUEST);
            }
        });

        viewSurveys.setVisibility(View.INVISIBLE);

        createSurvey.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOG_IN_REQUEST) {
            if (resultCode == RESULT_OK) {
                user = data.getStringExtra("user");
                login.setVisibility(View.INVISIBLE);
                logout.setVisibility(View.VISIBLE);
                viewSurveys.setVisibility(View.VISIBLE);
                createSurvey.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, SurveryWizard.class);
                        intent.putExtra("user", user);
                        Log.i(TAG, "the user logged in is: " + user);
                        startActivity(intent);
                    }
                });


                createSurvey.setVisibility(View.VISIBLE);
                viewSurveys.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
                        startActivity(intent);
                    }
                });

            }
        } else if (requestCode == QR_CODE_REQUEST) {
            location = data.getStringExtra("location");
            Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
            intent.putExtra("location", data.getStringExtra("location"));
            Log.i(TAG, "location is: " + location);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //I think I have to do this to update what user is logged in
        mAuth = FirebaseAuth.getInstance();
    }
}
