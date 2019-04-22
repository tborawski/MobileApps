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
public class SignUpTest {
    private String firstNameToBeTyped;
    private String lastNameToBeTyped;
    private String emailToBeTyped;
    private String passwordToBeTyped;

    @Rule
    public ActivityTestRule<SignUpActivity> activityRule = new ActivityTestRule<>(SignUpActivity.class);

    @Before
    public void initializeString() {
        firstNameToBeTyped = "John";
        lastNameToBeTyped = "Doe";
        emailToBeTyped = "doe@gmail.com";
        passwordToBeTyped = "Testing2019";
    }

    @Test
    public void loginActivityTest() {
        try {
            Thread.sleep(5000);
            onView(withId(R.id.first_name)).perform(typeText(firstNameToBeTyped), closeSoftKeyboard());
            onView(withId(R.id.last_name)).perform(typeText(lastNameToBeTyped), closeSoftKeyboard());
            onView(withId(R.id.email)).perform(typeText(emailToBeTyped), closeSoftKeyboard());
            onView(withId(R.id.password)).perform(typeText(passwordToBeTyped), closeSoftKeyboard());
            onView(withId(R.id.sign_up_button)).perform(click());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}