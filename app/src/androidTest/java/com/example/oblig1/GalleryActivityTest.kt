import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.oblig1.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import java.io.File


@RunWith(AndroidJUnit4::class)
@LargeTest
class GalleryActivityTest {

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }

    private lateinit var db: AnimalDatabase
    private lateinit var dao: AnimalDao

    @get:Rule
    val activityScenarioRule = activityScenarioRule<GalleryActivity>()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Intents.init()
        activityScenarioRule.scenario.onActivity{GalleryActivity}

        val context = ApplicationProvider.getApplicationContext<Context>()
        val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        db = AnimalDatabase.getDatabase(context, coroutineScope)
        dao = db.animalDao()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testAddNewEntryWithIntentStubbing() {
        val entriesBefore = dao.getNumberOfEntries()
        assertEquals(3, entriesBefore)

        // Stub the intent to return image data
        val testImageUriString = "android.resource://com.example.oblig1/${R.drawable.opossum}" // Assuming the image is in the drawable folder
        val testImageUri = Uri.parse(testImageUriString)

        // Prepare the intent
        var resultData = Intent(Intent.ACTION_OPEN_DOCUMENT)
        resultData.type = "image/*"
        resultData.addCategory(Intent.CATEGORY_OPENABLE)
        resultData.setData(testImageUri)
        resultData.apply {
            putExtra("name", "Test")
        }

        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(result)

        // Perform the actions that triggers the intent
        onView(withId(R.id.addEntryButton)).perform(click())
        onView(withText("Enter Name")).inRoot(isDialog())
        onView(withId(R.id.inputEditText))
            .perform(typeText("Test"), closeSoftKeyboard())
        onView(withText("OK")).inRoot(isDialog()).perform(click())

        // Verify that the intent was sent
        intended(hasAction(Intent.ACTION_OPEN_DOCUMENT))

        Thread.sleep(1000)

        CoroutineScope(Dispatchers.IO).launch {
            val entriesAfter = dao.getNumberOfEntries()
            assertEquals(4, entriesAfter)
        }
    }
}