Isochrone and Isodistance with Google Maps
====


[![Build Status](https://travis-ci.org/XinyueZ/FusedLocationWithMVP.svg?branch=feature%2isochrone-isodistance)](https://travis-ci.org/XinyueZ/FusedLocationWithMVP)

> Isochrone is a polygon representing how far you would go from a single point in every direction, following each road in a given timeframe. It's the best way to find out where you should live if you want to be «at most 5 minutes away from a tube station». Isodistance is similar, but ignores duration taken for traveling. For example by car you would go faster on highways, but with isodistance highways and sidewalks are the same.

### Glossary

isochrone = same duration from one start point
isodistance = same distance from one start point

### Reference

https://isochrone.dugwood.com/index.html 

https://github.com/dugwood/isochrone-isodistance-with-google-maps

http://drewfustin.com/isochrones/

# Connect with play-service and show map


> Use Map to show current location with Play-service after v11.0.1

Great progress is that we don't need Google GoogleApiClient explict to initilize the location-service.

Info:
1. https://android-developers.googleblog.com/2017/06/reduce-friction-with-new-location-apis.html
2. https://developer.android.com/training/location/receive-location-updates.html
3. https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API

# Don't forget map-key, this repo doesn't contain: google_maps_key(string res)
# Don't forget gcp-key for Map-API: gcp_key(string res)

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
