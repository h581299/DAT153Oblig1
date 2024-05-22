package com.example.oblig1

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import junit.framework.TestCase
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizActivityTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<QuizActivity>()

    @Test
    fun testCorrectAnswerUpdatesScore() {
        // Launch the QuizActivity
        ActivityScenario.launch(QuizActivity::class.java)

        // Retrieve the tag from the ImageView
        var imageTag: String? = null
        onView(withId(R.id.imageView)).check { view, _ ->
            val imageView = view as ImageView
            imageTag = imageView.tag as? String
        }

        // Check that the tag is not null
        assert(imageTag != null)

        // Find the button with the text matching the tag
        val correctButtonMatcher: Matcher<View> = allOf(
            isAssignableFrom(Button::class.java),
            withText(equalTo(imageTag))
        )

        // Click the correct button
        onView(correctButtonMatcher).perform(click())

        // Verify the score has increased
        onView(withId(R.id.scoreTextView)).check(matches(withText("Score: 1")))
    }

    @Test
    fun testIncorrectAnswerDoesNotUpdateScore() {
        // Launch the QuizActivity
        ActivityScenario.launch(QuizActivity::class.java)

        // Retrieve the tag from the ImageView
        var imageTag: String? = null
        onView(withId(R.id.imageView)).check { view, _ ->
            val imageView = view as ImageView
            imageTag = imageView.tag as? String
        }

        // Check that the tag is not null
        assert(imageTag != null)

        // Custom matcher to find a button with text that does not match the tag
        fun withTextNotEqualTo(expectedText: String): Matcher<View> {
            return object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description) {
                    description.appendText("with text not equal to: $expectedText")
                }

                override fun matchesSafely(view: View): Boolean {
                    if (view !is Button) return false
                    return view.text.toString() != expectedText
                }
            }
        }

        // Click one of the incorrect buttons (assuming there are exactly three buttons)
        var clicked = false
        for (buttonId in listOf(R.id.option1Button, R.id.option2Button, R.id.option3Button)) {
            try {
                if (!clicked) {
                    onView(allOf(withId(buttonId), withTextNotEqualTo(imageTag!!))).perform(click())
                    clicked = true
                }
            } catch (e: Exception) {
                // Ignore if no match found for this button, move to the next button
            }
        }

        // Verify the score has not changed
        onView(withId(R.id.scoreTextView)).check(matches(withText("Score: 0")))
    }
}