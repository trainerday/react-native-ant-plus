package com.reactnativeantplus

import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread

class AntPlusDevice (val context: ReactApplicationContext, val antPlus: AntPlusModule, val antDeviceNumber: Int, deviceTypeNumber: Int) {
  private var deviceType: DeviceType = DeviceType.getValueFromInt(deviceTypeNumber)
  private var device: Any? = null
  var isConnected: Boolean = false

  fun connect(promise: Promise) {
    runOnUiThread {
      when (deviceType) {
        DeviceType.WEIGHT_SCALE -> {
          device = AntPlusWeightScale(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusWeightScale).init()
        }
        DeviceType.HEARTRATE -> {
          device = AntPlusHeartRate(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusHeartRate).init()
        }
      }
      isConnected = true
    }
  }

  fun disconnect(promise: Promise) {
    if (device == null) {
      promise.reject(Error("Device not found"))
      return
    }

    runOnUiThread {
      when (deviceType) {
        DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).disconnect(promise)
        DeviceType.HEARTRATE -> (device as AntPlusHeartRate).disconnect(promise)
      }
    }
    isConnected = false
  }

  fun subscribe(events: ReadableArray, isOnlyNewData: Boolean) {
    when (deviceType) {
      DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).subscribe(events, isOnlyNewData)
      DeviceType.HEARTRATE -> (device as AntPlusHeartRate).subscribe(events, isOnlyNewData)
    }
  }

  fun unsubscribe(events: ReadableArray) {
    when (deviceType) {
      DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).unsubscribe(events)
      DeviceType.HEARTRATE -> (device as AntPlusHeartRate).unsubscribe(events)
    }
  }
}
