# FiveDayForecaster

## Overview (Getting Started)

**FiveDayForecaster** is a simple app that retrieves weather predictions for every three hours
spanning five days. To run this app you should use Android Gradle Plugin version 8.4.0, Gradle version 8.9, 
and JDK 17.

##### Dependencies

This project uses the following dependencies:
- AndroidX (core ktx, appcompat)
- AndroidX activity
- Google material
- Google gson
- Google play services location
- AndroidX (constraint layout, cardview, recyclerview)
- Squareup picasso
- AndroidX navigation (fragment ktx, ui ktx)
- AndroidX room (runtime, ktx, testing, compiler)
- Koin (core, android)
- Ktor (core, android, serialization)

Plugins:
- KSP
- Kotlin version 1.9.23
- Jetbrains parcelize
- AndroidX safeArgs

## Project Walkthrough

**FiveDayForecaster** is a three screen app that uses the user's location to retrieve a five day 
weather forecast for that location. The first screen is a location input screen that allows the 
user to either enter a zip code _or_ use the device's hardware (with permission) to retrieve a 
weather forecast from the OpenWeatherMap APIs. Once the user clicks into the zip code entry field,
a numbers-only keyboard appears for the user to use for data entry. The user can then click
the 'Submit' button to use that zip code or the 'Clear' button to clear their entry.

![Login_Screen](images/fiveday_location.png)
![Login_Screen](images/fiveday_location_entry.png)

Also on this screen is a 'Use Device Location' button, which allows the user to use the device's
hardware to determine the location. Clicking this button triggers a location permission check. If
permissions have not yet been granted, a dialog appears for the user to enter their permission.
After the user enters either a zip code or uses the device's location, a List Screen appears
which indicates the location of the weather forecast at the top, weather predictions
for every three hours in a scrollable list, and a new button which allows the user to initiate
a new search.

![Login_Screen](images/fiveday_permission.png)
![Login_Screen](images/fiveday_list.png)

Clicking on any of the items in the scrollable list navigates the user to a detail page which
contains more information about that specific time's weather prediction, including cloud cover 
percentage, wind speed, and visibility. An icon image is loaded that represents that weather
prediction.

If, for some reason, the device loses network access, the app defaults to the forecast which is 
stored in the local database (Room) and shows the user a dialog which explains that there was a 
network issue.

![Login_Screen](images/fiveday_network_error.png)
![Login_Screen](images/fiveday_detail.png)


## Rubric

##### Android UI/UX

##### Local & Network Data


##### Android System & Hardware Integration



