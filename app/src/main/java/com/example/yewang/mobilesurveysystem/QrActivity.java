package com.example.yewang.mobilesurveysystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QrActivity extends AppCompatActivity {

    private static final String TAG = "Group-project";
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    String userName = "";
    String location = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate QrActivity");
        setContentView(R.layout.qr_activity);

        initViews();
    }

    private void initViews() {
        Log.i(TAG, "initViews Qr_Activity");
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);


        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueEventListener valListener = new ValueEventListener() {
                    Intent result = new Intent();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot = dataSnapshot.child(userName);
                        for (DataSnapshot coord : dataSnapshot.getChildren()) {
                            Log.i(TAG, coord.getKey());
                            if(coord.getKey().equals(location)) {
                                result.putExtra("location", location);
                                result.putExtra("user", userName);
                                setResult(RESULT_OK, result);
                                Log.i(TAG, "data found");
                                finish();
                                break;
                            }

                        }
                        if(result.getStringExtra("location") == null) {
                            Log.i(TAG, "snapshot does not exist");
                            Toast.makeText(getApplicationContext(), "Location invalid", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Database Error");
                    }
                };

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("New Survey");
                mDatabase.addValueEventListener(valListener);
            }
        });
    }

    private void initialiseDetectorsAndSources() {

        Log.i(TAG, "initialize detectors and sources");

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(QrActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(QrActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG, "surface changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "surface destroyed");
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.i(TAG, "scanner stopped");
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {
                            Log.i(TAG, "before read");
                            String code = barcodes.valueAt(0).displayValue;
                            Log.i(TAG, "after read");
                            String[] splitCode = code.split(";");
                            if (splitCode.length == 2) {
                                userName = splitCode[0];
                                location = splitCode[1];
                                Log.i(TAG, "userName is " + userName);
                                Log.i(TAG, "location is " + location);
                                txtBarcodeValue.setText(location);
                                btnAction.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                            } else {
                                Log.e(TAG, "splitCode size is " + splitCode.length);
                                txtBarcodeValue.setText("INVALID QR CODE");
                                btnAction.setBackgroundColor(getResources().getColor(R.color.colorRed));
                            }
                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        initialiseDetectorsAndSources();
    }
}
