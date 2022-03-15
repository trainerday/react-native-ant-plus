package com.reactnativeantplus.devices

/**
 * Implementing the AntPlusBikePowerPcc
 * https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.html
 */

import android.util.Log
import com.reactnativeantplus.AntPlusModule
import com.reactnativeantplus.AntPlusPlugin
import com.reactnativeantplus.events.AntPlusCommonEvent
import com.reactnativeantplus.events.AntPlusEvent
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import java.math.BigDecimal
import java.util.*


class AntPlusBikePower(val context: ReactApplicationContext, val antPlus: AntPlusModule, val antDeviceNumber: Int, private val connectPromise: Promise) {
  private var bikePower: AntPlusBikePowerPcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusBikePowerPcc>? = null
  private val deviceData = HashMap<String, Any>()

  private var wheelCircumference = BigDecimal("2.07")

  fun init() {
    releaseHandle = requestAccess(
      context,
      antDeviceNumber,
      0,
      resultReceiver,
      AntPlusPlugin.stateReceiver(antPlus, antDeviceNumber)
    )
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
      when (event) {
        Event.AutoZeroStatus.toString() -> subscribeAutoZeroStatus(isOnlyNewData)
        Event.CalculatedCrankCadence.toString() -> subscribeCalculatedCrankCadence(isOnlyNewData)
        Event.CalculatedPower.toString() -> subscribeCalculatedPower(isOnlyNewData)
        Event.CalculatedTorque.toString() -> subscribeCalculatedTorque(isOnlyNewData)
        Event.CalculatedWheelDistance.toString() -> subscribeCalculatedWheelDistance(isOnlyNewData)
        Event.CalculatedWheelSpeed.toString() -> subscribeCalculatedWheelSpeed(isOnlyNewData)
        Event.CalibrationMessage.toString() -> subscribeCalibrationMessage(isOnlyNewData)
        Event.CrankParameters.toString() -> subscribeCrankParameters(isOnlyNewData)
        Event.InstantaneousCadence.toString() -> subscribeInstantaneousCadence(isOnlyNewData)
        Event.MeasurementOutputData.toString() -> subscribeMeasurementOutputData(isOnlyNewData)
        Event.PedalPowerBalance.toString() -> subscribePedalPowerBalance(isOnlyNewData)
        Event.PedalSmoothness.toString() -> subscribePedalSmoothness(isOnlyNewData)
        Event.RawCrankTorqueData.toString() -> subscribeRawCrankTorqueData(isOnlyNewData)
        Event.RawCtfData.toString() -> subscribeRawCtfData(isOnlyNewData)
        Event.RawPowerOnlyData.toString() -> subscribeRawPowerOnlyData(isOnlyNewData)
        Event.RawWheelTorqueData.toString() -> subscribeRawWheelTorqueData(isOnlyNewData)
        Event.TorqueEffectiveness.toString() -> subscribeTorqueEffectiveness(isOnlyNewData)

        AntPlusCommonEvent.BatteryStatus.toString() -> subscribeBatteryStatus(isOnlyNewData)
        AntPlusCommonEvent.ManufacturerIdentification.toString() -> subscribeManufacturerIdentification(isOnlyNewData)
        AntPlusCommonEvent.ManufacturerSpecific.toString() -> subscribeManufacturerSpecificData(isOnlyNewData)
        AntPlusCommonEvent.ProductInformation.toString() -> subscribeProductInformation(isOnlyNewData)
        AntPlusCommonEvent.Rssi.toString() -> subscribeRssi(isOnlyNewData)
      }
    }
  }

  fun unsubscribe(events: ReadableArray) {
    events.toArrayList().forEach { event ->
      when (event) {
        Event.AutoZeroStatus.toString() -> unsubscribeAutoZeroStatus()
        Event.CalculatedCrankCadence.toString() -> unsubscribeCalculatedCrankCadence()
        Event.CalculatedPower.toString() -> unsubscribeCalculatedPower()
        Event.CalculatedTorque.toString() -> unsubscribeCalculatedTorque()
        Event.CalculatedWheelDistance.toString() -> unsubscribeCalculatedWheelDistance()
        Event.CalculatedWheelSpeed.toString() -> unsubscribeCalculatedWheelSpeed()
        Event.CalibrationMessage.toString() -> unsubscribeCalibrationMessage()
        Event.CrankParameters.toString() -> unsubscribeCrankParameters()
        Event.InstantaneousCadence.toString() -> unsubscribeInstantaneousCadence()
        Event.MeasurementOutputData.toString() -> unsubscribeMeasurementOutputData()
        Event.PedalPowerBalance.toString() -> unsubscribePedalPowerBalance()
        Event.PedalSmoothness.toString() -> unsubscribePedalSmoothness()
        Event.RawCrankTorqueData.toString() -> unsubscribeRawCrankTorqueData()
        Event.RawCtfData.toString() -> unsubscribeRawCtfData()
        Event.RawPowerOnlyData.toString() -> unsubscribeRawPowerOnlyData()
        Event.RawWheelTorqueData.toString() -> unsubscribeRawWheelTorqueData()
        Event.TorqueEffectiveness.toString() -> unsubscribeTorqueEffectiveness()

        AntPlusCommonEvent.BatteryStatus.toString() -> unsubscribeBatteryStatus()
        AntPlusCommonEvent.ManufacturerIdentification.toString() -> unsubscribeManufacturerIdentification()
        AntPlusCommonEvent.ManufacturerSpecific.toString() -> unsubscribeManufacturerSpecificData()
        AntPlusCommonEvent.ProductInformation.toString() -> unsubscribeProductInformation()
        AntPlusCommonEvent.Rssi.toString() -> unsubscribeRssi()
      }
    }
  }

  private fun unsubscribeAutoZeroStatus() {
    bikePower!!.subscribeAutoZeroStatusEvent(null)
    deviceData.remove("autoZeroStatus")
  }

  private fun subscribeAutoZeroStatus(isOnlyNewData: Boolean) {
    bikePower!!.subscribeAutoZeroStatusEvent { estTimestamp, eventFlags, autoZeroStatus ->
      if (isOnlyNewData && deviceData["autoZeroStatus"] == autoZeroStatus) {
        return@subscribeAutoZeroStatusEvent
      }

      deviceData["autoZeroStatus"] = autoZeroStatus

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.AutoZeroStatus.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putString("autoZeroStatus", autoZeroStatus.toString())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeCalculatedCrankCadence() {
    bikePower!!.subscribeCalculatedCrankCadenceEvent(null)
    deviceData.remove("dataSource")
    deviceData.remove("calculatedCrankCadence")

  }
  private fun subscribeCalculatedCrankCadence(isOnlyNewData: Boolean) {
    bikePower!!.subscribeCalculatedCrankCadenceEvent { estTimestamp, eventFlags, dataSource, calculatedCrankCadence ->
      if (isOnlyNewData && deviceData["dataSource"] == dataSource && deviceData["calculatedCrankCadence"] == calculatedCrankCadence) {
        return@subscribeCalculatedCrankCadenceEvent
      }

      deviceData["dataSource"] = dataSource
      deviceData["calculatedCrankCadence"] = calculatedCrankCadence

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.CalculatedCrankCadence.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putString("dataSource", dataSource.toString())
      eventData.putDouble("calculatedCrankCadence", calculatedCrankCadence.toDouble())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeCalculatedPower() {
    bikePower!!.subscribeCalculatedPowerEvent(null)
    deviceData.remove("dataSource")
    deviceData.remove("calculatedPower")
  }

  private fun subscribeCalculatedPower(isOnlyNewData: Boolean) {
    bikePower!!.subscribeCalculatedPowerEvent { estTimestamp, eventFlags, dataSource, calculatedPower ->
      if (isOnlyNewData && deviceData["dataSource"] == dataSource && deviceData["calculatedPower"] == calculatedPower) {
        return@subscribeCalculatedPowerEvent
      }

      deviceData["dataSource"] = dataSource
      deviceData["calculatedPower"] = calculatedPower

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.CalculatedPower.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putString("dataSource", dataSource.toString())
      eventData.putDouble("calculatedPower", calculatedPower.toDouble())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeCalculatedTorque() {
    bikePower!!.subscribeCalculatedTorqueEvent(null)
    deviceData.remove("dataSource")
    deviceData.remove("calculatedTorque")

  }

  private fun subscribeCalculatedTorque(isOnlyNewData: Boolean) {
    bikePower!!.subscribeCalculatedTorqueEvent { estTimestamp, eventFlags, dataSource, calculatedTorque ->
      if (isOnlyNewData && deviceData["dataSource"] == dataSource && deviceData["calculatedTorque"] == calculatedTorque) {
        return@subscribeCalculatedTorqueEvent
      }
      deviceData["dataSource"] = dataSource
      deviceData["calculatedTorque"] = calculatedTorque

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.CalculatedTorque.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putString("dataSource", dataSource.toString())
      eventData.putDouble("calculatedTorque", calculatedTorque.toDouble())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeCalculatedWheelDistance() {
    bikePower!!.subscribeCalculatedWheelDistanceEvent(null)
    deviceData.remove("dataSource")
    deviceData.remove("calculatedWheelDistance")
  }

  private fun subscribeCalculatedWheelDistance(isOnlyNewData: Boolean) {
    bikePower!!.subscribeCalculatedWheelDistanceEvent(object : CalculatedWheelDistanceReceiver(
      wheelCircumference
    ) {
      override fun onNewCalculatedWheelDistance(
        estTimestamp: Long, eventFlags: EnumSet<EventFlag?>?,
        dataSource: DataSource,
        calculatedWheelDistance: BigDecimal
      ) {
        runOnUiThread {
          if (isOnlyNewData && deviceData["dataSource"] == dataSource && deviceData["calculatedWheelDistance"] == calculatedWheelDistance) {
            return@runOnUiThread
          }

          deviceData["dataSource"] = dataSource
          deviceData["calculatedWheelDistance"] = calculatedWheelDistance

          val eventData = Arguments.createMap()

          eventData.putString("event", Event.CalculatedWheelDistance.toString())
          eventData.putInt("estTimestamp", estTimestamp.toInt())
          eventData.putString("eventFlags", eventFlags.toString())

          eventData.putString("dataSource", dataSource.toString())
          eventData.putDouble("calculatedWheelDistance", calculatedWheelDistance.toDouble())

          antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
        }
      }
    })
  }

  private fun unsubscribeCalculatedWheelSpeed() {
    bikePower!!.subscribeCalculatedWheelSpeedEvent(null)
    deviceData.remove("dataSource")
    deviceData.remove("calculatedWheelSpeed")

  }

  private fun subscribeCalculatedWheelSpeed(isOnlyNewData: Boolean) {
    bikePower!!.subscribeCalculatedWheelSpeedEvent(object : CalculatedWheelSpeedReceiver(wheelCircumference) {
      override fun onNewCalculatedWheelSpeed(
        estTimestamp: Long,
        eventFlags: EnumSet<EventFlag>,
        dataSource: DataSource,
        calculatedWheelSpeed: BigDecimal
      ) {
        runOnUiThread {
          if (isOnlyNewData && deviceData["dataSource"] == dataSource && deviceData["calculatedWheelSpeed"] == calculatedWheelSpeed) {
            return@runOnUiThread
          }
          deviceData["dataSource"] = dataSource
          deviceData["calculatedWheelSpeed"] = calculatedWheelSpeed

          val eventData = Arguments.createMap()

          eventData.putString("event", Event.CalculatedWheelSpeed.toString())
          eventData.putInt("estTimestamp", estTimestamp.toInt())
          eventData.putString("eventFlags", eventFlags.toString())

          eventData.putString("dataSource", dataSource.toString())
          eventData.putDouble("calculatedWheelSpeed", calculatedWheelSpeed.toDouble())

          antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
        }
      }
    })
  }

  private fun unsubscribeCalibrationMessage() {
    bikePower!!.subscribeCalibrationMessageEvent(null)
    deviceData.remove("calibrationMessage")

  }
  private fun subscribeCalibrationMessage(isOnlyNewData: Boolean) {
    bikePower!!.subscribeCalibrationMessageEvent { estTimestamp, eventFlags, calibrationMessage ->
      if (isOnlyNewData && deviceData["calibrationMessage"] == calibrationMessage) {
        return@subscribeCalibrationMessageEvent
      }
      deviceData["calibrationMessage"] = calibrationMessage

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.CalibrationMessage.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      val message = Arguments.createMap()

      message.putString("calibrationId", calibrationMessage.calibrationId.toString())
      message.putInt("calibrationData", calibrationMessage.calibrationData)
      message.putInt("ctfOffset", calibrationMessage.ctfOffset)
      try {
        message.putArray("manufacturerSpecificData", AntPlusModule.bytesToWritableArray(calibrationMessage.manufacturerSpecificData))
      } catch (throwable: Throwable) {
        Log.e("ManufacturerSpecific", "bytesToWritableArray", throwable)
      }

      eventData.putMap("calibrationMessage", message)

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeCrankParameters() {
    bikePower!!.subscribeCrankParametersEvent(null)
    deviceData.remove("crankParameters")

  }
  private fun subscribeCrankParameters(isOnlyNewData: Boolean) {
    bikePower!!.subscribeCrankParametersEvent { estTimestamp, eventFlags, crankParameters ->
      if (isOnlyNewData && deviceData["crankParameters"] == crankParameters) {
        return@subscribeCrankParametersEvent
      }

      deviceData["crankParameters"] = crankParameters

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.CrankParameters.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      val parameters = Arguments.createMap()

      parameters.putDouble("fullCrankLength", crankParameters.fullCrankLength.toDouble())
      parameters.putString("crankLengthStatus", crankParameters.crankLengthStatus.toString())
      parameters.putString("sensorSoftwareMismatchStatus", crankParameters.sensorSoftwareMismatchStatus.toString())
      parameters.putString("sensorAvailabilityStatus", crankParameters.sensorAvailabilityStatus.toString())
      parameters.putString("customCalibrationStatus", crankParameters.customCalibrationStatus.toString())
      parameters.putBoolean("isAutoCrankLengthSupported", crankParameters.isAutoCrankLengthSupported)

      eventData.putMap("crankParameters", parameters)

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeInstantaneousCadence() {
    bikePower!!.subscribeInstantaneousCadenceEvent(null)
    deviceData.remove("dataSource")
    deviceData.remove("instantaneousCadence")
  }

  private fun subscribeInstantaneousCadence(isOnlyNewData: Boolean) {
    bikePower!!.subscribeInstantaneousCadenceEvent { estTimestamp, eventFlags, dataSource, instantaneousCadence ->
      if (isOnlyNewData && deviceData["dataSource"] == dataSource && deviceData["instantaneousCadence"] == instantaneousCadence) {
        return@subscribeInstantaneousCadenceEvent
      }

      deviceData["dataSource"] = dataSource
      deviceData["instantaneousCadence"] = instantaneousCadence

      val eventData = Arguments.createMap()

      eventData.putString("event", "InstantaneousCadence")
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putString("dataSource", dataSource.toString())
      eventData.putInt("instantaneousCadence", instantaneousCadence)
      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeMeasurementOutputData() {
    bikePower!!.subscribeMeasurementOutputDataEvent(null)
    deviceData.remove("numOfDataTypes")
    deviceData.remove("dataType")
    deviceData.remove("timeStamp")
    deviceData.remove("measurementValue")

  }
  private fun subscribeMeasurementOutputData(isOnlyNewData: Boolean) {
    bikePower!!.subscribeMeasurementOutputDataEvent { estTimestamp, eventFlags, numOfDataTypes, dataType, timeStamp, measurementValue ->
      if (isOnlyNewData  && deviceData["numOfDataTypes"] == numOfDataTypes && deviceData["dataType"] == dataType && deviceData["timeStamp"] == timeStamp && deviceData["measurementValue"] == measurementValue) {
        return@subscribeMeasurementOutputDataEvent
      }

      deviceData["numOfDataTypes"] = numOfDataTypes
      deviceData["dataType"] = dataType
      deviceData["timeStamp"] = timeStamp
      deviceData["measurementValue"] = measurementValue

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.MeasurementOutputData.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putInt("numOfDataTypes", numOfDataTypes)
      eventData.putInt("dataType", dataType)
      eventData.putDouble("timeStamp", timeStamp.toDouble())
      eventData.putDouble("measurementValue", measurementValue.toDouble())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribePedalPowerBalance() {
    bikePower!!.subscribePedalPowerBalanceEvent(null)
    deviceData.remove("rightPedalIndicator")
    deviceData.remove("pedalPowerPercentage")

  }
  private fun subscribePedalPowerBalance(isOnlyNewData: Boolean) {
    bikePower!!.subscribePedalPowerBalanceEvent { estTimestamp, eventFlags, rightPedalIndicator, pedalPowerPercentage ->
      if (isOnlyNewData && deviceData["rightPedalIndicator"] == rightPedalIndicator && deviceData["pedalPowerPercentage"] == pedalPowerPercentage) {
        return@subscribePedalPowerBalanceEvent
      }
      deviceData["rightPedalIndicator"] = rightPedalIndicator
      deviceData["pedalPowerPercentage"] = pedalPowerPercentage

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.PedalPowerBalance.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putBoolean("rightPedalIndicator", rightPedalIndicator)
      eventData.putInt("pedalPowerPercentage", pedalPowerPercentage)

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribePedalSmoothness() {
    bikePower!!.subscribePedalSmoothnessEvent(null)
    deviceData.remove("powerOnlyUpdateEventCount")
    deviceData.remove("separatePedalSmoothnessSupport")
    deviceData.remove("leftOrCombinedPedalSmoothness")
    deviceData.remove("rightPedalSmoothness")
  }

  private fun subscribePedalSmoothness(isOnlyNewData: Boolean) {
    bikePower!!.subscribePedalSmoothnessEvent { estTimestamp, eventFlags, powerOnlyUpdateEventCount, separatePedalSmoothnessSupport, leftOrCombinedPedalSmoothness, rightPedalSmoothness ->
      if (
        isOnlyNewData &&
        deviceData["powerOnlyUpdateEventCount"] == powerOnlyUpdateEventCount &&
        deviceData["separatePedalSmoothnessSupport"] == separatePedalSmoothnessSupport &&
        deviceData["leftOrCombinedPedalSmoothness"] == leftOrCombinedPedalSmoothness &&
        deviceData["powerOnlyUpdateEventCount"] == powerOnlyUpdateEventCount
      ) {
        return@subscribePedalSmoothnessEvent
      }
      deviceData["powerOnlyUpdateEventCount"] = powerOnlyUpdateEventCount
      deviceData["separatePedalSmoothnessSupport"] = separatePedalSmoothnessSupport
      deviceData["leftOrCombinedPedalSmoothness"] = leftOrCombinedPedalSmoothness
      deviceData["rightPedalSmoothness"] = rightPedalSmoothness

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.PedalSmoothness.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putInt("powerOnlyUpdateEventCount", powerOnlyUpdateEventCount.toInt())
      eventData.putBoolean("separatePedalSmoothnessSupport", separatePedalSmoothnessSupport)
      eventData.putDouble("leftOrCombinedPedalSmoothness", powerOnlyUpdateEventCount.toDouble())
      eventData.putDouble("rightPedalSmoothness", rightPedalSmoothness.toDouble())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeRawCrankTorqueData() {
    bikePower!!.subscribeRawCrankTorqueDataEvent(null)
    deviceData.remove("crankTorqueUpdateEventCount")
    deviceData.remove("accumulatedCrankTicks")
    deviceData.remove("accumulatedCrankPeriod")
    deviceData.remove("accumulatedCrankTorque")
  }

  private fun subscribeRawCrankTorqueData(isOnlyNewData: Boolean) {
    bikePower!!.subscribeRawCrankTorqueDataEvent {
      estTimestamp,
      eventFlags,
      crankTorqueUpdateEventCount,
      accumulatedCrankTicks,
      accumulatedCrankPeriod,
      accumulatedCrankTorque ->
      if (
        isOnlyNewData &&
        deviceData["crankTorqueUpdateEventCount"] == crankTorqueUpdateEventCount &&
        deviceData["accumulatedCrankTicks"] == accumulatedCrankTicks &&
        deviceData["accumulatedCrankPeriod"] == accumulatedCrankPeriod &&
        deviceData["accumulatedCrankTorque"] == accumulatedCrankTorque
      ) {
        return@subscribeRawCrankTorqueDataEvent
      }
      deviceData["crankTorqueUpdateEventCount"] = crankTorqueUpdateEventCount
      deviceData["accumulatedCrankTicks"] = accumulatedCrankTicks
      deviceData["accumulatedCrankPeriod"] = accumulatedCrankPeriod
      deviceData["accumulatedCrankTorque"] = accumulatedCrankTorque

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.RawCrankTorqueData.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putInt("crankTorqueUpdateEventCount", crankTorqueUpdateEventCount.toInt())
      eventData.putInt("accumulatedCrankTicks", accumulatedCrankTicks.toInt())
      eventData.putDouble("accumulatedCrankPeriod", accumulatedCrankPeriod.toDouble())
      eventData.putDouble("accumulatedCrankTorque", accumulatedCrankTorque.toDouble())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeRawCtfData() {
    bikePower!!.subscribeRawCtfDataEvent(null)
    deviceData.remove("ctfUpdateEventCount")
    deviceData.remove("instantaneousSlope")
    deviceData.remove("accumulatedTimeStamp")
    deviceData.remove("accumulatedTorqueTicksStamp")
  }

  private fun subscribeRawCtfData(isOnlyNewData: Boolean) {
    bikePower!!.subscribeRawCtfDataEvent { estTimestamp, eventFlags,
                                           ctfUpdateEventCount,
                                           instantaneousSlope,
                                           accumulatedTimeStamp,
                                           accumulatedTorqueTicksStamp
      ->
      if (isOnlyNewData &&
        deviceData["ctfUpdateEventCount"] == ctfUpdateEventCount &&
        deviceData["instantaneousSlope"] == instantaneousSlope &&
        deviceData["accumulatedTimeStamp"] == accumulatedTimeStamp &&
        deviceData["accumulatedTorqueTicksStamp"] == accumulatedTorqueTicksStamp
      ) {
        return@subscribeRawCtfDataEvent
      }
      deviceData["ctfUpdateEventCount"] = ctfUpdateEventCount
      deviceData["instantaneousSlope"] = instantaneousSlope
      deviceData["accumulatedTimeStamp"] = accumulatedTimeStamp
      deviceData["accumulatedTorqueTicksStamp"] = accumulatedTorqueTicksStamp

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.RawCtfData.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putInt("ctfUpdateEventCount", ctfUpdateEventCount.toInt())
      eventData.putDouble("instantaneousSlope", instantaneousSlope.toDouble())
      eventData.putDouble("accumulatedTimeStamp", accumulatedTimeStamp.toDouble())
      eventData.putInt("accumulatedTorqueTicksStamp", accumulatedTorqueTicksStamp.toInt())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeRawPowerOnlyData() {
    bikePower!!.subscribeRawPowerOnlyDataEvent(null)
    deviceData.remove("powerOnlyUpdateEventCount")
    deviceData.remove("instantaneousPower")
    deviceData.remove("accumulatedPower")
  }

  private fun subscribeRawPowerOnlyData(isOnlyNewData: Boolean) {
    bikePower!!.subscribeRawPowerOnlyDataEvent { estTimestamp, eventFlags, powerOnlyUpdateEventCount, instantaneousPower, accumulatedPower ->
      if (isOnlyNewData && deviceData["powerOnlyUpdateEventCount"] == powerOnlyUpdateEventCount && deviceData["instantaneousPower"] == instantaneousPower && deviceData["accumulatedPower"] == accumulatedPower) {
        return@subscribeRawPowerOnlyDataEvent
      }

      deviceData["powerOnlyUpdateEventCount"] = powerOnlyUpdateEventCount
      deviceData["instantaneousPower"] = instantaneousPower
      deviceData["accumulatedPower"] = accumulatedPower

      val eventData = Arguments.createMap()
      eventData.putString("event", Event.RawPowerOnlyData.toString())

      eventData.putString("event", Event.RawWheelTorqueData.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putInt("powerOnlyUpdateEventCount", powerOnlyUpdateEventCount.toInt())
      eventData.putInt("instantaneousPower", instantaneousPower)
      eventData.putInt("accumulatedPower", accumulatedPower.toInt())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeRawWheelTorqueData() {
    bikePower!!.subscribeRawWheelTorqueDataEvent(null)
    deviceData.remove("wheelTorqueUpdateEventCount")
    deviceData.remove("accumulatedWheelTicks")
    deviceData.remove("accumulatedWheelPeriod")
    deviceData.remove("accumulatedWheelTorque")

  }
  private fun subscribeRawWheelTorqueData(isOnlyNewData: Boolean) {
    bikePower!!.subscribeRawWheelTorqueDataEvent { estTimestamp, eventFlags,
                                                   wheelTorqueUpdateEventCount,
                                                   accumulatedWheelTicks,
                                                   accumulatedWheelPeriod,
                                                   accumulatedWheelTorque
      ->
      if (
        isOnlyNewData &&
        deviceData["wheelTorqueUpdateEventCount"] == wheelTorqueUpdateEventCount &&
        deviceData["accumulatedWheelTicks"] == accumulatedWheelTicks &&
        deviceData["accumulatedWheelPeriod"] == accumulatedWheelPeriod &&
        deviceData["accumulatedWheelTorque"] == accumulatedWheelTorque
      ) {
        return@subscribeRawWheelTorqueDataEvent
      }
      deviceData["wheelTorqueUpdateEventCount"] = wheelTorqueUpdateEventCount
      deviceData["accumulatedWheelTicks"] = accumulatedWheelTicks
      deviceData["accumulatedWheelPeriod"] = accumulatedWheelPeriod
      deviceData["accumulatedWheelTorque"] = accumulatedWheelTorque

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.RawWheelTorqueData.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putInt("wheelTorqueUpdateEventCount", wheelTorqueUpdateEventCount.toInt())
      eventData.putInt("accumulatedWheelTicks", accumulatedWheelTicks.toInt())
      eventData.putDouble("accumulatedWheelPeriod", accumulatedWheelPeriod.toDouble())
      eventData.putDouble("accumulatedWheelTorque", accumulatedWheelTorque.toDouble())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeTorqueEffectiveness() {
    bikePower!!.subscribeTorqueEffectivenessEvent(null)
    deviceData.remove("powerOnlyUpdateEventCount")
    deviceData.remove("leftTorqueEffectiveness")
    deviceData.remove("rightTorqueEffectiveness")
  }

  private fun subscribeTorqueEffectiveness(isOnlyNewData: Boolean) {
    bikePower!!.subscribeTorqueEffectivenessEvent { estTimestamp, eventFlags, powerOnlyUpdateEventCount, leftTorqueEffectiveness, rightTorqueEffectiveness ->
      if (isOnlyNewData && deviceData["powerOnlyUpdateEventCount"] == powerOnlyUpdateEventCount && deviceData["leftTorqueEffectiveness"] == leftTorqueEffectiveness && deviceData["rightTorqueEffectiveness"] == rightTorqueEffectiveness) {
        return@subscribeTorqueEffectivenessEvent
      }
      deviceData["powerOnlyUpdateEventCount"] = powerOnlyUpdateEventCount
      deviceData["leftTorqueEffectiveness"] = leftTorqueEffectiveness
      deviceData["rightTorqueEffectiveness"] = rightTorqueEffectiveness

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.TorqueEffectiveness.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putInt("powerOnlyUpdateEventCount", powerOnlyUpdateEventCount.toInt())
      eventData.putDouble("leftTorqueEffectiveness", leftTorqueEffectiveness.toDouble())
      eventData.putDouble("rightTorqueEffectiveness", rightTorqueEffectiveness.toDouble())

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeBatteryStatus() {
    bikePower!!.subscribeBatteryStatusEvent(null)
    deviceData.remove("cumulativeOperatingTime")
    deviceData.remove("batteryVoltage")
    deviceData.remove("batteryStatus")
    deviceData.remove("cumulativeOperatingTimeResolution")
    deviceData.remove("numberOfBatteries")
    deviceData.remove("batteryIdentifier")
  }

  private fun subscribeBatteryStatus(isOnlyNewData: Boolean) {
    bikePower!!.subscribeBatteryStatusEvent { estTimestamp, eventFlags, cumulativeOperatingTime, batteryVoltage, batteryStatus, cumulativeOperatingTimeResolution, numberOfBatteries, batteryIdentifier ->
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
      eventData.putString("event", AntPlusCommonEvent.BatteryStatus.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("cumulativeOperatingTime", cumulativeOperatingTime.toInt())
      eventData.putDouble("batteryVoltage", batteryVoltage.toDouble())
      eventData.putString("batteryStatus", batteryStatus.toString())
      eventData.putInt("cumulativeOperatingTimeResolution", cumulativeOperatingTimeResolution)
      eventData.putInt("numberOfBatteries", numberOfBatteries)
      eventData.putInt("batteryIdentifier", batteryIdentifier)

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeManufacturerIdentification() {
    bikePower!!.subscribeManufacturerIdentificationEvent(null)
    deviceData.remove("hardwareRevision")
    deviceData.remove("manufacturerID")
    deviceData.remove("modelNumber")

  }

  private fun subscribeManufacturerIdentification(isOnlyNewData: Boolean) {
    bikePower!!.subscribeManufacturerIdentificationEvent { estTimestamp, eventFlags, hardwareRevision, manufacturerID, modelNumber ->
      if (isOnlyNewData && deviceData["hardwareRevision"] == hardwareRevision && deviceData["manufacturerID"] == manufacturerID && deviceData["modelNumber"] == modelNumber) {
        return@subscribeManufacturerIdentificationEvent
      }

      deviceData["hardwareRevision"] = hardwareRevision
      deviceData["manufacturerID"] = manufacturerID
      deviceData["modelNumber"] = modelNumber

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusCommonEvent.ManufacturerIdentification.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putInt("hardwareRevision", hardwareRevision)
      eventData.putInt("manufacturerID", manufacturerID)
      eventData.putInt("modelNumber", modelNumber)

      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeManufacturerSpecificData() {
    bikePower!!.subscribeManufacturerSpecificDataEvent(null)
    deviceData.remove("rawDataBytes")
  }

  private fun subscribeManufacturerSpecificData(isOnlyNewData: Boolean) {
    bikePower!!.subscribeManufacturerSpecificDataEvent { estTimestamp, eventFlags, rawDataBytes ->
      if (isOnlyNewData && deviceData["rawDataBytes"] == rawDataBytes) {
        return@subscribeManufacturerSpecificDataEvent
      }

      deviceData["rawDataBytes"] = rawDataBytes

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusCommonEvent.ManufacturerSpecific.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      try {
        eventData.putArray("rawDataBytes", AntPlusModule.bytesToWritableArray(rawDataBytes))
      } catch (throwable: Throwable) {
        Log.e("ManufacturerSpecific", "rawDataBytes", throwable)
      }
      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeProductInformation() {
    bikePower!!.subscribeProductInformationEvent(null)
    deviceData.remove("softwareRevision")
    deviceData.remove("supplementaryRevision")
    deviceData.remove("serialNumber")
  }

  private fun subscribeProductInformation(isOnlyNewData: Boolean) {
    bikePower!!.subscribeProductInformationEvent { estTimestamp, eventFlags, softwareRevision, supplementaryRevision, serialNumber ->
      if (isOnlyNewData && deviceData["softwareRevision"] == softwareRevision && deviceData["supplementaryRevision"] == supplementaryRevision && deviceData["serialNumber"] == serialNumber) {
        return@subscribeProductInformationEvent
      }

      deviceData["softwareRevision"] = softwareRevision
      deviceData["supplementaryRevision"] = supplementaryRevision
      deviceData["serialNumber"] = serialNumber

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusCommonEvent.ProductInformation.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putInt("softwareRevision", softwareRevision)
      eventData.putInt("supplementaryRevision", supplementaryRevision)
      eventData.putInt("serialNumber", serialNumber.toInt())
      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  private fun unsubscribeRssi() {
    bikePower!!.subscribeRssiEvent(null)
    deviceData.remove("rssi")
  }

  private fun subscribeRssi(isOnlyNewData: Boolean) {
    bikePower!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return@subscribeRssiEvent
      }

      deviceData["rssi"] = rssi

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusCommonEvent.Rssi.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putInt("rssi", rssi)
      antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
    }
  }

  fun request(requestName: String, args: ReadableMap, promise: Promise) {
    when (Request.valueOf(requestName)) {
      Request.CommandBurst -> requestCommandBurst(args, promise)
      Request.CrankParameters -> requestCrankParameters(promise)
      Request.CustomCalibrationParameters -> requestCustomCalibrationParameters(args, promise)
      Request.ManualCalibration -> requestManualCalibration(promise)
      Request.SetAutoZero -> requestSetAutoZero(args, promise)
      Request.SetCrankParameters -> requestSetCrankParameters(args, promise)
      Request.SetCtfSlope -> requestSetCtfSlope(args, promise)
      Request.SetCustomCalibrationParameters -> requestSetCustomCalibrationParameters(args, promise)
    }
  }

  private fun requestCommandBurst(args: ReadableMap, promise: Promise) {
    val requestedCommandId = args.getInt("requestedCommandId")
    val commandData = args.getArray("commandData")?.let { AntPlusModule.writableArrayToBytes(it) }

    bikePower!!.requestCommandBurst(requestedCommandId, commandData) { requestStatus ->
      promise.resolve(requestStatus.toString())
    }
  }

  private fun	requestCrankParameters(promise: Promise) {
    bikePower!!.requestCrankParameters(null) { estTimestamp, eventFlags, crankParameters ->
      val parameters = Arguments.createMap()

      parameters.putDouble("fullCrankLength", crankParameters.fullCrankLength.toDouble())
      parameters.putString("crankLengthStatus", crankParameters.crankLengthStatus.toString())
      parameters.putString("sensorSoftwareMismatchStatus", crankParameters.sensorSoftwareMismatchStatus.toString())
      parameters.putString("sensorAvailabilityStatus", crankParameters.sensorAvailabilityStatus.toString())
      parameters.putString("customCalibrationStatus", crankParameters.customCalibrationStatus.toString())
      parameters.putBoolean("isAutoCrankLengthSupported", crankParameters.isAutoCrankLengthSupported)

      promise.resolve(parameters)
    }
  }

  private fun	requestCustomCalibrationParameters(args: ReadableMap, promise: Promise) {
    val manufacturerSpecificParameters = args.getArray("manufacturerSpecificParameters")?.let { AntPlusModule.writableArrayToBytes(it) }

    bikePower!!.requestCustomCalibrationParameters(manufacturerSpecificParameters) { requestStatus ->
      promise.resolve(requestStatus.toString())
    }
  }

  private fun	requestManualCalibration(promise: Promise) {
    bikePower!!.requestManualCalibration { requestStatus ->
      promise.resolve(requestStatus.toString())
    }
  }

  private fun	requestSetAutoZero(args: ReadableMap, promise: Promise) {
    val autoZeroEnable = args.getBoolean("autoZeroEnable")
    bikePower!!.requestSetAutoZero(autoZeroEnable) { requestStatus ->
      promise.resolve(requestStatus.toString())
    }
  }

  private fun	requestSetCrankParameters(args: ReadableMap, promise: Promise) {
    val crankLengthSetting = args.getString("crankLengthSetting")
      ?.let { CrankLengthSetting.valueOf(it) }
    val fullCrankLength = BigDecimal(args.getDouble("fullCrankLength"))

    bikePower!!.requestSetCrankParameters(crankLengthSetting, fullCrankLength) { requestStatus ->
      promise.resolve(requestStatus.toString())
    }
  }

  private fun	requestSetCtfSlope(args: ReadableMap, promise: Promise) {
    val slope = BigDecimal(args.getDouble("slope"))

    bikePower!!.requestSetCtfSlope(slope) { requestStatus ->
      promise.resolve(requestStatus.toString())
    }
  }

  private fun	requestSetCustomCalibrationParameters(args: ReadableMap, promise: Promise) {
    val manufacturerSpecificParameters = args.getArray("manufacturerSpecificParameters")?.let { AntPlusModule.writableArrayToBytes(it) }

    bikePower!!.requestSetCustomCalibrationParameters(manufacturerSpecificParameters)  { requestStatus ->
      promise.resolve(requestStatus.toString())
    }
  }


  private var resultReceiver =
    IPluginAccessResultReceiver<AntPlusBikePowerPcc> { result, resultCode, initialDeviceState ->
      val status = Arguments.createMap()
      status.putString("name", result.deviceName)
      status.putString("state", initialDeviceState.toString())
      status.putString("code", resultCode.toString())

      if (resultCode === RequestAccessResult.SUCCESS) {
        bikePower = result
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

  enum class Event(var value: String) {
    AutoZeroStatus("AutoZeroStatus"),
    CalculatedCrankCadence("CalculatedCrankCadence"),
    CalculatedPower("CalculatedPower"),
    CalculatedTorque("CalculatedTorque"),
    CalculatedWheelDistance("CalculatedWheelDistance"),
    CalculatedWheelSpeed("CalculatedWheelSpeed"),
    CalibrationMessage("CalibrationMessage"),
    CrankParameters("CrankParameters"),
    InstantaneousCadence("InstantaneousCadence"),
    MeasurementOutputData("MeasurementOutputData"),
    PedalPowerBalance("PedalPowerBalance"),
    PedalSmoothness("PedalSmoothness"),
    RawCrankTorqueData("RawCrankTorqueData"),
    RawCtfData("RawCtfData"),
    RawPowerOnlyData("RawPowerOnlyData"),
    RawWheelTorqueData("RawWheelTorqueData"),
    TorqueEffectiveness("TorqueEffectiveness")
  }

  enum class Request(var value: String) {
    CommandBurst("CommandBurst"),
    CrankParameters("CrankParameters"),
    CustomCalibrationParameters("CustomCalibrationParameters"),
    ManualCalibration("ManualCalibration"),
    SetAutoZero("SetAutoZero"),
    SetCrankParameters("SetCrankParameters"),
    SetCtfSlope("SetCtfSlope"),
    SetCustomCalibrationParameters("SetCustomCalibrationParameters")
  }
}
