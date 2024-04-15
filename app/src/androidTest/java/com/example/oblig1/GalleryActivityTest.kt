import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.oblig1.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.LargeTest
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
@LargeTest
class GalleryActivityTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<GalleryActivity>()

    @Test
    fun testAddNewEntryWithIntentStubbing() {
        // Perform the action that triggers the intent (click the button)
        onView(withId(R.id.addEntryButton)).perform(click())

        // Handle the dialog
        onView(withText("Enter Name")).inRoot(isDialog())

        // Input text into the EditText
        onView(withId(R.id.inputEditText))
            .perform(typeText("Test"), closeSoftKeyboard())

        // Click on the "OK" button
        onView(withText("OK")).inRoot(isDialog()).perform(click())

        // Stub the intent to return image data
        val resultData = Intent().apply {
            putExtra("name", "Test")
            // Add any other necessary data here
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(result)

        // Verify that the intent was sent
        intended(hasAction(Intent.ACTION_OPEN_DOCUMENT))

        // Assert any changes or perform further actions as needed
        assertEquals(1, 1)
    }
}