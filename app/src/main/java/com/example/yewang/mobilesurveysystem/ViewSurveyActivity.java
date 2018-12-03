package com.example.yewang.mobilesurveysystem;

import android.app.ListActivity;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewSurveyActivity extends AppCompatActivity {

    private static final String TAG = "Group-project";
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_survey);
        listView = (ExpandableListView) findViewById(R.id.lvExp);
        initData();
    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("New Survey").child(user.getUid());
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null || !b || dataSnapshot == null) {
                    Log.i(TAG, "Failed to get DataSnapshot");
                } else {
                    Log.i(TAG, "Successfully got DataSnapshot");
                    int loc = 0;
                    for (DataSnapshot uniqueID : dataSnapshot.getChildren()) {
                        for (DataSnapshot surveyTitle : uniqueID.getChildren()) {
                            listDataHeader.add(surveyTitle.getKey());
                            List<String> questions = new ArrayList<>();
                            for (DataSnapshot questionNumber : surveyTitle.getChildren()) {
                                for (DataSnapshot questionName : questionNumber.getChildren()) {
                                    String toAdd = questionName.getKey();
                                    String[] split = questionName.getValue().toString().split(",");
                                    toAdd += ": average score is " + split[0];
                                        questions.add(toAdd);
                                }
                            }
                            listHash.put(listDataHeader.get(loc), questions);
                        }
                        loc++;
                    }
                    listAdapter = new ExpandableListAdapter(ViewSurveyActivity.this, listDataHeader, listHash);
                    listView.setAdapter(listAdapter);
                }
            }
        });
    }
}
