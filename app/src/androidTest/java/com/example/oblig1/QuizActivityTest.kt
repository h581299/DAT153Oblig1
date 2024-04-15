package com.example.oblig1

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<QuizActivity> = ActivityTestRule(QuizActivity::class.java)

    @Test
    fun testCorrectAnswerUpdatesScore() {
        // Simulate answering a question correctly
        activityRule.activity.checkAnswer("CorrectAnswer", "CorrectAnswer")

        // Verify that the score is updated
        onView(withId(R.id.scoreTextView)).check(matches(withText("Score: 1"))) // Assuming the initial score is 0
    }

    @Test
    fun testIncorrectAnswerDoesNotUpdateScore() {
        // Simulate answering a question incorrectly
        activityRule.activity.checkAnswer("IncorrectAnswer", "CorrectAnswer")

        // Verify that the score is not updated
        onView(withId(R.id.scoreTextView)).check(matches(withText("Score: 0"))) // Assuming the initial score is 0
    }
}