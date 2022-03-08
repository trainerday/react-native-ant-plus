package com.reactnativeantplus

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray

class AntPlusHeartRate(reactContext: ReactApplicationContext, antPlusModule: AntPlusModule, _antDeviceNumber: Int, connectPromise: Promise) {
  private val context: ReactApplicationContext = reactContext
  private val antPlus: AntPlusModule = antPlusModule
  private var heartRate: AntPlusHeartRatePcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusHeartRatePcc>? = null
  private val deviceData = HashMap<String, Any>()
  private val antDeviceNumber = _antDeviceNumber

  fun init() {
    releaseHandle = requestAccess(
      context,
      antDeviceNumber,
      0,
      resultReceiver,
      stateReceiver
    )
  }

  fun subscribe(events: ReadableArray, isOnlyNewData: Boolean) {
    events.toArrayList().forEach { event ->
      when (event) {
        AntPlusHeartRateEvents.CalculatedRrInterval.toString() -> subscribeCalculatedRrIntervalEvent(isOnlyNewData)
        AntPlusHeartRateEvents.HeartRateData.toString() -> subscribeHeartRateDataEvent(isOnlyNewData)
        AntPlusHeartRateEvents.Page4AddtData.toString() -> subscribePage4AddtDataEvent(isOnlyNewData)

        AntPlusHeartRateEvents.CumulativeOperatingTime.toString() -> subscribeCumulativeOperatingTimeEvent(isOnlyNewData)
        AntPlusHeartRateEvents.ManufacturerAndSerial.toString() -> subscribeManufacturerAndSerialEvent(isOnlyNewData)
        AntPlusHeartRateEvents.VersionAndModel.toString() -> subscribeVersionAndModelEvent(isOnlyNewData)
        AntPlusHeartRateEvents.Rssi.toString() -> subscribeRssiEvent(isOnlyNewData)
      }
    }
  }

  fun subscribeCalculatedRrIntervalEvent(isOnlyNewData: Boolean) {
    heartRate!!.subscribeCalculatedRrIntervalEvent { estTimestamp, eventFlags, rrInterval, flag ->
      if (isOnlyNewData && deviceData["rrInterval"] == rrInterval) {
        return@subscribeCalculatedRrIntervalEvent
      }

      deviceData["rrInterval"] = rrInterval

      val device = Arguments.createMap()
      device.putString("event", AntPlusHeartRateEvents.CalculatedRrInterval.toString())
      device.putString("eventFlags", eventFlags.toString())
      device.putInt("estTimestamp", estTimestamp.toInt())
      device.putDouble("rrInterval", rrInterval.toDouble())
      device.putString("flag", flag.toString())
      antPlus.sendEvent(AntPlusEvent.heartRate, device)
    }
  }

  fun subscribeHeartRateDataEvent(isOnlyNewData: Boolean) {
    heartRate!!.subscribeHeartRateDataEvent { estTimestamp, eventFlags, computedHeartRate, heartBeatCounter, heartBeatEventTime, dataState ->
      if (isOnlyNewData && deviceData["heartRate"] == computedHeartRate) {
        return@subscribeHeartRateDataEvent
      }

      deviceData["heartRate"] = computedHeartRate

      val device = Arguments.createMap()
      device.putString("event", AntPlusHeartRateEvents.HeartRateData.toString())
      device.putString("eventFlags", eventFlags.toString())
      device.putInt("heartRate", computedHeartRate)
      device.putInt("deviceNumber", antDeviceNumber)
      device.putInt("heartBeatCount", heartBeatCounter.toInt())
      device.putInt("heartBeatEventTime", heartBeatEventTime.toInt())
      device.putInt("dataState", dataState.intValue)
      antPlus.sendEvent(AntPlusEvent.heartRate, device)
    }
  }

  fun subscribePage4AddtDataEvent(isOnlyNewData: Boolean) {
    heartRate!!.subscribePage4AddtDataEvent { estTimestamp, eventFlags, manufacturerSpecificByte, previousHeartBeatEventTime ->
      if (isOnlyNewData && deviceData["manufacturerSpecificByte"] == manufacturerSpecificByte && deviceData["previousHeartBeatEventTime"] == previousHeartBeatEventTime) {
        return@subscribePage4AddtDataEvent
      }

      deviceData["manufacturerSpecificByte"] = manufacturerSpecificByte
      deviceData["previousHeartBeatEventTime"] = previousHeartBeatEventTime

      val device = Arguments.createMap()
      device.putString("event", AntPlusHeartRateEvents.Page4AddtData.toString())
      device.putString("eventFlags", eventFlags.toString())
      device.putInt("estTimestamp", estTimestamp.toInt())
      device.putInt("manufacturerSpecificByte", manufacturerSpecificByte)
      device.putInt("previousHeartBeatEventTime", previousHeartBeatEventTime.toInt())
      antPlus.sendEvent(AntPlusEvent.heartRate, device)
    }
  }

  fun subscribeCumulativeOperatingTimeEvent(isOnlyNewData: Boolean) {
    heartRate!!.subscribeCumulativeOperatingTimeEvent { estTimestamp, eventFlags, cumulativeOperatingTime ->
      if (isOnlyNewData && deviceData["cumulativeOperatingTime"] == cumulativeOperatingTime) {
        return@subscribeCumulativeOperatingTimeEvent
      }

      deviceData["cumulativeOperatingTime"] = cumulativeOperatingTime

      val map = Arguments.createMap()
      map.putString("event", AntPlusLegacyCommonEvents.CumulativeOperatingTime.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("cumulativeOperatingTime", cumulativeOperatingTime.toInt())
      antPlus.sendEvent(AntPlusEvent.heartRate, map)
    }
  }

  fun subscribeManufacturerAndSerialEvent(isOnlyNewData: Boolean) {
    heartRate!!.subscribeManufacturerAndSerialEvent { estTimestamp, eventFlags, manufacturerID, serialNumber ->
      if (isOnlyNewData && deviceData["manufacturerID"] == manufacturerID && deviceData["serialNumber"] == serialNumber) {
        return@subscribeManufacturerAndSerialEvent
      }

      deviceData["manufacturerID"] = manufacturerID
      deviceData["serialNumber"] = serialNumber

      val map = Arguments.createMap()
      map.putString("event", AntPlusHeartRateEvents.ManufacturerAndSerial.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("manufacturerID", manufacturerID)
      map.putInt("serialNumber", serialNumber)
      antPlus.sendEvent(AntPlusEvent.heartRate, map)
    }
  }

  fun subscribeVersionAndModelEvent(isOnlyNewData: Boolean) {
    heartRate!!.subscribeVersionAndModelEvent { estTimestamp, eventFlags, hardwareVersion, softwareVersion, modelNumber ->
      if (isOnlyNewData && deviceData["hardwareVersion"] == hardwareVersion && deviceData["softwareVersion"] == softwareVersion && deviceData["modelNumber"] == modelNumber) {
        return@subscribeVersionAndModelEvent
      }

      deviceData["hardwareVersion"] = hardwareVersion
      deviceData["softwareVersion"] = softwareVersion
      deviceData["modelNumber"] = modelNumber

      val map = Arguments.createMap()
      map.putString("event", AntPlusHeartRateEvents.VersionAndModel.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("hardwareVersion", hardwareVersion)
      map.putInt("softwareVersion", softwareVersion)
      map.putInt("modelNumber", modelNumber)
      antPlus.sendEvent(AntPlusEvent.heartRate, map)
    }
  }

  fun unsubscribeRssiEvent() {
    heartRate!!.subscribeRssiEvent(null)
  }

  fun subscribeRssiEvent(isOnlyNewData: Boolean) {
    heartRate!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return@subscribeRssiEvent
      }

      deviceData["rssi"] = rssi

      val map = Arguments.createMap()
      map.putString("event", AntPlusHeartRateEvents.Rssi.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("rssi", rssi)
      antPlus.sendEvent(AntPlusEvent.heartRate, map)
    }
  }

  protected var resultReceiver =
    IPluginAccessResultReceiver<AntPlusHeartRatePcc> { result, resultCode, initialDeviceState ->
      val status = Arguments.createMap()
      status.putString("name", result.deviceName)
      status.putString("state", initialDeviceState.toString())
      status.putString("code", resultCode.toString())

      if (resultCode === RequestAccessResult.SUCCESS) {
        heartRate = result
        status.putBoolean("connected", true)
      } else {
        status.putBoolean("connected", false)
      }
      connectPromise.resolve(status)
    }

  protected var stateReceiver = IDeviceStateChangeReceiver { newDeviceState ->
    val device = Arguments.createMap()
    device.putString("event", "DeviceStateChangeReceiver")
    device.putString("name", heartRate!!.deviceName)
    device.putInt("deviceNumber", heartRate!!.getAntDeviceNumber())
    device.putString("flag", newDeviceState.toString())
    antPlus.sendEvent(AntPlusEvent.heartRate, device)
  }

  fun disconnect(promise: Promise) {
    destroy()
    promise.resolve(true)
  }

  private fun destroy() {
    if (releaseHandle != null) {
      releaseHandle!!.close()
    }
  }
}
