package me.rohinee.banksuite.features

import kotlinx.coroutines.flow.Flow

/**
 * Common interface for Auto Payment feature.
 * Each bank variant provides its own implementation.
 */
interface IAutoPaymentManager {

    /**
     * Check if auto payment is enabled for this bank variant
     */
    val isFeatureAvailable: Boolean

    /**
     * Get the name of the auto payment service
     */
    fun getServiceName(): String

    /**
     * Auto payment fee charged by the bank
     */
    fun getAutoPaymentFee(): Double

    /**
     * Maximum amount that can be set for auto payment
     */
    fun getMaxAutoPaymentAmount(): Double

    /**
     * Enable auto payment for a customer
     */
    suspend fun enableAutoPayment(accountId: String): AutoPaymentResult

    /**
     * Disable auto payment
     */
    suspend fun disableAutoPayment(accountId: String): AutoPaymentResult

    /**
     * Get auto payment schedule
     */
    suspend fun getAutoPaymentSchedule(accountId: String): Flow<AutoPaymentSchedule>

    /**
     * Update auto payment amount
     */
    suspend fun updateAutoPaymentAmount(accountId: String, amount: Double): AutoPaymentResult
}

/**
 * Result of auto payment operation
 */
data class AutoPaymentResult(
    val success: Boolean,
    val message: String,
    val transactionId: String? = null
)

/**
 * Auto payment schedule details
 */
data class AutoPaymentSchedule(
    val scheduleId: String,
    val amount: Double,
    val frequency: PaymentFrequency,
    val nextPaymentDate: String,
    val accountId: String,
    val beneficiaryName: String
)

/**
 * Payment frequency options
 */
enum class PaymentFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY
}