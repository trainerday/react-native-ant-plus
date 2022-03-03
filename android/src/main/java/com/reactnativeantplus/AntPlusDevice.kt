package com.reactnativeantplus

import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread

class AntPlusDevice(
  context: ReactApplicationContext,
  antPlus: AntPlusModule,
  antDeviceNumber: Int,
  deviceTypeNumber: Int
) {
  private var context: ReactApplicationContext = context
  private var antPlus: AntPlusModule = antPlus
  private var antDeviceNumber = antDeviceNumber
  private var deviceType: DeviceType = DeviceType.getValueFromInt(deviceTypeNumber)
  private var device: Any? = null

  fun connect(promise: Promise) {
    runOnUiThread {
      when (deviceType) {
        DeviceType.HEARTRATE -> {
          device = AntPlusHeartRate(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusHeartRate).init()
        }
      }
    }
  }

  fun disconnect(promise: Promise) {
    if (device == null) {
      promise.reject(Error("Device not found"))
      return
    }

    runOnUiThread {
      when (deviceType) {
        DeviceType.HEARTRATE -> (device as AntPlusHeartRate).disconnect(promise)
      }
    }
  }

  fun subscribe(events: ReadableArray, isOnlyNewData: Boolean) {
    when (deviceType) {
      DeviceType.HEARTRATE -> (device as AntPlusHeartRate).subscribe(events, isOnlyNewData)
    }
  }
}
