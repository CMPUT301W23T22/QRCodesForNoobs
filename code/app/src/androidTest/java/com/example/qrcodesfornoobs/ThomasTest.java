package com.example.qrcodesfornoobs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrcodesfornoobs.Activity.TakePhotoActivity;
import com.example.qrcodesfornoobs.Models.Creature;
import com.example.qrcodesfornoobs.Models.Player;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ThomasTest {
    private Solo solo;
    private static String MOCK_USERNAME = "_ThomasTest_";
    private static String MOCK_CODE = "###Thomas###";
    private FirebaseFirestore db;
    @Rule
    public ActivityTestRule<TakePhotoActivity> rule = new ActivityTestRule<>(TakePhotoActivity.class,true,false);

    /**
     * Sets up the solo variable and clear username cache before each test.
     */
    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        db = FirebaseFirestore.getInstance();
        Player mockPlayer = new Player(MOCK_USERNAME, "123321456654");
        db.collection("Players").document(MOCK_USERNAME).set(mockPlayer);
        // Get a reference to the SharedPreferences object of the launched activity
//        SharedPreferences sharedPreferences = rule.getActivity().getSharedPreferences(SignInActivity.CACHE_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("username");
//        editor.apply();
        Player.LOCAL_USERNAME = MOCK_USERNAME;
    }
    @Test
    public void addNewQRCodeToAccountTest() throws InterruptedException { // US 01.01.01
        // Create a new intent with the target activity class
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), TakePhotoActivity.class);
        // Add the String extra to the intent
        intent.putExtra("code", MOCK_CODE);
        // Set the intent on the rule object
        rule.launchActivity(intent);

        solo.waitForActivity(TakePhotoActivity.class);
        assertTrue(solo.waitForLogMessage("[INTENT TESTING] confirm button binded."));
        solo.clickOnView(solo.getView(R.id.confirm_button));
        assertTrue(solo.waitForLogMessage("[INTENT TESTING] Code added successfully!"));
    }

    @Test
    public void ableToRecordPhotoLocationTest() { // part of US 02.01.01
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), TakePhotoActivity.class);
        // Add the String extra to the intent
        intent.putExtra("code", MOCK_CODE);
        // Set the intent on the rule object
        rule.launchActivity(intent);
        solo.waitForActivity(TakePhotoActivity.class);
        View cameraButton = solo.getView(R.id.cameraButton);
        assertNotNull(cameraButton);
        // cannot do any further testing since we're using built-in camera lib. Please refer to
        // https://stackoverflow.com/questions/3840034/how-do-i-write-a-solo-robotium-testcase-that-uses-the-builtin-camera-to-take-a-p
        // for more details
    }

    @Test
    public void uniqueVisualRepresentationCode() { // US 02.06.01
        Intent intent1 = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), TakePhotoActivity.class);
        // Add the String extra to the intent
        intent1.putExtra("code", MOCK_CODE + System.currentTimeMillis());
        // Set the intent on the rule object
        rule.launchActivity(intent1);
        solo.waitForActivity(TakePhotoActivity.class);
        ImageView imageView1 = solo.getImage(0);
        solo.sleep(1000);

        Bitmap bitmap1 = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();

        solo.finishOpenedActivities();
        Intent intent2 = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), TakePhotoActivity.class);
        intent2.putExtra("code", MOCK_CODE + System.currentTimeMillis());
        rule.launchActivity(intent2);
        solo.waitForActivity(TakePhotoActivity.class);
        ImageView imageView2 = solo.getImage(0);
        solo.sleep(1000);
        Bitmap bitmap2 = ((BitmapDrawable) imageView2.getDrawable()).getBitmap();
        assertFalse(bitmap1.sameAs(bitmap2));
    }

    @After
    public void tearDown() {
        db.collection("Players").document(MOCK_USERNAME).delete();
        db.collection("Creatures").document(new Creature(MOCK_CODE, null).getHash()).delete();
        solo.finishOpenedActivities();
    }

//    /**
//     * Signs into the app as a new user, entry will then be deleted at the end of the test.
//     * @throws Exception
//     */
//    @Test
//    public void SuccessfulSignIn() throws Exception{
//        Activity activity = rule.getActivity();
//        solo.assertCurrentActivity("Wrong Activity",SignInActivity.class);
//        solo.enterText((EditText) solo.getView(R.id.username_EditText),"TestReynel");
//        solo.clickOnView(solo.getView(R.id.sign_in_button));
//        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//        //deleting the added entry
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Players")
//                .document("TestReynel")
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error deleting document", e);
//                    }
//                });
//    }
//    /**
//     * Signs into the app as a existing user with a different device, should remain on signin page.
//     * @throws Exception
//     */
//    @Test
//    public void UnsuccessfulSignIn() throws Exception{
//        Activity activity = rule.getActivity();
//        solo.assertCurrentActivity("Wrong Activity",SignInActivity.class);
//        EditText editText = (EditText) solo.getView(R.id.username_EditText);
//        solo.enterText(editText,"differentDevice");
//        solo.clickOnView(solo.getView(R.id.sign_in_button));
//        solo.waitForText("Username Already Exists!");
//        String error1 = editText.getError().toString();
//        assertEquals("Username Already Exists!", error1);
//        solo.assertCurrentActivity("Wrong Activity", SignInActivity.class);
//        solo.clearEditText(editText);
//        solo.enterText(editText,"");
//        solo.clickOnView(solo.getView(R.id.sign_in_button));
//        solo.clickOnView(solo.getView(R.id.sign_in_button));
//        solo.waitForText("Provide a Username!");
//        String error2 = editText.getError().toString();
//        assertEquals("Provide a Username!", error2);
//    }
//    /**
//     * Clears username cache after every test
//     */
//    @After
//    public void cleanup(){
//        SharedPreferences sharedPreferences = rule.getActivity().getSharedPreferences(SignInActivity.CACHE_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("username");
//        editor.apply();
//        System.out.println(sharedPreferences.getString("username",""));
//    }
//    String TAG = "IntentTesting";
}
