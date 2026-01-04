package me.rohinee.banksuite

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import me.rohinee.banksuite.config.FeatureConfig

/**
 * Instrumented tests for MainActivity and Compose UI
 */
@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDashboardTitle_isDisplayed() {
        composeTestRule.setContent {
            BankingDashboard(modifier = androidx.compose.ui.Modifier)
        }

        // Check if "Available Services" section is displayed
        composeTestRule.onNodeWithText("Available Services")
            .assertIsDisplayed()
    }

    @Test
    fun testAppInfoSection_isDisplayed() {
        composeTestRule.setContent {
            BankingDashboard(modifier = androidx.compose.ui.Modifier)
        }

        // Check if App Information section is displayed
        composeTestRule.onNodeWithText("App Information")
            .assertIsDisplayed()
    }

    @Test
    fun testEnvironmentSection_isDisplayed() {
        composeTestRule.setContent {
            BankingDashboard(modifier = androidx.compose.ui.Modifier)
        }

        // Check if Environment section is displayed
        composeTestRule.onNodeWithText("Current Environment")
            .assertIsDisplayed()
    }

    @Test
    fun testFeatureItems_areDisplayed() {
        composeTestRule.setContent {
            BankingDashboard(modifier = androidx.compose.ui.Modifier)
        }

        // Check if feature items are displayed
        composeTestRule.onNodeWithText("UPI")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Transactions")
            .assertIsDisplayed()
    }

    @Test
    fun testScrollContent_isScrollable() {
        composeTestRule.setContent {
            BankingDashboard(modifier = androidx.compose.ui.Modifier)
        }

        // Verify that all three main sections exist
        composeTestRule.onNodeWithText("Current Environment").assertIsDisplayed()
        composeTestRule.onNodeWithText("Available Services").assertIsDisplayed()
        composeTestRule.onNodeWithText("App Information").assertIsDisplayed()
    }
}