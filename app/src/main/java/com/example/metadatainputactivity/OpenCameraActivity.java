package com.example.metadatainputactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class OpenCameraActivity extends AppCompatActivity {

    private static final String TAG = "OpenCameraActivity";
    private String folderName;
    private String time_gap;
    private boolean isAutoCapturing = false;
    private Handler handler;
    private Runnable autoCaptureRunnable;
    private File captureDirectory;
    private ImageCapture imageCapture;
    private PreviewView previewView;
    private Vibrator vibrator;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                Boolean cameraPermission = permissions.get(Manifest.permission.CAMERA);
                // Boolean storagePermission = permissions.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                // if (cameraPermission != null && cameraPermission && storagePermission != null && storagePermission) {
                if (cameraPermission != null && cameraPermission) {
                    createCaptureDirectory();
                    setupCamera();
                } else {
                    Toast.makeText(OpenCameraActivity.this, "Permissions are required.", Toast.LENGTH_SHORT).show();
                    requestPermissions();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_camera);

        initializeFields();
        setupUi();
        checkPermissions();
    }

    private void initializeFields() {
        folderName = getIntent().getStringExtra("FOLDER_NAME");
        time_gap = getIntent().getStringExtra("TIME_INTERVAL");
        captureDirectory = new File(getExternalFilesDir(null), folderName);
        handler = new Handler(Looper.getMainLooper());
        previewView = findViewById(R.id.preview_view);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE); // Initialize the Vibrator service
    }

    private void setupUi() {
        Button autoCaptureButton = findViewById(R.id.auto_capture_button);
        Button manualCaptureButton = findViewById(R.id.manual_capture_button);
        Button newDirectoryButton = findViewById(R.id.new_directory_button);

        autoCaptureButton.setOnClickListener(v -> {
            if (isAutoCapturing) {
                stopAutoCapture();
                autoCaptureButton.setText("Start Auto Capture");
            } else {
                startAutoCapture();
                autoCaptureButton.setText("Stop Auto Capture");
            }
            isAutoCapturing = !isAutoCapturing;
            vibrate(); // Vibrate when auto capture button is clicked
        });

        manualCaptureButton.setOnClickListener(v -> {
            captureImage();
            vibrate(); // Vibrate when manual capture button is clicked
        });

        newDirectoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(OpenCameraActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            vibrate(); // Vibrate when new directory button is clicked
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        } else {
            createCaptureDirectory();
            setupCamera();
        }
    }

    private void createCaptureDirectory() {
        if (!captureDirectory.exists()) {
            if (captureDirectory.mkdirs()) {
                Log.d(TAG, "Capture directory created: " + captureDirectory.getAbsolutePath());
            } else {
                Log.e(TAG, "Failed to create capture directory");
            }
        }
    }

    private void setupCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Create preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Create image capture use case
                imageCapture = new ImageCapture.Builder().build();

                // Unbind any previous use cases before binding new ones
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);

                Log.d(TAG, "Camera setup completed");
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(OpenCameraActivity.this, "Error setting up camera.", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void captureImage() {
        if (imageCapture == null) {
            Log.e(TAG, "ImageCapture not initialized");
            Toast.makeText(this, "Camera not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique filename using UUID
        String uniqueFilename = folderName + "_" + UUID.randomUUID().toString() + ".jpg";
        File photoFile = new File(captureDirectory, uniqueFilename);

        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(options, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Log.d(TAG, "Image saved: " + photoFile.getAbsolutePath());
                Toast.makeText(OpenCameraActivity.this, "Image Captured", Toast.LENGTH_SHORT).show();
                vibrate(); // Vibrate when image is captured
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
                Toast.makeText(OpenCameraActivity.this, "Error capturing image.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startAutoCapture() {
        // Retrieve the time interval from the Intent
        String timeGapString = getIntent().getStringExtra("TIME_INTERVAL");
        long timeGapMillis = 5000; // Default to 5 seconds if no value is provided

        // Convert the time interval from seconds to milliseconds
        if (timeGapString != null) {
            try {
                int timeGapSeconds = Integer.parseInt(timeGapString);
                timeGapMillis = timeGapSeconds * 1000L;
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid time interval format, using default value.");
                // Optionally handle invalid format here
            }
        }

        // Create and post the auto-capture runnable
        long finalTimeGapMillis = timeGapMillis;
        autoCaptureRunnable = new Runnable() {
            @Override
            public void run() {
                captureImage();
                handler.postDelayed(this, finalTimeGapMillis); // Capture at the specified interval
            }
        };
        handler.post(autoCaptureRunnable);
    }

    private void stopAutoCapture() {
        if (autoCaptureRunnable != null) {
            handler.removeCallbacks(autoCaptureRunnable);
            autoCaptureRunnable = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoCapture();
    }

    private void requestPermissions() {
        requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    private void vibrate() {
        if (vibrator != null) {
            vibrator.vibrate(100); // Vibrate for 100 milliseconds
        }
    }
}
