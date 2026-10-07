package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AutoCategoryClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun testAppNameString() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PayMitra", appName)
    }

    @Test
    fun testAutoCategoryClassification() {
        // Test hospital tagging
        val hospitalResult = AutoCategoryClassifier.classify("Apollo Pharmacy", "apollopharmacy@icici", "Medicines")
        assertNotNull(hospitalResult)
        assertEquals("hospital", hospitalResult?.categoryKey)

        // Test food tagging
        val foodResult = AutoCategoryClassifier.classify("Swiggy Online", "swiggy@axis", "Biryani dinner")
        assertNotNull(foodResult)
        assertEquals("food", foodResult?.categoryKey)

        // Test fuel / travel tagging
        val travelResult = AutoCategoryClassifier.classify("HPCL Petrol Pump", "hp@sbi", "Fuel")
        assertNotNull(travelResult)
        assertEquals("travel", travelResult?.categoryKey)

        // Test personal tagging
        val personalResult = AutoCategoryClassifier.classify("Suresh Brother", "suresh@upi", "Family gift")
        assertNotNull(personalResult)
        assertEquals("personal", personalResult?.categoryKey)
    }

    @Test
    fun testBilingualTranslations() {
        assertEquals("PayMitra UPI", AppStrings.get("app_title", AppLanguage.ENGLISH))
        assertEquals("పేమిత్ర UPI", AppStrings.get("app_title", AppLanguage.TELUGU))

        assertEquals("Food & Dining", AppStrings.get("cat_food", AppLanguage.ENGLISH))
        assertEquals("ఆహారం & భోజనం", AppStrings.get("cat_food", AppLanguage.TELUGU))

        assertEquals("Hospital & Health", AppStrings.get("cat_hospital", AppLanguage.ENGLISH))
        assertEquals("ఆసుపత్రి & ఆరోగ్యం", AppStrings.get("cat_hospital", AppLanguage.TELUGU))

        assertEquals("Critical Emergency SOS", AppStrings.get("emergency_sos", AppLanguage.ENGLISH))
        assertEquals("అత్యవసర SOS సహాయం", AppStrings.get("emergency_sos", AppLanguage.TELUGU))
    }
}
