package com.reactnativeantplus.devices

/**
 * Implementing the AntPlusHeartRatePcc
 * https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusHeartRatePcc.html
 */

import com.reactnativeantplus.AntPlusModule
import com.reactnativeantplus.AntPlusPlugin
import com.reactnativeantplus.events.AntPlusEvent
import com.reactnativeantplus.events.AntPlusLegacyCommonEvent
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
        Event.CalculatedRrInterval.toString() -> subscribeCalculatedRrInterval(isOnlyNewData)
        Event.HeartRateData.toString() -> subscribeHeartRateData(isOnlyNewData)
        Event.Page4AddtData.toString() -> subscribePage4AddtData(isOnlyNewData)

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
        Event.CalculatedRrInterval.toString() -> unsubscribeCalculatedRrInterval()
        Event.HeartRateData.toString() -> unsubscribeHeartRateData()
        Event.Page4AddtData.toString() -> unsubscribePage4AddtData()

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

      val eventData = AntPlusPlugin.createEventDataMap(Event.CalculatedRrInterval.toString(), estTimestamp, eventFlags)
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

      val eventData = AntPlusPlugin.createEventDataMap(Event.HeartRateData.toString(), estTimestamp, eventFlags)

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

      val eventData = AntPlusPlugin.createEventDataMap(Event.Page4AddtData.toString(), estTimestamp, eventFlags)

      eventData.putInt("manufacturerSpecificByte", manufacturerSpecificByte)
      eventData.putInt("previousHeartBeatEventTime", previousHeartBeatEventTime.toInt())
      antPlus.sendEvent(AntPlusEvent.heartRate, eventData)
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

      val eventData = AntPlusPlugin.createEventDataMap(AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString(), estTimestamp, eventFlags)

      eventData.putInt("cumulativeOperatingTime", cumulativeOperatingTime.toInt())
      antPlus.sendEvent(AntPlusEvent.heartRate, eventData)
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

      val eventData = AntPlusPlugin.createEventDataMap(AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString(), estTimestamp, eventFlags)

      eventData.putInt("manufacturerID", manufacturerID)
      eventData.putInt("serialNumber", serialNumber)
      antPlus.sendEvent(AntPlusEvent.heartRate, eventData)
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

      val eventData = AntPlusPlugin.createEventDataMap(AntPlusLegacyCommonEvent.VersionAndModel.toString(), estTimestamp, eventFlags)

      eventData.putInt("hardwareVersion", hardwareVersion)
      eventData.putInt("softwareVersion", softwareVersion)
      eventData.putInt("modelNumber", modelNumber)
      antPlus.sendEvent(AntPlusEvent.heartRate, eventData)
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

      val eventData = AntPlusPlugin.createEventDataMap(AntPlusLegacyCommonEvent.Rssi.toString(), estTimestamp, eventFlags)
      eventData.putInt("rssi", rssi)
      antPlus.sendEvent(AntPlusEvent.heartRate, eventData)
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

  enum class Event(val event: String) {
    CalculatedRrInterval("CalculatedRrInterval"),
    HeartRateData("HeartRateData"),
    Page4AddtData("Page4AddtData")
  }
}
