package com.reactnativeantplus.events

enum class AntPlusEvent(val event: String) {
  searchStatus("searchStatus"),
  rssi("rssi"),
  foundDevice("foundDevice"),
  devicesStateChange("devicesStateChange"),

  bikeCadence("bikeCadence"),
  bikePower("bikePower"),
  bikeSpeedDistance("bikeSpeedDistance"),
  bikeSpeedAndCadence("bikeSpeedAndCadence"),
  environment("environment"),
  fitnessEquipment("fitnessEquipment"),
  weightScale("weightScale"),
  heartRate("heartRate"),
  strideSdm("strideSdm")
}

