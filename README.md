# trolley-tracker-android-client - [Archived in June 2023]

<p>From 2014-2021, Code For Greenville members built and maintained the technology which allowed thousands of locals and visitors to track the downtown Greenville trolleys in real-time from their mobile devices.<p>

<p>As of June 30, 2023, <a href="https://codeforamerica.org/news/reflections-on-the-brigade-networks-next-chapter/">Code For America officially withdrew fiscal sponsorship and use of the "Code For" trademark to <strong>all</strong> national brigades</a>, including Code For Greenville.</p>

<p>After July 1st, 2023, contributors can get involved with two re-branded efforts:</p>

<ul>
	<li>For ongoing civic projects, connect with <a href="https://opencollective.com/code-for-the-carolinas">Code For The Carolina</a> (which itself will rebrand by the end of 2023)</li>
	<li>For local tech APIs and OpenData projects, see the <a href="https://github.com/hackgvl">HackGreenville Labs repositories on GitHub</a> and connect with the team in the <em>#hg-labs</em> channel on the <a href="https://hackgreenville.com/join-slack">HackGreenville Slack</a></li>
</ul>

## Summary
Android Client Application for the Trolley Tracker, which tracks the downtown Greenville, SC Trolley. This is a project of Code for Greenville, the Greenville South Carolina chapter of Code for America.

## Contributing
* Pull down the repository and build with Android Studio using Gradle.
* This application leverages the Google Maps API, which offers a free tier, thus, get a [Google Maps API key here](https://developers.google.com/maps/documentation/android-api/start)
* Create an XML file which contains the Google Maps API Key that you got above to and save to `app/src/main/res/values/google_maps_api.xml`
```
<resources>
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">
        {GOOGLE-MAPS-API-KEY}
    </string>
</resources>
```
* You should now be able to run the build as expected. 
(NOTE: By default in the development environment we run the app in DEBUG mode. The API is defined in the `Constants` file. Here we define the DEBUG mode URL, which is pointing to our Development API. This is loading test data, not a live Trolley)
