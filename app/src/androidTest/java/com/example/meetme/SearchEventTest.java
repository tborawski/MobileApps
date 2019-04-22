package com.example.meetme;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchEventTest {
    private String stringToBeTyped;

    @Rule
    public ActivityTestRule<MainPageActivity> ActivityRule = new ActivityTestRule<>(MainPageActivity.class);

    @Before
    public void initializeString() {
        stringToBeTyped = "Soc";
    }

    @Test
    public void searchEventTest() {
        onView(withId(R.id.search_event)).perform(typeText(stringToBeTyped), closeSoftKeyboard());
    }
}

