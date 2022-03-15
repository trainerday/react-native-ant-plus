package com.reactnativeantplus

import android.util.Log
import com.reactnativeantplus.devices.*
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
        DeviceType.BIKE_POWER -> {
          device = AntPlusBikePower(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusBikePower).init()
        }
        DeviceType.BIKE_CADENCE, DeviceType.BIKE_SPD, DeviceType.BIKE_SPDCAD -> {
          device = AntPlusBikeSpeedAndCadence(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusBikeSpeedAndCadence).init(deviceType)
        }
        DeviceType.ENVIRONMENT -> {
          device = AntPlusEnvironment(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusEnvironment).init()
        }
        DeviceType.FITNESS_EQUIPMENT -> {
          device = AntPlusFitnessEquipment(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusFitnessEquipment).init()
        }
        DeviceType.WEIGHT_SCALE -> {
          device = AntPlusWeightScale(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusWeightScale).init()
        }
        DeviceType.HEARTRATE -> {
          device = AntPlusHeartRate(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusHeartRate).init()
        }
        DeviceType.STRIDE_SDM -> {
          device = AntPlusStrideSdm(context, antPlus, antDeviceNumber, promise)
          (device as AntPlusStrideSdm).init()
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
        DeviceType.BIKE_POWER -> (device as AntPlusBikePower).disconnect(promise)
        DeviceType.BIKE_CADENCE, DeviceType.BIKE_SPD, DeviceType.BIKE_SPDCAD -> (device as AntPlusBikeSpeedAndCadence).disconnect(promise)
        DeviceType.ENVIRONMENT -> (device as AntPlusEnvironment).disconnect(promise)
        DeviceType.FITNESS_EQUIPMENT -> (device as AntPlusFitnessEquipment).disconnect(promise)
        DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).disconnect(promise)
        DeviceType.HEARTRATE -> (device as AntPlusHeartRate).disconnect(promise)
        else -> promise.reject(Error("Device is not supported"))
      }
    }
    isConnected = false
  }

  fun subscribe(events: ReadableArray, isOnlyNewData: Boolean) {
    when (deviceType) {
      DeviceType.BIKE_POWER -> (device as AntPlusBikePower).subscribe(events, isOnlyNewData)
      DeviceType.BIKE_CADENCE, DeviceType.BIKE_SPD, DeviceType.BIKE_SPDCAD -> (device as AntPlusBikeSpeedAndCadence).subscribe(events, isOnlyNewData)
      DeviceType.ENVIRONMENT -> (device as AntPlusEnvironment).subscribe(events, isOnlyNewData)
      DeviceType.FITNESS_EQUIPMENT -> (device as AntPlusFitnessEquipment).subscribe(events, isOnlyNewData)
      DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).subscribe(events, isOnlyNewData)
      DeviceType.HEARTRATE -> (device as AntPlusHeartRate).subscribe(events, isOnlyNewData)
      DeviceType.STRIDE_SDM -> (device as AntPlusStrideSdm).subscribe(events, isOnlyNewData)
      else -> Log.e("subscribe", "Device is not supported")
    }
  }

  fun unsubscribe(events: ReadableArray) {
    when (deviceType) {
      DeviceType.BIKE_POWER -> (device as AntPlusBikePower).unsubscribe(events)
      DeviceType.BIKE_CADENCE, DeviceType.BIKE_SPD, DeviceType.BIKE_SPDCAD -> (device as AntPlusBikeSpeedAndCadence).unsubscribe(events)
      DeviceType.ENVIRONMENT -> (device as AntPlusEnvironment).unsubscribe(events)
      DeviceType.FITNESS_EQUIPMENT -> (device as AntPlusFitnessEquipment).unsubscribe(events)
      DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).unsubscribe(events)
      DeviceType.HEARTRATE -> (device as AntPlusHeartRate).unsubscribe(events)
      DeviceType.STRIDE_SDM -> (device as AntPlusStrideSdm).unsubscribe(events)
      else -> Log.e("unsubscribe", "Device is not supported")
    }
  }

  fun setVariables(variables: ReadableMap, promise: Promise) {
    when (deviceType) {
      DeviceType.BIKE_POWER -> (device as AntPlusBikePower).setVariables(variables, promise)
      DeviceType.FITNESS_EQUIPMENT -> (device as AntPlusFitnessEquipment).setVariables(variables, promise)
      DeviceType.BIKE_SPD, DeviceType.BIKE_SPDCAD -> (device as AntPlusBikeSpeedAndCadence).setVariables(variables, promise)
      else -> promise.reject(Error("Device is not supported"))
    }
  }

  fun getVariables(variables: ReadableMap, promise: Promise) {
    when (deviceType) {
      DeviceType.BIKE_POWER -> (device as AntPlusBikePower).getVariables(variables, promise)
      DeviceType.FITNESS_EQUIPMENT -> (device as AntPlusFitnessEquipment).getVariables(variables, promise)
      DeviceType.BIKE_SPD, DeviceType.BIKE_SPDCAD -> (device as AntPlusBikeSpeedAndCadence).getVariables(variables, promise)
      else -> promise.reject(Error("Device is not supported"))
    }
  }

  fun request(requestName: String, args: ReadableMap, promise: Promise) {
    when (deviceType) {
      DeviceType.BIKE_POWER -> (device as AntPlusBikePower).request(requestName, args, promise)
      DeviceType.FITNESS_EQUIPMENT -> (device as AntPlusFitnessEquipment).request(requestName, args, promise)
      DeviceType.WEIGHT_SCALE -> (device as AntPlusWeightScale).request(requestName, args, promise)
      else -> promise.reject(Error("Device is not supported"))
    }
  }
}
