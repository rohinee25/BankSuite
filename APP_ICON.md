# BankSuite App Icon

## Overview
The BankSuite app icon features a professional banking design with:

### Visual Elements

1. **Bank Building**: A classical building silhouette representing a financial institution
   - Three neoclassical columns (symbolizing stability and trust)
   - Triangular roof (symbolizing growth and protection)
   - Centered entrance door (symbolizing accessibility)

2. **Color Scheme**:
   - **Primary Blue (#1A237E)**: Trust, reliability, and professionalism
   - **Gold/Yellow (#FFD54F)**: Wealth, prosperity, and premium service
   - **White**: Cleanliness, clarity, and transparency
   - **Green Badge (#4CAF50)**: Security and protection

3. **Security Shield**: A green shield with checkmark in the top-right corner
   - Represents secure transactions
   - Indicates trust and safety
   - Green color symbolizes "go/safe" status

## Icon Files Structure

```
app/src/main/res/
├── drawable/
│   ├── ic_launcher_background.xml  # Gradient blue background
│   ├── ic_launcher_foreground.xml # Bank building + security badge
│   ├── ic_notification.xml        # Simplified version for notifications
│   └── ic_splash.xml             # Splash screen logo
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml           # Adaptive icon (square)
│   └── ic_launcher_round.xml     # Adaptive icon (round)
└── mipmap-{hdpi, mdpi, xhdpi, xxhdpi, xxxhdpi}/
    ├── ic_launcher.webp          # Generated from adaptive icon
    └── ic_launcher_round.webp    # Generated from adaptive icon
```

## Adaptive Icon Features

The app uses **Android Adaptive Icons**, which provide:

- **Flexibility**: Different shapes on different devices (squircle, circle, rounded square, tear-drop, etc.)
- **Layers**: Background and foreground layers are separated
- **Visual Consistency**: Maintains identity across various Android devices
- **Animation**: Supports home screen icon animations on supported launchers

## Generating Different Variants

Android automatically generates all mipmap assets from the adaptive icon definition. The system creates:
- Standard launcher icons
- Round launcher icons
- Notification icons
- Home screen icons in various shapes

## Color Palette

| Usage | Color | Hex Code |
|-------|-------|----------|
| Primary | Deep Blue | `#1A237E` |
| Lighter Blue | Medium Blue | `#3950C3` |
| Accent | Gold | `#FFD54F` |
| Security | Green | `#4CAF50` |
| Success | Green | `#4CAF50` |
| Error | Red | `#F44336` |
| Warning | Orange | `#FF9800` |
| Info | Blue | `#2196F3` |

## Modifying the Icon

### To Change Colors

Edit `ic_launcher_background.xml` for background colors:
```xml
<path
    android:fillColor="#YOUR_COLOR"
    android:pathData="..." />
```

Edit `ic_launcher_foreground.xml` for icon element colors.

### To Change the Design

The icon is built using vector graphics (SVG paths). Modify the paths in:
- `ic_launcher_foreground.xml` for the bank building and badge
- `ic_launcher_background.xml` for the background gradient

### To Add Brand-Specific Icons

For bank-specific icons, create flavor-specific drawable files:

```bash
app/src/bankA/res/drawable-xxhdpi/
    ic_launcher_foreground.xml
```

## Guidelines

- ✅ Keep the design simple and recognizable at small sizes
- ✅ Maintain contrast for accessibility
- ✅ Follow Material Design icon guidelines
- ✅ Test on different backgrounds
- ✅ Ensure visibility in light and dark themes
- ❌ Don't use too many colors
- ❌ Don't add text to the icon
- ❌ Don't use complex gradients

## Splash Screen

The splash screen uses `ic_splash.xml` which features a larger version of the bank building centered on the primary blue background.

To use the splash screen theme in AndroidManifest.xml:

```xml
<activity
    android:name=".SplashActivity"
    android:theme="@style/Theme.BankSuite.Splash"
    ...>
```

## Notification Icon

The notification icon (`ic_notification.xml`) is a simplified version of the main icon, optimized for the small notification area size.

## Testing

Test the icon on:
1. Different device dpi densities (hdpi, xhdpi, xxhdpi, xxxhdpi)
2. Different launcher shapes (pixel, samsung, oneplus, etc.)
3. Dark and light themes
4. Notification tray
5. App info screen