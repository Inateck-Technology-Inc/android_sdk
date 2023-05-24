# BARCODE SCANNER SDK FOR ANDROID
The SDK provides developers with a set of tools to set the scanner for android platform. Support Android API level 31 and above.

## Configuration
To use a library in your project, there are generally the following steps:
1. Add the "app/../jniLibs/*","libs/droidplug-debug.aar" library to your project;
2. "settings.gradle" file adds "flatDir";
3. "build.grade" adds "implementation(name: 'droidplug-debug', ext: 'aar')";
4. "proguard-rules.pro" adds "#btleplug resources -keep class com.nonpolynomial.** { *; }  -keep class io.github.gedgygedgy.** { *; }"
5. Import the "/src/main/java/com/inateck/RustJNI.kt" file in your project;

## Documentation
For more information about the SDK and APIs, go to [http://docs.inateckoffice.com/web/#/8/24](http://docs.inateckoffice.com/web/#/8/24) .

