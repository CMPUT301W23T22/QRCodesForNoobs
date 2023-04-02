package com.example.qrcodesfornoobs;

import android.app.Activity;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrcodesfornoobs.Activity.MainActivity;
import com.example.qrcodesfornoobs.Activity.ProfileActivity;


import com.example.qrcodesfornoobs.Models.Player;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.qrcodesfornoobs.Models.Player;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


/**
 * Test class for SearchFragment. Robotium test framework is used.
 */

public class SearchFragmentTest {
    private Solo solo;
    private FirebaseFirestore db;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Player.LOCAL_USERNAME = "test";
        db = FirebaseFirestore.getInstance();
        Player mockPlayer = new Player("test", "123321456654");
        db.collection("Players").document("test").set(mockPlayer);
        rule.launchActivity(null);
    }

    /**
     * Gets the activity of the fragment.
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkOpenFragment(){
        solo.assertCurrentActivity("Wrong activity.", MainActivity.class);
        solo.waitForFragmentById(solo.getView(R.id.search).getId(),2000);
        solo.clickOnView(solo.getView(R.id.search));
    }

    /**
     * US 05.01.01: As a player, I want to search for other players by username.
     */
    @Test
    public void checkSearchUser(){
        checkOpenFragment();
        solo.clickOnView(solo.getView(R.id.radioUser));
        solo.clickOnView(solo.getView(R.id.username_search));
        assertFalse(solo.searchText("test"));
        solo.enterText(0, "test");
        solo.sendKey(Solo.ENTER);
        assertTrue(solo.waitForText("test", 1, 2000));
    }

    /**
     * US 01.07.01: As a player, I want to see other players profiles.
     */
    @Test
    public void checkSelectUserProfile(){
        checkSearchUser();
        solo.clickInRecyclerView(0);
        solo.assertCurrentActivity("Wrong activity.", ProfileActivity.class);
    }

    /**
     * US 02.03.01 As a player, I want to be able to browse QR codes that other players have scanned.
     */
    @Test
    public void checkBrowsePlayerQR(){
        checkSelectUserProfile();
        solo.searchText("ChiGoVeeTee");
    }

    /**
     *  US 05.02.01 As a player, I want to search for nearby QR codes by using geolocation.
     */
    @Test
    public void checkBrowseByGeolocation(){
        checkOpenFragment();
        solo.clickOnView(solo.getView(R.id.radioLocation));
        solo.clickOnView(solo.getView(R.id.longitude_search));
        assertFalse(solo.searchText("-121"));
        solo.enterText(0, "-121");
        solo.clickOnView(solo.getView(R.id.latitude_search));
        assertFalse(solo.searchText("38"));
        solo.enterText(0, "38");
        solo.sendKey(Solo.ENTER);
        assertTrue(solo.waitForText("s", 1, 2000));

    }


    @After
    public void tearDown() throws Exception{
        db.collection("Players").document("test").delete();
        solo.finishOpenedActivities();
    }
}
