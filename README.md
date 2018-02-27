# trolley-tracker-android-client

## Summary
This is the Android Client Application for the Trolley Tracker which tracks the downtown Greenville, SC Trolley. This is a project of Code for Greenville, the Greenville South Carolina chapter of Code for America.

## Contributing
* Pull down the repository and build with Android Studio using Gradle.
* This application leverages the Google Maps API, which offers a free tier, thus, get a [Google Maps API key here](https://developers.google.com/maps/documentation/android-api/start)
* Create a .json file which contains the Google Maps API Key that you got above to and save to `app/src/main/res/values/google_maps_api.xml`
```
<resources>
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">
        {GOOGLE-MAPS-API-KEY}
    </string>
</resources>
```
* You should now be able to run the build as expected. 
(NOTE: By default in the development environment we run the app in DEBUG mode, in the Constants file we define the DEBUG mode URL, which is pointing to our Development API which is loading test data, not a live Trolley)
