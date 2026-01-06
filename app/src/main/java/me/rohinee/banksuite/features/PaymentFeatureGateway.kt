package me.rohinee.banksuite.features

import me.rohinee.banksuite.BuildConfig
import me.rohinee.banksuite.config.AppLogger

/**
 * Gateway for creating AutoPaymentManager instances.
 * This factory pattern ensures correct implementation is loaded based on bank variant.
 *
 * IMPORTANT:
 * - Bank A and Bank B have their own AutoPaymentManager implementation
 * - Bank C does NOT have this feature - code is excluded from the APK
 *
 * This demonstrates APK-based code inclusion/exclusion:
 * - BankA APK: Contains Bank A's AutoPaymentManager code
 * - BankB APK: Contains Bank B's AutoPaymentManager code
 * - BankC APK: NO AutoPaymentManager code at all!
 */
object PaymentFeatureGateway {

    private const val TAG = "PaymentFeatureGateway"

    private var cachedManager: IAutoPaymentManager? = null

    /**
     * Create an AutoPaymentManager instance using reflection.
     *
     * @return IAutoPaymentManager implementation if feature is available, null otherwise
     *
     * How it works:
     * 1. BankA build → Includes "me.rohinee.banksuite.features.AutoPaymentManager" (BankA impl)
     * 2. BankB build → Includes "me.rohinee.banksuite.features.bankspecific.AutoPaymentManager" (BankB impl)
     * 3. BankC build → NO AutoPaymentManager class exists → Returns null
     */
    fun createAutoPaymentManager(): IAutoPaymentManager? {
        if (cachedManager != null) {
            return cachedManager
        }

        return try {
            val className = when (BuildConfig.BANK_CODE) {
                "A" -> "me.rohinee.banksuite.features.AutoPaymentManager"
                "B" -> "me.rohinee.banksuite.features.bankspecific.AutoPaymentManager"
                "C" -> return null
                else -> return null
            }

            AppLogger.i(TAG, "Attempting to load: $className for bank ${BuildConfig.BANK_CODE}")

            val managerClass = Class.forName(className)
            val instance = managerClass.getDeclaredConstructor().newInstance() as IAutoPaymentManager

            cachedManager = instance
            AppLogger.i(TAG, "Successfully created ${BuildConfig.BANK_CODE} AutoPaymentManager")
            instance
        } catch (e: ClassNotFoundException) {
            AppLogger.w(TAG, "AutoPaymentManager not found for bank ${BuildConfig.BANK_CODE}: ${e.message}")
            null
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error creating AutoPaymentManager", e)
            null
        }
    }

    /**
     * Check if Auto Payment feature is available for current bank variant
     */
    fun isAutoPaymentFeatureAvailable(): Boolean {
        val available = BuildConfig.BANK_CODE in listOf("A", "B")
        AppLogger.i(TAG, "Auto Payment feature available: $available for bank ${BuildConfig.BANK_CODE}")
        return available
    }

    /**
     * Get feature description for the current bank
     */
    fun getFeatureDescription(): String {
        return when (BuildConfig.BANK_CODE) {
            "A" -> "Bank A AutoPay - Automated recurring payments with instant transfers"
            "B" -> "BankB SmartPay - Intelligent scheduling with cashback rewards"
            "C" -> "Auto Payment feature not available for Bank C"
            else -> "Feature not available"
        }
    }

    /**
     * Get all payment frequencies supported by Auto Payment
     */
    fun getSupportedFrequencies(): List<PaymentFrequency> {
        return listOf(
            PaymentFrequency.DAILY,
            PaymentFrequency.WEEKLY,
            PaymentFrequency.MONTHLY,
            PaymentFrequency.QUARTERLY,
            PaymentFrequency.YEARLY
        )
    }
}