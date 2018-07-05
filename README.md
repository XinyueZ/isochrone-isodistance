# Connect with play-service and show map

> Use Map to show current location with Play-service after v11.0.1

Great progress is that we don't need Google GoogleApiClient explict to initilize the location-service.

Info:
1. https://android-developers.googleblog.com/2017/06/reduce-friction-with-new-location-apis.html
2. https://developer.android.com/training/location/receive-location-updates.html
3. https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API

# Don't forget map-key, this repo doesn't contain.

# Android Studio IDE setup 

For development, the latest version of Android Studio 3.2 is required. The latest version can be
downloaded from [here](https://developer.android.com/studio/preview/).

Sunflower uses [ktlint](https://ktlint.github.io/) to enforce Kotlin coding styles.
Here's how to configure it for use with Android Studio (instructions adapted
from the ktlint [README](https://github.com/shyiko/ktlint/blob/master/README.md)):

- Close Android Studio if it's open

- Download ktlint:

  `curl -sSLO https://github.com/shyiko/ktlint/releases/download/0.24.0/ktlint && chmod a+x ktlint`

- Inside the project root directory run:

  `ktlint --apply-to-idea-project --android`

- Remove ktlint if desired:

  `rm ktlint`

- Start Android Studio
