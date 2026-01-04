package me.rohinee.banksuite

import android.app.Application
import me.rohinee.banksuite.config.AnalyticsConfig
import me.rohinee.banksuite.config.AppLogger
import me.rohinee.banksuite.config.FeatureConfig

/**
 * Main Application class for BankSuite.
 * Initializes all necessary components at app startup.
 */
class BankSuiteApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Log app startup info
        AppLogger.i("BankSuiteApplication", "BankSuite app started")
        AppLogger.i("BankSuiteApplication", FeatureConfig.getAppInfo())

        // Initialize analytics if enabled for the current environment
        initializeAnalytics()

        // Initialize other services here
        // - Crash reporting (e.g., Firebase Crashlytics)
        // - Dependency injection
        // - Database
        // - etc.
    }

    private fun initializeAnalytics() {
        try {
            AnalyticsConfig.initialize(this)
        } catch (e: Exception) {
            AppLogger.e("BankSuiteApplication", "Failed to initialize analytics", e)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        AppLogger.i("BankSuiteApplication", "BankSuite app terminated")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        AppLogger.w("BankSuiteApplication", "Low memory warning received")
    }
}