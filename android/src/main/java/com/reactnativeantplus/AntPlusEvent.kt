package com.reactnativeantplus

enum class AntPlusEvent(val event: String) {
  searchStatus("searchStatus"),
  rssi("rssi"),
  foundDevice("foundDevice"),
  devicesStateChange("devicesStateChange"),

  weightScale("weightScale"),
  heartRate("heartRate"),

  bikeCadence("bikeCadence"),
  bikeSpeedDistance("bikeSpeedDistance"),
  bikeSpeed("bikeSpeed"),
  bikePower("bikePower"),
  strideSdm("strideSdm"),
  environment("environment"),
  fitnessEquipment("fitnessEquipment"),
  error("error")
}
