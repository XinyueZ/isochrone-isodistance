Isochrone and Isodistance(Kotlin, JVM, Android, Backend)
========================================================

[![Build Status](https://travis-ci.com/XinyueZ/isochrone-isodistance.svg?token=pe7v7WLzsrqGjiNTXRMy&branch=master)](https://travis-ci.com/XinyueZ/isochrone-isodistance)

> Isochrone is a polygon representing how far you would go from a single point in every direction, following each road in a given timeframe. It's the best way to find out where you should live if you want to be «at most 5 minutes away from a tube station». Isodistance is similar, but ignores duration taken for traveling. For example by car you would go faster on highways, but with isodistance highways and sidewalks are the same.

# Glossary

isochrone = approximately equal time (minute) to target.

isodistance = approximately equal distance (meter) to target.

# Support

- JVM code, for Kotlin ,Java etc.
- Android is first support.
- Server-side is supported, however, the gradle should be adjusted for special development environment at backend-developer.

# Feature

- **Input:** lat-lng, travel mode, time in minutes. **Output:** list of locations based on approximately equal time.
- **Input:** address in text, travel model, distance in meters. **Output:** list of locations based on approximately equal distance.

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


👍 *isodistance*, approximately equal distance to target.

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

### Price for map associated APIs

- [Price policy](https://developers.google.com/maps/billing/understanding-cost-of-use)

- [Credits 200 Dollar / Month](https://cloud.google.com/maps-platform/pricing/sheet/?__utmx=-&__utmz=102347093.1534326730.1.1.utmcsr%3D%28direct%29%7Cutmccn%3D%28direct%29%7Cutmcmd%3D%28none%29&_ga=2.34177445.52834081.1534326545-1060443736.1534254902&__utmv=-&__utmk=196880428&__utma=102347093.1060443736.1534254902.1534326730.1534326730.1&__utmb=102347093.0.10.1534326730&__utmc=102347093)

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

  `curl -sSLO https://github.com/shyiko/ktlint/releases/download/0.27.0/ktlint && chmod a+x ktlint`

- Inside the project root directory run:

  `ktlint --apply-to-idea-project --android`

- Remove ktlint if desired:

  `rm ktlint`

- Start Android Studio

# Show(different result according to traffic status)

![Sample isochrone](./medium/sample.gif)

# License

```
Copyright 2018 Chris Xinyue Zhao

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements. See the NOTICE file distributed with this work for
additional information regarding copyright ownership. The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
