package com.reactnativeantplus

import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray

class AntPlusBikeCadence(val context: ReactApplicationContext, val antPlus: AntPlusModule, val antDeviceNumber: Int, private val connectPromise: Promise) {
  private var bikeCadence: AntPlusBikeCadencePcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusBikeCadencePcc>? = null
  private val deviceData = HashMap<String, Any>()

  fun init() {
    val isSpdCadCombinedSensor = false

    releaseHandle = requestAccess(
      context,
      antDeviceNumber,
      0,
      isSpdCadCombinedSensor,
      resultReceiver,
      AntPlusPlugin.stateReceiver(antPlus, antDeviceNumber)
    )
  }

  fun subscribe(events: ReadableArray, isOnlyNewData: Boolean) {
    events.toArrayList().forEach { event ->
      when (event as String) {
        AntPlusBikeCadenceEvent.CalculatedCadence.toString() -> subscribeCalculatedCadence(isOnlyNewData)
        AntPlusBikeCadenceEvent.MotionAndCadence.toString() -> subscribeMotionAndCadenceData(isOnlyNewData)
        AntPlusBikeCadenceEvent.RawCadence.toString() -> subscribeRawCadenceData(isOnlyNewData)
        AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString() ->subscribeCumulativeOperatingTime(isOnlyNewData)
        AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString() ->subscribeManufacturerAndSerial(isOnlyNewData)
        AntPlusLegacyCommonEvent.VersionAndModel.toString() ->subscribeVersionAndModel(isOnlyNewData)
        AntPlusLegacyCommonEvent.Rssi.toString() ->subscribeRssi(isOnlyNewData)
      }
    }
  }

  fun unsubscribe(events: ReadableArray) {
    events.toArrayList().forEach { event ->
      when (event) {
        AntPlusBikeCadenceEvent.CalculatedCadence.toString() -> unsubscribeCalculatedCadence()
        AntPlusBikeCadenceEvent.MotionAndCadence.toString() -> unsubscribeMotionAndCadenceData()
        AntPlusBikeCadenceEvent.RawCadence.toString() -> unsubscribeRawCadenceData()
        AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString() -> unsubscribeCumulativeOperatingTime()
        AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString() -> unsubscribeManufacturerAndSerial()
        AntPlusLegacyCommonEvent.VersionAndModel.toString() -> unsubscribeVersionAndModel()
        AntPlusLegacyCommonEvent.Rssi.toString() -> unsubscribeRssi()
      }
    }
  }

  fun unsubscribeCalculatedCadence() {
    bikeCadence!!.subscribeCalculatedCadenceEvent(null)
    deviceData.remove("calculatedCadence")
  }

  fun subscribeCalculatedCadence(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeCalculatedCadenceEvent { estTimestamp, eventFlags, calculatedCadence ->
      if (isOnlyNewData && deviceData["calculatedCadence"] == calculatedCadence) {
        return@subscribeCalculatedCadenceEvent
      }

      deviceData["calculatedCadence"] = calculatedCadence

      val device = Arguments.createMap()
      device.putString("event", AntPlusBikeCadenceEvent.CalculatedCadence.toString())
      device.putInt("estTimestamp", estTimestamp.toInt())
      device.putString("eventFlags", eventFlags.toString())
      device.putInt("calculatedCadence", calculatedCadence.toInt())
      antPlus.sendEvent(AntPlusEvent.bikeCadence, device)
    }
  }

  fun unsubscribeMotionAndCadenceData() {
    bikeCadence!!.subscribeMotionAndCadenceDataEvent(null)
    deviceData.remove("isPedallingStopped")
  }

  fun subscribeMotionAndCadenceData(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeMotionAndCadenceDataEvent { estTimestamp, eventFlags, isPedallingStopped ->
      if (isOnlyNewData && deviceData["isPedallingStopped"] == isPedallingStopped) {
        return@subscribeMotionAndCadenceDataEvent
      }

      deviceData["isPedallingStopped"] = isPedallingStopped

      val device = Arguments.createMap()
      device.putString("event", AntPlusBikeCadenceEvent.MotionAndCadence.toString())
      device.putInt("estTimestamp", estTimestamp.toInt())
      device.putString("eventFlags", eventFlags.toString())
      device.putBoolean("isPedallingStopped", isPedallingStopped)
      antPlus.sendEvent(AntPlusEvent.bikeCadence, device)
    }
  }

  fun unsubscribeRawCadenceData() {
    bikeCadence!!.subscribeRawCadenceDataEvent(null)
    deviceData.remove("timestampOfLastEvent")
    deviceData.remove("cumulativeRevolutions")
  }

  fun subscribeRawCadenceData(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeRawCadenceDataEvent { estTimestamp, eventFlags, timestampOfLastEvent, cumulativeRevolutions ->
      if (isOnlyNewData && deviceData["timestampOfLastEvent"] == timestampOfLastEvent && deviceData["cumulativeRevolutions"] == cumulativeRevolutions) {
        return@subscribeRawCadenceDataEvent
      }

      deviceData["timestampOfLastEvent"] = timestampOfLastEvent
      deviceData["cumulativeRevolutions"] = cumulativeRevolutions

      val device = Arguments.createMap()
      device.putString("event", AntPlusBikeCadenceEvent.RawCadence.toString())
      device.putInt("estTimestamp", estTimestamp.toInt())
      device.putDouble("timestampOfLastEvent", timestampOfLastEvent.toDouble())
      device.putInt("cumulativeRevolutions", cumulativeRevolutions.toInt())
      antPlus.sendEvent(AntPlusEvent.bikeCadence, device)
    }
  }

  private fun unsubscribeCumulativeOperatingTime() {
    bikeCadence!!.subscribeCumulativeOperatingTimeEvent(null)
  }

  private fun subscribeCumulativeOperatingTime(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeCumulativeOperatingTimeEvent { estTimestamp, eventFlags, cumulativeOperatingTime ->
      if (isOnlyNewData && deviceData["cumulativeOperatingTime"] == cumulativeOperatingTime) {
        return@subscribeCumulativeOperatingTimeEvent
      }

      deviceData["cumulativeOperatingTime"] = cumulativeOperatingTime

      val map = Arguments.createMap()
      map.putString("event", AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("cumulativeOperatingTime", cumulativeOperatingTime.toInt())
      antPlus.sendEvent(AntPlusEvent.bikeCadence, map)
    }
  }

  private fun unsubscribeManufacturerAndSerial() {
    bikeCadence!!.subscribeManufacturerAndSerialEvent(null)
  }

  private fun subscribeManufacturerAndSerial(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeManufacturerAndSerialEvent { estTimestamp, eventFlags, manufacturerID, serialNumber ->
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
      antPlus.sendEvent(AntPlusEvent.bikeCadence, map)
    }
  }

  private fun unsubscribeVersionAndModel() {
    bikeCadence!!.subscribeVersionAndModelEvent(null)
  }

  private fun subscribeVersionAndModel(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeVersionAndModelEvent { estTimestamp, eventFlags, hardwareVersion, softwareVersion, modelNumber ->
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
      antPlus.sendEvent(AntPlusEvent.bikeCadence, map)
    }
  }

  private fun unsubscribeRssi() {
    bikeCadence!!.subscribeRssiEvent(null)
  }

  private fun subscribeRssi(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return@subscribeRssiEvent
      }

      deviceData["rssi"] = rssi

      val map = Arguments.createMap()
      map.putString("event", AntPlusLegacyCommonEvent.Rssi.toString())
      map.putString("eventFlags", eventFlags.toString())
      map.putInt("estTimestamp", estTimestamp.toInt())
      map.putInt("rssi", rssi)
      antPlus.sendEvent(AntPlusEvent.bikeCadence, map)
    }
  }

  protected var resultReceiver =
    IPluginAccessResultReceiver<AntPlusBikeCadencePcc> { result, resultCode, initialDeviceState ->
      val status = Arguments.createMap()
      status.putString("name", result.deviceName)
      status.putString("state", initialDeviceState.toString())
      status.putString("code", resultCode.toString())

      if (resultCode === RequestAccessResult.SUCCESS) {
        bikeCadence = result
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
