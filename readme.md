Required Gradle dependencies
============================

### Manifest File

<uses-permission android:name="android.permission.INTERNET" />
<uses-feature
        android:name="android.hardware.touchscreen"
        android:required="false" />
    <uses-feature
        android:name="android.software.leanback"
        android:required="true" />
    <uses-feature
        android:name="android.hardware.camera"
        android:required="true" />


Add inside dependencies in build.gradle

### Snackbar

```implementation 'com.android.support:design:27.1.1'```


### Current location

```implementation 'com.google.android.gms:play-services-maps:17.0.0'```
```implementation 'com.google.android.gms:play-services-location:17.0.0'```