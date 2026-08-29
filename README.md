# featurevisor-example-android

A small Android application written entirely in Kotlin, showing how to use the [Featurevisor Java SDK](https://github.com/featurevisor/featurevisor-java).

Learn more about Featurevisor [here](https://featurevisor.com).

The application evaluates the same flag, variation, feature variables, and global variables as the other Featurevisor SDK examples.

## Requirements

Install Android Studio with Android SDK 36 and JDK 17.

The Featurevisor SDK is published through GitHub Packages. Add GitHub credentials with the `read:packages` scope to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

Keep these credentials outside the repository.

## Run the application

1. Open the repository in Android Studio.
2. Allow Gradle to resolve the Featurevisor dependency.
3. Select an Android emulator or connected device.
4. Run the `app` configuration.

The application uses this production datafile:

```text
https://featurevisor-example-cloudflare.pages.dev/production/featurevisor-sdk-v3.json
```

The direct SDK integration lives in `MainActivity.kt`. Change its context values to see how Featurevisor selects different rules, variations, and global variable overrides.

## Checks

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Learn more in the [Featurevisor Java SDK documentation](https://featurevisor.com/docs/sdks/java/).
