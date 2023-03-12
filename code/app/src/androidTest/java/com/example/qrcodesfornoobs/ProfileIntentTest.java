package com.example.qrcodesfornoobs;
import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.qrcodesfornoobs.Activity.MainActivity;
import com.example.qrcodesfornoobs.Activity.ProfileActivity;
import com.example.qrcodesfornoobs.Activity.SignInActivity;
import com.example.qrcodesfornoobs.Fragment.DashboardFragment;
import com.example.qrcodesfornoobs.Models.Player;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import junit.framework.TestCase;

public class ProfileIntentTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<ProfileActivity> rule =
            new ActivityTestRule<>(ProfileActivity.class, true, false);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        Player.LOCAL_USERNAME = "testfranny";

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        rule.launchActivity(null);

//        solo.assertCurrentActivity("Wrong Activity", SignInActivity.class);
//        solo.enterText((EditText) solo.getView(R.id.username_EditText), Player.LOCAL_USERNAME);
//        solo.clickOnView(solo.getView(R.id.sign_in_button));

    }

    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();

    }

    @Test
    public void checkOpenProfile() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
    }

    @Test
    public void checkUsername() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        assertTrue(solo.waitForText(Player.LOCAL_USERNAME));

    }

    /**
     * Checks that the recycler view displays properly
     */
    @Test
    public void checkViewCodes() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnView(solo.getView(R.id.toggle_recyclerView_button));
        assertTrue(solo.waitForView(R.id.recyclerView));
    }

    @Test
    public void checkRemoveCodes() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnView(solo.getView(R.id.toggle_recyclerView_button));
        RecyclerView recyclerView = (RecyclerView) solo.getView(R.id.recyclerView);
        assertTrue(solo.waitForView(R.id.recyclerView));
        int numCodesBefore = recyclerView.getAdapter().getItemCount();
        solo.drag(850,9,910,910,10);
        assertTrue(solo.waitForView(R.id.recyclerView));
        int numCodesAfter = recyclerView.getAdapter().getItemCount();
        assertEquals(numCodesAfter,numCodesBefore-1);
    }

    /**
     * Check that sort properly lists codes in ascending and descending order
     */
    @Test
    public void checkHighestLowestCodes() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnView(solo.getView(R.id.toggle_recyclerView_button));
        assertTrue(solo.waitForView(R.id.recyclerView));
        RecyclerView recyclerView = (RecyclerView) solo.getView(R.id.recyclerView);

        Spinner spinner = (Spinner) solo.getView(R.id.sort_list_spinner);


        // Click sort by ascending
        solo.clickOnView(spinner);
        solo.clickInList(0);
        assertTrue(solo.isSpinnerTextSelected("SCORE (ASCENDING)"));

        int previousNumber = Integer.MIN_VALUE;
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            TextView textView = viewHolder.itemView.findViewById(R.id.profile_code_points);
            String text = textView.getText().toString();
            String[] words = text.split(" ");
            int currNumber = Integer.parseInt(words[0]);
            assertTrue(currNumber >= previousNumber);
            previousNumber = currNumber;
        }
        // Click sort by descending
        solo.clickOnView(spinner);
        solo.clickInList(2);
        assertTrue(solo.isSpinnerTextSelected("SCORE (DESCENDING)"));

        previousNumber = Integer.MAX_VALUE;
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            TextView textView = viewHolder.itemView.findViewById(R.id.profile_code_points);
            String text = textView.getText().toString();
            String[] words = text.split(" ");
            int currNumber = Integer.parseInt(words[0]);
            assertTrue(currNumber <= previousNumber);
            previousNumber = currNumber;
        }
    }

    @Test
    public void checkSumScores(){
        //TODO
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnView(solo.getView(R.id.toggle_recyclerView_button));
        assertTrue(solo.waitForView(R.id.recyclerView));
        RecyclerView recyclerView = (RecyclerView) solo.getView(R.id.recyclerView);

    }

    /**
     * Check that the number of codes displayed at the top of profile
     * matches the number of codes displayed on profile (therefore the # of codes in db)
     */
    @Test
    public void checkTotalCodes(){
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnView(solo.getView(R.id.toggle_recyclerView_button));
        assertTrue(solo.waitForView(R.id.recyclerView));
        RecyclerView recyclerView = (RecyclerView) solo.getView(R.id.recyclerView);

        TextView codeCountText = (TextView) solo.getView(R.id.profile_playercodecount_textview);
        String text = codeCountText.getText().toString();
        String[] words = text.split(" ");
        int numCodesDisplayed = Integer.parseInt(words[0]);
        int numCodesInList = recyclerView.getAdapter().getItemCount();

        assertEquals(numCodesDisplayed, numCodesInList);

    }


}
