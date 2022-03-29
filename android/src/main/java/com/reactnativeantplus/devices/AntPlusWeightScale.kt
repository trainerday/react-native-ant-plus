package com.reactnativeantplus.devices

/**
 * Implementing the AntPlusWeightScalePcc
 * https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusWeightScalePcc.html
 */

import android.util.Log
import com.reactnativeantplus.AntPlusModule
import com.reactnativeantplus.AntPlusPlugin
import com.reactnativeantplus.events.AntPlusCommonEvent
import com.reactnativeantplus.events.AntPlusEvent
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.*


class AntPlusWeightScale(
  val context: ReactApplicationContext,
  val antPlus: AntPlusModule,
  val antDeviceNumber: Int,
  private val connectPromise: Promise
) {
  private var weightScale: AntPlusWeightScalePcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusWeightScalePcc>? = null
  private val deviceData = HashMap<String, Any>()
  private val userProfile = UserProfile()

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
        Event.BodyWeightBroadcast.toString() -> subscribeBodyWeightBroadcast(isOnlyNewData)

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
        Event.BodyWeightBroadcast.toString() -> unsubscribeBodyWeightBroadcast()

        AntPlusCommonEvent.BatteryStatus.toString() -> unsubscribeBatteryStatus()
        AntPlusCommonEvent.ManufacturerIdentification.toString() -> unsubscribeManufacturerIdentification()
        AntPlusCommonEvent.ManufacturerSpecific.toString() -> unsubscribeManufacturerSpecificData()
        AntPlusCommonEvent.ProductInformation.toString() -> unsubscribeProductInformation()
        AntPlusCommonEvent.Rssi.toString() -> unsubscribeRssi()
      }
    }
  }

  private fun unsubscribeBodyWeightBroadcast() {
    weightScale!!.subscribeBodyWeightBroadcastEvent(null)
    deviceData.remove("bodyWeightStatus")
    deviceData.remove("bodyWeight")
  }

  private fun subscribeBodyWeightBroadcast(isOnlyNewData: Boolean) {
    weightScale!!.subscribeBodyWeightBroadcastEvent { estTimestamp, eventFlags, bodyWeightStatus, bodyWeight ->
      if (isOnlyNewData && deviceData["bodyWeightStatus"] == bodyWeightStatus && deviceData["bodyWeight"] == bodyWeight) {
        return@subscribeBodyWeightBroadcastEvent
      }

      deviceData["bodyWeightStatus"] = bodyWeightStatus
      deviceData["bodyWeight"] = bodyWeight

      val eventData = AntPlusPlugin.createEventDataMap(
        Event.BodyWeightBroadcast.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )
      eventData.putString("bodyWeightStatus", bodyWeightStatus.toString())
      eventData.putDouble("bodyWeight", bodyWeight.toDouble())
      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeBatteryStatus() {
    weightScale!!.subscribeBatteryStatusEvent(null)
    deviceData.remove("cumulativeOperatingTime")
    deviceData.remove("batteryVoltage")
    deviceData.remove("batteryStatus")
    deviceData.remove("cumulativeOperatingTimeResolution")
    deviceData.remove("numberOfBatteries")
    deviceData.remove("batteryIdentifier")
  }

  private fun subscribeBatteryStatus(isOnlyNewData: Boolean) {
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

      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeManufacturerIdentification() {
    weightScale!!.subscribeManufacturerIdentificationEvent(null)
    deviceData.remove("hardwareRevision")
    deviceData.remove("manufacturerID")
    deviceData.remove("modelNumber")

  }

  private fun subscribeManufacturerIdentification(isOnlyNewData: Boolean) {
    weightScale!!.subscribeManufacturerIdentificationEvent { estTimestamp, eventFlags, hardwareRevision, manufacturerID, modelNumber ->
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

      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeManufacturerSpecificData() {
    weightScale!!.subscribeManufacturerSpecificDataEvent(null)
    deviceData.remove("rawDataBytes")
  }

  private fun subscribeManufacturerSpecificData(isOnlyNewData: Boolean) {
    weightScale!!.subscribeManufacturerSpecificDataEvent { estTimestamp, eventFlags, rawDataBytes ->
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
      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeProductInformation() {
    weightScale!!.subscribeProductInformationEvent(null)
    deviceData.remove("softwareRevision")
    deviceData.remove("supplementaryRevision")
    deviceData.remove("serialNumber")
  }

  private fun subscribeProductInformation(isOnlyNewData: Boolean) {
    weightScale!!.subscribeProductInformationEvent { estTimestamp, eventFlags, softwareRevision, supplementaryRevision, serialNumber ->
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
      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  private fun unsubscribeRssi() {
    weightScale!!.subscribeRssiEvent(null)
    deviceData.remove("rssi")
  }

  private fun subscribeRssi(isOnlyNewData: Boolean) {
    weightScale!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return@subscribeRssiEvent
      }

      deviceData["rssi"] = rssi

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusCommonEvent.Rssi.toString(), estTimestamp, eventFlags,
        antDeviceNumber
      )

      eventData.putInt("rssi", rssi)
      antPlus.sendEvent(AntPlusEvent.weightScale, eventData)
    }
  }

  fun request(requestName: String, args: ReadableMap, promise: Promise) {
    when (Request.valueOf(requestName)) {
      Request.AdvancedMeasurement -> requestAdvancedMeasurement(args, promise)
      Request.BasicMeasurement -> requestBasicMeasurement(promise)
      Request.Capabilities -> requestCapabilities(promise)
      Request.DownloadAllHistory -> requestDownloadAllHistory(promise)
    }
  }

  private fun requestAdvancedMeasurement(userData: ReadableMap, promise: Promise) {
    userProfile.gender = if (userData.getInt("gender") == 1) Gender.MALE else Gender.FEMALE
    userProfile.age = userData.getInt("age")
    userProfile.height = userData.getInt("height")
    userProfile.activityLevel = userData.getInt("activityLevel")
    userProfile.lifetimeAthlete = userData.getBoolean("athlete")

    weightScale!!.requestAdvancedMeasurement({ estTimestamp, eventFlags, status, measurement ->
      if (status === WeightScaleRequestStatus.SUCCESS) {
        val requestData = Arguments.createMap()

        val measurementData = Arguments.createMap()

        measurementData.putDouble("bodyWeight", measurement.bodyWeight.toDouble())
        measurementData.putDouble(
          "hydrationPercentage",
          measurement.hydrationPercentage.toDouble()
        )
        measurementData.putDouble(
          "bodyFatPercentage",
          measurement.bodyFatPercentage.toDouble()
        )
        measurementData.putDouble("muscleMass", measurement.muscleMass.toDouble())
        measurementData.putDouble("boneMass", measurement.boneMass.toDouble())
        measurementData.putDouble(
          "activeMetabolicRate",
          measurement.activeMetabolicRate.toDouble()
        )
        measurementData.putDouble(
          "basalMetabolicRate",
          measurement.basalMetabolicRate.toDouble()
        )

        requestData.putMap("measurement", measurementData)

        promise.resolve(requestData)
      }
    }, userProfile)
  }

  private fun requestBasicMeasurement(promise: Promise) {
    weightScale!!.requestBasicMeasurement { estTimestamp, eventFlags, status, bodyWeight ->
      if (status === WeightScaleRequestStatus.SUCCESS) {
        val requestData = Arguments.createMap()

        requestData.putString("status", status.toString())
        requestData.putDouble("bodyWeight", bodyWeight.toDouble())

        promise.resolve(requestData)
      }
    }
  }

  //  Requests the capabilities of weight scale and the identifier of the currently 'selected' user profile, if any.
  private fun requestCapabilities(promise: Promise) {
    weightScale!!.requestCapabilities { estTimestamp, eventFlags, status, userProfileID, historySupport, userProfileExchangeSupport, userProfileSelected ->
      if (status === WeightScaleRequestStatus.SUCCESS) {
        val requestData = Arguments.createMap()

        requestData.putString("event", "BasicMeasurement")
        requestData.putString("eventFlags", eventFlags.toString())
        requestData.putInt("estTimestamp", estTimestamp.toInt())
        requestData.putString("status", status.toString())
        requestData.putInt("userProfileID", userProfileID)
        requestData.putBoolean("historySupport", historySupport)
        requestData.putBoolean("userProfileExchangeSupport", userProfileExchangeSupport)
        requestData.putBoolean("userProfileSelected", userProfileSelected)

        promise.resolve(requestData)
      }
    }
  }

  //  Requests a download of all of the history data from the device.
  private fun requestDownloadAllHistory(promise: Promise) {
    weightScale!!.requestDownloadAllHistory(
      { status ->

//                `status` - The AntFsRequestStatus indicating the result of the request.
      },
      { downloadedFitFile ->

//                `downloadedFitFile` - The FIT file downloaded.
      }, { state, transferredBytes, totalBytes ->

//                `state` - The current ANT-FS state.
//                `transferredBytes` - The number of bytes transferred. Note: There are cases in the process of retrying a download that this number can restart at a lower value. This number is always the progress towards the total, not a count of total number of bytes sent over the connection.
//                `totalBytes` - The total number of bytes to be transferred.
    }
    )
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

  enum class Event(val event: String) {
    BodyWeightBroadcast("BodyWeightBroadcast"),
  }

  enum class Request(val event: String) {
    AdvancedMeasurement("AdvancedMeasurement"),
    BasicMeasurement("BasicMeasurement"),
    Capabilities("Capabilities"),
    DownloadAllHistory("DownloadAllHistory")
  }
}
