package com.example.metadatainputactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText folderNameInput;
    private EditText timeInterval;
    private Button saveButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        folderNameInput = findViewById(R.id.folder_name_input);
        timeInterval = findViewById(R.id.time_interval);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {
            String folderName = folderNameInput.getText().toString();
            String time_gap = timeInterval.getText().toString();

            if (!folderName.isEmpty() && !time_gap.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, OpenCameraActivity.class);
                intent.putExtra("FOLDER_NAME", folderName);
                intent.putExtra("TIME_INTERVAL", time_gap);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(this,"You have empty field!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}