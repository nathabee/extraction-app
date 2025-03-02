package com.example.extractionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.io.IOException;
import org.opencv.core.Scalar;
import org.opencv.core.Core;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView imgInput, imgOutput;
    private SeekBar seekThreshold1, seekThreshold2;
    private Spinner spinnerSize;
    private Uri inputImageUri;
    private Bitmap inputBitmap;

    // Declare the new ImageView
    private ImageView imgTransparent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize OpenCV (Use system library instead of deprecated method)
        try {
            System.loadLibrary("opencv_java4");
            Log.d(TAG, "✅ OpenCV Loaded Successfully!");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "❌ OpenCV Initialization Failed!", e);
            Toast.makeText(this, "OpenCV Initialization Failed!", Toast.LENGTH_SHORT).show();
        }

        // UI References
        imgInput = findViewById(R.id.img_input);
        imgOutput = findViewById(R.id.img_output);
        seekThreshold1 = findViewById(R.id.seek_threshold1);
        seekThreshold2 = findViewById(R.id.seek_threshold2);
        spinnerSize = findViewById(R.id.spinner_size);
        imgTransparent = findViewById(R.id.img_transparent);

        Button btnSelectImage = findViewById(R.id.btn_select_image);
        Button btnProcess = findViewById(R.id.btn_process);

        Button btnSelectBg = findViewById(R.id.btn_select_bg);
        ImageView imgBackground = findViewById(R.id.img_background);


        // Set Spinner Options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.image_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(adapter);

        // Select Image from Gallery
        ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            inputBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            imgInput.setImageBitmap(inputBitmap);
                            imgInput.setContentDescription("User-selected input image");
                            inputImageUri = selectedImageUri;
                        } catch (IOException e) {
                            Log.e(TAG, "Error loading image", e);
                        }
                    }
                });

        ActivityResultLauncher<Intent> bgImagePicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedBgUri = result.getData().getData();
                        try {
                            Bitmap bgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedBgUri);
                            imgBackground.setImageBitmap(bgBitmap);
                            imgBackground.setContentDescription("User-selected background image");
                        } catch (IOException e) {
                            Log.e(TAG, "Error loading background image", e);
                        }
                    }
                });


        btnSelectImage.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePicker.launch(pickIntent);
        });


        btnSelectBg.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            bgImagePicker.launch(pickIntent);
        });


        btnProcess.setOnClickListener(v -> processImage());
        //Toast.makeText(this, "Processing image: " + inputImageUri.getPath(), Toast.LENGTH_SHORT).show();




    }

    private void processImage() {
        if (inputBitmap == null) {
            Toast.makeText(this, "Select an image first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert input image to OpenCV Mat
        Mat imgMat = new Mat();
        Bitmap processedBitmap = inputBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(processedBitmap, imgMat);

        // Convert to grayscale and detect edges
        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(imgMat, imgMat, seekThreshold1.getProgress(), seekThreshold2.getProgress());

        // Convert back to Bitmap for edges
        Bitmap edgeBitmap = Bitmap.createBitmap(processedBitmap.getWidth(), processedBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgMat, edgeBitmap);
        imgOutput.setImageBitmap(edgeBitmap); // Display edge image

        // Generate transparent image (background removal simulation)
        Bitmap transparentBitmap = removeBackground(inputBitmap);
        imgTransparent.setImageBitmap(transparentBitmap); // Display transparent image
    }


    // Function to remove background (Simple HSV-based approach)
    private Bitmap removeBackground(Bitmap srcBitmap) {
        Mat srcMat = new Mat();
        Utils.bitmapToMat(srcBitmap, srcMat);

        // Convert to HSV
        Mat hsvMat = new Mat();
        Imgproc.cvtColor(srcMat, hsvMat, Imgproc.COLOR_RGB2HSV);

        // Define color range (Assume the top-left pixel is background)
        double[] bgColor = hsvMat.get(0, 0);
        double hue = bgColor[0];
        double lowerHue = Math.max(hue - 30, 0);
        double upperHue = Math.min(hue + 30, 179);

        // Create a mask for background
        Mat mask = new Mat();
        Core.inRange(hsvMat, new Scalar(lowerHue, 50, 50), new Scalar(upperHue, 255, 255), mask);

        // Convert original to RGBA (Add alpha channel)
        Mat resultMat = new Mat();
        Imgproc.cvtColor(srcMat, resultMat, Imgproc.COLOR_RGB2RGBA);

        // Apply transparency
        for (int i = 0; i < mask.rows(); i++) {
            for (int j = 0; j < mask.cols(); j++) {
                if (mask.get(i, j)[0] == 255) { // Background detected
                    double[] rgba = resultMat.get(i, j);
                    rgba[3] = 0; // Set alpha to transparent
                    resultMat.put(i, j, rgba);
                }
            }
        }

        // Convert Mat back to Bitmap
        Bitmap transparentBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(resultMat, transparentBitmap);

        return transparentBitmap;
    }
}
