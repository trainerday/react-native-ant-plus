package com.reactnativeantplus.devices

/**
 * Implementing the AntPlusFitnessEquipmentPcc
 * https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusFitnessEquipmentPcc.html
 */

import android.util.Log
import com.reactnativeantplus.AntPlusModule
import com.reactnativeantplus.AntPlusPlugin
import com.reactnativeantplus.events.AntPlusCommonEvent
import com.reactnativeantplus.events.AntPlusEvent
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pcc.defines.RequestStatus
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.*
import java.math.BigDecimal
import java.util.*


class AntPlusFitnessEquipment(
  val context: ReactApplicationContext,
  val antPlus: AntPlusModule,
  val antDeviceNumber: Int,
  private val connectPromise: Promise
) {
  private var fitnessEquipment: AntPlusFitnessEquipmentPcc? = null
  private var releaseHandle: PccReleaseHandle<AntPlusFitnessEquipmentPcc>? = null
  private val deviceData = HashMap<String, Any>()
  private var equipmentType: EquipmentType? = null

  private var subscriptionsDone = false
  private var wheelCircumference = BigDecimal("0.7")

  fun init() {
    releaseHandle = requestNewOpenAccess(
      context,
      antDeviceNumber,
      0,
      resultReceiver,
      AntPlusPlugin.stateReceiver(antPlus, antDeviceNumber),
      fitnessEquipmentStateReceiver
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
        Event.CalibrationInProgress.toString() -> subscribeCalibrationInProgress(isOnlyNewData)
        Event.CalibrationResponse.toString() -> subscribeCalibrationResponse(isOnlyNewData)
        Event.Capabilities.toString() -> subscribeCapabilities(isOnlyNewData)
        Event.GeneralFitnessEquipmentData.toString() -> subscribeGeneralFitnessEquipmentData(isOnlyNewData)
        Event.GeneralMetabolicData.toString() -> subscribeGeneralMetabolicData(isOnlyNewData)
        Event.GeneralSettings.toString() -> subscribeGeneralSettings(isOnlyNewData)
        Event.LapOccured.toString() -> subscribeLapOccured(isOnlyNewData)
        Event.UserConfiguration.toString() -> subscribeUserConfiguration(isOnlyNewData)

        TreadmillEvent.Treadmill.toString() -> subscribeTreadmillData(isOnlyNewData)

        ClimberEvent.ClimberData.toString() -> subscribeClimberData(isOnlyNewData)

        EllipticalEvent.EllipticalData.toString() -> subscribeEllipticalData(isOnlyNewData)

        NordicSkierEvent.NordicSkierData.toString() -> subscribeNordicSkierData(isOnlyNewData)

        RowerEvent.RowerData.toString() -> subscribeRowerData(isOnlyNewData)

        BikeEvent.BikeData.toString() -> subscribeBikeData(isOnlyNewData)

        TrainerEvent.BasicResistance.toString() -> subscribeBasicResistance(isOnlyNewData)
        TrainerEvent.CalculatedTrainerDistance.toString() -> subscribeCalculatedTrainerDistance(isOnlyNewData)
        TrainerEvent.CalculatedTrainerPower.toString() -> subscribeCalculatedTrainerPower(isOnlyNewData)
        TrainerEvent.CalculatedTrainerSpeed.toString() -> subscribeCalculatedTrainerSpeed(isOnlyNewData)
        TrainerEvent.CommandStatus.toString() -> subscribeCommandStatus(isOnlyNewData)
        TrainerEvent.RawTrainerData.toString() -> subscribeRawTrainerData(isOnlyNewData)
        TrainerEvent.RawTrainerTorqueData.toString() -> subscribeRawTrainerTorqueData(isOnlyNewData)
        TrainerEvent.TargetPower.toString() -> subscribeTargetPower(isOnlyNewData)
        TrainerEvent.TrackResistance.toString() -> subscribeTrackResistance(isOnlyNewData)
        TrainerEvent.TrainerStatus.toString() -> subscribeTrainerStatus(isOnlyNewData)
        TrainerEvent.WindResistance.toString() -> subscribeWindResistance(isOnlyNewData)

        AntPlusCommonEvent.BatteryStatus.toString() -> subscribeBatteryStatus(isOnlyNewData)
        AntPlusCommonEvent.ManufacturerIdentification.toString() -> subscribeManufacturerIdentification(isOnlyNewData)
        AntPlusCommonEvent.ManufacturerSpecific.toString() -> subscribeManufacturerSpecificData(isOnlyNewData)
        AntPlusCommonEvent.ProductInformation.toString() -> subscribeProductInformation(isOnlyNewData)
        AntPlusCommonEvent.Rssi.toString() -> subscribeRssi(isOnlyNewData)

        else -> Log.d("subscribe", "Event not found")
      }
    }
  }

  fun unsubscribe(events: ReadableArray) {
    events.toArrayList().forEach { event ->
      when (event) {
        Event.CalibrationInProgress.toString() -> unsubscribeCalibrationInProgress()
        Event.CalibrationResponse.toString() -> unsubscribeCalibrationResponse()
        Event.Capabilities.toString() -> unsubscribeCapabilities()
        Event.GeneralFitnessEquipmentData.toString() -> unsubscribeGeneralFitnessEquipmentData()
        Event.GeneralMetabolicData.toString() -> unsubscribeGeneralMetabolicData()
        Event.GeneralSettings.toString() -> unsubscribeGeneralSettings()
        Event.LapOccured.toString() -> unsubscribeLapOccured()
        Event.UserConfiguration.toString() -> unsubscribeUserConfiguration()

        TreadmillEvent.Treadmill.toString() -> unsubscribeTreadmillData()

        ClimberEvent.ClimberData.toString() -> unsubscribeClimberData()

        EllipticalEvent.EllipticalData.toString() -> unsubscribeEllipticalData()

        NordicSkierEvent.NordicSkierData.toString() -> unsubscribeNordicSkierData()

        RowerEvent.RowerData.toString() -> unsubscribeRowerData()

        BikeEvent.BikeData.toString() -> unsubscribeBikeData()

        TrainerEvent.BasicResistance.toString() -> unsubscribeBasicResistance()
        TrainerEvent.CalculatedTrainerDistance.toString() -> unsubscribeCalculatedTrainerDistance()
        TrainerEvent.CalculatedTrainerPower.toString() -> unsubscribeCalculatedTrainerPower()
        TrainerEvent.CalculatedTrainerSpeed.toString() -> unsubscribeCalculatedTrainerSpeed()
        TrainerEvent.CommandStatus.toString() -> unsubscribeCommandStatus()
        TrainerEvent.RawTrainerData.toString() -> unsubscribeRawTrainerData()
        TrainerEvent.RawTrainerTorqueData.toString() -> unsubscribeRawTrainerTorqueData()
        TrainerEvent.TargetPower.toString() -> unsubscribeTargetPower()
        TrainerEvent.TrackResistance.toString() -> unsubscribeTrackResistance()
        TrainerEvent.TrainerStatus.toString() -> unsubscribeTrainerStatus()
        TrainerEvent.WindResistance.toString() -> unsubscribeWindResistance()

        AntPlusCommonEvent.BatteryStatus.toString() -> unsubscribeBatteryStatus()
        AntPlusCommonEvent.ManufacturerIdentification.toString() -> unsubscribeManufacturerIdentification()
        AntPlusCommonEvent.ManufacturerSpecific.toString() -> unsubscribeManufacturerSpecificData()
        AntPlusCommonEvent.ProductInformation.toString() -> unsubscribeProductInformation()
        AntPlusCommonEvent.Rssi.toString() -> unsubscribeRssi()
        else -> Log.d("unsubscribe", "Event not found")
      }
    }
  }

  private fun unsubscribeCalibrationInProgress() {
    fitnessEquipment!!.subscribeCalibrationInProgressEvent(null)
    deviceData.remove("calibrationInProgress")
  }

  private fun subscribeCalibrationInProgress(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeCalibrationInProgressEvent { estTimestamp, eventFlags, calibrationInProgress ->
      if (isOnlyNewData && deviceData["calibrationInProgress"] == calibrationInProgress) {
        return@subscribeCalibrationInProgressEvent
      }

      deviceData["calibrationInProgress"] = calibrationInProgress

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.CalibrationInProgress.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      val calibrationInProgressData = Arguments.createMap()

      calibrationInProgressData.putDouble("currentTemperature", calibrationInProgress.currentTemperature.toDouble())
      calibrationInProgressData.putString("speedCondition", calibrationInProgress.speedCondition.toString())
      calibrationInProgressData.putBoolean("spinDownCalibrationPending", calibrationInProgress.spinDownCalibrationPending)
      calibrationInProgressData.putDouble("targetSpeed", calibrationInProgress.targetSpeed.toDouble())
      calibrationInProgressData.putInt("targetSpinDownTime", calibrationInProgress.targetSpinDownTime)
      calibrationInProgressData.putString("temperatureCondition", calibrationInProgress.temperatureCondition.toString())
      calibrationInProgressData.putBoolean("zeroOffsetCalibrationPending", calibrationInProgress.zeroOffsetCalibrationPending)

      eventData.putMap("calibrationInProgress", calibrationInProgressData)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeCalibrationResponse() {
    fitnessEquipment!!.subscribeCalibrationResponseEvent(null)
    deviceData.remove("calibrationResponse")
  }

  private fun subscribeCalibrationResponse(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeCalibrationResponseEvent { estTimestamp, eventFlags, calibrationResponse ->
      if (isOnlyNewData && deviceData["calibrationResponse"] == calibrationResponse) {
        return@subscribeCalibrationResponseEvent
      }

      deviceData["calibrationResponse"] = calibrationResponse

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.CalibrationResponse.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      val calibrationResponseData = Arguments.createMap()

      calibrationResponseData.putBoolean("spinDownCalibrationSuccess", calibrationResponse.spinDownCalibrationSuccess)
      calibrationResponseData.putInt("spinDownTime", calibrationResponse.spinDownTime)
      calibrationResponseData.putDouble("temperature", calibrationResponse.temperature.toDouble())
      calibrationResponseData.putInt("zeroOffset", calibrationResponse.zeroOffset)
      calibrationResponseData.putBoolean("zeroOffsetCalibrationSuccess", calibrationResponse.zeroOffsetCalibrationSuccess)

      eventData.putMap("calibrationResponse", calibrationResponseData)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeCapabilities() {
    fitnessEquipment!!.subscribeCapabilitiesEvent(null)
    deviceData.remove("capabilities")
  }

  private fun subscribeCapabilities(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeCapabilitiesEvent { estTimestamp, eventFlags, capabilities ->
      if (isOnlyNewData && deviceData["capabilities"] == capabilities) {
        return@subscribeCapabilitiesEvent
      }

      deviceData["capabilities"] = capabilities

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.Capabilities.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      val capabilitiesData = Arguments.createMap()

      capabilitiesData.putInt("maximumResistance", capabilities.maximumResistance)
      capabilitiesData.putBoolean("simulationModeSupport", capabilities.simulationModeSupport)
      capabilitiesData.putBoolean("targetPowerModeSupport", capabilities.targetPowerModeSupport)

      eventData.putMap("capabilities", capabilitiesData)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeGeneralFitnessEquipmentData() {
    fitnessEquipment!!.subscribeGeneralFitnessEquipmentDataEvent(null)
    deviceData.remove("elapsedTime")
    deviceData.remove("cumulativeDistance")
    deviceData.remove("instantaneousSpeed")
    deviceData.remove("virtualInstantaneousSpeed")
    deviceData.remove("instantaneousHeartRate")
    deviceData.remove("heartRateDataSource")
  }

  private fun subscribeGeneralFitnessEquipmentData(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeGeneralFitnessEquipmentDataEvent { estTimestamp, eventFlags, elapsedTime, cumulativeDistance, instantaneousSpeed, virtualInstantaneousSpeed, instantaneousHeartRate, heartRateDataSource ->
      if (
        isOnlyNewData &&
        deviceData["elapsedTime"] == elapsedTime &&
        deviceData["cumulativeDistance"] == cumulativeDistance &&
        deviceData["instantaneousSpeed"] == instantaneousSpeed &&
        deviceData["virtualInstantaneousSpeed"] == virtualInstantaneousSpeed &&
        deviceData["instantaneousHeartRate"] == instantaneousHeartRate &&
        deviceData["heartRateDataSource"] == heartRateDataSource
      ) {
        return@subscribeGeneralFitnessEquipmentDataEvent
      }

      deviceData["elapsedTime"] = elapsedTime
      deviceData["cumulativeDistance"] = cumulativeDistance
      deviceData["instantaneousSpeed"] = instantaneousSpeed
      deviceData["virtualInstantaneousSpeed"] = virtualInstantaneousSpeed
      deviceData["instantaneousHeartRate"] = instantaneousHeartRate
      deviceData["heartRateDataSource"] = heartRateDataSource

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.GeneralFitnessEquipmentData.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putDouble("elapsedTime", elapsedTime.toDouble())
      eventData.putInt("cumulativeDistance", cumulativeDistance.toInt())
      eventData.putDouble("instantaneousSpeed", instantaneousSpeed.toDouble())
      eventData.putBoolean("virtualInstantaneousSpeed", virtualInstantaneousSpeed)
      eventData.putInt("instantaneousHeartRate", instantaneousHeartRate)
      eventData.putString("heartRateDataSource", heartRateDataSource.toString())

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeGeneralMetabolicData() {
    fitnessEquipment!!.subscribeGeneralMetabolicDataEvent(null)
    deviceData.remove("instantaneousMetabolicEquivalents")
    deviceData.remove("instantaneousCaloricBurn")
    deviceData.remove("cumulativeCalories")
  }

  private fun subscribeGeneralMetabolicData(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeGeneralMetabolicDataEvent { estTimestamp, eventFlags, instantaneousMetabolicEquivalents, instantaneousCaloricBurn, cumulativeCalories ->
      if (
        isOnlyNewData &&
        deviceData["instantaneousMetabolicEquivalents"] == instantaneousMetabolicEquivalents &&
        deviceData["instantaneousCaloricBurn"] == instantaneousCaloricBurn &&
        deviceData["cumulativeCalories"] == cumulativeCalories
      ) {
        return@subscribeGeneralMetabolicDataEvent
      }

      deviceData["instantaneousMetabolicEquivalents"] = instantaneousMetabolicEquivalents
      deviceData["instantaneousCaloricBurn"] = instantaneousCaloricBurn
      deviceData["cumulativeCalories"] = cumulativeCalories

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.GeneralMetabolicData.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putDouble("instantaneousMetabolicEquivalents", instantaneousMetabolicEquivalents.toDouble())
      eventData.putDouble("instantaneousCaloricBurn", instantaneousCaloricBurn.toDouble())
      eventData.putInt("cumulativeCalories", cumulativeCalories.toInt())

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeGeneralSettings() {
    fitnessEquipment!!.subscribeGeneralSettingsEvent(null)
    deviceData.remove("cycleLength")
    deviceData.remove("inclinePercentage")
    deviceData.remove("resistanceLevel")
  }

  private fun subscribeGeneralSettings(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeGeneralSettingsEvent { estTimestamp, eventFlags, cycleLength, inclinePercentage, resistanceLevel ->
      if (
        isOnlyNewData &&
        deviceData["cycleLength"] == cycleLength &&
        deviceData["inclinePercentage"] == inclinePercentage &&
        deviceData["resistanceLevel"] == resistanceLevel
      ) {
        return@subscribeGeneralSettingsEvent
      }

      deviceData["cycleLength"] = cycleLength
      deviceData["inclinePercentage"] = inclinePercentage
      deviceData["resistanceLevel"] = resistanceLevel

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.GeneralSettings.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putDouble("resistanceLevel", resistanceLevel.toDouble())
      eventData.putDouble("resistanceLevel", resistanceLevel.toDouble())
      eventData.putInt("resistanceLevel", resistanceLevel)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeLapOccured() {
    fitnessEquipment!!.subscribeLapOccuredEvent(null)
    deviceData.remove("lapCount")
  }

  private fun subscribeLapOccured(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeLapOccuredEvent { estTimestamp, eventFlags, lapCount ->
      if (
        isOnlyNewData &&
        deviceData["lapCount"] == lapCount
      ) {
        return@subscribeLapOccuredEvent
      }

      deviceData["lapCount"] = lapCount

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.LapOccured.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      eventData.putInt("lapCount", lapCount)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeUserConfiguration() {
    fitnessEquipment!!.subscribeUserConfigurationEvent(null)
    deviceData.remove("userConfiguration")
  }

  private fun subscribeUserConfiguration(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeUserConfigurationEvent { estTimestamp, eventFlags, userConfiguration ->
      if (
        isOnlyNewData &&
        deviceData["userConfiguration"] == userConfiguration
      ) {
        return@subscribeUserConfigurationEvent
      }

      deviceData["userConfiguration"] = userConfiguration

      val eventData = Arguments.createMap()

      eventData.putString("event", Event.UserConfiguration.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      val userConfigurationData = Arguments.createMap()

      userConfigurationData.putDouble("bicycleWeight", userConfiguration.bicycleWeight.toDouble())
      userConfigurationData.putDouble("bicycleWheelDiameter", userConfiguration.bicycleWheelDiameter.toDouble())
      userConfigurationData.putDouble("gearRatio", userConfiguration.gearRatio.toDouble())
      userConfigurationData.putDouble("userWeight", userConfiguration.userWeight.toDouble())

      eventData.putMap("userConfiguration", userConfigurationData)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeTreadmillData() {
    if (equipmentType != EquipmentType.TREADMILL) {
      return
    }

    fitnessEquipment!!.treadmillMethods.subscribeTreadmillDataEvent(null)
    deviceData.remove("instantaneousCadence")
    deviceData.remove("cumulativeNegVertDistance")
    deviceData.remove("cumulativePosVertDistance")
  }

  private fun subscribeTreadmillData(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.TREADMILL) {
      return
    }

    fitnessEquipment!!.treadmillMethods.subscribeTreadmillDataEvent { estTimestamp, eventFlags, instantaneousCadence, cumulativeNegVertDistance, cumulativePosVertDistance ->
      if (
        isOnlyNewData &&
        deviceData["instantaneousCadence"] == instantaneousCadence &&
        deviceData["cumulativeNegVertDistance"] == cumulativeNegVertDistance &&
        deviceData["cumulativePosVertDistance"] == cumulativePosVertDistance
      ) {
        return@subscribeTreadmillDataEvent
      }

      deviceData["instantaneousCadence"] = instantaneousCadence
      deviceData["cumulativeNegVertDistance"] = cumulativeNegVertDistance
      deviceData["cumulativePosVertDistance"] = cumulativePosVertDistance

      val eventData = AntPlusPlugin.createEventDataMap(
        TreadmillEvent.Treadmill.toString(),
        estTimestamp,
        eventFlags
      )

      eventData.putInt("instantaneousCadence", instantaneousCadence)
      eventData.putInt("cumulativeNegVertDistance", cumulativeNegVertDistance.toInt())
      eventData.putInt("cumulativePosVertDistance", cumulativePosVertDistance.toInt())

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeClimberData() {
    if (equipmentType != EquipmentType.CLIMBER) {
      return
    }

    fitnessEquipment!!.climberMethods.subscribeClimberDataEvent(null)
    deviceData.remove("cumulativeStrideCycles")
    deviceData.remove("instantaneousCadence")
    deviceData.remove("instantaneousPower")
  }

  private fun subscribeClimberData(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.CLIMBER) {
      return
    }

    fitnessEquipment!!.climberMethods.subscribeClimberDataEvent { estTimestamp, eventFlags, cumulativeStrideCycles, instantaneousCadence, instantaneousPower ->
      if (
        isOnlyNewData &&
        deviceData["cumulativeStrideCycles"] == cumulativeStrideCycles &&
        deviceData["instantaneousCadence"] == instantaneousCadence &&
        deviceData["instantaneousPower"] == instantaneousPower
      ) {
        return@subscribeClimberDataEvent
      }

      deviceData["cumulativeStrideCycles"] = cumulativeStrideCycles
      deviceData["instantaneousCadence"] = instantaneousCadence
      deviceData["instantaneousPower"] = instantaneousPower

      val eventData = AntPlusPlugin.createEventDataMap(ClimberEvent.ClimberData.toString(), estTimestamp, eventFlags)

      eventData.putInt("cumulativeStrideCycles", cumulativeStrideCycles.toInt())
      eventData.putInt("instantaneousCadence", instantaneousCadence)
      eventData.putInt("instantaneousPower", instantaneousPower)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeEllipticalData() {
    if (equipmentType != EquipmentType.ELLIPTICAL) {
      return
    }

    fitnessEquipment!!.ellipticalMethods.subscribeEllipticalDataEvent(null)
    deviceData.remove("instantaneousCadence")
    deviceData.remove("cumulativePosVertDistance")
    deviceData.remove("cumulativeStrides")
    deviceData.remove("instantaneousPower")
  }

  private fun subscribeEllipticalData(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.ELLIPTICAL) {
      return
    }

    fitnessEquipment!!.ellipticalMethods.subscribeEllipticalDataEvent { estTimestamp, eventFlags, cumulativePosVertDistance, cumulativeStrides, instantaneousCadence, instantaneousPower ->
      if (
        isOnlyNewData &&
        deviceData["instantaneousCadence"] == instantaneousCadence &&
        deviceData["cumulativePosVertDistance"] == cumulativePosVertDistance &&
        deviceData["cumulativeStrides"] == cumulativeStrides &&
        deviceData["instantaneousPower"] == instantaneousPower
      ) {
        return@subscribeEllipticalDataEvent
      }

      deviceData["instantaneousCadence"] = instantaneousCadence
      deviceData["cumulativePosVertDistance"] = cumulativePosVertDistance
      deviceData["cumulativeStrides"] = cumulativeStrides
      deviceData["instantaneousPower"] = instantaneousPower

      val eventData = AntPlusPlugin.createEventDataMap(EllipticalEvent.EllipticalData.toString(), estTimestamp, eventFlags)

      eventData.putDouble("cumulativePosVertDistance", cumulativePosVertDistance.toDouble())
      eventData.putInt("cumulativeStrides", cumulativeStrides.toInt())
      eventData.putInt("instantaneousCadence", instantaneousCadence)
      eventData.putInt("instantaneousPower", instantaneousPower)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeNordicSkierData() {
    if (equipmentType != EquipmentType.NORDICSKIER) {
      return
    }

    fitnessEquipment!!.nordicSkierMethods.subscribeNordicSkierDataEvent(null)
    deviceData.remove("cumulativeStrides")
    deviceData.remove("instantaneousCadence")
    deviceData.remove("instantaneousPower")
  }

  private fun subscribeNordicSkierData(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.NORDICSKIER) {
      return
    }

    fitnessEquipment!!.nordicSkierMethods.subscribeNordicSkierDataEvent { estTimestamp, eventFlags, cumulativeStrides, instantaneousCadence, instantaneousPower ->
      if (
        isOnlyNewData &&
        deviceData["cumulativeStrides"] == cumulativeStrides &&
        deviceData["instantaneousCadence"] == instantaneousCadence &&
        deviceData["instantaneousPower"] == instantaneousPower
      ) {
        return@subscribeNordicSkierDataEvent
      }

      deviceData["cumulativeStrides"] = cumulativeStrides
      deviceData["instantaneousCadence"] = instantaneousCadence
      deviceData["instantaneousPower"] = instantaneousPower

      val eventData = AntPlusPlugin.createEventDataMap(NordicSkierEvent.NordicSkierData.toString(), estTimestamp, eventFlags)

      eventData.putInt("cumulativeStrides", cumulativeStrides.toInt())
      eventData.putInt("instantaneousCadence", instantaneousCadence)
      eventData.putInt("instantaneousPower", instantaneousPower)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeRowerData() {
    if (equipmentType != EquipmentType.ROWER) {
      return
    }

    fitnessEquipment!!.rowerMethods.subscribeRowerDataEvent(null)
    deviceData.remove("cumulativeStrokes")
    deviceData.remove("instantaneousCadence")
    deviceData.remove("instantaneousPower")
  }

  private fun subscribeRowerData(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.ROWER) {
      return
    }

    fitnessEquipment!!.rowerMethods.subscribeRowerDataEvent { estTimestamp, eventFlags, cumulativeStrokes, instantaneousCadence, instantaneousPower ->
      if (
        isOnlyNewData &&
        deviceData["cumulativeStrokes"] == cumulativeStrokes &&
        deviceData["instantaneousCadence"] == instantaneousCadence &&
        deviceData["instantaneousPower"] == instantaneousPower
      ) {
        return@subscribeRowerDataEvent
      }

      deviceData["cumulativeStrokes"] = cumulativeStrokes
      deviceData["instantaneousCadence"] = instantaneousCadence
      deviceData["instantaneousPower"] = instantaneousPower

      val eventData = AntPlusPlugin.createEventDataMap(RowerEvent.RowerData.toString(), estTimestamp, eventFlags)

      eventData.putInt("cumulativeStrokes", cumulativeStrokes.toInt())
      eventData.putInt("instantaneousCadence", instantaneousCadence)
      eventData.putInt("instantaneousPower", instantaneousPower)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeCalculatedTrainerPower() {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }

    fitnessEquipment!!.trainerMethods.subscribeCalculatedTrainerPowerEvent(null)
    deviceData.remove("calculatedPower")
  }

  private fun subscribeCalculatedTrainerPower(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }

    fitnessEquipment!!.trainerMethods.subscribeCalculatedTrainerPowerEvent { estTimestamp, eventFlags, dataSource, calculatedPower ->
      if (isOnlyNewData && deviceData["calculatedPower"] == calculatedPower) {
        return@subscribeCalculatedTrainerPowerEvent
      }

      deviceData["calculatedPower"] = calculatedPower

      val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.CalculatedTrainerPower.toString(), estTimestamp, eventFlags)

      eventData.putDouble("calculatedPower", calculatedPower.toDouble())
      eventData.putString("dataSource", dataSource.toString())

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeCalculatedTrainerSpeed() {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }

    fitnessEquipment!!.trainerMethods.subscribeCalculatedTrainerSpeedEvent(null)
    deviceData.remove("calculatedSpeed")
  }

  private fun subscribeCalculatedTrainerSpeed(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }

    fitnessEquipment!!.trainerMethods.subscribeCalculatedTrainerSpeedEvent(object :
      CalculatedTrainerSpeedReceiver(wheelCircumference) {
      override fun onNewCalculatedTrainerSpeed(estTimestamp: Long, eventFlags: EnumSet<EventFlag>, dataSource: TrainerDataSource, calculatedSpeed: BigDecimal) {
        if (isOnlyNewData && deviceData["calculatedSpeed"] == calculatedSpeed) {
          return
        }

        deviceData["calculatedSpeed"] = calculatedSpeed

        val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.CalculatedTrainerSpeed.toString(), estTimestamp, eventFlags)

        eventData.putDouble("calculatedSpeed", calculatedSpeed.toDouble())
        eventData.putString("dataSource", dataSource.toString())

        antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
      }
    })
  }

  private fun unsubscribeBasicResistance() {
    when (equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeBasicResistanceEvent(null)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeBasicResistanceEvent(null)
      else -> Log.e("Unsubscribe", "Device not support BasicResistance")
    }
    deviceData.remove("totalResistance")
  }

  private fun subscribeBasicResistance(isOnlyNewData: Boolean) {
    fun subscribe(estTimestamp: Long, eventFlags: EnumSet<EventFlag>, totalResistance: BigDecimal) {
      if (isOnlyNewData && deviceData["totalResistance"] == totalResistance) {
        return
      }

      deviceData["totalResistance"] = totalResistance

      val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.BasicResistance.toString(), estTimestamp, eventFlags)
      eventData.putDouble("totalResistance", totalResistance.toDouble())
      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }

    when(equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeBasicResistanceEvent(::subscribe)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeBasicResistanceEvent(::subscribe)
      else -> Log.e("Subscribe", "Device not support BasicResistance")
    }
  }

  private fun unsubscribeCalculatedTrainerDistance() {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }
    fitnessEquipment!!.trainerMethods.subscribeCalculatedTrainerDistanceEvent(null)
    deviceData.remove("calculatedDistance")
  }

  private fun subscribeCalculatedTrainerDistance(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }
    fitnessEquipment!!.trainerMethods.subscribeCalculatedTrainerDistanceEvent(object : CalculatedTrainerDistanceReceiver(wheelCircumference) {
      override fun onNewCalculatedTrainerDistance(
        estTimestamp: Long,
        eventFlags: EnumSet<EventFlag>,
        dataSource: TrainerDataSource,
        calculatedDistance: BigDecimal
      ) {
        if (isOnlyNewData && deviceData["calculatedDistance"] == calculatedDistance) {
          return
        }

        deviceData["calculatedDistance"] = calculatedDistance

        val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.CalculatedTrainerDistance.toString(), estTimestamp, eventFlags)
        eventData.putDouble("calculatedDistance", calculatedDistance.toDouble())
        antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
      }
    })
  }

  private fun unsubscribeCommandStatus() {
    when (equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeCommandStatusEvent(null)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeCommandStatusEvent(null)
      else -> Log.e("Unsubscribe", "Device not support CommandStatus")
    }

    deviceData.remove("commandStatus")
  }

  private fun subscribeCommandStatus(isOnlyNewData: Boolean) {
    fun subscribe(estTimestamp: Long, eventFlags: EnumSet<EventFlag>, commandStatus: CommandStatus) {
      if (isOnlyNewData && deviceData["commandStatus"] == commandStatus) {
        return
      }

      deviceData["commandStatus"] = commandStatus

      val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.CommandStatus.toString(), estTimestamp, eventFlags)

      val commandStatusData = Arguments.createMap()

      commandStatusData.putInt("lastReceivedSequenceNumber", commandStatus.lastReceivedSequenceNumber)
      commandStatusData.putString("status", commandStatus.status.toString())
      commandStatusData.putArray("rawResponseData", AntPlusModule.bytesToWritableArray(commandStatus.rawResponseData))
      commandStatusData.putString("lastReceivedCommandId", commandStatus.lastReceivedCommandId.toString())
      commandStatusData.putDouble("totalResistance", commandStatus.totalResistance.toDouble())
      commandStatusData.putDouble("targetPower", commandStatus.targetPower.toDouble())
      commandStatusData.putDouble("windResistanceCoefficient", commandStatus.windResistanceCoefficient.toDouble())
      commandStatusData.putInt("windSpeed", commandStatus.windSpeed)
      commandStatusData.putDouble("draftingFactor", commandStatus.draftingFactor.toDouble())
      commandStatusData.putDouble("grade", commandStatus.grade.toDouble())
      commandStatusData.putDouble("rollingResistanceCoefficient", commandStatus.rollingResistanceCoefficient.toDouble())

      eventData.putMap("commandStatus", commandStatusData)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }

    when(equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeCommandStatusEvent(::subscribe)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeCommandStatusEvent(::subscribe)
      else -> Log.e("Subscribe", "Device not support CommandStatus")
    }
  }

  private fun unsubscribeRawTrainerData() {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }
    fitnessEquipment!!.trainerMethods.subscribeRawTrainerDataEvent(null)
    deviceData.remove("instantaneousCadence")
    deviceData.remove("instantaneousPower")
    deviceData.remove("accumulatedPower")
  }

  private fun subscribeRawTrainerData(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }
    fitnessEquipment!!.trainerMethods.subscribeRawTrainerDataEvent { estTimestamp, eventFlags, updateEventCount, instantaneousCadence, instantaneousPower, accumulatedPower ->
      if (isOnlyNewData &&
        deviceData["instantaneousCadence"] == instantaneousCadence &&
        deviceData["instantaneousPower"] == instantaneousPower &&
        deviceData["accumulatedPower"] == accumulatedPower
      ) {
        return@subscribeRawTrainerDataEvent
      }

      deviceData["instantaneousCadence"] = instantaneousCadence
      deviceData["instantaneousPower"] = instantaneousPower
      deviceData["accumulatedPower"] = accumulatedPower

      val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.RawTrainerData.toString(), estTimestamp, eventFlags)

      eventData.putInt("updateEventCount", updateEventCount.toInt())
      eventData.putInt("instantaneousCadence", instantaneousCadence)
      eventData.putInt("instantaneousPower", instantaneousPower)
      eventData.putInt("accumulatedPower", accumulatedPower.toInt())

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeRawTrainerTorqueData() {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }
    fitnessEquipment!!.trainerMethods.subscribeRawTrainerTorqueDataEvent(null)
    deviceData.remove("accumulatedWheelTicks")
    deviceData.remove("accumulatedWheelPeriod")
    deviceData.remove("accumulatedTorque")
  }

  private fun subscribeRawTrainerTorqueData(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }
    fitnessEquipment!!.trainerMethods.subscribeRawTrainerTorqueDataEvent { estTimestamp, eventFlags, updateEventCount, accumulatedWheelTicks, accumulatedWheelPeriod, accumulatedTorque ->
      if (isOnlyNewData &&
        deviceData["accumulatedWheelTicks"] == accumulatedWheelTicks &&
        deviceData["accumulatedWheelPeriod"] == accumulatedWheelPeriod &&
        deviceData["accumulatedTorque"] == accumulatedTorque
      ) {
        return@subscribeRawTrainerTorqueDataEvent
      }

      deviceData["accumulatedWheelTicks"] = accumulatedWheelTicks
      deviceData["accumulatedWheelPeriod"] = accumulatedWheelPeriod
      deviceData["accumulatedTorque"] = accumulatedTorque

      val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.RawTrainerTorqueData.toString(), estTimestamp, eventFlags)

      eventData.putInt("updateEventCount", updateEventCount.toInt())
      eventData.putInt("accumulatedWheelTicks", accumulatedWheelTicks.toInt())
      eventData.putDouble("accumulatedWheelPeriod", accumulatedWheelPeriod.toDouble())
      eventData.putDouble("accumulatedTorque", accumulatedTorque.toDouble())

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeTargetPower() {
    when (equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeTargetPowerEvent(null)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeTargetPowerEvent(null)
      else -> Log.e("Unsubscribe", "Device not support TargetPower")
    }

    deviceData.remove("targetPower")
  }

  private fun subscribeTargetPower(isOnlyNewData: Boolean) {
    fun subscribe(estTimestamp: Long, eventFlags: EnumSet<EventFlag>, targetPower: BigDecimal) {
      if (isOnlyNewData && deviceData["targetPower"] == targetPower) {
        return
      }

      deviceData["targetPower"] = targetPower

      val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.TargetPower.toString(), estTimestamp, eventFlags)
      eventData.putDouble("targetPower", targetPower.toDouble())
      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }

    when(equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeTargetPowerEvent(::subscribe)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeTargetPowerEvent(::subscribe)
      else -> Log.e("Subscribe", "Device not support TargetPower")
    }
  }

  private fun unsubscribeTrackResistance() {
    when (equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeTrackResistanceEvent(null)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeTrackResistanceEvent(null)
      else -> Log.e("Unsubscribe", "Device not support TrackResistance")
    }
    deviceData.remove("grade")
    deviceData.remove("rollingResistanceCoefficient")
  }

  private fun subscribeTrackResistance(isOnlyNewData: Boolean) {
    fun subscribe(estTimestamp: Long, eventFlags: EnumSet<EventFlag>, grade: BigDecimal, rollingResistanceCoefficient: BigDecimal) {
      if (isOnlyNewData && deviceData["grade"] == grade && deviceData["rollingResistanceCoefficient"] == rollingResistanceCoefficient) {
        return
      }

      deviceData["grade"] = grade
      deviceData["rollingResistanceCoefficient"] = rollingResistanceCoefficient

      val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.TrackResistance.toString(), estTimestamp, eventFlags)

      eventData.putDouble("grade", grade.toDouble())
      eventData.putDouble("rollingResistanceCoefficient", rollingResistanceCoefficient.toDouble())

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }

    when(equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeTrackResistanceEvent(::subscribe)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeTrackResistanceEvent(::subscribe)
      else -> Log.e("Subscribe", "Device not support TrackResistance")
    }
  }

  private fun unsubscribeTrainerStatus() {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }
    fitnessEquipment!!.trainerMethods.subscribeTrainerStatusEvent(null)
    deviceData.remove("trainerStatusFlags")
  }

  private fun subscribeTrainerStatus(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.TRAINER) {
      return
    }
    fitnessEquipment!!.trainerMethods.subscribeTrainerStatusEvent { estTimestamp, eventFlags, trainerStatusFlags ->
      if (isOnlyNewData && deviceData["trainerStatusFlags"] == trainerStatusFlags) {
        return@subscribeTrainerStatusEvent
      }

      deviceData["trainerStatusFlags"] = trainerStatusFlags

      val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.TrainerStatus.toString(), estTimestamp, eventFlags)
      eventData.putString("trainerStatusFlags", trainerStatusFlags.toString())
      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeWindResistance() {
    when (equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeWindResistanceEvent(null)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeWindResistanceEvent(null)
      else -> Log.e("Unsubscribe", "Device not support WindResistance")
    }
    deviceData.remove("windResistanceCoefficient")
    deviceData.remove("windSpeed")
    deviceData.remove("draftingFactor")
  }

  private fun subscribeWindResistance(isOnlyNewData: Boolean) {
    fun subscribe(estTimestamp: Long, eventFlags: EnumSet<EventFlag>, windResistanceCoefficient: BigDecimal, windSpeed: Int, draftingFactor: BigDecimal) {
      if (isOnlyNewData &&
        deviceData["windResistanceCoefficient"] == windResistanceCoefficient &&
        deviceData["windSpeed"] == windSpeed &&
        deviceData["draftingFactor"] == draftingFactor
      ) {
        return
      }

      deviceData["windResistanceCoefficient"] = windResistanceCoefficient
      deviceData["windSpeed"] = windSpeed
      deviceData["draftingFactor"] = draftingFactor

      val eventData = AntPlusPlugin.createEventDataMap(TrainerEvent.WindResistance.toString(), estTimestamp, eventFlags)

      eventData.putDouble("windResistanceCoefficient", windResistanceCoefficient.toDouble())
      eventData.putInt("windSpeed", windSpeed)
      eventData.putDouble("draftingFactor", draftingFactor.toDouble())

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }

    when(equipmentType) {
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.subscribeWindResistanceEvent(::subscribe)
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.subscribeWindResistanceEvent(::subscribe)
      else -> Log.e("Subscribe", "Device not support WindResistance")
    }
  }

  private fun unsubscribeBikeData() {
    if (equipmentType != EquipmentType.BIKE) {
      return
    }

    fitnessEquipment!!.bikeMethods.subscribeBikeDataEvent(null)
    deviceData.remove("instantaneousCadence")
    deviceData.remove("instantaneousPower")
  }

  private fun subscribeBikeData(isOnlyNewData: Boolean) {
    if (equipmentType != EquipmentType.BIKE) {
      return
    }

    fitnessEquipment!!.bikeMethods.subscribeBikeDataEvent { estTimestamp, eventFlags, instantaneousCadence, instantaneousPower ->
      if (isOnlyNewData && deviceData["instantaneousCadence"] == instantaneousCadence && deviceData["instantaneousPower"] == instantaneousPower) {
        return@subscribeBikeDataEvent
      }

      deviceData["instantaneousCadence"] = instantaneousCadence
      deviceData["instantaneousPower"] = instantaneousPower

      val eventData = AntPlusPlugin.createEventDataMap(BikeEvent.BikeData.toString(), estTimestamp, eventFlags)

      eventData.putInt("instantaneousCadence", instantaneousCadence)
      eventData.putInt("instantaneousPower", instantaneousPower)

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  //    Methods inherited from class AntPlusCommonPcc ------------
  private fun unsubscribeBatteryStatus() {
    fitnessEquipment!!.subscribeBatteryStatusEvent(null)
    deviceData.remove("cumulativeOperatingTime")
    deviceData.remove("batteryVoltage")
    deviceData.remove("batteryStatus")
    deviceData.remove("cumulativeOperatingTimeResolution")
    deviceData.remove("numberOfBatteries")
    deviceData.remove("batteryIdentifier")
  }

  private fun subscribeBatteryStatus(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeBatteryStatusEvent { estTimestamp, eventFlags, cumulativeOperatingTime, batteryVoltage, batteryStatus, cumulativeOperatingTimeResolution, numberOfBatteries, batteryIdentifier ->
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

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeManufacturerIdentification() {
    fitnessEquipment!!.subscribeManufacturerIdentificationEvent(null)
    deviceData.remove("hardwareRevision")
    deviceData.remove("manufacturerID")
    deviceData.remove("modelNumber")

  }

  private fun subscribeManufacturerIdentification(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeManufacturerIdentificationEvent { estTimestamp, eventFlags, hardwareRevision, manufacturerID, modelNumber ->
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

      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeManufacturerSpecificData() {
    fitnessEquipment!!.subscribeManufacturerSpecificDataEvent(null)
    deviceData.remove("rawDataBytes")
  }

  private fun subscribeManufacturerSpecificData(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeManufacturerSpecificDataEvent { estTimestamp, eventFlags, rawDataBytes ->
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
      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeProductInformation() {
    fitnessEquipment!!.subscribeProductInformationEvent(null)
    deviceData.remove("softwareRevision")
    deviceData.remove("supplementaryRevision")
    deviceData.remove("serialNumber")
  }

  private fun subscribeProductInformation(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeProductInformationEvent { estTimestamp, eventFlags, softwareRevision, supplementaryRevision, serialNumber ->
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
      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  private fun unsubscribeRssi() {
    fitnessEquipment!!.subscribeRssiEvent(null)
    deviceData.remove("rssi")
  }

  private fun subscribeRssi(isOnlyNewData: Boolean) {
    fitnessEquipment!!.subscribeRssiEvent { estTimestamp, eventFlags, rssi ->
      if (isOnlyNewData && deviceData["rssi"] == rssi) {
        return@subscribeRssiEvent
      }

      deviceData["rssi"] = rssi

      val eventData = Arguments.createMap()
      eventData.putString("event", AntPlusCommonEvent.Rssi.toString())
      eventData.putString("eventFlags", eventFlags.toString())
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putInt("rssi", rssi)
      antPlus.sendEvent(AntPlusEvent.fitnessEquipment, eventData)
    }
  }

  fun request(requestName: String, args: ReadableMap, promise: Promise) {
    when (requestName) {
      Request.Capabilities.toString() -> requestCapabilities(promise)
      Request.SetUserConfiguration.toString() -> requestSetUserConfiguration(args, promise)
      Request.SpinDownCalibration.toString() -> requestSpinDownCalibration(promise)
      Request.UserConfiguration.toString() -> requestUserConfiguration(promise)
      Request.ZeroOffsetCalibration.toString() -> requestZeroOffsetCalibration(promise)

      TrainerRequest.BasicResistance.toString() -> requestBasicResistance(promise)
      TrainerRequest.CommandStatus.toString() -> requestCommandStatus(promise)
      TrainerRequest.SetBasicResistance.toString() -> requestSetBasicResistance(args, promise)
      TrainerRequest.SetTargetPower.toString() -> requestSetTargetPower(args, promise)
      TrainerRequest.SetTrackResistance.toString() -> requestSetTrackResistance(args, promise)
      TrainerRequest.SetWindResistance.toString() -> requestSetWindResistance(args, promise)
      TrainerRequest.TargetPower.toString() -> requestTargetPower(promise)
      TrainerRequest.TrackResistance.toString() -> requestTrackResistance(promise)
      TrainerRequest.WindResistance.toString() -> requestWindResistance(promise)

      else -> Log.e("Request", "Request $requestName not found")
    }
  }

  private fun requestCapabilities(promise: Promise) {
    fitnessEquipment!!.requestCapabilities(null) { a, b, capabilities ->
      val capabilitiesData = Arguments.createMap()

      capabilitiesData.putBoolean("basicResistanceModeSupport", capabilities.basicResistanceModeSupport)
      capabilitiesData.putInt("maximumResistance", capabilities.maximumResistance)
      capabilitiesData.putBoolean("simulationModeSupport", capabilities.simulationModeSupport)
      capabilitiesData.putBoolean("targetPowerModeSupport", capabilities.targetPowerModeSupport)

      promise.resolve(capabilities.toString())
    }
  }

  private fun requestSetUserConfiguration(args: ReadableMap, promise: Promise) {
    val userConfig = UserConfiguration()
    userConfig.bicycleWeight = BigDecimal(args.getDouble("bicycleWeight"))
    userConfig.gearRatio = BigDecimal(args.getDouble("gearRatio"))
    userConfig.bicycleWheelDiameter = BigDecimal(args.getDouble("bicycleWheelDiameter"))
    userConfig.userWeight = BigDecimal(args.getDouble("userWeight"))

    fitnessEquipment!!.requestSetUserConfiguration(userConfig) { requestStatus ->
      promise.resolve(requestStatus.toString())
    }
  }

  private fun requestSpinDownCalibration(promise: Promise) {
    fitnessEquipment!!.requestSpinDownCalibration({ requestStatus ->
      promise.resolve(requestStatus.toString())
    }, null, null)
  }

  private fun requestUserConfiguration(promise: Promise) {
    fitnessEquipment!!.requestUserConfiguration(null) { a, b, userConfiguration ->
      val userConfigurationData = Arguments.createMap()
      userConfigurationData.putDouble("bicycleWeight", userConfiguration.bicycleWeight.toDouble())
      userConfigurationData.putDouble("bicycleWheelDiameter", userConfiguration.bicycleWheelDiameter.toDouble())
      userConfigurationData.putDouble("gearRatio", userConfiguration.gearRatio.toDouble())
      userConfigurationData.putDouble("userWeight", userConfiguration.userWeight.toDouble())
      promise.resolve(userConfigurationData)
    }
  }

  private fun requestZeroOffsetCalibration(promise: Promise) {
    fitnessEquipment!!.requestZeroOffsetCalibration({ requestStatus ->
      promise.resolve(requestStatus.toString())
    }, null, null)
  }

  private fun requestBasicResistance(promise: Promise) {
    fun basicResistanceReceiver(a: Long , b: EnumSet<EventFlag>, totalResistance: BigDecimal) {
      promise.resolve(totalResistance.toDouble())
    }

    when(equipmentType) {
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestBasicResistance(null, ::basicResistanceReceiver)
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestBasicResistance(null, ::basicResistanceReceiver)
      else -> promise.reject(Error("Device not support requestBasicResistance"))
    }
  }

  private fun requestCommandStatus(promise: Promise) {
    fun commandStatusReceiver(var1: Long , var3: EnumSet<EventFlag>, commandStatus: CommandStatus) {
      val commandStatusData = Arguments.createMap()

      commandStatusData.putInt("lastReceivedSequenceNumber", commandStatus.lastReceivedSequenceNumber)
      commandStatusData.putString("status", commandStatus.status.toString())
      commandStatusData.putArray("rawResponseData", AntPlusModule.bytesToWritableArray(commandStatus.rawResponseData))
      commandStatusData.putString("lastReceivedCommandId", commandStatus.lastReceivedCommandId.toString())
      commandStatusData.putDouble("totalResistance", commandStatus.totalResistance.toDouble())
      commandStatusData.putDouble("targetPower", commandStatus.targetPower.toDouble())
      commandStatusData.putDouble("windResistanceCoefficient", commandStatus.windResistanceCoefficient.toDouble())
      commandStatusData.putInt("windSpeed", commandStatus.windSpeed)
      commandStatusData.putDouble("draftingFactor", commandStatus.draftingFactor.toDouble())
      commandStatusData.putDouble("grade", commandStatus.grade.toDouble())
      commandStatusData.putDouble("rollingResistanceCoefficient", commandStatus.rollingResistanceCoefficient.toDouble())

      promise.resolve(commandStatusData)
    }

    when(equipmentType) {
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestCommandStatus(null, ::commandStatusReceiver)
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestCommandStatus(null, ::commandStatusReceiver)
      else -> promise.reject(Error("Device not support requestCommandStatus"))
    }
  }

  private fun requestSetBasicResistance(args: ReadableMap, promise: Promise) {
    val totalResistance = BigDecimal(args.getDouble("totalResistance"))

    fun requestFinishedReceiver(status: RequestStatus) {
      promise.resolve(status.toString())
    }

    when(equipmentType) {
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestSetBasicResistance(totalResistance, ::requestFinishedReceiver)
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestSetBasicResistance(totalResistance, ::requestFinishedReceiver)
      else -> promise.reject(Error("Device not support requestSetBasicResistance"))
    }
  }

  private fun requestSetTargetPower(args: ReadableMap, promise: Promise) {
    val target = BigDecimal(args.getDouble("target"))

    fun requestFinishedReceiver(status: RequestStatus) {
      promise.resolve(status.toString())
    }

    when(equipmentType) {
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestSetTargetPower(target, ::requestFinishedReceiver)
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestSetTargetPower(target, ::requestFinishedReceiver)
      else -> promise.reject(Error("Device not support requestSetTargetPower"))
    }
  }

  private fun requestSetTrackResistance(args: ReadableMap, promise: Promise) {
    val grade = BigDecimal(args.getDouble("grade"))
    val rollingResistanceCoefficient = BigDecimal(args.getDouble("rollingResistanceCoefficient"))

    fun requestFinishedReceiver(status: RequestStatus) {
      promise.resolve(status.toString())
    }

    when(equipmentType) {
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestSetTrackResistance(grade, rollingResistanceCoefficient, ::requestFinishedReceiver)
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestSetTrackResistance(grade, rollingResistanceCoefficient, ::requestFinishedReceiver)
      else -> promise.reject(Error("Device not support requestSetTrackResistance"))
    }
  }

  private fun requestSetWindResistance(args: ReadableMap, promise: Promise) {
    val windSpeed = args.getInt("windSpeed")
    val draftingFactor = BigDecimal(args.getDouble("draftingFactor"))

    fun requestFinishedReceiver(status: RequestStatus) {
      promise.resolve(status.toString())
    }

    if (args.hasKey("windResistanceCoefficient")) {
      val windResistanceCoefficient = BigDecimal(args.getDouble("windResistanceCoefficient"))

      when(equipmentType) {
        EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestSetWindResistance(windResistanceCoefficient, windSpeed, draftingFactor, ::requestFinishedReceiver)
        EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestSetWindResistance(windResistanceCoefficient, windSpeed, draftingFactor, ::requestFinishedReceiver)
        else -> promise.reject(Error("Device not support requestSetWindResistance"))
      }
    } else {
      val frontalSurfaceArea = BigDecimal(args.getDouble("frontalSurfaceArea"))
      val dragCoefficient = BigDecimal(args.getDouble("dragCoefficient"))
      val airDensity = BigDecimal(args.getDouble("airDensity"))

      when(equipmentType) {
        EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestSetWindResistance(frontalSurfaceArea, dragCoefficient, airDensity, windSpeed, draftingFactor, ::requestFinishedReceiver)
        EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestSetWindResistance(frontalSurfaceArea, dragCoefficient, airDensity, windSpeed, draftingFactor, ::requestFinishedReceiver)
        else -> promise.reject(Error("Device not support requestSetWindResistance"))
      }
    }
  }

  private fun requestTargetPower(promise: Promise) {
    fun targetPowerReceiver(a: Long , b: EnumSet<EventFlag>, targetPower: BigDecimal) {
      promise.resolve(targetPower.toDouble())
    }

    when(equipmentType) {
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestTargetPower(null, ::targetPowerReceiver)
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestTargetPower(null, ::targetPowerReceiver)
      else -> promise.reject(Error("Device not support requestTargetPower"))
    }
  }

  private fun requestTrackResistance(promise: Promise) {
    fun trackResistanceReceiver(a: Long , b: EnumSet<EventFlag>, grade: BigDecimal, rollingResistanceCoefficient: BigDecimal) {
      val resistance = Arguments.createMap()
      resistance.putDouble("grade", grade.toDouble())
      resistance.putDouble("rollingResistanceCoefficient", rollingResistanceCoefficient.toDouble())
      promise.resolve(resistance)
    }

    when(equipmentType) {
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestTrackResistance(null, ::trackResistanceReceiver)
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestTrackResistance(null, ::trackResistanceReceiver)
      else -> promise.reject(Error("Device not support requestTrackResistance"))
    }
  }

  private fun requestWindResistance(promise: Promise) {
    fun windResistanceReceiver(a: Long , b: EnumSet<EventFlag>, windResistanceCoefficient: BigDecimal, windSpeed: Int, draftingFactor: BigDecimal) {
      val resistance = Arguments.createMap()
      resistance.putDouble("windResistanceCoefficient", windResistanceCoefficient.toDouble())
      resistance.putInt("windSpeed", windSpeed)
      resistance.putDouble("draftingFactor", draftingFactor.toDouble())
      promise.resolve(resistance)
    }

    when(equipmentType) {
      EquipmentType.BIKE -> fitnessEquipment!!.bikeMethods.requestWindResistance(null, ::windResistanceReceiver)
      EquipmentType.TRAINER -> fitnessEquipment!!.trainerMethods.requestWindResistance(null, ::windResistanceReceiver)
      else -> promise.reject(Error("Device not support requestWindResistance"))
    }
  }


  private var fitnessEquipmentStateReceiver =
    IFitnessEquipmentStateReceiver { estTimestamp, eventFlags, type, equipmentState ->
      if (subscriptionsDone || equipmentType == EquipmentType.UNKNOWN || equipmentType == EquipmentType.UNRECOGNIZED) {
        return@IFitnessEquipmentStateReceiver
      }

      val status = Arguments.createMap()
      status.putString("name", fitnessEquipment!!.deviceName)
      status.putString("type", type.toString())
      status.putBoolean("connected", true)
      status.putString("state", equipmentState.toString())
      connectPromise.resolve(status)

      equipmentType = type
    }

  private var resultReceiver =
    IPluginAccessResultReceiver<AntPlusFitnessEquipmentPcc> { result, resultCode, initialDeviceState ->
      if (resultCode === RequestAccessResult.SUCCESS) {
        fitnessEquipment = result
      } else {
        val status = Arguments.createMap()
        status.putString("name", result.deviceName)
        status.putString("state", initialDeviceState.toString())
        status.putString("code", resultCode.toString())
        status.putBoolean("connected", false)
        connectPromise.resolve(status)
      }
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
    CalibrationInProgress("CalibrationInProgress"),
    CalibrationResponse("CalibrationResponse"),
    Capabilities("Capabilities"),
    GeneralFitnessEquipmentData("GeneralFitnessEquipmentData"),
    GeneralMetabolicData("GeneralMetabolicData"),
    GeneralSettings("GeneralSettings"),
    LapOccured("LapOccured"),
    UserConfiguration("UserConfiguration")
  }

  enum class BikeEvent(val event: String) {
    BasicResistance("BasicResistance"),
    BikeData("BikeData"),
    CommandStatus("CommandStatus"),
    TargetPower("TargetPower"),
    TrackResistance("TrackResistance"),
    WindResistance("WindResistance"),
  }

  enum class ClimberEvent(val event: String) {
    ClimberData("ClimberData")
  }

  enum class EllipticalEvent(val event: String) {
    EllipticalData("EllipticalData")
  }

  enum class NordicSkierEvent(val event: String) {
    NordicSkierData("NordicSkierData")
  }

  enum class RowerEvent(val event: String) {
    RowerData("RowerData")
  }

  enum class TrainerEvent(val event: String) {
    BasicResistance("BasicResistance"),
    CalculatedTrainerDistance("CalculatedTrainerDistance"),
    CalculatedTrainerPower("CalculatedTrainerPower"),
    CalculatedTrainerSpeed("CalculatedTrainerSpeed"),
    CommandStatus("CommandStatus"),
    RawTrainerData("RawTrainerData"),
    RawTrainerTorqueData("RawTrainerTorqueData"),
    TargetPower("TargetPower"),
    TrackResistance("TrackResistance"),
    TrainerStatus("TrainerStatus"),
    WindResistance("WindResistance"),
  }

  enum class TreadmillEvent(val event: String) {
    Treadmill("Treadmill")
  }

  enum class Request(val event: String) {
    Capabilities("Capabilities"),
    SetUserConfiguration("SetUserConfiguration"),
    SpinDownCalibration("SpinDownCalibration"),
    UserConfiguration("UserConfiguration"),
    ZeroOffsetCalibration("ZeroOffsetCalibration")
  }

  enum class BikeRequest(val event: String) {
    BasicResistance("BasicResistance"),
    CommandStatus("CommandStatus"),
    SetBasicResistance("SetBasicResistance"),
    SetTargetPower("SetTargetPower"),
    SetTrackResistance("SetTrackResistance"),
    SetWindResistance("SetWindResistance"),
    TargetPower("TargetPower"),
    TrackResistance("TrackResistance"),
    WindResistance("WindResistance")
  }

  enum class TrainerRequest(val event: String) {
    BasicResistance("BasicResistance"),
    CommandStatus("CommandStatus"),
    SetBasicResistance("SetBasicResistance"),
    SetTargetPower("SetTargetPower"),
    SetTrackResistance("SetTrackResistance"),
    SetWindResistance("SetWindResistance"),
    TargetPower("TargetPower"),
    TrackResistance("TrackResistance"),
    WindResistance("WindResistance")
  }
}
