package com.example.qrcodesfornoobs.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.qrcodesfornoobs.Creature;
import com.example.qrcodesfornoobs.databinding.ActivityTakePhotoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class TakePhotoActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 666;
    final String TAG = "Sample";
    ActivityTakePhotoBinding binding;

    Bitmap codeRepresentationBitmap;
    Bitmap photoBitmap;
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

                Location location = null; //setting this in part4
                Creature creature = new Creature(scannedCode, location);
                if (binding.saveLocationCheckBox.isChecked()) {
                    // set local isPhotoLocation to the photo
                }
                // TODO: implement checking existence
                uploadImages(creature, binding.saveImageCheckBox.isChecked()).thenAccept((urlList) -> {
                    creature.setPhotoLocationUrl(urlList.get(0));
                    creature.setPhotoCreatureUrl(urlList.get(1));
                    uploadToDatabase(creature);
                });
                finish();
            }
        });
        randomizeCodeRepresentationImage();
        openCamera();
    }

    private void randomizeCodeRepresentationImage() {
        long randomSeed = System.currentTimeMillis();
        Glide.with(this)
                .asBitmap()
                .load("https://picsum.photos/200?random=" + randomSeed)
                .centerCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        codeRepresentationBitmap = resource;
                        binding.codeRepresentationImageView.setImageBitmap(codeRepresentationBitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void uploadToDatabase(Creature creature) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Creatures").document(creature.getHash())
                .set(creature)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Code added successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Failed to add code!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private CompletableFuture<ArrayList<String>> uploadImages(Creature creature, boolean isSavingLocationPhoto) {
        CompletableFuture<String> locationPhotoFuture = CompletableFuture.supplyAsync(() -> {
            if (photoBitmap == null || !isSavingLocationPhoto) {
                return null;
            }
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA).format(new Date());
            String storageLocation = "photo_location/" + date;
            StorageReference locationPhotoStorageReference = FirebaseStorage.getInstance().getReference(storageLocation);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = locationPhotoStorageReference.putBytes(data);

            CompletableFuture<String> getUriFuture = new CompletableFuture<>();
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                locationPhotoStorageReference.getDownloadUrl().addOnSuccessListener(uri -> getUriFuture.complete(uri.toString()));
            }).addOnFailureListener(e -> getUriFuture.completeExceptionally(null));

            return getUriFuture.join();
        });

        CompletableFuture<String> creaturePhotoFuture = CompletableFuture.supplyAsync(() -> {
            if (codeRepresentationBitmap == null) {
                return null;
            }
            String storageLocation = "photo_creature/" + creature.getHash();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(storageLocation);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            codeRepresentationBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageReference.putBytes(data);

            CompletableFuture<String> getUriFuture = new CompletableFuture<>();
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> getUriFuture.complete(uri.toString()));
            }).addOnFailureListener(e -> getUriFuture.completeExceptionally(null));

            return getUriFuture.join();
        });

        CompletableFuture<ArrayList<String>> combinedFuture = locationPhotoFuture.thenCombine(creaturePhotoFuture, (photoLocationUrl, photoCreatureUrl) -> {
            ArrayList<String> urls = new ArrayList<>(Arrays.asList(photoLocationUrl, photoCreatureUrl));
            return urls;
        });
        return combinedFuture;
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
                photoBitmap = (Bitmap) data.getExtras().get("data");
                binding.locationImageView.setImageBitmap(photoBitmap);
            }
        }
    }
}