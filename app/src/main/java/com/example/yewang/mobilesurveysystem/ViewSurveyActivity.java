package com.example.yewang.mobilesurveysystem;

import android.app.ListActivity;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewSurveyActivity extends ListActivity {

    private static final String TAG = "Group-project";
    private ArrayList<String> surveys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() for ViewSurvey called!");
        super.onCreate(savedInstanceState);
        surveys = new ArrayList<String>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("New Survey").child(user.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    for (DataSnapshot k : i.getChildren()) {
                        surveys.add(k.getKey());

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewSurveyActivity.this, R.layout.list_item, surveys);
                    setListAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        Log.i(TAG, "this was clicked: " + position);
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("New Survey").child(user.getUid()).child(surveys.get(position));
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot i : dataSnapshot.getChildren()) {
//                    Log.i(TAG, "question: " + i.getKey());
//                    questions.add(i.getKey());
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewSurveyActivity.this, R.layout.list_item, questions);
//                setListAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
