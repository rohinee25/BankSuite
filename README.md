# BankSuite - Multi-Environment, Multi-Bank Android App

A comprehensive banking application demonstrating enterprise-grade feature control using Android Build Variants with Flavor Dimensions.

## 🏗️ Architecture Overview

This app uses **Flavor Dimensions** to manage:
- **Environment**: dev, qa, staging, preprod, prod
- **Bank Variants**: Bank A, Bank B, Bank C

## 🎯 Feature Matrix

| Feature          | Bank A | Bank B | Bank C |
|------------------|--------|--------|--------|
| Savings Account  | ✅     | ✅     | ❌     |
| Current Account  | ❌     | ✅     | ✅     |
| Loans            | ✅     | ❌     | ❌     |
| Credit Card      | ❌     | ❌     | ✅     |
| UPI              | ✅     | ✅     | ✅     |

## 🔧 Build Variants (Available Combinations)

This architecture generates **15 variants** (5 environments × 3 banks):

```
devBankADebug, devBankADebug, devBRelease, ...
qaBankADebug, qaBankBRelease, ...
stagingBankBDebug, stagingBankCRelease, ...
preprodBankADebug, preprodBankBRelease, ...
prodBankADebug, prodBankCRelease, ...
```

### Example Full Variants:
- `devBankADebug`
- `qaBankBDebug`
- `stagingBankCRelease`
- `preprodBankADebug`
- `prodBankCRelease`

## 🛠️ How to Build

### Using Android Studio
1. **Select Build Variant**: View → Tool Windows → Build Variants
2. Choose from available variants (e.g., `devBankADebug`, `prodBankCRelease`)
3. Click **Run** or **Build → Make Project**

### Using Command Line

```bash
# Build specific variant
./gradlew assembleDevBankADebug
./gradlew assembleProdBankCRelease

# Install on connected device
./gradlew installDevBankADebug
./gradlew installProdBankCRelease

# Build all variants (for CI/CD)
./gradlew assembleDebug
./gradlew assembleRelease
```

## 📁 Project Structure

```
app/
├── src/
│   ├── main/                    # Common source code
│   │   ├── java/me/rohinee/banksuite/
│   │   │   ├── config/
│   │   │   │   ├── FeatureConfig.kt      # Runtime feature flags
│   │   │   │   ├── AnalyticsConfig.kt    # Analytics configuration
│   │   │   │   └── AppLogger.kt          # Logging utility
│   │   │   ├── MainActivity.kt           # Main UI
│   │   │   └── BankSuiteApplication.kt   # Application class
│   │   └── res/
│   ├── dev/                      # Environment-specific overrides
│   ├── qa/
│   ├── staging/
│   ├── preprod/
│   ├── prod/
│   ├── bankA/                    # Bank-specific code (if needed)
│   ├── bankB/
│   └── bankC/
└── build.gradle.kts              # App-level build configuration
```

## 🔑 Key Components

### 1. FeatureConfig.kt - Runtime Feature Control

```kotlin
// Check if a feature is enabled
if (FeatureConfig.isSavingsAccountEnabled()) {
    showSavingsAccountUI()
}

// Get current bank info
val bankName = FeatureConfig.getBankName()
val environment = FeatureConfig.getEnvironment()

// Get all enabled features
val features = FeatureConfig.getEnabledFeatures()
```

### 2. Build Configuration

#### Environment Flavors
| Flavor | App ID Suffix | Base URL                   | Analytics |
|--------|--------------|----------------------------|-----------|
| dev    | `.dev`       | `https://dev.api.bank.com`  | ❌      |
| qa     | `.qa`        | `https://qa.api.bank.com`   | ❌      |
| staging| `.stg`       | `https://stg.api.bank.com`  | ✅      |
| preprod| `.pp`        | `https://pp.api.bank.com`   | ✅      |
| prod   | (none)       | `https://api.bank.com`      | ✅      |

#### Bank Flavors
| Flavor | App Name    | Bank Code |
|--------|------------|-----------|
| bankA  | Bank A     | A         |
| bankB  | Bank B     | B         |
| bankC  | Bank C     | C         |

### 3. Logging & Analytics

```kotlin
// Logging - automatically disabled in prod/release builds
AppLogger.d("MainActivity", "Debug message")
AppLogger.i("MainActivity", "Info message")
AppLogger.e("MainActivity", "Error message", exception)

// Analytics - only enabled for staging/preprod/prod
AnalyticsConfig.initialize(context)
AnalyticsConfig.trackEvent("button_clicked", mapOf("screen" to "dashboard"))
AnalyticsConfig.trackScreen("Dashboard")
```

## 🔐 Banking-Grade Features

### Logging Control
- **Debug Builds**: Logging enabled in all environments
- **Release Builds**: Logging disabled for security

### Analytics Configuration
| Environment | Analytics | Notes           |
|-------------|-----------|-----------------|
| dev         | ❌       | Development only |
| qa          | ❌       | Automation only |
| staging     | ✅       | UAT phase       |
| preprod     | ✅       | Prod-like test  |
| prod        | ✅       | Production tracking|

## 🧪 Testing Strategy

| Environment | Purpose                        | Users              |
|-------------|--------------------------------|---------------------|
| dev         | Daily developer testing        | Developers         |
| qa          | Automated testing              | QA Automation      |
| staging     | User Acceptance Testing (UAT)  | QA & Stakeholders  |
| preprod     | Final pre-production checks    | QA & DevOps        |
| prod        | App Store / Production         | End Users          |

## 🎨 Customization

### Adding a New Bank

1. Add bank flavor in `app/build.gradle.kts`:

```kotlin
create("bankD") {
    dimension = "bank"
    resValue("string", "app_name", "Bank D")
    buildConfigField("String", "BANK_CODE", "\"D\"")
}
```

2. Update `FeatureConfig.kt` with bank-specific features:

```kotlin
fun isNewFeatureEnabled(): Boolean =
    BuildConfig.BANK_CODE == "D"
```

3. Optionally create `app/src/bankD/` for bank-specific code

### Adding a New Environment

1. Add environment flavor in `app/build.gradle.kts`:

```kotlin
create("uat") {
    dimension = "env"
    applicationIdSuffix = ".uat"
    buildConfigField("String", "BASE_URL", "\"https://uat.api.bank.com\"")
    buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
}
```

2. Create `app/src/uat/res/values/` for environment-specific resources

## 📱 Sample Screens

The app displays:
- **Environment Info Card**: Shows current environment and bank
- **Feature Grid**: Dynamically shows enabled services based on bank
- **App Information**: Build config details for debugging

## 🚀 CI/CD Considerations

```yaml
# Example: Build only specific variants in CI
dev-builds:
  - devBankADebug
  - devBankBDebug  
  - devBankCDebug

prod-builds:
  - prodBankARelease
  - prodBankBRelease
  - prodBankCRelease
```

## 💡 Best Practices

1. **Use runtime feature flags** for UI changes (FeatureConfig)
2. **Use flavor source sets** only for completely different implementations
3. **Never commit secrets** - use environment variables or secure storage
4. **Disable logs in prod** - handled automatically via build configuration
5. **Test all variants** - ensure each bank variant works correctly

## 🔧 Troubleshooting

### Build Variant Not Showing
- Sync Gradle: `./gradlew clean build`
- Check Build Variant dropdown in Android Studio

### BuildConfig Fields Not Generated
- Add `buildConfig = true` in `buildFeatures` block
- Clean and rebuild project

### Flavor Resources Not Merging
- Verify resource file names match across flavors
- Check for naming conflicts

## 📄 License

This is a sample project demonstrating Android build variants and flavor dimensions.

---

**Interview-Ready Explanation:**

"We use two flavor dimensions: environment and bank. Environment controls backend URLs, logging, and analytics. Bank flavors control branding and feature availability. Runtime feature flags decide UI visibility, while flavor source sets handle bank-specific logic."