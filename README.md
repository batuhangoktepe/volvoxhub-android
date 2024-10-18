# VolvoxHub Android SDK

The `volvoxhub-android` SDK simplifies common integrations needed in mobile projects by handling services like RevenueCat, AppsFlyer, OneSignal, and Amplitude automatically in the background. It also allows remote control of localization through JSON and provides a customizable popup for banned users, so you can focus on building your app while the SDK takes care of the heavy lifting.

## Features
- **RevenueCat Integration**: Manage consumable and subscription products, start purchases via RevenueCat.
- **AppsFlyer Integration**: Sync AppsFlyerUID with the hub service.
- **OneSignal Integration**: Update push tokens through the hub service.
- **Amplitude Integration**: Log events with dynamically fetched keys.
- **Localization**: Parse and manage localization files remotely.
- **Customizable Popup**: Jetpack Compose-based popup for banned users, easily customizable.

## Installation
To integrate the SDK into your project, add the following to your `build.gradle.kts`:

```kotlin
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.batuhangoktepe:volvoxhub-android:0.0.1'
}
```

# Setup

Initialize the SDK in your Application class:

```kotlin
val volvoxHubConfiguration = Configuration(
    context = this, 
    appName = "YOUR_PROJECT_NAME", 
    appId = "YOUR_APP_ID", 
    packageName = packageName
)
VolvoxHub.initialize(volvoxHubConfiguration)
```

Start authorization with the following code:

```kotlin
VolvoxHub.getInstance().startAuthorization(object : VolvoxHubInitListener {
    override fun onInitCompleted(volvoxHubResponse: VolvoxHubResponse) {
        // Handle success
    }

    override fun onInitFailed(error: Int) {
        // Handle failure
    }
})
```

# License

This project is licensed under the MIT License.

# Contact

For inquiries about joining the hub system or for more information, please contact batuhan@volvoxmobile.com.

