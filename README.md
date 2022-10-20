<h1>BLE Background Scanning and Connect Research</h1>

![KakaoTalk_Photo_2022-10-20-22-44-45](https://user-images.githubusercontent.com/42525347/196965680-12cdba04-9a77-404e-b99e-242d72bab41c.jpeg)


Author
-------------------
PeterJeong


Architecture
--------
1. Service(Component) <-> Repository <-> ViewModel <-> View(Activity)
2. BtScanService에서 백그라운드 블루투스 탐색을, BtGattService에서 백그라운드에서 블루투스 연결을 수행합니다.


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
