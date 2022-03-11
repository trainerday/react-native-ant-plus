package com.reactnativeantplus

import android.util.Log
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread

class AntPlusDevice (val context: ReactApplicationContext, val antPlus: AntPlusModule, val antDeviceNumber: Int, deviceTypeNumber: Int) {
  private var deviceType: DeviceType = DeviceType.getValueFromInt(deviceTypeNumber)
  private var device: Any? = null
  var isConnected: Boolean = false

  fun connect(promise: Promise) {
    runOnUiThread {
      isConnected = true
      when (deviceType) {
        DeviceType.BIKE_CADENCE -> {
          device = AntPlusBikeCadence(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusBikeCadence).init()
        }
        DeviceType.WEIGHT_SCALE -> {
          device = AntPlusWeightScale(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusWeightScale).init()
        }
        DeviceType.HEARTRATE -> {
          device = AntPlusHeartRate(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusHeartRate).init()
        }
        else -> {
          isConnected = false
          promise.reject(Error("Device is not supported"))
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
        DeviceType.BIKE_CADENCE -> (device as AntPlusBikeCadence).disconnect(promise)
        DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).disconnect(promise)
        DeviceType.HEARTRATE -> (device as AntPlusHeartRate).disconnect(promise)
        else -> promise.reject(Error("Device is not supported"))
      }
    }
    isConnected = false
  }

  fun subscribe(events: ReadableArray, isOnlyNewData: Boolean) {
    when (deviceType) {
      DeviceType.BIKE_CADENCE -> (device as AntPlusBikeCadence).subscribe(events, isOnlyNewData)
      DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).subscribe(events, isOnlyNewData)
      DeviceType.HEARTRATE -> (device as AntPlusHeartRate).subscribe(events, isOnlyNewData)
      else -> Log.e("subscribe", "Device is not supported")
    }
  }

  fun unsubscribe(events: ReadableArray) {
    when (deviceType) {
      DeviceType.BIKE_CADENCE -> (device as AntPlusBikeCadence).unsubscribe(events)
      DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).unsubscribe(events)
      DeviceType.HEARTRATE -> (device as AntPlusHeartRate).unsubscribe(events)
      else -> Log.e("unsubscribe", "Device is not supported")
    }
  }

  fun request(requestName: String, args: ReadableMap, promise: Promise) {
    when (deviceType) {
      DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).request(requestName, args, promise)
      else -> promise.reject(Error("Device is not supported"))
    }
  }
}
