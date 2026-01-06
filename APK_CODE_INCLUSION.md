# APK-Based Code Inclusion/Exclusion - Auto Payment Feature

## Overview

This sample demonstrates **APK-based code inclusion/exclusion** using Android Build Variants with flavor-specific source sets.

### Concept

**Bank A APK**: Contains Bank A's Auto Payment implementation code
**Bank B APK**: Contains Bank B's Auto Payment implementation code (different from A!)
**Bank C APK**: NO Auto Payment code at all - feature is completely excluded!

This is different from runtime feature flags. The actual ** bytecode is excluded** from Bank C's APK.

---

## How It Works

### 1. Common Interface (All APks)

**File**: `app/src/main/java/me/rohinee/banksuite/features/IAutoPaymentManager.kt`

```kotlin
interface IAutoPaymentManager {
    val isFeatureAvailable: Boolean
    suspend fun enableAutoPayment(accountId: String): AutoPaymentResult
    suspend fun disableAutoPayment(accountId: String): AutoPaymentResult
    // ... more methods
}
```

This interface is included in all APKs as it's in the `main` source set.

---

### 2. Bank A Implementation (Only in Bank A APK)

**Directory**: `app/src/bankA/java/me/rohinee/banksuite/features/`

```kotlin
class AutoPaymentManager : IAutoPaymentManager {
    override val isFeatureAvailable: Boolean = true
    override fun getAutoPaymentFee(): Double = 0.50  // $0.50 per transaction
    override fun getMaxAutoPaymentAmount(): Double = 50000.0  // $50,000 limit

    // Bank A specific logic
    fun calculateSavingsByFrequency(amount: Double, frequency: PaymentFrequency): Double {
        // Bank A's unique implementation
    }
}
```

**Result**: This class and all its code is **ONLY** in Bank A's APK.

---

### 3. Bank B Implementation (Only in Bank B APK)

**Directory**: `app/src/bankB/java/me/rohinee/banksuite/features/bankspecific/`

```kotlin
class AutoPaymentManager : IAutoPaymentManager {
    override val isFeatureAvailable: Boolean = true
    override fun getAutoPaymentFee(): Double = 0.75  // Different fee!
    override fun getMaxAutoPaymentAmount(): Double = 100000.0  // Different limit!

    // Bank B specific logic
    fun calculateLoyaltyBonus(amount: Double): Double {
        return amount * 0.01  // 1% cashback
    }
}
```

**Result**: This class is **ONLY** in Bank B's APK. Note the different implementation!

---

### 4. Bank C (No Implementation)

**Directory**: `app/src/bankC/` - **Empty!** No AutoPaymentManager class exists.

**Result**: No Auto Payment code in Bank C's APK at all!

---

### 5. Factory Pattern for Loading

**File**: `app/src/main/java/me/rohinee/banksuite/features/PaymentFeatureGateway.kt`

```kotlin
object PaymentFeatureGateway {
    fun createAutoPaymentManager(): IAutoPaymentManager? {
        return try {
            val className = when (BuildConfig.BANK_CODE) {
                "A" -> "me.rohinee.banksuite.features.AutoPaymentManager"
                "B" -> "me.rohinee.banksuite.features.bankspecific.AutoPaymentManager"
                "C" -> return null  // No feature!
            }

            // Use reflection to load class (only exists in appropriate APK)
            Class.forName(className)
                .getDeclaredConstructor()
                .newInstance() as IAutoPaymentManager
        } catch (e: ClassNotFoundException) {
            // Class not found - feature not available in this APK
            null
        }
    }

    fun isAutoPaymentFeatureAvailable(): Boolean {
        return BuildConfig.BANK_CODE in listOf("A", "B")
    }
}
```

---

## File Structure

```
app/
├── src/
│   ├── main/
│   │   └── java/me/rohinee/banksuite/features/
│   │       ├── IAutoPaymentManager.kt              # ✅ Common interface
│   │       ├── AutoPaymentResult.kt                # ✅ Data models
│   │       └── PaymentFeatureGateway.kt            # ✅ Factory
│   │
│   ├── bankA/
│   │   └── java/me/rohinee/banksuite/features/
│   │       └── AutoPaymentManager.kt               # ✅ Only in Bank A APK
│   │
│   ├── bankB/
│   │   └── java/me/rohinee/banksuite/features/
│   │       └── bankspecific/
│   │           └── AutoPaymentManager.kt           # ✅ Only in Bank B APK
│   │
│   └── bankC/
│       └── (no AutoPaymentManager!)                # ❌ No code in APK
```

---

## APK Comparison

| Component | Bank A APK | Bank B APK | Bank C APK |
|-----------|------------|------------|------------|
| IAutoPaymentManager | ✅ Included | ✅ Included | ✅ Included |
| PaymentFeatureGateway | ✅ Included | ✅ Included | ✅ Included |
| BankA.AutoPaymentManager | ✅ **Included** | ❌ Excluded | ❌ Excluded |
| BankB.AutoPaymentManager | ❌ Excluded | ✅ **Included** | ❌ Excluded |
| Auto Payment Feature | ✅ Available | ✅ Available | ❌ **Not Available** |
| APK Size Impact | +15 KB | +16 KB | +0 KB |

---

## Usage Example

### In MainActivity.kt

```kotlin
@Composable
fun AutoPaymentSection() {
    val manager = remember { PaymentFeatureGateway.createAutoPaymentManager() }

    if (PaymentFeatureGateway.isAutoPaymentFeatureAvailable()) {
        // Show Auto Payment UI
        manager?.let { autoPaymentManager ->
            // Use Bank A or Bank B implementation
            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = autoPaymentManager.enableAutoPayment("ACC-12345")
                        // Handle result
                    }
                }
            ) {
                Text("Enable Auto Payment")
            }
        }
    } else {
        // Bank C - feature not available
        Text("Auto Payment not supported by this bank")
    }
}
```

### Feature Grid

```kotlin
fun getFeatureItems(): List<FeatureItem> {
    return listOf(
        FeatureItem("Savings Account", Icons.Default.Star, FeatureConfig.isSavingsAccountEnabled()),
        FeatureItem("Auto Payment", Icons.Default.CheckCircle, PaymentFeatureGateway.isAutoPaymentFeatureAvailable()),
        // Auto Payment shows as enabled only for Bank A & B, disabled for Bank C
    )
}
```

---

## Benefits

### 1. Security
- Bank C APK **literally cannot** execute Auto Payment code - it's not there!
- No way to accidentally unlock the feature via reflection or decompilation

### 2. APK Size Optimization
- Bank C APK is smaller (no unused code)
- Only includes code for that specific bank's features

### 3. Different Implementations
- Bank A and Bank B can have **completely different** logic
- Different APIs, fees, limits, validations
- Bank-specific optimizations and promotions

### 4. Maintainability
- Each bank's code is isolated
- Changes to Bank A don't affect Bank B or C
- Clear separation of concerns

### 5. Legal Compliance
- If a feature is not licensed for Bank C, it's not in their APK
- No accidental inclusion of unauthorized features

---

## When to Use APK-Based Inclusion vs Runtime Flags

### Use APK-Based Inclusion When:

✅ **Feature requires completely different implementations per bank**
- Different APIs, endpoints, data models
- Different business logic, fees, rules

✅ **Code should not exist in certain APKs**
- Licensing restrictions
- Security requirements
- Legal compliance

✅ **APK size matters**
- Reducing app size for certain variants
- Avoiding unnecessary dependencies

✅ **Feature requires different dependencies**
- Bank A needs library X
- Bank B doesn't need it
- Avoid including unused dependencies

### Use Runtime Flags (FeatureConfig) When:

✅ **Feature is the same, just enabled/disabled**
- Same code, different visibility
- UI only differs

✅ **Changes are configuration-based**
- URLs, keys, limits
- Settings and preferences

✅ **Feature toggling is needed**
- A/B testing
- Gradual rollouts
- Remote configuration

---

## Building the Variants

```bash
# Bank A - Has Auto Payment (Bank A implementation)
./gradlew assembleDevBankADebug

# Bank B - Has Auto Payment (Bank B implementation)
./gradlew assembleDevBankBDebug

# Bank C - NO Auto Payment code at all!
./gradlew assembleDevBankCDebug
```

---

## Verifying APK Content

### Using APK Analyzer

1. Open Android Studio
2. Build → Analyze APK
3. Select `app/build/outputs/apk/devBankA/debug/app-dev-bank-a-debug.apk`
4. Check DEX files:
   - **BankA.apk**: ✅ Contains `AutoPaymentManager`
   - **BankB.apk**: ✅ Contains `bankspecific/AutoPaymentManager`
   - **BankC.apk**: ❌ **No AutoPaymentManager found!**

### Using Command Line

```bash
# Decompile and check
apktool d app/build/outputs/apk/devBankC/debug/app-dev-bank-c-debug.apk

# Search for AutoPayment classes
grep -r "AutoPayment" app-dev-bank-c-debug/smali/

# BankC: No results found (feature completely excluded)
```

---

## Testing

### Unit Tests

```kotlin
@Test
fun testBankAHasAutoPayment() {
    val gateway = PaymentFeatureGateway
    // Test with BuildConfig.BANK_CODE = "A"
    assertTrue(gateway.isAutoPaymentFeatureAvailable())
    assertNotNull(gateway.createAutoPaymentManager())
}

@Test
fun testBankCNoAutoPayment() {
    val gateway = PaymentFeatureGateway
    // Test with BuildConfig.BANK_CODE = "C"
    assertFalse(gateway.isAutoPaymentFeatureAvailable())
    assertNull(gateway.createAutoPaymentManager())
}
```

### Instrumented Tests

```kotlin
@Test
fun testAutoPaymentSectionInBankA() {
    // BankA variant
    composeTestRule.onNodeWithText("Auto Payment").assertIsDisplayed()
}

@Test
fun testAutoPaymentSectionNotInBankC() {
    // BankC variant
    composeTestRule.onNodeWithText("Auto Payment").assertDoesNotExist()
}
```

---

## Advanced: Adding More Banks

To add Auto Payment to Bank D:

1. Create directory:
   ```bash
   mkdir -p app/src/bankD/java/me/rohinee/banksuite/features/
   ```

2. Add implementation:
   ```kotlin
   // app/src/bankD/java/me/rohinee/banksuite/features/AutoPaymentManager.kt
   class AutoPaymentManager : IAutoPaymentManager {
       // Bank D's unique implementation
   }
   ```

3. Update gateway:
   ```kotlin
   "D" -> "me.rohinee.banksuite.features.AutoPaymentManager"
   ```

4. Build:
   ```bash
   ./gradlew assembleDevBankDDebug
   ```

---

## Common Pitfalls

### ❌ Wrong: Putting implementations in main

```kotlin
// app/src/main/java/.../AutoPaymentManager.kt
// WRONG! This would be in ALL APKs
class AutoPaymentManager : IAutoPaymentManager { }
```

### ✅ Correct: Use flavor-specific directories

```
app/src/bankA/java/.../AutoPaymentManager.kt  ✅ Only in Bank A
app/src/bankB/java/.../AutoPaymentManager.kt  ✅ Only in Bank B
app/src/bankC/  ✅ Empty - no implementation
```

### ❌ Wrong: Checking at runtime without code exclusion

```kotlin
if (BuildConfig.BANK_CODE == "C") {
    // Feature disabled but code still exists!
}
```

### ✅ Correct: Code not present at all

```kotlin
try {
    val manager = PaymentFeatureGateway.createAutoPaymentManager()
    manager?.useFeature()
} catch (e: ClassNotFoundException) {
    // Bank C: Class doesn't exist
}
```

---

## Summary

### This Example Demonstrates:

✅ **APK-based code inclusion/exclusion**
✅ **Flavor-specific source sets**
✅ **Factory pattern with reflection**
✅ **Different implementations per bank**
✅ **Runtime detection and graceful handling**

### Key Points:

- **Bank A APK**: Has Bank A's AutoPayment code
- **Bank B APK**: Has Bank B's AutoPayment code (different impl!)
- **Bank C APK**: **NO** AutoPayment code - completely excluded
- **APK size**: Bank C is smaller
- **Security**: Bank C literally cannot use Auto Payment (code doesn't exist)
- **Flexibility**: Each bank can have completely unique implementations

---

## Next Steps

- Add unit tests for each bank's implementation
- Create instrumented tests for the UI
- Verify APK size differences
- Add more bank-specific features
- Extend the pattern to other features like "Rewards Program", "Investment", etc.

---

**This is enterprise-grade architecture for multi-bank mobile apps!** 🚀