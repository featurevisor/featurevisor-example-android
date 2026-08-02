# featurevisor-example-android

A small Android application showing how to use the [Featurevisor Java SDK](https://github.com/featurevisor/featurevisor-java) from Java.

The application downloads a Featurevisor datafile, creates an SDK instance, and evaluates the `mobile_experience` feature as a flag, variation, and string variable.

## Requirements

Install Android Studio with Android SDK 37 and JDK 17.

The Featurevisor Java SDK is published through GitHub Packages. Create a GitHub personal access token with the `read:packages` scope, then add these values to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

Keep these credentials outside the repository. For GitHub Actions, grant the example repository access to the `featurevisor-java` package.

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

The important SDK usage is in `MainActivity.java`:

```java
DatafileContent datafile = DatafileContent.fromJson(datafileJson);
Featurevisor f = Featurevisor.createFeaturevisor(
    new Featurevisor.FeaturevisorOptions().datafile(datafile)
);

Map<String, Object> context = new HashMap<>();
context.put("userId", "mobile-user");
context.put("country", "nl");

boolean enabled = f.isEnabled("mobile_experience", context);
String variation = f.getVariation("mobile_experience", context);
String message = f.getVariableString(
    "mobile_experience",
    "welcome_message",
    context
);
```

## Checks

Run the unit tests, Android lint, and a debug build with:

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Learn more in the [Featurevisor Java SDK documentation](https://featurevisor.com/docs/sdks/java/).
