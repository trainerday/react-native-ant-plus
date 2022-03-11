package com.reactnativeantplus

import android.util.Log
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.requestAccess
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.*

class AntPlusWeightScale(val context: ReactApplicationContext, val antPlus: AntPlusModule, val antDeviceNumber: Int, private val connectPromise: Promise) {
  private var weightScale: AntPlusWeightScalePcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusWeightScalePcc>? = null
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
        AntPlusWeightScaleEvent.BodyWeightBroadcast.toString() -> subscribeBodyWeightBroadcastEvent(isOnlyNewData)
        AntPlusWeightScaleEvent.BatteryStatus.toString() -> subscribeBatteryStatusEvent(isOnlyNewData)
        AntPlusWeightScaleEvent.ManufacturerIdentification.toString() -> subscribeManufacturerIdentificationEvent(isOnlyNewData)
        AntPlusWeightScaleEvent.ManufacturerSpecific.toString() -> subscribeManufacturerSpecificDataEvent(isOnlyNewData)
        AntPlusWeightScaleEvent.ProductInformation.toString() -> subscribeProductInformationEvent(isOnlyNewData)
        AntPlusWeightScaleEvent.Rssi.toString() -> subscribeRssiEvent(isOnlyNewData)
      }
    }
  }

  fun unsubscribe(events: ReadableArray) {
    events.toArrayList().forEach { event ->
      when (event) {
        AntPlusWeightScaleEvent.BodyWeightBroadcast.toString() -> unsubscribeBodyWeightBroadcastEvent()
        AntPlusWeightScaleEvent.BatteryStatus.toString() -> unsubscribeBatteryStatusEvent()
        AntPlusWeightScaleEvent.ManufacturerIdentification.toString() -> unsubscribeManufacturerIdentificationEvent()
        AntPlusWeightScaleEvent.ManufacturerSpecific.toString() -> unsubscribeManufacturerSpecificDataEvent()
        AntPlusWeightScaleEvent.ProductInformation.toString() -> unsubscribeProductInformationEvent()
        AntPlusWeightScaleEvent.Rssi.toString() -> unsubscribeRssiEvent()
      }
    }
  }

  private fun unsubscribeBodyWeightBroadcastEvent() {
    weightScale!!.subscribeBodyWeightBroadcastEvent(null)
  }

  private fun subscribeBodyWeightBroadcastEvent(isOnlyNewData: Boolean) {
    weightScale!!.subscribeBodyWeightBroadcastEvent { estTimestamp, eventFlags, bodyWeightStatus, bodyWeight ->
      if (isOnlyNewData && deviceData["bodyWeightStatus"] == bodyWeightStatus && deviceData["bodyWeight"] == bodyWeight) {
        return@subscribeBodyWeightBroadcastEvent
      }

      deviceData["bodyWeightStatus"] = bodyWeightStatus
      deviceData["bodyWeight"] = bodyWeight

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusWeightScaleEvent.BodyWeightBroadcast.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putString("bodyWeightStatus", bodyWeightStatus.toString())
      eventData.putDouble("bodyWeight", bodyWeight.toDouble())
      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeBatteryStatusEvent() {
    weightScale!!.subscribeBatteryStatusEvent(null)
  }

  private fun subscribeBatteryStatusEvent(isOnlyNewData: Boolean) {
    weightScale!!.subscribeBatteryStatusEvent { estTimestamp, eventFlags, cumulativeOperatingTime, batteryVoltage, batteryStatus, cumulativeOperatingTimeResolution, numberOfBatteries, batteryIdentifier ->
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

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusWeightScaleEvent.BatteryStatus.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("cumulativeOperatingTime", cumulativeOperatingTime.toInt())
      eventData.putDouble("batteryVoltage", batteryVoltage.toDouble())
      eventData.putString("batteryStatus", batteryStatus.toString())
      eventData.putInt("cumulativeOperatingTimeResolution", cumulativeOperatingTimeResolution)
      eventData.putInt("numberOfBatteries", numberOfBatteries)
      eventData.putInt("batteryIdentifier", batteryIdentifier)

      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeManufacturerIdentificationEvent() {
    weightScale!!.subscribeManufacturerIdentificationEvent(null)
  }

  private fun subscribeManufacturerIdentificationEvent(isOnlyNewData: Boolean) {
    weightScale!!.subscribeManufacturerIdentificationEvent { estTimestamp, eventFlags, hardwareRevision, manufacturerID, modelNumber ->
      if (isOnlyNewData && deviceData["hardwareRevision"] == hardwareRevision && deviceData["manufacturerID"] == manufacturerID && deviceData["modelNumber"] == modelNumber) {
        return@subscribeManufacturerIdentificationEvent
      }

      deviceData["hardwareRevision"] = hardwareRevision
      deviceData["manufacturerID"] = manufacturerID
      deviceData["modelNumber"] = modelNumber

      val eventData = Arguments.createMap()
      eventData.putString(
        "event",
        AntPlusWeightScaleEvent.ManufacturerIdentification.toString()
      )
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putInt("hardwareRevision", hardwareRevision)
      eventData.putInt("manufacturerID", manufacturerID)
      eventData.putInt("modelNumber", modelNumber)

      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeManufacturerSpecificDataEvent() {
    weightScale!!.subscribeManufacturerSpecificDataEvent(null)
  }

  private fun subscribeManufacturerSpecificDataEvent(isOnlyNewData: Boolean) {
    weightScale!!.subscribeManufacturerSpecificDataEvent { estTimestamp, eventFlags, rawDataBytes ->
      if (isOnlyNewData && deviceData["rawDataBytes"] == rawDataBytes) {
        return@subscribeManufacturerSpecificDataEvent
      }

      deviceData["rawDataBytes"] = rawDataBytes

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusWeightScaleEvent.ManufacturerSpecific.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      try {
        eventData.putArray("rawDataBytes", antPlus.bytesToWritableArray(rawDataBytes))
      } catch (throwable: Throwable) {
        Log.e("ManufacturerSpecific", "rawDataBytes", throwable)
      }
      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeProductInformationEvent() {
    weightScale!!.subscribeProductInformationEvent(null)
  }

  private fun subscribeProductInformationEvent(isOnlyNewData: Boolean) {
    weightScale!!.subscribeProductInformationEvent { estTimestamp, eventFlags, softwareRevision, supplementaryRevision, serialNumber ->
      if (isOnlyNewData && deviceData["softwareRevision"] == softwareRevision && deviceData["supplementaryRevision"] == supplementaryRevision && deviceData["serialNumber"] == serialNumber) {
        return@subscribeProductInformationEvent
      }

      deviceData["softwareRevision"] = softwareRevision
      deviceData["supplementaryRevision"] = supplementaryRevision
      deviceData["serialNumber"] = serialNumber

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusWeightScaleEvent.ProductInformation.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putInt("softwareRevision", softwareRevision)
      eventData.putInt("supplementaryRevision", supplementaryRevision)
      eventData.putInt("serialNumber", serialNumber.toInt())
      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeRssiEvent() {
    weightScale!!.subscribeRssiEvent(null)
  }

  private fun subscribeRssiEvent(isOnlyNewData: Boolean) {
    weightScale!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return@subscribeRssiEvent
      }

      deviceData["rssi"] = rssi

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusWeightScaleEvent.Rssi.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putInt("rssi", rssi)
      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private val resultReceiver =
    IPluginAccessResultReceiver<AntPlusWeightScalePcc> { result, resultCode, initialDeviceState ->
      val status = Arguments.createMap()
      status.putString("name", result.deviceName)
      status.putString("state", initialDeviceState.toString())
      status.putString("code", resultCode.toString())

      if (resultCode === RequestAccessResult.SUCCESS) {
        weightScale = result
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
