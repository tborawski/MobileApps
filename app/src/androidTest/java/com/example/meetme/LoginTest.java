package com.example.meetme;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {
    private String emailToBeTyped;
    private String passwordToBeTyped;

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void initializeString() {
        emailToBeTyped = "hpainhas@hotmail.com";
        passwordToBeTyped = "Testing2019";
    }

    @Test
    public void loginActivityTest() {
        onView(withId(R.id.email)).perform(typeText(emailToBeTyped), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(passwordToBeTyped), closeSoftKeyboard());
        onView(withId(R.id.sign_in)).perform(click());
    }
}