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
import com.example.qrcodesfornoobs.Models.Creature;
import com.example.qrcodesfornoobs.Models.Player;
import com.example.qrcodesfornoobs.databinding.ActivityTakePhotoBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class TakePhotoActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 666;
    final String TAG = "Sample";
    ActivityTakePhotoBinding binding;

    Bitmap photoCreatureBitmap;
    Bitmap photoLocationBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String scannedCode = getIntent().getExtras().getString("code");
        Location location = null; //setting this in part4
        Creature newCreature = new Creature(scannedCode, location);
        checkValidCreatureToAdd(newCreature).thenAccept((isValid) -> {
            if (!isValid) {
                Toast.makeText(getBaseContext(), "You already have this code!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            checkExistingCreature(newCreature).thenAccept((modifiedDbCreature) -> {

                loadPhotoCreatureImageView(modifiedDbCreature);

                // make sure camera permission is granted
                while (ContextCompat.checkSelfPermission(TakePhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TakePhotoActivity.this, new String[]{android.Manifest.permission.CAMERA}, 666);
                }

                binding.cameraButton.setOnClickListener(v -> openCamera());
                binding.confirmButton.setOnClickListener(v -> {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    // if scanned creature is already in db
                    if (modifiedDbCreature != null) {
                        if (binding.saveLocationCheckBox.isChecked()) {
                            // set local isPhotoLocation to the photo
                        }
                        if (binding.saveImageCheckBox.isChecked()) {
                            uploadPhotoLocation().thenAccept(photoLocationUrl -> {
                                modifiedDbCreature.setPhotoLocationUrl(photoLocationUrl);
                                uploadToDatabase(modifiedDbCreature);
                            });
                            return;
                        }
                        uploadToDatabase(modifiedDbCreature);
                        return;
                    }
                    // if scanned creature is not in db
                    if (binding.saveLocationCheckBox.isChecked()) {
                        // set local isPhotoLocation to the photo
                    }
                    uploadPhotoCreature(newCreature).thenAccept((photoCreatureUrl) -> {
                        newCreature.setPhotoCreatureUrl(photoCreatureUrl);
                        if (binding.saveImageCheckBox.isChecked()) {
                            uploadPhotoLocation().thenAccept((photoLocationUrl) -> {
                                newCreature.setPhotoLocationUrl(photoLocationUrl);
                                uploadToDatabase(newCreature);
                            });
                        }
                    });
                });
            });
        }).exceptionally(e -> {
            Toast.makeText(getBaseContext(), "Can't fetch creature from database. Please try again later!", Toast.LENGTH_SHORT).show();
            finish();
            return null;
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.progressBar.setVisibility(View.GONE);
    }

    /**
     * @param creature
     * @return true if local player does not have the code, false otherwise
     */
    private CompletableFuture<Boolean> checkValidCreatureToAdd(Creature creature) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference playerDoc = db.collection("Players").document(Player.LOCAL_USERNAME);
        playerDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Player dbPlayer = documentSnapshot.toObject(Player.class);
                if (!dbPlayer.containsCreature(creature)) {
                    future.complete(true);
                    return;
                }
            }
            future.complete(false);
        }).addOnFailureListener(e -> future.completeExceptionally(e));
        return future;
    }

    /**
     * @param creature
     * @return return a creature from db if it exists, null otherwise
     */
    private CompletableFuture<Creature> checkExistingCreature(Creature creature) {
        CompletableFuture<Creature> future = new CompletableFuture<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference creatureDoc = db.collection("Creatures").document(creature.getHash());
        creatureDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Creature dbCreature = documentSnapshot.toObject(Creature.class);
                dbCreature.incrementScan(); // TODO: update with local info
                future.complete(dbCreature);
                return;
            }
            future.complete(null);
        }).addOnFailureListener(e -> future.completeExceptionally(e));
        return future;
    }

    /**
     * Load photo into photo location image view (use dbCreature url to load if it exists)
     *
     * @param dbCreature
     */
    private void loadPhotoCreatureImageView(Creature dbCreature) {
        long randomSeed = System.currentTimeMillis();
        String photoLink = dbCreature == null ? "https://picsum.photos/200?random=" + randomSeed : dbCreature.getPhotoCreatureUrl();
        Glide.with(getBaseContext())
                .asBitmap()
                .load(photoLink)
                .centerCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        photoCreatureBitmap = resource;
                        binding.codeRepresentationImageView.setImageBitmap(photoCreatureBitmap);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void uploadToDatabase(Creature creature) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // update Creatures collection
        db.collection("Creatures").document(creature.getHash())
                .set(creature)
                .addOnSuccessListener(aVoid -> {
                    finish();
                    Toast.makeText(getBaseContext(), "Code added successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getBaseContext(), "Failed to add code!", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error writing document", e);
                });

        // update Players collection
        db.collection("Players").document(Player.LOCAL_USERNAME)
                .update("creatures", FieldValue.arrayUnion(creature.getHash()));
    }

    private CompletableFuture<String> uploadPhotoLocation() {
        CompletableFuture<String> locationPhotoFuture = CompletableFuture.supplyAsync(() -> {
            if (photoLocationBitmap == null) {
                return null;
            }
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA).format(new Date());
            String storageLocation = "photo_location/" + date;
            StorageReference locationPhotoStorageReference = FirebaseStorage.getInstance().getReference(storageLocation);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photoLocationBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = locationPhotoStorageReference.putBytes(data);

            CompletableFuture<String> getUriFuture = new CompletableFuture<>();
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                locationPhotoStorageReference.getDownloadUrl().addOnSuccessListener(uri -> getUriFuture.complete(uri.toString()));
            }).addOnFailureListener(e -> getUriFuture.completeExceptionally(null));

            return getUriFuture.join();
        });
        return locationPhotoFuture;
    }

    private CompletableFuture<String> uploadPhotoCreature(Creature creature) {
        CompletableFuture<String> creaturePhotoFuture = CompletableFuture.supplyAsync(() -> {
            if (photoCreatureBitmap == null) {
                return null;
            }
            String storageLocation = "photo_creature/" + creature.getHash();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(storageLocation);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photoCreatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageReference.putBytes(data);

            CompletableFuture<String> getUriFuture = new CompletableFuture<>();
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> getUriFuture.complete(uri.toString()));
            }).addOnFailureListener(e -> getUriFuture.completeExceptionally(null));

            return getUriFuture.join();
        });
        return creaturePhotoFuture;
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
                photoLocationBitmap = (Bitmap) data.getExtras().get("data");
                binding.locationImageView.setImageBitmap(photoLocationBitmap);
            }
        }
    }
}