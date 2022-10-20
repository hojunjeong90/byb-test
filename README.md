<h1>BLE Background Scanning and Connect Research</h1>

![KakaoTalk_Photo_2022-10-20-22-44-45](https://user-images.githubusercontent.com/42525347/196965680-12cdba04-9a77-404e-b99e-242d72bab41c.jpeg)


Author
-------------------
PeterJeong

Features
--------
1. Continuously attempt to scan and connect to BLE in the background using the foreground service
2. Optimize by setting location and ble scanning duration in the background

Requirements
-------------------
- Kotlin
- Android Gradle Plugin 7.2.2
- Gradle 7.3.3
- CompileSdk 33
- minSdk 28
- targetSdk 30

Getting Started
--------
1. To change test Location Latlng, 'Constants > TestLatLng' 
2. To see how background scanning service works and change BLE Scanning periods and Location distance, go to 'service/BtScanService'
3. To see how background connecting service works, go to 'service/BtGattService'
