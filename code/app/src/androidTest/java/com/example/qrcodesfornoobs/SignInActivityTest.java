package com.example.qrcodesfornoobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrcodesfornoobs.Activity.MainActivity;
import com.example.qrcodesfornoobs.Activity.SignInActivity;
import com.example.qrcodesfornoobs.Models.Player;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class SignInActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<SignInActivity> rule = new ActivityTestRule<>(SignInActivity.class,true,false);

    /**
     * Sets up the solo variable and clear username cache before each test.
     */
    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // Get a reference to the SharedPreferences object of the launched activity
        Player.LOCAL_USERNAME = null;
        rule.launchActivity(null);
        SharedPreferences sharedPreferences = rule.getActivity().getSharedPreferences(SignInActivity.CACHE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.apply();
    }
    /**
     * Signs into the app as a new user, entry will then be deleted at the end of the test.
     * @throws Exception
     */
    @Test
    public void SuccessfulSignIn() throws Exception{
        Activity activity = rule.getActivity();
        solo.assertCurrentActivity("Wrong Activity",SignInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_EditText),"TestReynel");
        solo.clickOnView(solo.getView(R.id.sign_in_button));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        //deleting the added entry
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Players")
                .document("TestReynel")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
    /**
     * Signs into the app as a existing user with a different device, should remain on signin page.
     * @throws Exception
     */
    @Test
    public void UnsuccessfulSignIn() throws Exception{
        Activity activity = rule.getActivity();
        solo.assertCurrentActivity("Wrong Activity",SignInActivity.class);
        EditText editText = (EditText) solo.getView(R.id.username_EditText);
        solo.enterText(editText,"differentDevice");
        solo.clickOnView(solo.getView(R.id.sign_in_button));
        solo.waitForText("Username Already Exists!");
        String error1 = editText.getError().toString();
        assertEquals("Username Already Exists!", error1);
        solo.assertCurrentActivity("Wrong Activity", SignInActivity.class);
        solo.clearEditText(editText);
        solo.enterText(editText,"");
        solo.clickOnView(solo.getView(R.id.sign_in_button));
        solo.clickOnView(solo.getView(R.id.sign_in_button));
        solo.waitForText("Provide a Username!");
        String error2 = editText.getError().toString();
        assertEquals("Provide a Username!", error2);
    }
    /**
     * Clears username cache after every test
     */
    @After
    public void cleanup(){
        SharedPreferences sharedPreferences = rule.getActivity().getSharedPreferences(SignInActivity.CACHE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.apply();
        solo.finishOpenedActivities();
    }
    String TAG = "IntentTesting";
}

