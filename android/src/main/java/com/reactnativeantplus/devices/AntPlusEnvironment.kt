package com.reactnativeantplus.devices

/**
 * Implementing the AntPlusEnvironmentPcc
 * https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusEnvironmentPcc.html
 */

import android.util.Log
import com.reactnativeantplus.AntPlusModule
import com.reactnativeantplus.AntPlusPlugin
import com.reactnativeantplus.events.AntPlusCommonEvent
import com.reactnativeantplus.events.AntPlusEvent
import com.dsi.ant.plugins.antplus.pcc.AntPlusEnvironmentPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusEnvironmentPcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray

class AntPlusEnvironment(
  val context: ReactApplicationContext,
  val antPlus: AntPlusModule,
  val antDeviceNumber: Int,
  private val connectPromise: Promise
) {
  private var environment: AntPlusEnvironmentPcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusEnvironmentPcc>? = null
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
        Event.TemperatureData.toString() -> subscribeTemperatureData(isOnlyNewData)

        AntPlusCommonEvent.BatteryStatus.toString() -> subscribeBatteryStatus(
          isOnlyNewData
        )
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
        Event.TemperatureData.toString() -> unsubscribeTemperatureData()

        AntPlusCommonEvent.BatteryStatus.toString() -> unsubscribeBatteryStatus()
        AntPlusCommonEvent.ManufacturerIdentification.toString() -> unsubscribeManufacturerIdentification()
        AntPlusCommonEvent.ManufacturerSpecific.toString() -> unsubscribeManufacturerSpecificData()
        AntPlusCommonEvent.ProductInformation.toString() -> unsubscribeProductInformation()
        AntPlusCommonEvent.Rssi.toString() -> unsubscribeRssi()
      }
    }
  }

  private fun unsubscribeTemperatureData() {
    environment!!.subscribeTemperatureDataEvent(null)
    deviceData.remove("currentTemperature")
    deviceData.remove("eventCount")
    deviceData.remove("lowLast24Hours")
    deviceData.remove("highLast24Hours")
  }

  private fun subscribeTemperatureData(isOnlyNewData: Boolean) {
    environment!!.subscribeTemperatureDataEvent { estTimestamp, eventFlags, currentTemperature, eventCount, lowLast24Hours, highLast24Hours ->
      if (
        isOnlyNewData &&
        deviceData["currentTemperature"] == currentTemperature &&
        deviceData["eventCount"] == eventCount &&
        deviceData["lowLast24Hours"] == lowLast24Hours &&
        deviceData["highLast24Hours"] == highLast24Hours
      ) {
        return@subscribeTemperatureDataEvent
      }
      deviceData["currentTemperature"] = currentTemperature
      deviceData["eventCount"] = eventCount
      deviceData["lowLast24Hours"] = lowLast24Hours
      deviceData["highLast24Hours"] = highLast24Hours

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.TemperatureData.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )

      eventData.putDouble("eventCount", eventCount.toDouble())
      eventData.putInt("currentTemperature", currentTemperature.toInt())
      eventData.putDouble("lowLast24Hours", lowLast24Hours.toDouble())
      eventData.putDouble("highLast24Hours", highLast24Hours.toDouble())

      antPlus.sendEvent(AntPlusEvent.environment, eventData)
    }
  }

  private fun unsubscribeBatteryStatus() {
    environment!!.subscribeBatteryStatusEvent(null)
    deviceData.remove("cumulativeOperatingTime")
    deviceData.remove("batteryVoltage")
    deviceData.remove("batteryStatus")
    deviceData.remove("cumulativeOperatingTimeResolution")
    deviceData.remove("numberOfBatteries")
    deviceData.remove("batteryIdentifier")
  }

  private fun subscribeBatteryStatus(isOnlyNewData: Boolean) {
    environment!!.subscribeBatteryStatusEvent { estTimestamp, eventFlags, cumulativeOperatingTime, batteryVoltage, batteryStatus, cumulativeOperatingTimeResolution, numberOfBatteries, batteryIdentifier ->
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
        AntPlusCommonEvent.BatteryStatus.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )

      eventData.putInt("cumulativeOperatingTime", cumulativeOperatingTime.toInt())
      eventData.putDouble("batteryVoltage", batteryVoltage.toDouble())
      eventData.putString("batteryStatus", batteryStatus.toString())
      eventData.putInt("cumulativeOperatingTimeResolution", cumulativeOperatingTimeResolution)
      eventData.putInt("numberOfBatteries", numberOfBatteries)
      eventData.putInt("batteryIdentifier", batteryIdentifier)

      antPlus.sendEvent(AntPlusEvent.environment, eventData)
    }
  }

  private fun unsubscribeManufacturerIdentification() {
    environment!!.subscribeManufacturerIdentificationEvent(null)
    deviceData.remove("hardwareRevision")
    deviceData.remove("manufacturerID")
    deviceData.remove("modelNumber")

  }

  private fun subscribeManufacturerIdentification(isOnlyNewData: Boolean) {
    environment!!.subscribeManufacturerIdentificationEvent { estTimestamp, eventFlags, hardwareRevision, manufacturerID, modelNumber ->
      if (isOnlyNewData && deviceData["hardwareRevision"] == hardwareRevision && deviceData["manufacturerID"] == manufacturerID && deviceData["modelNumber"] == modelNumber) {
        return@subscribeManufacturerIdentificationEvent
      }

      deviceData["hardwareRevision"] = hardwareRevision
      deviceData["manufacturerID"] = manufacturerID
      deviceData["modelNumber"] = modelNumber

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.ManufacturerIdentification.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )

      eventData.putInt("hardwareRevision", hardwareRevision)
      eventData.putInt("manufacturerID", manufacturerID)
      eventData.putInt("modelNumber", modelNumber)

      antPlus.sendEvent(AntPlusEvent.environment, eventData)
    }
  }

  private fun unsubscribeManufacturerSpecificData() {
    environment!!.subscribeManufacturerSpecificDataEvent(null)
    deviceData.remove("rawDataBytes")
  }

  private fun subscribeManufacturerSpecificData(isOnlyNewData: Boolean) {
    environment!!.subscribeManufacturerSpecificDataEvent { estTimestamp, eventFlags, rawDataBytes ->
      if (isOnlyNewData && deviceData["rawDataBytes"] == rawDataBytes) {
        return@subscribeManufacturerSpecificDataEvent
      }

      deviceData["rawDataBytes"] = rawDataBytes

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.ManufacturerSpecific.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )
      try {
        eventData.putArray("rawDataBytes", AntPlusModule.bytesToWritableArray(rawDataBytes))
      } catch (throwable: Throwable) {
        Log.e("ManufacturerSpecific", "rawDataBytes", throwable)
      }
      antPlus.sendEvent(AntPlusEvent.environment, eventData)
    }
  }

  private fun unsubscribeProductInformation() {
    environment!!.subscribeProductInformationEvent(null)
    deviceData.remove("softwareRevision")
    deviceData.remove("supplementaryRevision")
    deviceData.remove("serialNumber")
  }

  private fun subscribeProductInformation(isOnlyNewData: Boolean) {
    environment!!.subscribeProductInformationEvent { estTimestamp, eventFlags, softwareRevision, supplementaryRevision, serialNumber ->
      if (isOnlyNewData && deviceData["softwareRevision"] == softwareRevision && deviceData["supplementaryRevision"] == supplementaryRevision && deviceData["serialNumber"] == serialNumber) {
        return@subscribeProductInformationEvent
      }

      deviceData["softwareRevision"] = softwareRevision
      deviceData["supplementaryRevision"] = supplementaryRevision
      deviceData["serialNumber"] = serialNumber

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.ProductInformation.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )

      eventData.putInt("softwareRevision", softwareRevision)
      eventData.putInt("supplementaryRevision", supplementaryRevision)
      eventData.putInt("serialNumber", serialNumber.toInt())

      antPlus.sendEvent(AntPlusEvent.environment, eventData)
    }
  }

  private fun unsubscribeRssi() {
    environment!!.subscribeRssiEvent(null)
    deviceData.remove("rssi")
  }

  private fun subscribeRssi(isOnlyNewData: Boolean) {
    environment!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return@subscribeRssiEvent
      }

      deviceData["rssi"] = rssi

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.Rssi.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )

      eventData.putInt("rssi", rssi)

      antPlus.sendEvent(AntPlusEvent.environment, eventData)
    }
  }

  private var resultReceiver =
    IPluginAccessResultReceiver<AntPlusEnvironmentPcc> { result, resultCode, initialDeviceState ->
      val status = Arguments.createMap()
      status.putString("name", result.deviceName)
      status.putString("state", initialDeviceState.toString())
      status.putString("code", resultCode.toString())

      if (resultCode === RequestAccessResult.SUCCESS) {
        environment = result
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
    TemperatureData("TemperatureData")
  }
}
