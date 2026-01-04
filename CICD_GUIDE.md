# BankSuite CI/CD Pipeline - Complete Guide

## Table of Contents
1. [Overview](#overview)
2. [Local Setup & Testing](#local-setup--testing)
3. [CI/CD Pipeline Architecture](#cicd-pipeline-architecture)
4. [Step-by-Step CI/CD Implementation](#step-by-step-cicd-implementation)
5. [Lint Checks](#lint-checks)
6. [Testing](#testing)
7. [Code Coverage](#code-coverage)
8. [GitHub Actions Setup](#github-actions-setup)
9. [Best Practices](#best-practices)
10. [Troubleshooting](#troubleshooting)

---

## Overview

The BankSuite CI/CD pipeline automates:
- ✅ Code Quality Checks (Linting)
- ✅ Unit Testing
- ✅ Instrumented Testing
- ✅ Multi-Variant Building (15 variants)
- ✅ Code Coverage Reporting
- ✅ Automated APK Generation

### Pipeline Flow

```
┌─────────────┐
│   Push/PR   │
└──────┬──────┘
       │
       ▼
┌─────────────┐     ┌─────────────────┐
│     lint    │────▶│ Upload Reports  │
└──────┬──────┘     └─────────────────┘
       │
       ▼
┌─────────────┐     ┌─────────────────┐
│ Unit Tests  │────▶│ Upload Reports  │
└──────┬──────┘     └─────────────────┘
       │
       ▼
┌─────────────────┐
│ Instr. Tests    │──(If needed)──▶
│ (Android Emu)   │
└─────────────────┘
       │
       ▼
┌─────────────────┐
│ Build Debug APK │
│ (3 bank variants)│
└─────────────────┘
       │ (main branch only)
       ▼
┌─────────────────┐
│ Build Release   │
│ APK (signed)    │
└─────────────────┘
```

---

## Local Setup & Testing

### Prerequisites
- JDK 17 or higher
- Android SDK
- Git

### Running Tests Locally

```bash
# Lint check
./gradlew lintDevBankADebug

# Unit tests
./gradlew testDevBankADebugUnitTest

# All unit tests (all variants)
./gradlew test --continue

# Instrumented tests (requires device/emulator)
./gradlew connectedDevBankADebugAndroidTest

# Code coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### Viewing Reports

```bash
# Lint report (HTML)
open app/build/reports/lint-results-devBankADebug.html

# Unit test report (HTML)
open app/build/reports/tests/testDevBankADebugUnitTest/index.html

# Coverage report (HTML)
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

---

## CI/CD Pipeline Architecture

### Jobs Breakdown

| Job | Purpose | When Runs | Dependencies |
|-----|---------|-----------|--------------|
| **lint** | Code quality & static analysis | Always | None |
| **unit-tests** | Unit test execution | After lint | lint |
| **instrumented-tests** | UI/Automation tests | After lint & unit tests | lint, unit-tests |
| **build** | Build debug APKs | After lint & unit tests | lint, unit-tests |
| **coverage** | Generate coverage reports | After unit tests | unit-tests |
| **build-production** | Build signed release APKs | main branch only | All tests pass |
| **notify** | Send notifications | On failure | Any job |

---

## Step-by-Step CI/CD Implementation

### Step 1: Configure Build Gradle

Your `app/build.gradle.kts` already includes:

```kotlin
// Jacoco for code coverage
jacoco {
    toolVersion = "0.8.11"
}

// Lint configuration
lint {
    abortOnError = false
    xmlReport = true
    htmlReport = true
}

// Test configuration
testOptions {
    unitTests {
        isIncludeAndroidResources = true
        isReturnDefaultValues = true
    }
}
```

### Step 2: Write Unit Tests

Create tests in `app/src/test/`:

```kotlin
// Example: app/src/test/java/me/rohinee/banksuite/config/FeatureConfigTest.kt
@RunWith(JUnit4::class)
class FeatureConfigTest {

    @Test
    fun testFeatureEnabledForBankA() {
        val bankCode = "A"
        assertTrue(bankCode == "A")
    }
}
```

### Step 3: Write Instrumented Tests

Create tests in `app/src/androidTest/`:

```kotlin
// Example: app/src/androidTest/java/MainActivityInstrumentedTest.kt
@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDashboardTitle_isDisplayed() {
        // Compose UI testing
    }
}
```

### Step 4: Configure Lint Rules

Create `app/lint.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<lint>
    <issue id="NewApi" severity="error"/>
    <issue id="UnusedResources" severity="warning"/>
    <!-- Add more rules as needed -->
</lint>
```

---

## Lint Checks

### What Lint Checks

- **Code Quality**: Unused code, hardcoded strings, deprecated APIs
- **Performance**: Overdraw, inefficient layouts
- **Accessibility**: Missing content descriptions
- **Security**: Exported components, sensitive data
- **Internationalization**: Missing translations

### Custom Lint Rules

```bash
# Run lint for all variants
./gradlew lint

# Run lint for specific variant
./gradlew lintDevBankADebug

# Lint with baselines
./gradlew lintDevBankADebug --baseline app/lint-baseline.xml
```

### Fixing Lint Issues

```bash
# Auto-fix some issues
./gradlew lintDevBankADebug --auto-fix

# Generate baseline to suppress existing issues
./gradlew lintDevBankADebug --baseline app/lint-baseline.xml
```

---

## Testing

### Unit Tests

**Purpose**: Test business logic in isolation

```kotlin
// app/src/test/java/
- FeatureConfigTest.kt (tests feature flags)
- AnalyticsConfigTest.kt (tests analytics logic)
- AppConfigTest.kt (tests configuration)
```

**Running locally:**

```bash
# Run unit tests
./gradlew testDevBankADebugUnitTest

# Run with coverage
./gradlew testDevBankADebugUnitTest jacocoTestReport

# Run all variant tests
./gradlew test --continue
```

### Instrumented Tests

**Purpose**: Test UI components and Android integrations

```kotlin
// app/src/androidTest/java/
- MainActivityInstrumentedTest.kt (tests Compose UI)
- NavigationTest.kt (tests navigation)
- ConfigInstrumentedTest.kt (tests BuildConfig)
```

**Running locally:**

```bash
# Requires emulator or connected device
./gradlew connectedDevBankADebugAndroidTest

# Run specific test class
./gradlew connectedDevBankADebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=me.rohinee.banksuite.MainActivityInstrumentedTest
```

### Test Coverage

```bash
# Generate coverage report
./gradlew testDebugUnitTest jacocoTestReport

# Coverage goals:
- Minimum 70% line coverage
- Critical paths 100% covered
- UI logic 80% covered
```

---

## Code Coverage

### JaCoCo Setup

Coverage is configured in `app/build.gradle.kts`:

```kotlin
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

### Viewing Coverage

```bash
# Generate report
./gradlew clean testDebugUnitTest jacocoTestReport

# Open HTML report
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

### Coverage Integration with Codecov

The CI/CD pipeline automatically uploads coverage to Codecov if configured:

1. Sign up at https://codecov.io
2. Add repository
3. GitHub Actions will upload automatically
4. View coverage badges in README

---

## GitHub Actions Setup

### Repository Setup

1. **Enable GitHub Actions**:
   - Go to repository Settings → Actions → General
   - Enable "Allow all actions and reusable workflows"

2. **Add Secrets** (for production builds):
   - Settings → Secrets and variables → Actions
   - Add:
     - `KEYSTORE_FILE` (base64-encoded keystore)
     - `KEYSTORE_PASSWORD`
     - `KEY_ALIAS`
     - `KEY_PASSWORD`
     - `SLACK_WEBHOOK_URL` (optional, for notifications)

### Encoding Keystore

```bash
# Convert keystore to base64
base64 -i release-keystore.jks | pbcopy  # macOS
base64 -w 0 release-keystore.jks > keystore.base64  # Linux

# Copy the output to GitHub Secrets as KEYSTORE_FILE
```

### Workflow Triggers

The pipeline runs on:
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`
- Manual trigger via Actions tab

### Stages Execution Flow

```yaml
# Workflow stages
Stage 1: Lint (runs first, blocks on failure)
Stage 2: Unit Tests (parallel with later stages)
Stage 3: Instrumented Tests (slow, runs async)
Stage 4: Build Debug APKs (matrix of 3 bank variants)
Stage 5: Coverage (generates reports)
Stage 6: Build Production (main branch only, after all tests pass)
```

---

## Best Practices

### 1. Test-First Development
```kotlin
// Write test first
@Test
fun testSavingsAccountEnabled() {
    assertTrue(FeatureConfig.isSavingsAccountEnabled())
}

// Then implement
fun isSavingsAccountEnabled() = BuildConfig.BANK_CODE == "A" || BuildConfig.BANK_CODE == "B"
```

### 2. Fast Feedback Loop
- Run tests locally before pushing
- Use Git pre-commit hooks
- Keep tests isolated and fast

### 3. Coverage Goals
- New code: Minimum 80% coverage
- Existing code: Maintain 70%
- Critical paths: 100%

### 4. Lint Zero-Tolerance
```kotlin
// On main branch, fail build on lint errors
lint {
    abortOnError = true  // In release builds
}
```

### 5. Parallel Execution
```yaml
jobs:
  lint: # Can run in parallel
  test: # Can run in parallel
  build:
    strategy:
      matrix:
        variant: [devBankA, devBankB, devBankC]  # Parallel builds
```

### 6. Artifact Management
- Keep artifacts for 7 days (debug)
- Keep artifacts for 30 days (production)
- Use build numbers/tags for versioning

---

## Troubleshooting

### Lint Issues

**Problem**: Lint fails on PR
```bash
# Run lint locally
./gradlew lintDevBankADebug

# Fix issues or suppress if needed
// Add @SuppressLint annotation
// Or add to lint.xml
```

### Test Failures

**Problem**: Unit test fails in CI but passes locally
```bash
# Check Java/Kotlin version
./gradlew --version

# Check test isolation (no dependencies on Android framework)
./gradlew testDevBankADebugUnitTest --info

# Clear Gradle cache
./gradlew clean test
```

### Instrumented Test Timeout

**Problem**: Emulator tests timeout
```yaml
# Increase timeout in workflow
- name: Run Instrumented Tests
  timeout-minutes: 30  # Increase if needed
```

### Build Fails with "Ambiguous Task"

**Problem**: Multiple variants cause ambiguous task names
```bash
# Specify full task name
./gradlew testDevBankADebugUnitTest

# Or use wildcard
./gradlew test
```

### Gradle Cache Issues

**Problem**: Cache causes old dependencies
```bash
# Clear caches
rm -rf ~/.gradle/caches

# In CI, use cache key with version
key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}-v1
```

### Out of Memory

**Problem**: Build runs out of memory
```kotlin
// In gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
```

---

## Monitoring & Notifications

### Slack Integration

Configure webhook in repository secrets:

1. Create Slack incoming webhook
2. Add `SLACK_WEBHOOK_URL` to GitHub Secrets
3. Notifications sent on pipeline failure

### Coverage Badges

Add to README.md:

```markdown
[![codecov](https://codecov.io/gh/YOUR_USERNAME/BankSuite/branch/main/graph/badge.svg)](https://codecov.io/gh/YOUR_USERNAME/BankSuite)
[![Unit Tests](https://github.com/YOUR_USERNAME/BankSuite/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/BankSuite/actions/workflows/ci.yml)
```

### Build Status

Check build status in:
- GitHub Actions tab
- Pull request checks
- Branch protection rules

---

## Summary

### What Happens When You Push Code

1. **Lint Check** (1-2 min)
   - Android Lint runs
   - Reports uploaded as artifacts
   - Build fails if critical errors

2. **Unit Tests** (1-3 min)
   - All unit tests execute
   - Coverage report generated
   - Results published to GitHub

3. **Instrumented Tests** (5-10 min, optional)
   - Android emulator spins up
   - UI tests run automatically
   - Only if device/emulator available

4. **Build Debug APKs** (2-5 min)
   - Builds 3 debug variants (Bank A, B, C)
   - APKs uploaded as artifacts
   - Available for download

5. **Coverage Report** (1 min)
   - Uploads to Codecov
   - HTML report saved as artifact

6. **Production Build** (if main branch)
   - Builds 3 release variants
   - Signs with keystore
   - Available for deployment

### Time Estimates

- **Total CI Time**: ~5-15 min (without instrumented tests)
- **Full Pipeline**: ~15-25 min (with instrumented tests)

---

## Next Steps

1. ✅ Set up GitHub repository
2. ✅ Add GitHub Actions workflow
3. ✅ Configure secrets (for production builds)
4. ✅ Write tests for new features
5. ✅ Monitor pipeline runs
6. ✅ Optimize based on failures

## Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android Testing Guide](https://developer.android.com/training/testing)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Android Lint](https://developer.android.com/studio/write/lint)

---

Generated for BankSuite CI/CD Pipeline 🚀