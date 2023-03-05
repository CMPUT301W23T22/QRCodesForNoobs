package com.example.qrcodesfornoobs.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.qrcodesfornoobs.Creature;
import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.databinding.ActivityMainBinding;
import com.example.qrcodesfornoobs.databinding.ActivityTakePhotoBinding;

import java.security.NoSuchAlgorithmException;

public class TakePhotoActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 666;
    ActivityTakePhotoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // make sure camera permission is granted
        while (ContextCompat.checkSelfPermission(TakePhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TakePhotoActivity.this, new String[] {android.Manifest.permission.CAMERA}, 666);
        }

        binding.cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: upload code, photo (of qr & place), location (if selected) to db
                String scannedCode = getIntent().getExtras().getString("code");
                try {
                    Creature creature = new Creature(scannedCode);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "scanned code can't be hashed...", Toast.LENGTH_SHORT).show();
                }

                if (binding.saveImageCheckBox.isChecked()) {
                    // save photo (place) to db
                }
                if (binding.saveLocationCheckBox.isChecked()) {
                    Toast.makeText(getBaseContext(), "location will be saved in Project part 4", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getBaseContext(), "Code added successfully (to be implemented)", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        openCamera();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                binding.imageView.setImageBitmap(bitmap);
                Toast.makeText(getBaseContext(), "Take photo successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}