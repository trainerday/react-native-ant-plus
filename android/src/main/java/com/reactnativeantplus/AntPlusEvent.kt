package com.reactnativeantplus

enum class AntPlusEvent(val event: String) {
  searchStatus("searchStatus"),
  rssi("rssi"),
  foundDevice("foundDevice"),

  deviceConnected("deviceConnected"),
  heartRate("heartRate"),
  bikeCadence("bikeCadence"),
  bikeSpeedDistance("bikeSpeedDistance"),
  bikeSpeed("bikeSpeed"),
  bikePower("bikePower"),
  weightScale("weightScale"),
  strideSdm("strideSdm"),
  environment("environment"),
  fitnessEquipment("fitnessEquipment"),
  error("error")
}
