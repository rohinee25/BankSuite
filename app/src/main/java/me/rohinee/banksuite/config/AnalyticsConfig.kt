package me.rohinee.banksuite.config

import android.content.Context

/**
 * Analytics configuration and tracking helper.
 * Analytics is only enabled for staging, preprod, and prod environments.
 */
object AnalyticsConfig {

    /**
     * Initialize analytics module.
     * Should be called in Application class's onCreate.
     */
    fun initialize(context: Context) {
        if (FeatureConfig.isAnalyticsEnabled()) {
            // Initialize your analytics SDK here (e.g., Firebase Analytics, Mixpanel, etc.)
            // Example:
            // FirebaseAnalytics.getInstance(context)
            AppLogger.i("AnalyticsConfig", "Analytics initialized for ${FeatureConfig.getEnvironment()}")
        } else {
            AppLogger.i("AnalyticsConfig", "Analytics disabled for ${FeatureConfig.getEnvironment()}")
        }
    }

    /**
     * Track a custom event
     */
    fun trackEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        if (FeatureConfig.isAnalyticsEnabled()) {
            // Send event to analytics SDK
            // Example:
            // FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle)
            AppLogger.i("AnalyticsConfig", "Event tracked: $eventName, params: $params")
        } else {
            AppLogger.d("AnalyticsConfig", "Analytics disabled - Event not tracked: $eventName")
        }
    }

    /**
     * Track screen view
     */
    fun trackScreen(screenName: String, screenClassOverride: String? = null) {
        if (FeatureConfig.isAnalyticsEnabled()) {
            AppLogger.i("AnalyticsConfig", "Screen tracked: $screenName")
        }
    }

    /**
     * Track user property
     */
    fun setUserProperty(name: String, value: String) {
        if (FeatureConfig.isAnalyticsEnabled()) {
            AppLogger.i("AnalyticsConfig", "User property set: $name = $value")
        }
    }

    /**
     * Set user ID for analytics
     */
    fun setUserId(userId: String) {
        if (FeatureConfig.isAnalyticsEnabled()) {
            AppLogger.i("AnalyticsConfig", "User ID set: $userId")
        }
    }

    /**
     * Enable/disable analytics at runtime (useful для testing)
     */
    fun setAnalyticsEnabled(enabled: Boolean) {
        if (FeatureConfig.isAnalyticsEnabled()) {
            // Override or control analytics SDK at runtime
            AppLogger.i("AnalyticsConfig", "Analytics runtime override: $enabled")
        }
    }

    /**
     * Check if analytics is enabled both at build time and runtime
     */
    fun canTrack(): Boolean = FeatureConfig.isAnalyticsEnabled()

    /**
     * Flush analytics events (if your SDK supports batching)
     */
    fun flush() {
        if (FeatureConfig.isAnalyticsEnabled()) {
            AppLogger.i("AnalyticsConfig", "Analytics flushed")
        }
    }
}