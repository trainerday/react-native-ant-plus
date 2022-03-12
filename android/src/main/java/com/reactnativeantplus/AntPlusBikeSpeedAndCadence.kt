package com.reactnativeantplus

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

class AntPlusBikeSpeedAndCadence(val context: ReactApplicationContext, val antPlus: AntPlusModule, val antDeviceNumber: Int, private val connectPromise: Promise) {
  private var bikeCadence: AntPlusBikeCadencePcc? = null
  private var bikeSpeedDistance: AntPlusBikeSpeedDistancePcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusBikeCadencePcc>? = null
  private var releaseHandleSpeedDistance: PccReleaseHandle<AntPlusBikeSpeedDistancePcc>? = null
  private val deviceData = HashMap<String, Any>()
  private var isSpdCadCombinedSensor = false
  private var wheelCircumference: BigDecimal? = BigDecimal(2.07)
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

  fun subscribe(events: ReadableArray, isOnlyNewData: Boolean) {
    events.toArrayList().forEach { event ->
      when (event as String) {
        AntPlusBikeCadenceEvent.CalculatedCadence.toString() -> subscribeCalculatedCadence(isOnlyNewData)
        AntPlusBikeCadenceEvent.MotionAndCadence.toString() -> subscribeMotionAndCadenceData(isOnlyNewData)
        AntPlusBikeCadenceEvent.RawCadence.toString() -> subscribeRawCadenceData(isOnlyNewData)

        AntPlusBikeSpeedDistanceEvent.CalculatedAccumulatedDistance.toString() -> subscribeCalculatedAccumulatedDistance(isOnlyNewData)
        AntPlusBikeSpeedDistanceEvent.CalculatedSpeed.toString() -> subscribeCalculatedSpeed(isOnlyNewData)
        AntPlusBikeSpeedDistanceEvent.MotionAndSpeedData.toString() -> subscribeMotionAndSpeedData(isOnlyNewData)
        AntPlusBikeSpeedDistanceEvent.RawSpeedAndDistanceData.toString() -> subscribeRawSpeedAndDistanceData(isOnlyNewData)

        AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString() -> subscribeCumulativeOperatingTime(isOnlyNewData)
        AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString() -> subscribeManufacturerAndSerial(isOnlyNewData)
        AntPlusLegacyCommonEvent.VersionAndModel.toString() -> subscribeVersionAndModel(isOnlyNewData)
        AntPlusLegacyCommonEvent.Rssi.toString() -> subscribeRssi(isOnlyNewData)

        AntPlusBikeSpeedAndCadenceEvent.BatteryStatus.toString() -> subscribeBatteryStatus(isOnlyNewData)
      }
    }
  }

  fun unsubscribe(events: ReadableArray) {
    events.toArrayList().forEach { event ->
      when (event) {
        AntPlusBikeCadenceEvent.CalculatedCadence.toString() -> unsubscribeCalculatedCadence()
        AntPlusBikeCadenceEvent.MotionAndCadence.toString() -> unsubscribeMotionAndCadenceData()
        AntPlusBikeCadenceEvent.RawCadence.toString() -> unsubscribeRawCadenceData()

        AntPlusBikeSpeedDistanceEvent.CalculatedAccumulatedDistance.toString() -> unsubscribeCalculatedAccumulatedDistance()
        AntPlusBikeSpeedDistanceEvent.CalculatedSpeed.toString() -> unsubscribeCalculatedSpeed()
        AntPlusBikeSpeedDistanceEvent.MotionAndSpeedData.toString() -> unsubscribeMotionAndSpeedData()
        AntPlusBikeSpeedDistanceEvent.RawSpeedAndDistanceData.toString() -> unsubscribeRawSpeedAndDistanceData()

        AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString() -> unsubscribeCumulativeOperatingTime()
        AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString() -> unsubscribeManufacturerAndSerial()
        AntPlusLegacyCommonEvent.VersionAndModel.toString() -> unsubscribeVersionAndModel()
        AntPlusLegacyCommonEvent.Rssi.toString() -> unsubscribeRssi()

        AntPlusBikeSpeedAndCadenceEvent.BatteryStatus.toString() -> unsubscribeBatteryStatus()
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

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusBikeCadenceEvent.CalculatedCadence.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("calculatedCadence", calculatedCadence.toInt())
      antPlus.sendEvent(eventName, eventData)
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

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusBikeCadenceEvent.MotionAndCadence.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putBoolean("isPedallingStopped", isPedallingStopped)
      antPlus.sendEvent(eventName, eventData)
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

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusBikeCadenceEvent.RawCadence.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
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

        val eventData = Arguments.createMap()

        eventData.putString("event", AntPlusBikeSpeedDistanceEvent.CalculatedAccumulatedDistance.toString()
        )
        eventData.putInt("estTimestamp", estTimestamp.toInt())
        eventData.putString("eventFlags", eventFlags.toString())

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
        eventFlags: EnumSet<EventFlag?>,
        calculatedSpeed: BigDecimal
      ) {
        if (isOnlyNewData && deviceData["calculatedSpeed"] == calculatedSpeed) {
          return
        }

        deviceData["calculatedSpeed"] = calculatedSpeed

        val eventData = Arguments.createMap()
        eventData.putString("event", AntPlusBikeSpeedDistanceEvent.CalculatedSpeed.toString())
        eventData.putInt("estTimestamp", estTimestamp.toInt())
        eventData.putString("eventFlags", eventFlags.toString())

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

      val eventData = Arguments.createMap()
      eventData.putString(
        "event",
        AntPlusBikeSpeedDistanceEvent.MotionAndSpeedData.toString()
      )
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

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

      val eventData = Arguments.createMap()
      eventData.putString(
        "event",
        AntPlusBikeSpeedDistanceEvent.RawSpeedAndDistanceData.toString()
      )
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putDouble("timestampOfLastEvent", timestampOfLastEvent.toDouble())
      eventData.putInt("cumulativeRevolutions", cumulativeRevolutions.toInt())

      antPlus.sendEvent(eventName, eventData)
    }
  }

  private fun unsubscribeCumulativeOperatingTime() {
    if (isSpdCadCombinedSensor) bikeCadence!!.subscribeCumulativeOperatingTimeEvent(null)
    else bikeSpeedDistance!!.subscribeCumulativeOperatingTimeEvent(null)
    deviceData.remove("cumulativeOperatingTime")
  }

  private fun subscribeCumulativeOperatingTime(isOnlyNewData: Boolean) {

    fun subscribeCumulativeOperatingTimeEvent(estTimestamp: Int, eventFlags: String, cumulativeOperatingTime: Int) {
      if (isOnlyNewData && deviceData["cumulativeOperatingTime"] == cumulativeOperatingTime) {
        return
      }

      deviceData["cumulativeOperatingTime"] = cumulativeOperatingTime

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusLegacyCommonEvent.CumulativeOperatingTime.toString())
      eventData.putString("eventFlags", eventFlags)
      eventData.putInt("estTimestamp", estTimestamp)
      eventData.putInt("cumulativeOperatingTime", cumulativeOperatingTime)
      antPlus.sendEvent(eventName, eventData)
    }

    if (isSpdCadCombinedSensor || bikeCadence != null) {
      bikeCadence!!.subscribeCumulativeOperatingTimeEvent { estTimestamp, eventFlags, cumulativeOperatingTime ->
        subscribeCumulativeOperatingTimeEvent(estTimestamp.toInt(), eventFlags.toString(), cumulativeOperatingTime.toInt())
      }
    } else {
      bikeSpeedDistance!!.subscribeCumulativeOperatingTimeEvent { estTimestamp, eventFlags, cumulativeOperatingTime ->
        subscribeCumulativeOperatingTimeEvent(estTimestamp.toInt(), eventFlags.toString(), cumulativeOperatingTime.toInt())
      }
    }
  }

  private fun unsubscribeManufacturerAndSerial() {
    if (isSpdCadCombinedSensor) bikeCadence!!.subscribeManufacturerAndSerialEvent(null)
    else bikeSpeedDistance!!.subscribeManufacturerAndSerialEvent(null)

    deviceData.remove("manufacturerID")
    deviceData.remove("serialNumber")
  }

  private fun subscribeManufacturerAndSerial(isOnlyNewData: Boolean) {
    fun subscribeManufacturerAndSerialEvent(estTimestamp: Int, eventFlags: String, manufacturerID: Int, serialNumber: Int) {
      if (isOnlyNewData && deviceData["manufacturerID"] == manufacturerID && deviceData["serialNumber"] == serialNumber) {
        return
      }

      deviceData["manufacturerID"] = manufacturerID
      deviceData["serialNumber"] = serialNumber

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusLegacyCommonEvent.ManufacturerAndSerial.toString())
      eventData.putString("eventFlags", eventFlags)
      eventData.putInt("estTimestamp", estTimestamp)
      eventData.putInt("manufacturerID", manufacturerID)
      eventData.putInt("serialNumber", serialNumber)
      antPlus.sendEvent(eventName, eventData)
    }

    if (isSpdCadCombinedSensor || bikeCadence != null) {
      bikeCadence!!.subscribeManufacturerAndSerialEvent { estTimestamp, eventFlags, manufacturerID, serialNumber ->
        subscribeManufacturerAndSerialEvent(estTimestamp.toInt(), eventFlags.toString(), manufacturerID, serialNumber)
      }
    } else {
      bikeSpeedDistance!!.subscribeManufacturerAndSerialEvent { estTimestamp, eventFlags, manufacturerID, serialNumber ->
        subscribeManufacturerAndSerialEvent(estTimestamp.toInt(), eventFlags.toString(), manufacturerID, serialNumber)
      }
    }
  }

  private fun unsubscribeVersionAndModel() {
    if (isSpdCadCombinedSensor) bikeCadence!!.subscribeVersionAndModelEvent(null)
    else bikeSpeedDistance!!.subscribeVersionAndModelEvent(null)

    deviceData.remove("hardwareVersion")
    deviceData.remove("softwareVersion")
    deviceData.remove("modelNumber")
  }

  private fun subscribeVersionAndModel(isOnlyNewData: Boolean) {
    fun subscribeVersionAndModelEvent(estTimestamp: Int, eventFlags: String, hardwareVersion: Int, softwareVersion: Int, modelNumber: Int) {
      if (isOnlyNewData && deviceData["hardwareVersion"] == hardwareVersion && deviceData["softwareVersion"] == softwareVersion && deviceData["modelNumber"] == modelNumber) {
        return
      }

      deviceData["hardwareVersion"] = hardwareVersion
      deviceData["softwareVersion"] = softwareVersion
      deviceData["modelNumber"] = modelNumber

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusLegacyCommonEvent.VersionAndModel.toString())
      eventData.putString("eventFlags", eventFlags)
      eventData.putInt("estTimestamp", estTimestamp)
      eventData.putInt("hardwareVersion", hardwareVersion)
      eventData.putInt("softwareVersion", softwareVersion)
      eventData.putInt("modelNumber", modelNumber)
      antPlus.sendEvent(eventName, eventData)
    }

    if (isSpdCadCombinedSensor || bikeCadence != null) {
      bikeCadence!!.subscribeVersionAndModelEvent { estTimestamp, eventFlags, hardwareVersion, softwareVersion, modelNumber ->
        subscribeVersionAndModelEvent(estTimestamp.toInt(), eventFlags.toString(), hardwareVersion, softwareVersion, modelNumber)
      }
    } else {
      bikeSpeedDistance!!.subscribeVersionAndModelEvent { estTimestamp, eventFlags, hardwareVersion, softwareVersion, modelNumber ->
        subscribeVersionAndModelEvent(estTimestamp.toInt(), eventFlags.toString(), hardwareVersion, softwareVersion, modelNumber)
      }
    }
  }

  private fun unsubscribeRssi() {
    if (isSpdCadCombinedSensor) bikeCadence!!.subscribeRssiEvent(null)
    else bikeSpeedDistance!!.subscribeRssiEvent(null)

    deviceData.remove("rssi")
  }

  private fun subscribeRssi(isOnlyNewData: Boolean) {
    fun subscribeRssiEvent(estTimestamp: Int, eventFlags: String, rssi: Int) {
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return
      }

      deviceData["rssi"] = rssi

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusLegacyCommonEvent.Rssi.toString())
      eventData.putString("eventFlags", eventFlags)
      eventData.putInt("estTimestamp", estTimestamp)
      eventData.putInt("rssi", rssi)
      antPlus.sendEvent(eventName, eventData)
    }

    if (isSpdCadCombinedSensor || bikeCadence != null) {
      bikeCadence!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
        subscribeRssiEvent(estTimestamp.toInt(), eventFlags.toString(), rssi)
      }
    } else {
      bikeSpeedDistance!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
        subscribeRssiEvent(estTimestamp.toInt(), eventFlags.toString(), rssi)
      }
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

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusLegacyCommonEvent.Rssi.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())

      eventData.putDouble("batteryVoltage", batteryVoltage.toDouble())
      eventData.putString("batteryStatus", batteryStatus.toString())

      antPlus.sendEvent(eventName, eventData)
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

  protected var resultReceiverSpeedDistance =
    IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> { result, resultCode, initialDeviceState ->
      val status = Arguments.createMap()
      status.putString("name", result.deviceName)
      status.putString("state", initialDeviceState.toString())
      status.putString("code", resultCode.toString())

      if (resultCode === RequestAccessResult.SUCCESS) {
        bikeSpeedDistance = result
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
