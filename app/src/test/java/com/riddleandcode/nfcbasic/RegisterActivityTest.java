package com.riddleandcode.nfcbasic;

import com.riddleandcode.nfcbasic.activities.MainScreenSlidePagerActivity;
import com.riddleandcode.nfcbasic.activities.RegisterActivity;


import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {


    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void ensureTextChangesWork() {

        onView(withId(R.id.email)).perform(typeText("marc.madsen@mail.com"));
        onView(withId(R.id.password)).perform(typeText("marc.madsen"));
        onView(withId(R.id.log_in_button)).perform(click());
    }

    @Before
    public void grantPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                  "pm grant " + getTargetContext().getPackageName()
                        + " android.permission.NFC");
        }
    }
}