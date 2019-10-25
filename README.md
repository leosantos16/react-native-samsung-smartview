# react-native-samsung-smartview

## Getting started

`$ npm install react-native-samsung-smartview --save`

### Mostly automatic installation

`$ react-native link react-native-samsung-smartview`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-samsung-smartview` and add `SamsungSmartview.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libSamsungSmartview.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.SamsungSmartviewPackage;` to the imports at the top of the file
  - Add `new SamsungSmartviewPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-samsung-smartview'
  	project(':react-native-samsung-smartview').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-samsung-smartview/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-samsung-smartview')
  	```


## Usage
```javascript
import SamsungSmartview from 'react-native-samsung-smartview';

// TODO: What to do with the module?
SamsungSmartview;
```
