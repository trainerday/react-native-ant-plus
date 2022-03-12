package com.reactnativeantplus

enum class AntPlusBikeSpeedDistanceEvent(val event: String) {
  CalculatedAccumulatedDistance("CalculatedAccumulatedDistance"),
  CalculatedSpeed("CalculatedSpeed"),
  MotionAndSpeedData("MotionAndSpeedData"),
  RawSpeedAndDistanceData("RawSpeedAndDistanceData"),
}
