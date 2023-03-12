package com.example.qrcodesfornoobs;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PointF;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.widget.ScrollView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrcodesfornoobs.Activity.MainActivity;
import com.example.qrcodesfornoobs.Activity.ProfileActivity;
import com.example.qrcodesfornoobs.Activity.SettingsActivity;
import com.example.qrcodesfornoobs.Activity.SignInActivity;
import com.example.qrcodesfornoobs.Activity.TakePhotoActivity;
import com.example.qrcodesfornoobs.Fragment.DashboardFragment;
import com.example.qrcodesfornoobs.Fragment.MapFragment;
import com.example.qrcodesfornoobs.Models.Player;
import com.robotium.solo.Solo;
import com.smarteist.autoimageslider.SliderView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import junit.framework.AssertionFailedError;

public class DashboardFragmentTest {

        private Solo solo;
        private final String TAG = "TAG POST ROBOTIUM";


        @Rule
        public ActivityTestRule<MainActivity> rule =
                new ActivityTestRule<>(MainActivity.class, true, true);

        @Before
        public void setUp() throws Exception{
                solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
                Player.LOCAL_USERNAME = "TEST_BRETT";
        }

        @Test
        public void testHomePage() throws Exception{
                solo.assertCurrentActivity("Not in Dashboard", MainActivity.class);
                assertTrue(solo.waitForText(Player.LOCAL_USERNAME, 1, 2000));

                Fragment fragment = solo.getCurrentActivity().getFragmentManager().getFragments().get(0);
                Log.d(TAG, fragment.getTag());
        }

        @Test
        public void testSwipe() throws Exception{
                solo.assertCurrentActivity("Not in Dashboard", MainActivity.class);

                float fromX, toX, Y;

                Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                fromX = (float) (size.x * 0.1);
                toX = size.x - fromX;
                Y = (float) (size.y * 0.6);

                SliderView view = (SliderView) solo.getView(R.id.dashboard_sliderView);

                assertEquals(0, view.getCurrentPagePosition());
                assertNotEquals(1, view.getCurrentPagePosition());
                solo.drag(fromX, toX, Y, Y, 10);
                assertEquals(1, view.getCurrentPagePosition());
        }

        @Test
        public void testProfileButton() throws Exception{
                solo.assertCurrentActivity("Not in Dashboard", MainActivity.class);
                solo.clickOnView(solo.getView(R.id.profile_imageButton));

                solo.assertCurrentActivity("Not in Profile", ProfileActivity.class);
        }

        @Test
        public void testSettingsButton() throws Exception{
                solo.assertCurrentActivity("Not in Dashboard", MainActivity.class);
                solo.clickOnView(solo.getView(R.id.setting_imageButton));

                solo.assertCurrentActivity("Not in Settings", SettingsActivity.class);
        }

        @Test
        public void testPhotoButton() throws Exception{
                solo.assertCurrentActivity("Not in Dashboard", MainActivity.class);
                solo.clickOnView(solo.getView(R.id.camera));

                solo.assertCurrentActivity("Not in TakePhotoActivity", TakePhotoActivity.class);
        }

        @Test
        public void  testSearchButton() throws Exception{
                solo.assertCurrentActivity("Not in Dashboard", MainActivity.class);
                solo.clickOnView(solo.getView(R.id.search));

                assertTrue(solo.waitForText("SEARCH", 1, 2000));
        }

        @Test
        public void  testLeaderboardFragment() throws Exception {
                solo.assertCurrentActivity("Not in Dashboard", MainActivity.class);
                solo.clickOnView(solo.getView(R.id.leaderboard));

                assertTrue(solo.waitForText("Leaderboard Screen", 1, 2000));
        }

        @Test
        public void  testMapFragment() throws Exception{
                solo.assertCurrentActivity("Not in Dashboard", MainActivity.class);
                solo.clickOnView(solo.getView(R.id.map));

                assertTrue(solo.waitForText("Map Screen", 1, 2000));
        }

        @After
        public void tearDown() throws Exception{
                solo.finishOpenedActivities();
        }
}