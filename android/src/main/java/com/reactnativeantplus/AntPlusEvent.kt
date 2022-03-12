package com.reactnativeantplus

enum class AntPlusEvent(val event: String) {
  searchStatus("searchStatus"),
  rssi("rssi"),
  foundDevice("foundDevice"),
  devicesStateChange("devicesStateChange"),

  bikeCadence("bikeCadence"),
  bikePower("bikePower"),
  bikeSpeedDistance("bikeSpeedDistance"),
  bikeSpeedAndCadence("bikeSpeedAndCadence"),
  weightScale("weightScale"),
  heartRate("heartRate"),

  strideSdm("strideSdm"),
  environment("environment"),
  fitnessEquipment("fitnessEquipment"),
  error("error")
}
