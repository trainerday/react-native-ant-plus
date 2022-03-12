package com.reactnativeantplus

import android.util.Log
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.*
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pcc.defines.RequestStatus
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IRequestFinishedReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import java.math.BigDecimal
import java.util.*


class AntPlusBikePower(val context: ReactApplicationContext, val antPlus: AntPlusModule, val antDeviceNumber: Int, private val connectPromise: Promise) {
    private var bikePower: AntPlusBikePowerPcc? = null
    private var releaseHandle: PccReleaseHandle<AntPlusBikePowerPcc>? = null
    private val deviceData = HashMap<String, Any>()

    private val requestFinishedReceiver = IRequestFinishedReceiver { requestStatus ->
        runOnUiThread {
            when (requestStatus) {
                RequestStatus.SUCCESS -> Log.d("FinishedReceiver", "Request Successfully Sent")
                RequestStatus.FAIL_PLUGINS_SERVICE_VERSION -> Log.d("FinishedReceiver", "Plugin Service Upgrade Required?")
                else -> Log.d("FinishedReceiver", "Request Failed to be Sent")
            }
        }
    }

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
                AntPlusBikePowerEvent.AutoZeroStatus.toString() -> subscribeAutoZeroStatus(isOnlyNewData)
                AntPlusBikePowerEvent.CalculatedCrankCadence.toString() -> subscribeCalculatedCrankCadence(isOnlyNewData)
                AntPlusBikePowerEvent.CalculatedPower.toString() -> subscribeCalculatedPower(isOnlyNewData)
                AntPlusBikePowerEvent.CalculatedTorque.toString() -> subscribeCalculatedTorque(isOnlyNewData)
                AntPlusBikePowerEvent.CalculatedWheelDistance.toString() -> subscribeCalculatedWheelDistance(isOnlyNewData)
                AntPlusBikePowerEvent.CalculatedWheelSpeed.toString() -> subscribeCalculatedWheelSpeed(isOnlyNewData)
                AntPlusBikePowerEvent.CalibrationMessage.toString() -> subscribeCalibrationMessage(isOnlyNewData)
                AntPlusBikePowerEvent.CrankParameters.toString() -> subscribeCrankParameters(isOnlyNewData)
                AntPlusBikePowerEvent.InstantaneousCadence.toString() -> subscribeInstantaneousCadence(isOnlyNewData)
                AntPlusBikePowerEvent.MeasurementOutputData.toString() -> subscribeMeasurementOutputData(isOnlyNewData)
                AntPlusBikePowerEvent.PedalPowerBalance.toString() -> subscribePedalPowerBalance(isOnlyNewData)
                AntPlusBikePowerEvent.PedalSmoothness.toString() -> subscribePedalSmoothness(isOnlyNewData)
                AntPlusBikePowerEvent.RawCrankTorqueData.toString() -> subscribeRawCrankTorqueData(isOnlyNewData)
                AntPlusBikePowerEvent.RawCtfData.toString() -> subscribeRawCtfData(isOnlyNewData)
                AntPlusBikePowerEvent.RawPowerOnlyData.toString() -> subscribeRawPowerOnlyData(isOnlyNewData)
                AntPlusBikePowerEvent.RawWheelTorqueData.toString() -> subscribeRawWheelTorqueData(isOnlyNewData)
                AntPlusBikePowerEvent.TorqueEffectiveness.toString() -> subscribeTorqueEffectiveness(isOnlyNewData)

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
                AntPlusBikePowerEvent.AutoZeroStatus.toString() -> unsubscribeAutoZeroStatus()
                AntPlusBikePowerEvent.CalculatedCrankCadence.toString() -> unsubscribeCalculatedCrankCadence()
                AntPlusBikePowerEvent.CalculatedPower.toString() -> unsubscribeCalculatedPower()
                AntPlusBikePowerEvent.CalculatedTorque.toString() -> unsubscribeCalculatedTorque()
                AntPlusBikePowerEvent.CalculatedWheelDistance.toString() -> unsubscribeCalculatedWheelDistance()
                AntPlusBikePowerEvent.CalculatedWheelSpeed.toString() -> unsubscribeCalculatedWheelSpeed()
                AntPlusBikePowerEvent.CalibrationMessage.toString() -> unsubscribeCalibrationMessage()
                AntPlusBikePowerEvent.CrankParameters.toString() -> unsubscribeCrankParameters()
                AntPlusBikePowerEvent.InstantaneousCadence.toString() -> unsubscribeInstantaneousCadence()
                AntPlusBikePowerEvent.MeasurementOutputData.toString() -> unsubscribeMeasurementOutputData()
                AntPlusBikePowerEvent.PedalPowerBalance.toString() -> unsubscribePedalPowerBalance()
                AntPlusBikePowerEvent.PedalSmoothness.toString() -> unsubscribePedalSmoothness()
                AntPlusBikePowerEvent.RawCrankTorqueData.toString() -> unsubscribeRawCrankTorqueData()
                AntPlusBikePowerEvent.RawCtfData.toString() -> unsubscribeRawCtfData()
                AntPlusBikePowerEvent.RawPowerOnlyData.toString() -> unsubscribeRawPowerOnlyData()
                AntPlusBikePowerEvent.RawWheelTorqueData.toString() -> unsubscribeRawWheelTorqueData()
                AntPlusBikePowerEvent.TorqueEffectiveness.toString() -> unsubscribeTorqueEffectiveness()

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

            eventData.putString("event", AntPlusBikePowerEvent.AutoZeroStatus.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putString("autoZeroStatus", autoZeroStatus.toString())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `autoZeroStatus` - `string` - The AntPlusBikePowerPcc.AutoZeroStatus currently known for the power meter, aggregated from multiple calibration page types.
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

            eventData.putString("event", AntPlusBikePowerEvent.CalculatedCrankCadence.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putString("dataSource", dataSource.toString())
            eventData.putDouble("calculatedCrankCadence", calculatedCrankCadence.toDouble())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//          - `dataSource` - `string` - The AntPlusBikePowerPcc.DataSource indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
//          - `calculatedCrankCadence` - `number` - The average crank cadence calculated from sensor data. Units: RPM.
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

            eventData.putString("event", AntPlusBikePowerEvent.CalculatedPower.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putString("dataSource", dataSource.toString())
            eventData.putDouble("calculatedPower", calculatedPower.toDouble())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `dataSource` - `string` - The AntPlusBikePowerPcc.DataSource indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
//            - `calculatedPower` - `number` - The average power calculated from sensor data. Units: W.
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

            eventData.putString("event", AntPlusBikePowerEvent.CalculatedTorque.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putString("dataSource", dataSource.toString())
            eventData.putDouble("calculatedTorque", calculatedTorque.toDouble())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//          - `dataSource` - `string` - The AntPlusBikePowerPcc.DataSource indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
//          - `calculatedTorque` - `number` - The average torque calculated from sensor data. Units: Nm.
        }
    }

    private fun unsubscribeCalculatedWheelDistance() {
        bikePower!!.subscribeCalculatedWheelDistanceEvent(null)
        deviceData.remove("dataSource")
        deviceData.remove("calculatedWheelDistance")
    }

    private fun subscribeCalculatedWheelDistance(isOnlyNewData: Boolean) {
        val wheelCircumferenceInMeters = BigDecimal("2.07")

        bikePower!!.subscribeCalculatedWheelDistanceEvent(object : CalculatedWheelDistanceReceiver(
            wheelCircumferenceInMeters
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

                    eventData.putString("event", AntPlusBikePowerEvent.CalculatedWheelDistance.toString())
                    eventData.putInt("estTimestamp", estTimestamp.toInt())
                    eventData.putString("eventFlags", eventFlags.toString())

                    eventData.putString("dataSource", dataSource.toString())
                    eventData.putDouble("calculatedWheelDistance", calculatedWheelDistance.toDouble())

                    antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//          - `dataSource` - `string` - The AntPlusBikePowerPcc.DataSource indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
//          - `calculatedWheelDistance` - `string` - The accumulated distance calculated from sensor data. Units: m.
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
        val wheelCircumferenceInMeters = BigDecimal(2.07)

        bikePower!!.subscribeCalculatedWheelSpeedEvent(object : CalculatedWheelSpeedReceiver(wheelCircumferenceInMeters) {
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

                    eventData.putString("event", AntPlusBikePowerEvent.CalculatedWheelSpeed.toString())
                    eventData.putInt("estTimestamp", estTimestamp.toInt())
                    eventData.putString("eventFlags", eventFlags.toString())

                    eventData.putString("dataSource", dataSource.toString())
                    eventData.putDouble("calculatedWheelSpeed", calculatedWheelSpeed.toDouble())

                    antPlus.sendEvent(AntPlusEvent.bikePower, eventData)
                }
            }
        })

//            - `dataSource` - `string` - The AntPlusBikePowerPcc.DataSource indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
//            - `calculatedWheelSpeed` - `number` - The average speed calculated from sensor data. Units: km/h.
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

            eventData.putString("event", AntPlusBikePowerEvent.CalibrationMessage.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            val message = Arguments.createMap()

            message.putString("calibrationId", calibrationMessage.calibrationId.toString())
            message.putInt("calibrationData", calibrationMessage.calibrationData)
            message.putInt("ctfOffset", calibrationMessage.ctfOffset)
            try {
                message.putArray("manufacturerSpecificData", antPlus.bytesToWritableArray(calibrationMessage.manufacturerSpecificData))
            } catch (throwable: Throwable) {
                Log.e("ManufacturerSpecific", "bytesToWritableArray", throwable)
            }

            eventData.putMap("calibrationMessage", message)

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)


//            - `calibrationId` - `string` - The calibration ID sent by the power sensor
//            - `calibrationData` - `number` - The calibration data sent by the power sensor
//            - `ctfOffset` - `number` - The CTF Zero Offset sent by the CTF power sensor
//            - `manufacturerSpecificData` - `number[]` - The 6 manufacturer specific bytes sent by the power sensor
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

            eventData.putString("event", AntPlusBikePowerEvent.CrankParameters.toString())
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

//            - `crankParameters` - `object` - fullCrankLength, crankLengthStatus, sensorSoftwareMismatchStatus, sensorAvailabilityStatus, customCalibrationStatus, isAutoCrankLengthSupported
//            - `fullCrankLength` - `string` - The crank length value set in the power meter (up to 0.5mm resolution). Units: mm.
//            - `crankLengthStatus` - `string` - The AntPlusBikePowerPcc.CrankLengthStatus of the power meter.
//            - `sensorSoftwareMismatchStatus` - `string` - The AntPlusBikePowerPcc.SensorSoftwareMismatchStatus of the power meter.
//            - `sensorAvailabilityStatus` - `string` - The AntPlusBikePowerPcc.SensorAvailabilityStatus of the power meter.
//            - `customCalibrationStatus` - `string` - The AntPlusBikePowerPcc.CustomCalibrationStatus of the power meter, indicating if custom calibration is required.
//            - `isAutoCrankLengthSupported` - `boolean` - Indicates if the power meter is capable of automatically determining crank length.
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

//          - `dataSource` - `string` - The AntPlusBikePowerPcc.DataSource indicating which data page type this field was generated from.
//          - `instantaneousCadence` - `number` - Instantaneous cadence valid for display, computed by sensor (up to 1RPM resolution). '-1' = Invalid data. Units: RPM.
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

            eventData.putString("event", AntPlusBikePowerEvent.MeasurementOutputData.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putInt("numOfDataTypes", numOfDataTypes)
            eventData.putInt("dataType", dataType)
            eventData.putDouble("timeStamp", timeStamp.toDouble())
            eventData.putDouble("measurementValue", measurementValue.toDouble())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `numOfDataTypes` - `number` - The total number of different data types the power meter intends to send.
//            - `dataType` - `number` - The data type and engineering units applicable to the measurement value.
//            - `timeStamp` - `number` - The timestamp corresponding to the instantaneous measurement value. Units: s. Rollover: Every ~4.4 quadrillion s.
//            - `measurementValue` - `number` - The signed data value sent by the power meter, the data type may be used to infer the type of data being received.
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

            eventData.putString("event", AntPlusBikePowerEvent.PedalPowerBalance.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putBoolean("rightPedalIndicator", rightPedalIndicator)
            eventData.putInt("pedalPowerPercentage", pedalPowerPercentage)

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `rightPedalIndicator` - `boolean` - Indicates if the power meter is reporting the right pedal power (true), or if it does not know which pedal it is reporting (false).
//            - `pedalPowerPercentage` - `number` - The percentage of the user's total power contribution to a single pedal (up to 1% resolution). Units: %.

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

            eventData.putString("event", AntPlusBikePowerEvent.PedalSmoothness.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putInt("powerOnlyUpdateEventCount", powerOnlyUpdateEventCount.toInt())
            eventData.putBoolean("separatePedalSmoothnessSupport", separatePedalSmoothnessSupport)
            eventData.putDouble("leftOrCombinedPedalSmoothness", powerOnlyUpdateEventCount.toDouble())
            eventData.putDouble("rightPedalSmoothness", rightPedalSmoothness.toDouble())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `powerOnlyUpdateEventCount` - `number` - This field is incremented each time the sensor updates power-only data and is linked to the same field in the Power-Only Data Event. It can be used to help graph and associate data received in this event. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
//            - `separatePedalSmoothnessSupport` - `boolean` - Indicates if the power meter supports separate pedal smoothness or combined pedal smoothness reporting.
//            - `leftOrCombinedPedalSmoothness` - `number` - The left (or combined) pedal smoothness as determined by the sensor (up to 1/2% resolution. '-1' = Invalid or negative data. Units: %.
//            - `rightPedalSmoothness` - `number` - The right pedal smoothness as determined by the sensor (up to 1/2% resolution. '-1' = Invalid or negative data. Units: %.
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

            eventData.putString("event", AntPlusBikePowerEvent.RawCrankTorqueData.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putInt("crankTorqueUpdateEventCount", crankTorqueUpdateEventCount.toInt())
            eventData.putInt("accumulatedCrankTicks", accumulatedCrankTicks.toInt())
            eventData.putDouble("accumulatedCrankPeriod", accumulatedCrankPeriod.toDouble())
            eventData.putDouble("accumulatedCrankTorque", accumulatedCrankTorque.toDouble())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `crankTorqueUpdateEventCount` - `number` - This field is incremented each time the sensor updates crank torque data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
//            - `accumulatedCrankTicks` - `number` - The crank ticks increment with each crank revolution and indicates a full rotation of the crank. For systems that update synchronously with crank events (event-synchronous), the crank ticks and update event count increment at the same rate. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: rotations. Rollover: Every ~9 quintillion rotations.
//            - `accumulatedCrankPeriod` - `number` - Used to indicate the average rotation period of the crank during the last update interval, in increments of 1/2048s. Each crank period tick represents a 488-microsecond interval. In event-synchronous systems, the accumulated crank period field rolls over in 32 seconds. In fixed update (time-synchronous) systems the time to rollover depends on wheel speed, but is greater than 32 seconds. Units: s. Rollover: Every ~4 quadrillion s.
//            - `accumulatedCrankTorque` - `number` - The cumulative sum of the average torque measured every crank rotation event (up to 1/32 Nm resolution). Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: Nm. Rollover: Every ~280 quadrillion Nm.
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

            eventData.putString("event", AntPlusBikePowerEvent.RawCtfData.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putInt("ctfUpdateEventCount", ctfUpdateEventCount.toInt())
            eventData.putDouble("instantaneousSlope", instantaneousSlope.toDouble())
            eventData.putDouble("accumulatedTimeStamp", accumulatedTimeStamp.toDouble())
            eventData.putInt("accumulatedTorqueTicksStamp", accumulatedTorqueTicksStamp.toInt())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `ctfUpdateEventCount` - `number` - The update event count increments with each complete pedal stroke. The update event count is used to indicate the number of cadence events that have occurred between two consecutively received messages. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
//            - `instantaneousSlope` - `number` - The variation of the output frequency (up to 1/10 Nm/Hz resolution). Units: Nm/Hz.
//            - `accumulatedTimeStamp` - `number` - The crank torque-frequency message uses a 2000Hz clock to time cadence events. The time stamp field indicates the time of the most recent cadence event. Each time stamp tick represents a 500-microsecond interval. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: s. Rollover: Every ~5 quadrillion s.
//            - `accumulatedTorqueTicksStamp` - `number` - Represents the most recent value of torque ticks since the last registered revolution. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
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
            eventData.putString("event", AntPlusBikePowerEvent.RawPowerOnlyData.toString())
            eventData.putInt("powerOnlyUpdateEventCount", powerOnlyUpdateEventCount.toInt())
            eventData.putInt("instantaneousPower", instantaneousPower)
            eventData.putInt("accumulatedPower", accumulatedPower.toInt())
            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `powerOnlyUpdateEventCount` - `number` - This field is incremented each time the sensor updates power-only data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
//            - `instantaneousPower` - `number` - Instantaneous power computed by the sensor valid for display (up to 1W resolution). Units: W.
//            - `accumulatedPower` - `number` - Accumulated power is the running sum of the instantaneous power data and is incremented at each update of the update event count (up to 1W resolution). Can be used to help calculate power between RF outages. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: W. Rollover: Every ~9 quintillion W.
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

            eventData.putString("event", AntPlusBikePowerEvent.RawWheelTorqueData.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())


            eventData.putInt("wheelTorqueUpdateEventCount", wheelTorqueUpdateEventCount.toInt())
            eventData.putInt("accumulatedWheelTicks", accumulatedWheelTicks.toInt())
            eventData.putDouble("accumulatedWheelPeriod", accumulatedWheelPeriod.toDouble())
            eventData.putDouble("accumulatedWheelTorque", accumulatedWheelTorque.toDouble())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `wheelTorqueUpdateEventCount` - `number` - This field is incremented each time the sensor updates wheel torque data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
//            - `accumulatedWheelTicks` - `number` - The wheel ticks field increments with each wheel revolution and is used to calculate linear distance traveled (up to 1 rotation resolution). For event-synchronous systems, the wheel ticks and update event count increment at the same rate. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: rotations. Rollover: Every ~9 quintillion rotations.
//            - `accumulatedWheelPeriod` - `number` - The average rotation period of the wheel during the last update interval, in increments of 1/2048s. Each Wheel Period tick represents a 488-microsecond interval. In event-synchronous systems, the accumulated wheel period time stamp field rolls over in 32 seconds. In fixed time interval update systems, the time to rollover depends on wheel speed but is greater than 32 seconds. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: s. Rollover: Every ~4 quadrillion s.
//            - `accumulatedWheelTorque` - `number` - The cumulative sum of the average torque measured every update event count (up to 1/32 Nm resolution). Do NOT use wheel ticks to calculate linear speed. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: Nm. Rollover: Every ~280 quadrillion Nm.
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

            eventData.putString("event", AntPlusBikePowerEvent.TorqueEffectiveness.toString())
            eventData.putInt("estTimestamp", estTimestamp.toInt())
            eventData.putString("eventFlags", eventFlags.toString())

            eventData.putInt("powerOnlyUpdateEventCount", powerOnlyUpdateEventCount.toInt())
            eventData.putDouble("leftTorqueEffectiveness", leftTorqueEffectiveness.toDouble())
            eventData.putDouble("rightTorqueEffectiveness", rightTorqueEffectiveness.toDouble())

            antPlus.sendEvent(AntPlusEvent.bikePower, eventData)

//            - `powerOnlyUpdateEventCount` - `number` - This field is incremented each time the sensor updates power-only data and is linked to the same field in the Power-Only Data Event. It can be used to help graph and associate data received in this event. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
//            - `leftTorqueEffectiveness` - `number` - The Torque Effectiveness is calculated for each crank arm based on the positive (clockwise) and negative (anti-clockwise) torque applied to the crank over each revolution (up to 1/2% resolution). This is the torque effectiveness calculated for the left leg by the sensor. '-1' = Invalid or negative data. Units: %.
//            - `rightTorqueEffectiveness` - `number` - The Torque Effectiveness is calculated for each crank arm based on the positive (clockwise) and negative (anti-clockwise) torque applied to the crank over each revolution (up to 1/2% resolution). This is the torque effectiveness calculated for the right leg by the sensor. '-1' = Invalid or negative data. Units: %.
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
                eventData.putArray("rawDataBytes", antPlus.bytesToWritableArray(rawDataBytes))
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
        when (AntPlusBikePowerRequest.valueOf(requestName)) {
            AntPlusBikePowerRequest.CommandBurst -> requestCommandBurst(args, promise)
            AntPlusBikePowerRequest.CrankParameters -> requestCrankParameters(promise)
            AntPlusBikePowerRequest.CustomCalibrationParameters -> requestCustomCalibrationParameters(args, promise)
            AntPlusBikePowerRequest.ManualCalibration -> requestManualCalibration(promise)
            AntPlusBikePowerRequest.SetAutoZero -> requestSetAutoZero(args, promise)
            AntPlusBikePowerRequest.SetCrankParameters -> requestSetCrankParameters(args, promise)
            AntPlusBikePowerRequest.SetCtfSlope -> requestSetCtfSlope(args, promise)
            AntPlusBikePowerRequest.SetCustomCalibrationParameters -> requestSetCustomCalibrationParameters(args, promise)
        }
    }

    private fun requestCommandBurst(args: ReadableMap, promise: Promise) {
        val requestedCommandId = args.getInt("requestedCommandId")
        val commandData = args.getArray("commandData")?.let { antPlus.writableArrayToBytes(it) }

        bikePower!!.requestCommandBurst(requestedCommandId, commandData) { requestStatus ->
            promise.resolve(requestStatus.toString())
        }
    }

    private fun	requestCrankParameters(promise: Promise) {
        bikePower!!.requestCrankParameters { requestStatus ->
            promise.resolve(requestStatus.toString())
        }
    }

    private fun	requestCustomCalibrationParameters(args: ReadableMap, promise: Promise) {
        val manufacturerSpecificParameters = args.getArray("manufacturerSpecificParameters")?.let { antPlus.writableArrayToBytes(it) }

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
        val manufacturerSpecificParameters = args.getArray("manufacturerSpecificParameters")?.let { antPlus.writableArrayToBytes(it) }

        bikePower!!.requestSetCustomCalibrationParameters(manufacturerSpecificParameters)  { requestStatus ->
            promise.resolve(requestStatus.toString())
        }
    }


    protected var resultReceiver =
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
}
