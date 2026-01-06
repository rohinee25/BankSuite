package me.rohinee.banksuite.features.bankspecific

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.rohinee.banksuite.config.AppLogger
import me.rohinee.banksuite.features.AutoPaymentResult
import me.rohinee.banksuite.features.AutoPaymentSchedule
import me.rohinee.banksuite.features.IAutoPaymentManager
import me.rohinee.banksuite.features.PaymentFrequency

/**
 * Bank B implementation of Auto Payment feature.
 * This code will ONLY be included in Bank B APK.
 * Note: Different implementation than Bank A!
 */
class AutoPaymentManager : IAutoPaymentManager {

    companion object {
        private const val TAG = "BankB_AutoPayment"
    }

    override val isFeatureAvailable: Boolean = true

    override fun getServiceName(): String {
        return "BankB SmartPay"
    }

    override fun getAutoPaymentFee(): Double {
        return 0.75  // Bank B charges $0.75 per transaction
    }

    override fun getMaxAutoPaymentAmount(): Double {
        return 100000.0  // $100,000 limit (different from Bank A)
    }

    override suspend fun enableAutoPayment(accountId: String): AutoPaymentResult {
        AppLogger.i(TAG, "BankB: Enabling auto payment for account: $accountId")

        // Simulate API call with Bank B's slower backend
        delay(800)

        val transactionId = "BANKB-${accountId.takeLast(4)}-${System.currentTimeMillis()}"

        AppLogger.i(TAG, "BankB: Auto payment enabled with ID: $transactionId")

        return AutoPaymentResult(
            success = true,
            message = "BankB SmartPay activated for account $accountId",
            transactionId = transactionId
        )
    }

    override suspend fun disableAutoPayment(accountId: String): AutoPaymentResult {
        AppLogger.i(TAG, "BankB: Disabling auto payment for account: $accountId")

        delay(600)

        return AutoPaymentResult(
            success = true,
            message = "BankB SmartPay deactivated for account $accountId",
            transactionId = "BANKB-TERMINATE-${System.currentTimeMillis()}"
        )
    }

    override suspend fun getAutoPaymentSchedule(accountId: String): Flow<AutoPaymentSchedule> {
        AppLogger.i(TAG, "BankB: Fetching payment schedule for: $accountId")

        return flow {
            delay(300)

            emit(
                AutoPaymentSchedule(
                    scheduleId = "BKB-${accountId}",
                    amount = 2500.0,
                    frequency = PaymentFrequency.WEEKLY,
                    nextPaymentDate = "2026-01-12",
                    accountId = accountId,
                    beneficiaryName = "Insurance Premium"
                )
            )

            AppLogger.i(TAG, "BankB: Schedule retrieved")
        }
    }

    override suspend fun updateAutoPaymentAmount(
        accountId: String,
        amount: Double
    ): AutoPaymentResult {
        AppLogger.i(TAG, "BankB: Updating payment amount to $amount")

        // Bank B has different validation logic
        if (amount < 100) {
            return AutoPaymentResult(
                success = false,
                message = "BankB: Minimum amount must be $100"
            )
        }

        if (amount > getMaxAutoPaymentAmount()) {
            return AutoPaymentResult(
                success = false,
                message = "BankB: Exceeds limit of $${getMaxAutoPaymentAmount()}"
            )
        }

        delay(400)

        return AutoPaymentResult(
            success = true,
            message = "BankB: Payment amount updated to $$amount",
            transactionId = "BKB-MOD-${System.currentTimeMillis()}"
        )
    }

    /**
     * Bank B specific method - loyalty bonus calculator
     */
    fun calculateLoyaltyBonus(amount: Double): Double {
        // Bank B gives 1% cashback on all auto payments
        return amount * 0.01
    }

    /**
     * Bank B specific - check if eligible for tier upgrade
     */
    fun isEligibleForTierUpgrade(totalMonthlyAmount: Double): Boolean {
        return totalMonthlyAmount >= 10000.0
    }

    /**
     * Get Bank B specific promotions
     */
    fun getBankBPromotions(): List<String> {
        return listOf(
            "1% cashback on all SmartPay transactions",
            "Free tier upgrade for payments > $10,000/month",
            "No fees for first year"
        )
    }
}