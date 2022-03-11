package com.reactnativeantplus

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray

class AntPlusHeartRate(
  val context: ReactApplicationContext,
  val antPlus: AntPlusModule,
  val antDeviceNumber: Int,
  private val connectPromise: Promise
) {
  private var heartRate: AntPlusHeartRatePcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusHeartRatePcc>? = null
  private val deviceData = HashMap<String, Any>()

  fun init() {
    releaseHandle = requestAccess(
      context,
      antDeviceNumber,
      0,
      resultReceiver,
      AntPlusPlugin.stateReceiver(antPlus, antDeviceNumber)
    )
  }

  fun subscribe(events: ReadableArray, isOnlyNewData: Boolean) {
    events.toArrayList().forEach { event ->
      when (event) {
        AntPlusHeartRateEvent.CalculatedRrInterval.toString() -> subscribeCalculatedRrInterval(isOnlyNewData)
        AntPlusHeartRateEvent.HeartRateData.toString() -> subscribeHeartRateData(isOnlyNewData)
        AntPlusHeartRateEvent.Page4AddtData.toString() -> subscribePage4AddtData(isOnlyNewData)
        AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString() -> subscribeCumulativeOperatingTime(isOnlyNewData)
        AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString() -> subscribeManufacturerAndSerial(isOnlyNewData)
        AntPlusLegacyCommonEvent.VersionAndModel.toString() -> subscribeVersionAndModel(isOnlyNewData)
        AntPlusLegacyCommonEvent.Rssi.toString() -> subscribeRssi(isOnlyNewData)
      }
    }
  }

  fun unsubscribe(events: ReadableArray) {
    events.toArrayList().forEach { event ->
      when (event) {
        AntPlusHeartRateEvent.CalculatedRrInterval.toString() -> unsubscribeCalculatedRrInterval()
        AntPlusHeartRateEvent.HeartRateData.toString() -> unsubscribeHeartRateData()
        AntPlusHeartRateEvent.Page4AddtData.toString() -> unsubscribePage4AddtData()
        AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString() -> unsubscribeCumulativeOperatingTime()
        AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString() -> unsubscribeManufacturerAndSerial()
        AntPlusLegacyCommonEvent.VersionAndModel.toString() -> unsubscribeVersionAndModel()
        AntPlusLegacyCommonEvent.Rssi.toString() -> unsubscribeRssi()
      }
    }
  }

  private fun unsubscribeCalculatedRrInterval() {
    heartRate!!.subscribeCalculatedRrIntervalEvent(null)
    deviceData.remove("rrInterval")
    deviceData.remove("rrFlag")
  }

  private fun subscribeCalculatedRrInterval(isOnlyNewData: Boolean) {
    heartRate!!.subscribeCalculatedRrIntervalEvent { estTimestamp, eventFlags, rrInterval, flag ->
      if (isOnlyNewData && deviceData["rrInterval"] == rrInterval && deviceData["rrFlag"] == flag) {
        return@subscribeCalculatedRrIntervalEvent
      }

      deviceData["rrInterval"] = rrInterval
      deviceData["rrFlag"] = flag

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusHeartRateEvent.CalculatedRrInterval.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putDouble("rrInterval", rrInterval.toDouble())
      eventData.putString("rrFlag", flag.toString())
      antPlus.sendEvent(AntPlusEvent.heartRate, eventData)
    }
  }


  private fun unsubscribeHeartRateData() {
    heartRate!!.subscribeHeartRateDataEvent(null)
    deviceData.remove("heartRate")
  }

  private fun subscribeHeartRateData(isOnlyNewData: Boolean) {
    heartRate!!.subscribeHeartRateDataEvent { estTimestamp, eventFlags, computedHeartRate, heartBeatCounter, heartBeatEventTime, dataState ->
      if (isOnlyNewData && deviceData["heartRate"] == computedHeartRate) {
        return@subscribeHeartRateDataEvent
      }

      deviceData["heartRate"] = computedHeartRate

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusHeartRateEvent.HeartRateData.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("heartRate", computedHeartRate)
      eventData.putInt("heartBeatCount", heartBeatCounter.toInt())
      eventData.putInt("heartBeatEventTime", heartBeatEventTime.toInt())
      eventData.putInt("dataState", dataState.intValue)
      antPlus.sendEvent(AntPlusEvent.heartRate, eventData)
    }
  }

  private fun unsubscribePage4AddtData() {
    heartRate!!.subscribePage4AddtDataEvent(null)
    deviceData.remove("manufacturerSpecificByte")
    deviceData.remove("previousHeartBeatEventTime")
  }

  private fun subscribePage4AddtData(isOnlyNewData: Boolean) {
    heartRate!!.subscribePage4AddtDataEvent { estTimestamp, eventFlags, manufacturerSpecificByte, previousHeartBeatEventTime ->
      if (isOnlyNewData && deviceData["manufacturerSpecificByte"] == manufacturerSpecificByte && deviceData["previousHeartBeatEventTime"] == previousHeartBeatEventTime) {
        return@subscribePage4AddtDataEvent
      }

      deviceData["manufacturerSpecificByte"] = manufacturerSpecificByte
      deviceData["previousHeartBeatEventTime"] = previousHeartBeatEventTime

      val device = Arguments.createMap()
      device.putString("event", AntPlusHeartRateEvent.Page4AddtData.toString())
      device.putString("eventFlags", eventFlags.toString())
      device.putInt("estTimestamp", estTimestamp.toInt())
      device.putInt("manufacturerSpecificByte", manufacturerSpecificByte)
      device.putInt("previousHeartBeatEventTime", previousHeartBeatEventTime.toInt())
      antPlus.sendEvent(AntPlusEvent.heartRate, device)
    }
  }

  private fun unsubscribeCumulativeOperatingTime() {
    heartRate!!.subscribeCumulativeOperatingTimeEvent(null)
    deviceData.remove("cumulativeOperatingTime")
  }

  private fun subscribeCumulativeOperatingTime(isOnlyNewData: Boolean) {
    heartRate!!.subscribeCumulativeOperatingTimeEvent { estTimestamp, eventFlags, cumulativeOperatingTime ->
      if (isOnlyNewData && deviceData["cumulativeOperatingTime"] == cumulativeOperatingTime) {
        return@subscribeCumulativeOperatingTimeEvent
      }

      deviceData["cumulativeOperatingTime"] = cumulativeOperatingTime

      val map = Arguments.createMap()
      map.putString("event", AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("cumulativeOperatingTime", cumulativeOperatingTime.toInt())
      antPlus.sendEvent(AntPlusEvent.heartRate, map)
    }
  }

  private fun unsubscribeManufacturerAndSerial() {
    heartRate!!.subscribeManufacturerAndSerialEvent(null)
    deviceData.remove("manufacturerID")
    deviceData.remove("serialNumber")
  }

  private fun subscribeManufacturerAndSerial(isOnlyNewData: Boolean) {
    heartRate!!.subscribeManufacturerAndSerialEvent { estTimestamp, eventFlags, manufacturerID, serialNumber ->
      if (isOnlyNewData && deviceData["manufacturerID"] == manufacturerID && deviceData["serialNumber"] == serialNumber) {
        return@subscribeManufacturerAndSerialEvent
      }

      deviceData["manufacturerID"] = manufacturerID
      deviceData["serialNumber"] = serialNumber

      val map = Arguments.createMap()
      map.putString("event", AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("manufacturerID", manufacturerID)
      map.putInt("serialNumber", serialNumber)
      antPlus.sendEvent(AntPlusEvent.heartRate, map)
    }
  }

  private fun unsubscribeVersionAndModel() {
    heartRate!!.subscribeVersionAndModelEvent(null)
    deviceData.remove("hardwareVersion")
    deviceData.remove("softwareVersion")
    deviceData.remove("modelNumber")
  }

  private fun subscribeVersionAndModel(isOnlyNewData: Boolean) {
    heartRate!!.subscribeVersionAndModelEvent { estTimestamp, eventFlags, hardwareVersion, softwareVersion, modelNumber ->
      if (isOnlyNewData && deviceData["hardwareVersion"] == hardwareVersion && deviceData["softwareVersion"] == softwareVersion && deviceData["modelNumber"] == modelNumber) {
        return@subscribeVersionAndModelEvent
      }

      deviceData["hardwareVersion"] = hardwareVersion
      deviceData["softwareVersion"] = softwareVersion
      deviceData["modelNumber"] = modelNumber

      val map = Arguments.createMap()
      map.putString("event", AntPlusLegacyCommonEvent.VersionAndModel.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("hardwareVersion", hardwareVersion)
      map.putInt("softwareVersion", softwareVersion)
      map.putInt("modelNumber", modelNumber)
      antPlus.sendEvent(AntPlusEvent.heartRate, map)
    }
  }

  private fun unsubscribeRssi() {
    heartRate!!.subscribeRssiEvent(null)
    deviceData.remove("rssi")
  }

  private fun subscribeRssi(isOnlyNewData: Boolean) {
    heartRate!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return@subscribeRssiEvent
      }

      deviceData["rssi"] = rssi

      val map = Arguments.createMap()
      map.putString("event", AntPlusLegacyCommonEvent.Rssi.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("rssi", rssi)
      antPlus.sendEvent(AntPlusEvent.heartRate, map)
    }
  }

  private var resultReceiver =
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
