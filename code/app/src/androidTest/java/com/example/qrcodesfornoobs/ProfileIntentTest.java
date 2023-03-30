package com.example.qrcodesfornoobs;
import android.app.Activity;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.qrcodesfornoobs.Activity.ProfileActivity;
import com.example.qrcodesfornoobs.Fragment.CommentFragment;
import com.example.qrcodesfornoobs.Models.Player;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


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

    /**
     * Check that the profile activity opens successfully
     */
    @Test
    public void checkOpenProfile() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
    }

    /**
     * Check that the user's local username is equal to the one displayed on profile
     */
    @Test
    public void checkUsername() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        TextView nameOnProfileText = (TextView) solo.getView(R.id.profile_playername_textview);
        String nameOnProfile = nameOnProfileText.getText().toString();
        assertTrue(solo.waitForText(Player.LOCAL_USERNAME));
        assertEquals(Player.LOCAL_USERNAME,nameOnProfile);
    }

    /**
     * Check that the recycler view displays properly
     */
    @Test
    public void checkViewCodes() {
        // Check that activity is correct
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        // Open recycler view
        // Check that view displays
        assertTrue(solo.waitForView(R.id.recyclerView));

    }

    /**
     * Check that swiping on a list item removes it from the profile
     */
    @Test
    public void checkRemoveCodes() {
        checkViewCodes();

        RecyclerView recyclerView = (RecyclerView) solo.getView(R.id.recyclerView);

        // Get number of codes before deleting
        int numCodesBefore = recyclerView.getAdapter().getItemCount();
        // Swipe to delete
        solo.drag(850,9,910,910,10);
        assertTrue(solo.waitForView(R.id.recyclerView));
        // Get number of codes after deleting
        int numCodesAfter = recyclerView.getAdapter().getItemCount();
        // Check that code was deleted properly
        assertEquals(numCodesAfter,numCodesBefore-1);

    }



    /**
     * Check that sum of scores displayed at top of profile is equal to sum of scores in list
     */
    @Test
    public void checkSumScores(){
        checkViewCodes();

        RecyclerView recyclerView = (RecyclerView) solo.getView(R.id.recyclerView);
        TextView profileTotalPointsText = (TextView) solo.getView(R.id.profile_playerpoints_textview);

        // Get sum of points from top of profile
        int profileTotalPoints = extractNumber(profileTotalPointsText,0);

        // Iterate through list and sum up all points
        int sumPoints = 0;
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            TextView textView = viewHolder.itemView.findViewById(R.id.profile_code_points);
            sumPoints += extractNumber(textView,0);
        }

        assertEquals(sumPoints,profileTotalPoints);

    }
    /**
     * Check that sort properly lists codes in ascending and descending order
     */
    @Test
    public void checkLowestHighestCodes() {
        checkViewCodes();

        RecyclerView recyclerView = (RecyclerView) solo.getView(R.id.recyclerView);

        // Click sort by descending
        solo.clickOnView(solo.getView(R.id.sort_list_spinner));
        solo.clickInList(2);
        assertTrue(solo.isSpinnerTextSelected("SCORE (DESCENDING)"));

        // Iterate through list of creatures and check that the points decrease
        int previousNumber = Integer.MAX_VALUE;
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            TextView textView = viewHolder.itemView.findViewById(R.id.profile_code_points);
            int currNumber = extractNumber(textView,0);
            assertTrue(currNumber <= previousNumber);
            previousNumber = currNumber;
        }
        // Click sort by ascending
        solo.clickOnView(solo.getView(R.id.sort_list_spinner));
        solo.clickInList(0);
        assertTrue(solo.isSpinnerTextSelected("SCORE (ASCENDING)"));

        // Iterate through list of creatures and check that the points increase
        previousNumber = Integer.MIN_VALUE;
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            TextView textView = viewHolder.itemView.findViewById(R.id.profile_code_points);
            int currNumber = extractNumber(textView,0);
            Log.d("tag", "Previous number = " + previousNumber);
            Log.d("tag", "Current number = " + currNumber);
            assertTrue(currNumber >= previousNumber);
            previousNumber = currNumber;
        }
    }
    /**
     * Check that the number of codes displayed at the top of profile
     * matches the number of codes displayed on profile (therefore the # of codes in db)
     */
    @Test
    public void checkTotalCodes(){
        checkViewCodes();

        RecyclerView recyclerView = (RecyclerView) solo.getView(R.id.recyclerView);

        // Get number of codes from top of profile
        TextView codeCountText = (TextView) solo.getView(R.id.profile_playercodecount_textview);
        int numCodesDisplayed = extractNumber(codeCountText,0);

        // Get number of codes in list
        int numCodesInList = recyclerView.getAdapter().getItemCount();

        assertEquals(numCodesDisplayed, numCodesInList);

    }

    /**
     * Check that the string "Scanned by X players" is present in all list items
     */
    @Test
    public void checkOtherScanned(){
        checkViewCodes();

        RecyclerView recyclerView = (RecyclerView) solo.getView(R.id.recyclerView);

        TextView othersScannedText;
        String othersScanned;

        // Pattern to match
        String pattern = "^Scanned by \\d+ Player[s]?$";

        // Iterate through each item in list and make sure string matches
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            othersScannedText = (TextView) viewHolder.itemView.findViewById(R.id.profile_num_of_scans);
            othersScanned = othersScannedText.getText().toString();
            assertTrue(othersScanned.matches(pattern));
        }
    }

    @Test
    public void checkComments(){
        checkViewCodes();

        // Click on first item in recycler view
        solo.clickInRecyclerView(1);

        // Wait for comment fragment to appear
        FragmentManager fragmentManager = rule.getActivity().getSupportFragmentManager();
        CommentFragment fragment = (CommentFragment) fragmentManager.findFragmentByTag("Open Comments");
        assertTrue(solo.waitForFragmentById(fragment.getId()));

        // Initialize comment recycler view
        RecyclerView commentsRecyclerView = (RecyclerView) solo.getView(R.id.comment_recyclerView);

        // Get the number of comments before adding any
        int numCommentsBefore = commentsRecyclerView.getAdapter().getItemCount();

        EditText commentEditText = (EditText) solo.getView(R.id.comment_input_edittext);
        Button submitCommentButton = (Button) solo.getView(R.id.submit_comment_button);

        // Move dialog up a bit to detect the views
        solo.drag(850,850,1100,900,10);

        // Send a comment
        solo.clearEditText(commentEditText);
        solo.typeText(commentEditText, "testing adding comments");
        solo.clickOnView(submitCommentButton);

        // Wait for new comment to appear
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                int numCommentsAfter = commentsRecyclerView.getAdapter().getItemCount();
                return (numCommentsAfter - 1 == numCommentsBefore);
            }
        }, 5000);

    }


    /**
     * Extracts an integer value from the specified token of the textview
     * @param textView textview which contains string to be extracted
     * @param pos location to extract integer value from
     * @return an integer which represents the chosen digit of the textview
     */
    private int extractNumber(TextView textView, int pos){
        String text = textView.getText().toString();
        String[] temp = text.split(" ");
        return Integer.parseInt(temp[pos]);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();

    }
}
