package com.example.qrcodesfornoobs.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.qrcodesfornoobs.Models.Creature;
import com.example.qrcodesfornoobs.Models.Player;
import com.example.qrcodesfornoobs.databinding.ActivityTakePhotoBinding;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.LatLng;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Allows the user to take a photo of a creature and optionally a location.
 * The photo(s) can be saved to the Firebase Storage and the creature information is saved to the Firebase Firestore.
 */
public class TakePhotoActivity extends AppCompatActivity implements LocationListener {
    private static final int CAMERA_REQUEST = 666;
    final String TAG = "Sample";
    ActivityTakePhotoBinding binding;

    Bitmap photoCreatureBitmap;
    Bitmap photoLocationBitmap;

    LocationManager locationManager;
    Double latitude;
    Double longitude;
    String locationName;
    String geoHash;
    Creature newCreature;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized then this Bundle contains the data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.testText.setVisibility(View.INVISIBLE);

        // make sure camera permission is granted
        binding.saveLocationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 667);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 668);
                }

                binding.testText.setVisibility(View.VISIBLE);
                binding.testText.setText("Getting location, please wait...");

                // Requests user location when permission already granted.
                // See onResume() for first time permission request.
                getLocation();
            }

        });

        String scannedCode = getIntent().getExtras().getString("code");
        newCreature = new Creature(scannedCode);
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
                    if (binding.saveLocationCheckBox.isChecked()) {
                        // get current location
                        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, TakePhotoActivity.this);
//                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Log.d("LOC", location.toString());
                        if (location != null) {
                            Log.d("lat", String.valueOf(location.getLatitude()));
                            Log.d("lng", String.valueOf(location.getLongitude()));
                            newCreature.setLatitude(location.getLatitude());
                            newCreature.setLongitude(location.getLongitude());
                            newCreature.setLocationName(locationName);

                            geoHash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));
                            newCreature.setGeoHash(geoHash);

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("geoHash", geoHash);
                            updates.put("lat",latitude);
                            updates.put("lng", longitude);

                            DocumentReference ref = FirebaseFirestore.getInstance().collection("Geohashes").document(geoHash);
                            ref.set(updates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "Geohash Updated.");
                                        }
                                    });
                        }
                    }
                    // if scanned creature is already in db
                    if (modifiedDbCreature != null) {
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
                    uploadPhotoCreature(newCreature).thenAccept((photoCreatureUrl) -> {
                        newCreature.setPhotoCreatureUrl(photoCreatureUrl);
                        if (binding.saveImageCheckBox.isChecked()) {
                            uploadPhotoLocation().thenAccept((photoLocationUrl) -> {
                                newCreature.setPhotoLocationUrl(photoLocationUrl);
                                uploadToDatabase(newCreature);
                            });
                            return;
                        }
                        uploadToDatabase(newCreature);
                    });
                });
                System.out.println("[INTENT TESTING] confirm button binded."); // VITAL for intent testing
            });
        }).exceptionally(e -> {
            Toast.makeText(getBaseContext(), "Can't fetch creature from database. Please try again later!", Toast.LENGTH_SHORT).show();
            finish();
            return null;
        });
    }

    /**
     * Uploads the photo of the creature to the Firebase Storage
     * @param requestCode the request code passed in
     * @param permissions the requested permissions
     * @param grantResults the grant results for the corresponding permissions
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if location permission is not granted
        if (requestCode == 667 || requestCode == 668) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                binding.saveLocationCheckBox.setChecked(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // On first time requesting location permission, it pauses and resumes the app.
        // On resume, ensures the users location is up to date.
        // Without, after approving permission, the location is null.
        if (binding.saveLocationCheckBox.isChecked() &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this::onLocationChanged);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            try {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                Geocoder geocoder = new Geocoder(TakePhotoActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String getAddress = addresses.get(0).getAddressLine(0);
                locationName = getAddress;

                binding.testText.setText("Location: " + locationName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.progressBar.setVisibility(View.GONE);
    }

    /**
     * Checks whether the cretaure to add already exists in the db
     *
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

    /**
     * Uploads the creature information to Firebase Firestore.
     *
     * @param creature The creature to upload the information for.
     */
    private void uploadToDatabase(Creature creature) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // update Creatures collection
        db.collection("Creatures").document(creature.getHash())
                .set(creature)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("[INTENT TESTING] Code added successfully!");
                    finish();
                    Toast.makeText(getBaseContext(), "Code added successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getBaseContext(), "Failed to add code!", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error writing document", e);
                });

        // update Players collection
        db.collection("Players").document(Player.LOCAL_USERNAME)
                .update("creatures", FieldValue.arrayUnion(creature.getHash()),
                "score", FieldValue.increment(creature.getScore()));
    }

    /**
     * Uploads the location photo to Firebase Storage.
     *
     * @return A CompletableFuture that completes with the download URL of the uploaded photo.
     */
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

    /**
     * Uploads the creature photo to Firebase Storage.
     *
     * @param creature The creature to upload the photo for.
     * @return A CompletableFuture
     */
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

    /**
     * Opens the camera app to take a photo.
     */
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    /**
     * Called when an activity you launched exits
     *
     * @param requestCode The integer request code
     * @param resultCode The integer result code
     * @param data An Intent, which can return result data to the caller
     */
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


    @SuppressLint("MissingPermission")
    private void getLocation() {
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, TakePhotoActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT);

        try{
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            Geocoder geocoder = new Geocoder(TakePhotoActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String getAddress = addresses.get(0).getAddressLine(0);
            locationName = getAddress;

            binding.testText.setText("Location: "+locationName);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}