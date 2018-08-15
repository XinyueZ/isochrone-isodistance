Isochrone and Isodistance(Kotlin, Android)
====

> Isochrone is a polygon representing how far you would go from a single point in every direction, following each road in a given timeframe. It's the best way to find out where you should live if you want to be «at most 5 minutes away from a tube station». Isodistance is similar, but ignores duration taken for traveling. For example by car you would go faster on highways, but with isodistance highways and sidewalks are the same.

# Glossary

isochrone = approximately equal time (minute) to target.

isodistance = approximately equal distance (meter) to target.

# Status of project

👷 Run `./gradlew check`  or  `./gradlew check connectedCheck` to validate whole project including sample app and library firstly.

👍 *isochrone*, approximately equal time to target.

``` kotlin
getIsochrone(
             /*API-KEY for matrix and geocode APIs*/, 
             "walking", // Travel mode 
             "Address or lat-lng", //Target location, for "current location" is a good use-case. 
             120).consumeEach { // 120 minutes to target.
                        Log.d(TAG, "rad1: ${it.pretty()}")
                        // You can draw all points based on an Array<LatLng> .
                    }
```


👍 isodistance, approximately equal distance to target.

``` kotlin
getIsodistance(
             /*API-KEY for matrix and geocode APIs*/,
             "walking", // Travel mode
             "Address or lat-lng", //Target location, for "current location" is a good use-case.
             120).consumeEach { // 120 meters to target.
                        Log.d(TAG, "rad1: ${it.pretty()}")
                        // You can draw all points based on an Array<LatLng> .
                    }
```


More info: FindLocationPresenter.kt

# Setup

In order to use isochrone & isodistance the [Google's Distance Matrix API](https://developers.google.com/maps/documentation/distance-matrix/start) must be turned on. If the result locations must be sorted according to their bearings the [Google's Geocode API](https://developers.google.com/maps/documentation/geocoding/start) should also be used, please use same API-Key for both APIs.

### 🛑 Don't forget in project

- This repo doesn't contain: google_maps_key(string res)

- The gcp-key for matrix and geocode APIs: gcp_key(string res)


# Coroutines

Don't forget in client application:

```
kotlin {
    experimental {
        coroutines "enable"
    }
}
```

# Code robust & healthy with klint support
 
The project uses [ktlint](https://ktlint.github.io/) to enforce Kotlin coding styles.
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

# Show

![Sample isochrone](./medium/sample.gif)

# License

to be continued ...
