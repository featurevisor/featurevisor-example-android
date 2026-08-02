# featurevisor-example-android

A small Android application written entirely in Kotlin, showing how to use the [Featurevisor SDK](https://github.com/featurevisor/featurevisor-java).

The application downloads a Featurevisor datafile, creates an SDK instance, and evaluates the `mobile_experience` feature as a flag, variation, and string variable.

## Requirements

Install Android Studio with Android SDK 36 and JDK 17.

The project uses the Kotlin support built into Android Gradle Plugin 9, so it does not require the older Kotlin Android Gradle plugin.

The Featurevisor SDK is published through GitHub Packages. Create a GitHub personal access token with the `read:packages` scope, then add these values to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

Keep these credentials outside the repository. For GitHub Actions, grant the example repository access to the Featurevisor SDK package.

## Run the application

1. Open the repository in Android Studio.
2. Allow Gradle to sync and resolve the Featurevisor dependency.
3. Select an Android emulator or connected device.
4. Run the `app` configuration.

## Featurevisor usage

The application fetches this production datafile:

```text
https://featurevisor-example-cloudflare.pages.dev/production/featurevisor-mobile.json
```

The important SDK usage is in `MainActivity.kt`:

```kotlin
val datafile = DatafileContent.fromJson(datafileJson)
val f = Featurevisor.createFeaturevisor(
    Featurevisor.FeaturevisorOptions().datafile(datafile),
)

val context = mapOf<String, Any>(
    "userId" to "mobile-user",
    "country" to "nl",
)

val enabled = f.isEnabled("mobile_experience", context)
val variation = f.getVariation("mobile_experience", context)
val message = f.getVariableString(
    "mobile_experience",
    "welcome_message",
    context,
)
```

## Checks

Run the unit tests, Android lint, and a debug build with:

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Learn more in the [Featurevisor SDK documentation](https://featurevisor.com/docs/sdks/java/).
