package me.rohinee.banksuite.config

import me.rohinee.banksuite.BuildConfig

/**
 * Runtime feature configuration based on BuildConfig bank code.
 * This enables/disable features per bank variant.
 */
object FeatureConfig {

    /**
     * Returns the current bank code from BuildConfig
     */
    fun getBankCode(): String = BuildConfig.BANK_CODE

    /**
     * Returns the current environment name (derived from BASE_URL)
     */
    fun getEnvironment(): String = when {
        BuildConfig.BASE_URL.contains("dev") -> "dev"
        BuildConfig.BASE_URL.contains("qa") -> "qa"
        BuildConfig.BASE_URL.contains("stg") -> "staging"
        BuildConfig.BASE_URL.contains("pp") -> "preprod"
        else -> "prod"
    }

    // === Feature Flags ===

    /**
     * Savings Account feature
     * Enabled for: Bank A, Bank B
     * Disabled for: Bank C
     */
    fun isSavingsAccountEnabled(): Boolean =
        BuildConfig.BANK_CODE == "A" || BuildConfig.BANK_CODE == "B"

    /**
     * Current Account feature
     * Enabled for: Bank B, Bank C
     * Disabled for: Bank A
     */
    fun isCurrentAccountEnabled(): Boolean =
        BuildConfig.BANK_CODE == "B" || BuildConfig.BANK_CODE == "C"

    /**
     * Loans feature
     * Enabled for: Bank A
     * Disabled for: Bank B, Bank C
     */
    fun isLoansEnabled(): Boolean =
        BuildConfig.BANK_CODE == "A"

    /**
     * Credit Card feature
     * Enabled for: Bank C
     * Disabled for: Bank A, Bank B
     */
    fun isCreditCardEnabled(): Boolean =
        BuildConfig.BANK_CODE == "C"

    /**
     * UPI feature
     * Enabled for: All banks
     */
    fun isUPIEnabled(): Boolean = true

    /**
     * Logging enabled check
     * Based on ENABLE_LOGS BuildConfig field
     */
    fun isLoggingEnabled(): Boolean = BuildConfig.ENABLE_LOGS

    /**
     * Analytics enabled check
     * Based on ENABLE_ANALYTICS BuildConfig field
     */
    fun isAnalyticsEnabled(): Boolean = BuildConfig.ENABLE_ANALYTICS

    /**
     * Returns a list of all enabled features for the current bank
     */
    fun getEnabledFeatures(): List<String> {
        val features = mutableListOf<String>()

        if (isSavingsAccountEnabled()) features.add("Savings Account")
        if (isCurrentAccountEnabled()) features.add("Current Account")
        if (isLoansEnabled()) features.add("Loans")
        if (isCreditCardEnabled()) features.add("Credit Card")
        if (isUPIEnabled()) features.add("UPI")

        return features
    }

    /**
     * Returns app info for debugging
     */
    fun getAppInfo(): String {
        return """
            Bank: ${getBankName()}
            Environment: ${getEnvironment()}
            Base URL: ${BuildConfig.BASE_URL}
            Version: ${BuildConfig.VERSION_NAME}
            Logging: ${isLoggingEnabled()}
            Analytics: ${isAnalyticsEnabled()}
        """.trimIndent()
    }

    /**
     * Returns the readable bank name
     */
    fun getBankName(): String = when (BuildConfig.BANK_CODE) {
        "A" -> "Bank A"
        "B" -> "Bank B"
        "C" -> "Bank C"
        else -> "Unknown Bank"
    }
}

/**
 * Utility for logging based on feature config
 */
object AppLogger {
    fun d(tag: String, message: String) {
        if (FeatureConfig.isLoggingEnabled()) {
            println("[$tag] DEBUG: $message")
        }
    }

    fun i(tag: String, message: String) {
        if (FeatureConfig.isLoggingEnabled()) {
            println("[$tag] INFO: $message")
        }
    }

    fun w(tag: String, message: String) {
        if (FeatureConfig.isLoggingEnabled()) {
            println("[$tag] WARN: $message")
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (FeatureConfig.isLoggingEnabled()) {
            println("[$tag] ERROR: $message")
            throwable?.let { it.printStackTrace() }
        }
    }
}