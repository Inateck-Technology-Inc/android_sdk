package com.inateck

 class ScannerJNI {
     external fun connect(deviceId: String,appId: String,developerId: String,appKey: String): String

     external fun scan(): String

     private external fun init()

     external fun getBasicProperties(deviceId: String,propertyKey: String): String

     external fun getPropertiesInfoByKey(deviceId: String,propertyKey: String): String

     external fun editPropertiesInfoByKey(deviceId: String,propertyKey: String,data: String): String

     external fun getAllBarcodeProperties(deviceId: String): String

    init {
        System.loadLibrary("inateck_scanner")
        init()
    }
}