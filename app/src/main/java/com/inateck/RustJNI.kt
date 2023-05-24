package com.inateck

 class RustJNI {
     external fun connect(deviceId: String,appId: String,developerId: String,appKey: String): String

     external fun scan(): String

     private external fun init()

     external fun getBasicProperties(deviceId: String,propertyKey: String): String

     external fun getPropertiesInfo(deviceId: String,propertyKey: String): String

     external fun editPropertiesInfo(deviceId: String,propertyKey: String,data: String): String

     external fun getBarcodeProperties(deviceId: String): String

    init {
        System.loadLibrary("scanner")
        init()
    }
}