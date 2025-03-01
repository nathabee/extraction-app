package com.example.extractionapp; // Ensure this matches your app package name

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import org.opencv.android.OpenCVLoader;


public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private TextView opencvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        opencvStatus = findViewById(R.id.opencv_status);
        opencvStatus.setText(getString(R.string.checking_opencv)); // ✅ Use resource string
        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            initializeOpenCV();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeOpenCV();
            } else {
                opencvStatus.setText(getString(R.string.camera_permission_denied)); // ✅ Fixed
                opencvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            }
        }
    }

    private void initializeOpenCV() {

        // Load OpenCV manually
        try {
            System.loadLibrary("opencv_java4");
            opencvStatus.setText(getString(R.string.opencv_success));
            opencvStatus.setTextColor(getColor(android.R.color.holo_green_dark));
        } catch (UnsatisfiedLinkError e) {
            opencvStatus.setText(getString(R.string.opencv_failure));
            opencvStatus.setTextColor(getColor(android.R.color.holo_red_dark));
        }
    }
}



