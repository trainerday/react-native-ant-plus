package com.reactnativeantplus.devices

/**
 * Implementing the AntPlusStrideSdmPcc
 * https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusStrideSdmPcc.html
 */

import android.util.Log
import com.reactnativeantplus.AntPlusModule
import com.reactnativeantplus.AntPlusPlugin
import com.reactnativeantplus.events.AntPlusCommonEvent
import com.reactnativeantplus.events.AntPlusEvent
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray

class AntPlusStrideSdm(
  val context: ReactApplicationContext,
  val antPlus: AntPlusModule,
  val antDeviceNumber: Int,
  private val connectPromise: Promise
) {
  private var strideSdm: AntPlusStrideSdmPcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusStrideSdmPcc>? = null
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
        Event.CalorieData.toString() -> subscribeCalorieData(isOnlyNewData)
        Event.ComputationTimestamp.toString() -> subscribeComputationTimestamp(isOnlyNewData)
        Event.DataLatency.toString() -> subscribeDataLatency(isOnlyNewData)
        Event.Distance.toString() -> subscribeDistance(isOnlyNewData)
        Event.InstantaneousCadence.toString() -> subscribeInstantaneousCadence(isOnlyNewData)
        Event.InstantaneousSpeed.toString() -> subscribeInstantaneousSpeed(isOnlyNewData)
        Event.SensorStatus.toString() -> subscribeSensorStatus(isOnlyNewData)
        Event.StrideCount.toString() -> subscribeStrideCount(isOnlyNewData)

        AntPlusCommonEvent.BatteryStatus.toString() -> subscribeBatteryStatus(isOnlyNewData)
        AntPlusCommonEvent.ManufacturerIdentification.toString() -> subscribeManufacturerIdentification(
          isOnlyNewData
        )
        AntPlusCommonEvent.ManufacturerSpecific.toString() -> subscribeManufacturerSpecificData(
          isOnlyNewData
        )
        AntPlusCommonEvent.ProductInformation.toString() -> subscribeProductInformation(
          isOnlyNewData
        )
        AntPlusCommonEvent.Rssi.toString() -> subscribeRssi(isOnlyNewData)
      }
    }
  }

  fun unsubscribe(events: ReadableArray) {
    events.toArrayList().forEach { event ->
      when (event) {
        Event.CalorieData.toString() -> unsubscribeCalorieData()
        Event.ComputationTimestamp.toString() -> unsubscribeComputationTimestamp()
        Event.DataLatency.toString() -> unsubscribeDataLatency()
        Event.Distance.toString() -> unsubscribeDistance()
        Event.InstantaneousCadence.toString() -> unsubscribeInstantaneousCadence()
        Event.InstantaneousSpeed.toString() -> unsubscribeInstantaneousSpeed()
        Event.SensorStatus.toString() -> unsubscribeSensorStatus()
        Event.StrideCount.toString() -> unsubscribeStrideCount()

        AntPlusCommonEvent.BatteryStatus.toString() -> unsubscribeBatteryStatus()
        AntPlusCommonEvent.ManufacturerIdentification.toString() -> unsubscribeManufacturerIdentification()
        AntPlusCommonEvent.ManufacturerSpecific.toString() -> unsubscribeManufacturerSpecificData()
        AntPlusCommonEvent.ProductInformation.toString() -> unsubscribeProductInformation()
        AntPlusCommonEvent.Rssi.toString() -> unsubscribeRssi()
      }
    }
  }

  private fun unsubscribeCalorieData() {
    strideSdm!!.subscribeCalorieDataEvent(null)
    deviceData.remove("cumulativeCalories")
  }

  private fun subscribeCalorieData(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeCalorieDataEvent { estTimestamp, eventFlags, cumulativeCalories ->
      if (isOnlyNewData && deviceData["cumulativeCalories"] == cumulativeCalories) {
        return@subscribeCalorieDataEvent
      }

      deviceData["cumulativeCalories"] = cumulativeCalories

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.CalorieData.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )

      eventData.putInt("cumulativeCalories", cumulativeCalories.toInt())

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeComputationTimestamp() {
    strideSdm!!.subscribeComputationTimestampEvent(null)
    deviceData.remove("timestampOfLastComputation")
  }

  private fun subscribeComputationTimestamp(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeComputationTimestampEvent { estTimestamp, eventFlags, timestampOfLastComputation ->
      if (isOnlyNewData && deviceData["timestampOfLastComputation"] == timestampOfLastComputation) {
        return@subscribeComputationTimestampEvent
      }

      deviceData["timestampOfLastComputation"] = timestampOfLastComputation

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.ComputationTimestamp.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )
      eventData.putDouble("timestampOfLastComputation", timestampOfLastComputation.toDouble())

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeDataLatency() {
    strideSdm!!.subscribeDataLatencyEvent(null)
    deviceData.remove("updateLatency")
  }

  private fun subscribeDataLatency(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeDataLatencyEvent { estTimestamp, eventFlags, updateLatency ->
      if (isOnlyNewData && deviceData["updateLatency"] == updateLatency) {
        return@subscribeDataLatencyEvent
      }

      deviceData["updateLatency"] = updateLatency

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.DataLatency.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )
      eventData.putDouble("updateLatency", updateLatency.toDouble())

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeDistance() {
    strideSdm!!.subscribeDistanceEvent(null)
    deviceData.remove("cumulativeDistance")
  }

  private fun subscribeDistance(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeDistanceEvent { estTimestamp, eventFlags, cumulativeDistance ->
      if (isOnlyNewData && deviceData["cumulativeDistance"] == cumulativeDistance) {
        return@subscribeDistanceEvent
      }

      deviceData["cumulativeDistance"] = cumulativeDistance

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.Distance.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )
      eventData.putDouble("cumulativeDistance", cumulativeDistance.toDouble())

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeInstantaneousCadence() {
    strideSdm!!.subscribeInstantaneousCadenceEvent(null)
    deviceData.remove("instantaneousCadence")
  }

  private fun subscribeInstantaneousCadence(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeInstantaneousCadenceEvent { estTimestamp, eventFlags, instantaneousCadence ->
      if (isOnlyNewData && deviceData["instantaneousCadence"] == instantaneousCadence) {
        return@subscribeInstantaneousCadenceEvent
      }

      deviceData["instantaneousCadence"] = instantaneousCadence

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.InstantaneousCadence.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )
      eventData.putDouble("instantaneousCadence", instantaneousCadence.toDouble())

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeInstantaneousSpeed() {
    strideSdm!!.subscribeInstantaneousSpeedEvent(null)
    deviceData.remove("instantaneousSpeed")
  }

  private fun subscribeInstantaneousSpeed(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeInstantaneousSpeedEvent { estTimestamp, eventFlags, instantaneousSpeed ->
      if (isOnlyNewData && deviceData["instantaneousSpeed"] == instantaneousSpeed) {
        return@subscribeInstantaneousSpeedEvent
      }

      deviceData["instantaneousSpeed"] = instantaneousSpeed

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.InstantaneousSpeed.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )
      eventData.putDouble("instantaneousSpeed", instantaneousSpeed.toDouble())

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeSensorStatus() {
    strideSdm!!.subscribeSensorStatusEvent(null)
    deviceData.remove("sensorLocation")
    deviceData.remove("batteryStatus")
    deviceData.remove("sensorHealth")
    deviceData.remove("useState")
  }

  private fun subscribeSensorStatus(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeSensorStatusEvent { estTimestamp, eventFlags, sensorLocation, batteryStatus, sensorHealth, useState ->
      if (
        isOnlyNewData &&
        deviceData["sensorLocation"] == sensorLocation &&
        deviceData["batteryStatus"] == batteryStatus &&
        deviceData["sensorHealth"] == sensorHealth &&
        deviceData["useState"] == useState
      ) {
        return@subscribeSensorStatusEvent
      }

      deviceData["sensorLocation"] = sensorLocation
      deviceData["batteryStatus"] = batteryStatus
      deviceData["sensorHealth"] = sensorHealth
      deviceData["useState"] = useState

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.SensorStatus.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )

      eventData.putString("sensorLocation", sensorLocation.toString())
      eventData.putString("batteryStatus", batteryStatus.toString())
      eventData.putString("sensorHealth", sensorHealth.toString())
      eventData.putString("useState", useState.toString())

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeStrideCount() {
    strideSdm!!.subscribeStrideCountEvent(null)
    deviceData.remove("cumulativeStrides")
  }

  private fun subscribeStrideCount(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeStrideCountEvent { estTimestamp, eventFlags, cumulativeStrides ->
      if (isOnlyNewData && deviceData["cumulativeStrides"] == cumulativeStrides) {
        return@subscribeStrideCountEvent
      }

      deviceData["cumulativeStrides"] = cumulativeStrides

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.StrideCount.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )
      eventData.putInt("cumulativeStrides", cumulativeStrides.toInt())

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeBatteryStatus() {
    strideSdm!!.subscribeBatteryStatusEvent(null)
    deviceData.remove("cumulativeOperatingTime")
    deviceData.remove("batteryVoltage")
    deviceData.remove("batteryStatus")
    deviceData.remove("cumulativeOperatingTimeResolution")
    deviceData.remove("numberOfBatteries")
    deviceData.remove("batteryIdentifier")
  }

  private fun subscribeBatteryStatus(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeBatteryStatusEvent { estTimestamp, eventFlags, cumulativeOperatingTime, batteryVoltage, batteryStatus, cumulativeOperatingTimeResolution, numberOfBatteries, batteryIdentifier ->
      if (
        isOnlyNewData &&
        deviceData["cumulativeOperatingTime"] == cumulativeOperatingTime &&
        deviceData["batteryVoltage"] == batteryVoltage &&
        deviceData["batteryStatus"] == batteryStatus &&
        deviceData["cumulativeOperatingTimeResolution"] == cumulativeOperatingTimeResolution &&
        deviceData["numberOfBatteries"] == numberOfBatteries &&
        deviceData["batteryIdentifier"] == batteryIdentifier
      ) {
        return@subscribeBatteryStatusEvent
      }

      deviceData["cumulativeOperatingTime"] = cumulativeOperatingTime
      deviceData["batteryVoltage"] = batteryVoltage
      deviceData["batteryStatus"] = batteryStatus
      deviceData["cumulativeOperatingTimeResolution"] = cumulativeOperatingTimeResolution
      deviceData["numberOfBatteries"] = numberOfBatteries
      deviceData["batteryIdentifier"] = batteryIdentifier

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.BatteryStatus.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )

      eventData.putInt("cumulativeOperatingTime", cumulativeOperatingTime.toInt())
      eventData.putDouble("batteryVoltage", batteryVoltage.toDouble())
      eventData.putString("batteryStatus", batteryStatus.toString())
      eventData.putInt("cumulativeOperatingTimeResolution", cumulativeOperatingTimeResolution)
      eventData.putInt("numberOfBatteries", numberOfBatteries)
      eventData.putInt("batteryIdentifier", batteryIdentifier)

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeManufacturerIdentification() {
    strideSdm!!.subscribeManufacturerIdentificationEvent(null)
    deviceData.remove("hardwareRevision")
    deviceData.remove("manufacturerID")
    deviceData.remove("modelNumber")

  }

  private fun subscribeManufacturerIdentification(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeManufacturerIdentificationEvent { estTimestamp, eventFlags, hardwareRevision, manufacturerID, modelNumber ->
      if (isOnlyNewData && deviceData["hardwareRevision"] == hardwareRevision && deviceData["manufacturerID"] == manufacturerID && deviceData["modelNumber"] == modelNumber) {
        return@subscribeManufacturerIdentificationEvent
      }

      deviceData["hardwareRevision"] = hardwareRevision
      deviceData["manufacturerID"] = manufacturerID
      deviceData["modelNumber"] = modelNumber

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.ManufacturerIdentification.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )

      eventData.putInt("hardwareRevision", hardwareRevision)
      eventData.putInt("manufacturerID", manufacturerID)
      eventData.putInt("modelNumber", modelNumber)

      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeManufacturerSpecificData() {
    strideSdm!!.subscribeManufacturerSpecificDataEvent(null)
    deviceData.remove("rawDataBytes")
  }

  private fun subscribeManufacturerSpecificData(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeManufacturerSpecificDataEvent { estTimestamp, eventFlags, rawDataBytes ->
      if (isOnlyNewData && deviceData["rawDataBytes"] == rawDataBytes) {
        return@subscribeManufacturerSpecificDataEvent
      }

      deviceData["rawDataBytes"] = rawDataBytes

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.ManufacturerSpecific.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )

      try {
        eventData.putArray("rawDataBytes", AntPlusModule.bytesToWritableArray(rawDataBytes))
      } catch (throwable: Throwable) {
        Log.e("ManufacturerSpecific", "rawDataBytes", throwable)
      }
      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeProductInformation() {
    strideSdm!!.subscribeProductInformationEvent(null)
    deviceData.remove("softwareRevision")
    deviceData.remove("supplementaryRevision")
    deviceData.remove("serialNumber")
  }

  private fun subscribeProductInformation(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeProductInformationEvent { estTimestamp, eventFlags, softwareRevision, supplementaryRevision, serialNumber ->
      if (isOnlyNewData && deviceData["softwareRevision"] == softwareRevision && deviceData["supplementaryRevision"] == supplementaryRevision && deviceData["serialNumber"] == serialNumber) {
        return@subscribeProductInformationEvent
      }

      deviceData["softwareRevision"] = softwareRevision
      deviceData["supplementaryRevision"] = supplementaryRevision
      deviceData["serialNumber"] = serialNumber

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.ProductInformation.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )

      eventData.putInt("softwareRevision", softwareRevision)
      eventData.putInt("supplementaryRevision", supplementaryRevision)
      eventData.putInt("serialNumber", serialNumber.toInt())
      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private fun unsubscribeRssi() {
    strideSdm!!.subscribeRssiEvent(null)
    deviceData.remove("rssi")
  }

  private fun subscribeRssi(isOnlyNewData: Boolean) {
    strideSdm!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return@subscribeRssiEvent
      }

      deviceData["rssi"] = rssi

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.Rssi.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )

      eventData.putInt("rssi", rssi)
      antPlus.sendEvent(AntPlusEvent.strideSdm, eventData)
    }
  }

  private var resultReceiver =
    IPluginAccessResultReceiver<AntPlusStrideSdmPcc> { result, resultCode, initialDeviceState ->
      val status = Arguments.createMap()
      status.putString("state", initialDeviceState.toString())
      status.putString("code", resultCode.toString())
      status.putInt("antDeviceNumber", antDeviceNumber)

      if (resultCode === RequestAccessResult.SUCCESS) {
        strideSdm = result
        status.putBoolean("connected", true)
        status.putString("name", result.deviceName)
      } else {
        status.putBoolean("connected", false)
      }
      connectPromise.resolve(status)
    }

  fun disconnect(promise: Promise) {
    try {
      destroy()
      promise.resolve(true)
    } catch (throwable: Throwable) {
      promise.resolve(throwable)
    }
  }

  private fun destroy() {
    if (releaseHandle != null) {
      releaseHandle!!.close()
    }
  }

  enum class Event(val value: String) {
    CalorieData("CalorieData"),
    ComputationTimestamp("ComputationTimestamp"),
    DataLatency("DataLatency"),
    Distance("Distance"),
    InstantaneousCadence("InstantaneousCadence"),
    InstantaneousSpeed("InstantaneousSpeed"),
    SensorStatus("SensorStatus"),
    StrideCount("StrideCount"),
  }
}
