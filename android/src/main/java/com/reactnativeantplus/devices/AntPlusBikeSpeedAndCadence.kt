package com.reactnativeantplus.devices

/**
 * Implementing the AntPlusBikeCadencePcc and AntPlusBikeSpeedDistancePcc
 * https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikeCadencePcc.html
 * https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikeSpeedDistancePcc.html
 */

import com.reactnativeantplus.AntPlusModule
import com.reactnativeantplus.AntPlusPlugin
import com.reactnativeantplus.events.AntPlusEvent
import com.reactnativeantplus.events.AntPlusLegacyCommonEvent
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.*
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.*
import java.lang.Error
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

class AntPlusBikeSpeedAndCadence(
  val context: ReactApplicationContext,
  val antPlus: AntPlusModule,
  val antDeviceNumber: Int,
  private val connectPromise: Promise
) {
  private var bikeCadence: AntPlusBikeCadencePcc? = null
  private var bikeSpeedDistance: AntPlusBikeSpeedDistancePcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusBikeCadencePcc>? = null
  private var releaseHandleSpeedDistance: PccReleaseHandle<AntPlusBikeSpeedDistancePcc>? = null
  private val deviceData = HashMap<String, Any>()
  private var isSpdCadCombinedSensor = false
  private var wheelCircumference: BigDecimal = BigDecimal(2.07)
  private var eventName = AntPlusEvent.bikeCadence

  fun init(deviceType: DeviceType) {
    eventName = when (deviceType) {
      DeviceType.BIKE_CADENCE -> AntPlusEvent.bikeCadence
      DeviceType.BIKE_SPD -> AntPlusEvent.bikeSpeedDistance
      else -> AntPlusEvent.bikeSpeedAndCadence
    }

    isSpdCadCombinedSensor = deviceType == DeviceType.BIKE_SPDCAD

    if (isSpdCadCombinedSensor || deviceType == DeviceType.BIKE_CADENCE) {
      releaseHandle = requestAccess(
        context,
        antDeviceNumber,
        0,
        isSpdCadCombinedSensor,
        resultReceiver,
        AntPlusPlugin.stateReceiver(antPlus, antDeviceNumber)
      )
    }

    if (isSpdCadCombinedSensor || deviceType == DeviceType.BIKE_SPD) {
      releaseHandleSpeedDistance = AntPlusBikeSpeedDistancePcc.requestAccess(
        context,
        antDeviceNumber,
        0,
        isSpdCadCombinedSensor,
        resultReceiverSpeedDistance,
        AntPlusPlugin.stateReceiver(antPlus, antDeviceNumber)
      )
    }
  }

  fun setVariables(variables: ReadableMap, promise: Promise) {
    val keys = variables.keySetIterator()

    while (keys.hasNextKey()) {
      when (val key = keys.nextKey()) {
        "wheelCircumference" -> wheelCircumference = BigDecimal(variables.getDouble(key))
        else -> promise.reject(Error("Variable $key not found"))
      }
    }

    promise.resolve(true)
  }

  fun getVariables(variables: ReadableMap, promise: Promise) {
    val data = Arguments.createMap()
    val keys = variables.keySetIterator()

    while (keys.hasNextKey()) {
      when (val key = keys.nextKey()) {
        "wheelCircumference" -> data.putDouble("wheelCircumference", variables.getDouble(key))
        else -> data.putNull(key)
      }
    }

    promise.resolve(data)
  }

  fun subscribe(events: ReadableArray, isOnlyNewData: Boolean) {
    events.toArrayList().forEach { event ->
      when (event as String) {
        CadenceEvent.CalculatedCadence.toString() -> subscribeCalculatedCadence(
          isOnlyNewData
        )
        CadenceEvent.MotionAndCadenceData.toString() -> subscribeMotionAndCadenceData(
          isOnlyNewData
        )
        CadenceEvent.RawCadenceData.toString() -> subscribeRawCadenceData(isOnlyNewData)

        SpeedDistanceEvent.CalculatedAccumulatedDistance.toString() -> subscribeCalculatedAccumulatedDistance(
          isOnlyNewData
        )
        SpeedDistanceEvent.CalculatedSpeed.toString() -> subscribeCalculatedSpeed(
          isOnlyNewData
        )
        SpeedDistanceEvent.MotionAndSpeedData.toString() -> subscribeMotionAndSpeedData(
          isOnlyNewData
        )
        SpeedDistanceEvent.RawSpeedAndDistanceData.toString() -> subscribeRawSpeedAndDistanceData(
          isOnlyNewData
        )

        AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString() -> subscribeCumulativeOperatingTime(
          isOnlyNewData
        )
        AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString() -> subscribeManufacturerAndSerial(
          isOnlyNewData
        )
        AntPlusLegacyCommonEvent.VersionAndModel.toString() -> subscribeVersionAndModel(
          isOnlyNewData
        )
        AntPlusLegacyCommonEvent.Rssi.toString() -> subscribeRssi(isOnlyNewData)

        SpeedAndCadenceEvent.BatteryStatus.toString() -> subscribeBatteryStatus(
          isOnlyNewData
        )
      }
    }
  }

  fun unsubscribe(events: ReadableArray) {
    events.toArrayList().forEach { event ->
      when (event) {
        CadenceEvent.CalculatedCadence.toString() -> unsubscribeCalculatedCadence()
        CadenceEvent.MotionAndCadenceData.toString() -> unsubscribeMotionAndCadenceData()
        CadenceEvent.RawCadenceData.toString() -> unsubscribeRawCadenceData()

        SpeedDistanceEvent.CalculatedAccumulatedDistance.toString() -> unsubscribeCalculatedAccumulatedDistance()
        SpeedDistanceEvent.CalculatedSpeed.toString() -> unsubscribeCalculatedSpeed()
        SpeedDistanceEvent.MotionAndSpeedData.toString() -> unsubscribeMotionAndSpeedData()
        SpeedDistanceEvent.RawSpeedAndDistanceData.toString() -> unsubscribeRawSpeedAndDistanceData()

        AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString() -> unsubscribeCumulativeOperatingTime()
        AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString() -> unsubscribeManufacturerAndSerial()
        AntPlusLegacyCommonEvent.VersionAndModel.toString() -> unsubscribeVersionAndModel()
        AntPlusLegacyCommonEvent.Rssi.toString() -> unsubscribeRssi()

        SpeedAndCadenceEvent.BatteryStatus.toString() -> unsubscribeBatteryStatus()
      }
    }
  }

  private fun unsubscribeCalculatedCadence() {
    bikeCadence!!.subscribeCalculatedCadenceEvent(null)
    deviceData.remove("calculatedCadence")
  }

  private fun subscribeCalculatedCadence(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeCalculatedCadenceEvent { estTimestamp, eventFlags, calculatedCadence ->
      if (isOnlyNewData && deviceData["calculatedCadence"] == calculatedCadence) {
        return@subscribeCalculatedCadenceEvent
      }

      deviceData["calculatedCadence"] = calculatedCadence

      val eventData = AntPlusPlugin.createEventDataMap(
        CadenceEvent.CalculatedCadence.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )
      eventData.putInt("calculatedCadence", calculatedCadence.toInt())

      antPlus.sendEvent(eventName, eventData)
    }
  }

  private fun unsubscribeMotionAndCadenceData() {
    bikeCadence!!.subscribeMotionAndCadenceDataEvent(null)
    deviceData.remove("isPedallingStopped")
  }

  private fun subscribeMotionAndCadenceData(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeMotionAndCadenceDataEvent { estTimestamp, eventFlags, isPedallingStopped ->
      if (isOnlyNewData && deviceData["isPedallingStopped"] == isPedallingStopped) {
        return@subscribeMotionAndCadenceDataEvent
      }

      deviceData["isPedallingStopped"] = isPedallingStopped

      val eventData = AntPlusPlugin.createEventDataMap(
        CadenceEvent.MotionAndCadenceData.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )
      eventData.putBoolean("isPedallingStopped", isPedallingStopped)

      antPlus.sendEvent(eventName, eventData)
    }
  }

  private fun unsubscribeRawCadenceData() {
    bikeCadence!!.subscribeRawCadenceDataEvent(null)
    deviceData.remove("timestampOfLastEvent")
    deviceData.remove("cumulativeRevolutions")
  }

  private fun subscribeRawCadenceData(isOnlyNewData: Boolean) {
    bikeCadence!!.subscribeRawCadenceDataEvent { estTimestamp, eventFlags, timestampOfLastEvent, cumulativeRevolutions ->
      if (isOnlyNewData && deviceData["timestampOfLastEvent"] == timestampOfLastEvent && deviceData["cumulativeRevolutions"] == cumulativeRevolutions) {
        return@subscribeRawCadenceDataEvent
      }

      deviceData["timestampOfLastEvent"] = timestampOfLastEvent
      deviceData["cumulativeRevolutions"] = cumulativeRevolutions

      val eventData = AntPlusPlugin.createEventDataMap(
        CadenceEvent.RawCadenceData.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )
      eventData.putDouble("timestampOfLastEvent", timestampOfLastEvent.toDouble())
      eventData.putInt("cumulativeRevolutions", cumulativeRevolutions.toInt())

      antPlus.sendEvent(eventName, eventData)
    }
  }

  private fun unsubscribeCalculatedAccumulatedDistance() {
    bikeSpeedDistance!!.subscribeCalculatedAccumulatedDistanceEvent(null)
    deviceData.remove("calculatedAccumulatedDistance")
  }

  private fun subscribeCalculatedAccumulatedDistance(isOnlyNewData: Boolean) {
    bikeSpeedDistance!!.subscribeCalculatedAccumulatedDistanceEvent(object :
      AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver(wheelCircumference) {
      override fun onNewCalculatedAccumulatedDistance(
        estTimestamp: Long,
        eventFlags: EnumSet<EventFlag>,
        calculatedAccumulatedDistance: BigDecimal
      ) {

        if (isOnlyNewData && deviceData["calculatedAccumulatedDistance"] == calculatedAccumulatedDistance) {
          return
        }

        deviceData["calculatedAccumulatedDistance"] = calculatedAccumulatedDistance

        val eventData = AntPlusPlugin.createEventDataMap(
          SpeedDistanceEvent.CalculatedAccumulatedDistance.toString(),
          estTimestamp,
          eventFlags,
          antDeviceNumber
        )

        eventData.putDouble(
          "calculatedAccumulatedDistance",
          calculatedAccumulatedDistance.toDouble()
        )

        antPlus.sendEvent(eventName, eventData)
      }
    })
  }

  private fun unsubscribeCalculatedSpeed() {
    bikeSpeedDistance!!.subscribeCalculatedSpeedEvent(null)
    deviceData.remove("calculatedSpeed")
  }

  private fun subscribeCalculatedSpeed(isOnlyNewData: Boolean) {
    bikeSpeedDistance!!.subscribeCalculatedSpeedEvent(object :
      AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver(wheelCircumference) {
      override fun onNewCalculatedSpeed(
        estTimestamp: Long,
        eventFlags: EnumSet<EventFlag>,
        calculatedSpeed: BigDecimal
      ) {
        if (isOnlyNewData && deviceData["calculatedSpeed"] == calculatedSpeed) {
          return
        }

        deviceData["calculatedSpeed"] = calculatedSpeed

        val eventData = AntPlusPlugin.createEventDataMap(
          SpeedDistanceEvent.CalculatedSpeed.toString(),
          estTimestamp,
          eventFlags,
          antDeviceNumber
        )

        eventData.putDouble("calculatedSpeed", calculatedSpeed.toDouble())

        antPlus.sendEvent(eventName, eventData)
      }
    })
  }

  private fun unsubscribeMotionAndSpeedData() {
    bikeSpeedDistance!!.subscribeMotionAndSpeedDataEvent(null)
    deviceData.remove("isBikeStopped")
  }

  private fun subscribeMotionAndSpeedData(isOnlyNewData: Boolean) {
    bikeSpeedDistance!!.subscribeMotionAndSpeedDataEvent { estTimestamp, eventFlags, isBikeStopped ->
      if (isOnlyNewData && deviceData["isBikeStopped"] == isBikeStopped) {
        return@subscribeMotionAndSpeedDataEvent
      }

      val eventData = AntPlusPlugin.createEventDataMap(
        SpeedDistanceEvent.MotionAndSpeedData.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )
      eventData.putBoolean("isBikeStopped", isBikeStopped)

      antPlus.sendEvent(eventName, eventData)
    }
  }

  private fun unsubscribeRawSpeedAndDistanceData() {
    bikeSpeedDistance!!.subscribeRawSpeedAndDistanceDataEvent(null)
    deviceData.remove("timestampOfLastEvent")
    deviceData.remove("cumulativeRevolutions")
  }

  private fun subscribeRawSpeedAndDistanceData(isOnlyNewData: Boolean) {
    bikeSpeedDistance!!.subscribeRawSpeedAndDistanceDataEvent { estTimestamp, eventFlags, timestampOfLastEvent, cumulativeRevolutions ->
      if (isOnlyNewData && deviceData["timestampOfLastEvent"] == timestampOfLastEvent && deviceData["cumulativeRevolutions"] == cumulativeRevolutions) {
        return@subscribeRawSpeedAndDistanceDataEvent
      }

      deviceData["timestampOfLastEvent"] = timestampOfLastEvent
      deviceData["cumulativeRevolutions"] = cumulativeRevolutions

      val eventData = AntPlusPlugin.createEventDataMap(
        SpeedDistanceEvent.RawSpeedAndDistanceData.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )
      eventData.putDouble("timestampOfLastEvent", timestampOfLastEvent.toDouble())
      eventData.putInt("cumulativeRevolutions", cumulativeRevolutions.toInt())

      antPlus.sendEvent(eventName, eventData)
    }
  }

  private fun unsubscribeCumulativeOperatingTime() {
    (bikeCadence ?: bikeSpeedDistance)!!.subscribeCumulativeOperatingTimeEvent(null)
    deviceData.remove("cumulativeOperatingTime")
  }

  private fun subscribeCumulativeOperatingTime(isOnlyNewData: Boolean) {

    fun subscribeCumulativeOperatingTimeEvent(
      estTimestamp: Long,
      eventFlags: EnumSet<EventFlag>,
      cumulativeOperatingTime: Int
    ) {
      if (isOnlyNewData && deviceData["cumulativeOperatingTime"] == cumulativeOperatingTime) {
        return
      }

      deviceData["cumulativeOperatingTime"] = cumulativeOperatingTime

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )
      eventData.putInt("cumulativeOperatingTime", cumulativeOperatingTime)

      antPlus.sendEvent(eventName, eventData)
    }

    (bikeCadence
      ?: bikeSpeedDistance)!!.subscribeCumulativeOperatingTimeEvent { estTimestamp, eventFlags, cumulativeOperatingTime ->
      subscribeCumulativeOperatingTimeEvent(
        estTimestamp,
        eventFlags,
        cumulativeOperatingTime.toInt()
      )
    }
  }

  private fun unsubscribeManufacturerAndSerial() {
    (bikeCadence ?: bikeSpeedDistance)!!.subscribeManufacturerAndSerialEvent(null)

    deviceData.remove("manufacturerID")
    deviceData.remove("serialNumber")
  }

  private fun subscribeManufacturerAndSerial(isOnlyNewData: Boolean) {
    fun subscribeManufacturerAndSerialEvent(
      estTimestamp: Long,
      eventFlags: EnumSet<EventFlag>,
      manufacturerID: Int,
      serialNumber: Int
    ) {
      if (isOnlyNewData && deviceData["manufacturerID"] == manufacturerID && deviceData["serialNumber"] == serialNumber) {
        return
      }

      deviceData["manufacturerID"] = manufacturerID
      deviceData["serialNumber"] = serialNumber

      val eventData = AntPlusPlugin.createEventDataMap(
        CadenceEvent.CalculatedCadence.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )

      eventData.putString("event", AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString())
      eventData.putInt("manufacturerID", manufacturerID)
      eventData.putInt("serialNumber", serialNumber)
      antPlus.sendEvent(eventName, eventData)
    }

    (bikeCadence
      ?: bikeSpeedDistance)!!.subscribeManufacturerAndSerialEvent { estTimestamp, eventFlags, manufacturerID, serialNumber ->
      subscribeManufacturerAndSerialEvent(
        estTimestamp,
        eventFlags,
        manufacturerID,
        serialNumber
      )
    }
  }

  private fun unsubscribeVersionAndModel() {
    (bikeCadence ?: bikeSpeedDistance)!!.subscribeVersionAndModelEvent(null)

    deviceData.remove("hardwareVersion")
    deviceData.remove("softwareVersion")
    deviceData.remove("modelNumber")
  }

  private fun subscribeVersionAndModel(isOnlyNewData: Boolean) {
    fun subscribeVersionAndModelEvent(
      estTimestamp: Long,
      eventFlags: EnumSet<EventFlag>,
      hardwareVersion: Int,
      softwareVersion: Int,
      modelNumber: Int
    ) {
      if (isOnlyNewData && deviceData["hardwareVersion"] == hardwareVersion && deviceData["softwareVersion"] == softwareVersion && deviceData["modelNumber"] == modelNumber) {
        return
      }

      deviceData["hardwareVersion"] = hardwareVersion
      deviceData["softwareVersion"] = softwareVersion
      deviceData["modelNumber"] = modelNumber

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusLegacyCommonEvent.VersionAndModel.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )

      eventData.putInt("hardwareVersion", hardwareVersion)
      eventData.putInt("softwareVersion", softwareVersion)
      eventData.putInt("modelNumber", modelNumber)

      antPlus.sendEvent(eventName, eventData)
    }

    (bikeCadence
      ?: bikeSpeedDistance)!!.subscribeVersionAndModelEvent { estTimestamp, eventFlags, hardwareVersion, softwareVersion, modelNumber ->
      subscribeVersionAndModelEvent(
        estTimestamp,
        eventFlags,
        hardwareVersion,
        softwareVersion,
        modelNumber
      )
    }
  }

  private fun unsubscribeRssi() {
    (bikeCadence ?: bikeSpeedDistance)!!.subscribeRssiEvent(null)
    deviceData.remove("rssi")
  }

  private fun subscribeRssi(isOnlyNewData: Boolean) {
    fun subscribeRssiEvent(estTimestamp: Long, eventFlags: EnumSet<EventFlag>, rssi: Int) {
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return
      }

      deviceData["rssi"] = rssi

      val eventData = AntPlusPlugin.createEventDataMap(
        AntPlusLegacyCommonEvent.Rssi.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )

      eventData.putInt("rssi", rssi)
      antPlus.sendEvent(eventName, eventData)
    }

    (bikeCadence ?: bikeSpeedDistance)!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      subscribeRssiEvent(estTimestamp, eventFlags, rssi)
    }
  }

  private fun unsubscribeBatteryStatus() {
    if (isSpdCadCombinedSensor) bikeCadence!!.subscribeRssiEvent(null)
    deviceData.remove("batteryVoltage")
    deviceData.remove("batteryStatus")
  }

  private fun subscribeBatteryStatus(isOnlyNewData: Boolean) {
    if (!bikeCadence!!.isSpeedAndCadenceCombinedSensor) {
      return
    }

    bikeCadence!!.subscribeBatteryStatusEvent { estTimestamp, eventFlags, batteryVoltage, batteryStatus ->
      if (isOnlyNewData && deviceData["batteryVoltage"] == batteryVoltage && deviceData["batteryStatus"] == batteryStatus) {
        return@subscribeBatteryStatusEvent
      }

      deviceData["batteryVoltage"] = batteryVoltage
      deviceData["batteryStatus"] = batteryStatus

      val eventData = AntPlusPlugin.createEventDataMap(
        SpeedAndCadenceEvent.BatteryStatus.toString(),
        estTimestamp,
        eventFlags,
        antDeviceNumber
      )

      eventData.putDouble("batteryVoltage", batteryVoltage.toDouble())
      eventData.putString("batteryStatus", batteryStatus.toString())

      antPlus.sendEvent(eventName, eventData)
    }
  }

  private var resultReceiver =
    IPluginAccessResultReceiver<AntPlusBikeCadencePcc> { result, resultCode, initialDeviceState ->
      val status = Arguments.createMap()
      status.putString("state", initialDeviceState.toString())
      status.putString("code", resultCode.toString())
      status.putBoolean("connected", false)
      status.putInt("antDeviceNumber", antDeviceNumber)

      if (resultCode === RequestAccessResult.SUCCESS) {
        bikeCadence = result
        status.putBoolean("connected", true)
        status.putString("name", result.deviceName)
      }

      connectPromise.resolve(status)
    }

  private var resultReceiverSpeedDistance =
    IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> { result, resultCode, initialDeviceState ->
      val status = Arguments.createMap()
      status.putString("state", initialDeviceState.toString())
      status.putString("code", resultCode.toString())
      status.putInt("antDeviceNumber", antDeviceNumber)

      if (resultCode === RequestAccessResult.SUCCESS) {
        bikeSpeedDistance = result
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

  enum class CadenceEvent(val event: String) {
    CalculatedCadence("CalculatedCadence"),
    MotionAndCadenceData("MotionAndCadenceData"),
    RawCadenceData("RawCadenceData"),
  }

  enum class SpeedDistanceEvent(val event: String) {
    CalculatedAccumulatedDistance("CalculatedAccumulatedDistance"),
    CalculatedSpeed("CalculatedSpeed"),
    MotionAndSpeedData("MotionAndSpeedData"),
    RawSpeedAndDistanceData("RawSpeedAndDistanceData"),
  }

  enum class SpeedAndCadenceEvent(val event: String) {
    BatteryStatus("BatteryStatus")
  }
}
