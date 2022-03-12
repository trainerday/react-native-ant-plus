# react-native-ant-plus

[![npm downloads](https://img.shields.io/npm/dm/react-native-ant-plus?style=flat)](https://www.npmjs.com/package/react-native-ant-plus)
[![GitHub issues](https://img.shields.io/github/issues/trainerday/react-native-ant-plus.svg?style=flat)](https://github.com/trainerday/react-native-ant-plus/issues)

An Ant+ module for React Native

>### Important
>The library is in the early stages of development!


### At the moment devices supported:

- BIKE_CADENCE (122)
- BIKE_POWER (11)
- WEIGHT_SCALE (119)
- HEARTRATE (120)

## Supported Platforms

- Android

# Installation

```sh
npm install react-native-ant-plus
```

# Usage

```js
import AntPlus from "react-native-ant-plus"
```

You can take a look at an [example](https://github.com/trainerday/react-native-ant-plus/tree/master/example) to get a better understanding of how to use it.

## Methods

### startSearch(antPlusDeviceTypes, seconds, allowRssi)

This feature allows an application to search for multiple ANT+ device type simultaneously with a single ANT channel.
Returns a `Promise` object.

**Arguments**

- `antPlusDeviceTypes` - `Array of Integer` - the ANT+ Device Types.
- `seconds` - `Integer` - the amount of seconds to search.
- `allowRssi` - `Boolean` - allow the RSSI event.

**Example**

```js
const HEARTRATE = 120
const BIKE_POWER = 11

const antPlusDeviceTypes = [HEARTRATE, BIKE_POWER]
const secondsSearch = 30
const allowRssi = false

AntPlus.startSearch(antPlusDeviceTypes, secondsSearch, allowRssi).then(responce => {
  console.log(responce.isSearching);
});
```

### stopSearch()

Stop device search.
Returns a `Promise` object.

**Example**

```js
AntPlus.stopSearch().then(isStopped => {
  console.log(`Search devices stopped: ${isStopped}`)
}).catch(error => {
  console.log(error)
})
```

### connect(antDeviceNumber, antPlusDeviceType)

Attempts to connect to a device.
Returns a `Promise` object.

**Arguments**

- `antDeviceNumber` - `Integer` - the Ant device number.
- `antPlusDeviceType` - `Integer` - the ANT+ Device Type.

**Example**

```js
const antDeviceNumber = 012345
const antPlusDeviceType = AntPlusDeviceType.HEARTRATE

AntPlus.connect(antDeviceNumber, antPlusDeviceType).then(responce => {
  console.log(`Device ${responce.name}, state: ${responce.state}, is connected: ${responce.connected}`)
}).catch(error => {
  console.log(error)
});
```

### disconnect(antDeviceNumber)

Disconnect to a device.
Returns a `Promise` object.

**Arguments**

- `antDeviceNumber` - `Integer` - the Ant device number.

**Example**

```js
const antDeviceNumber = 012345

AntPlus.disconnect(antDeviceNumber).then(isDisconnect => {
  console.log(`Device disconnected: ${isDisconnect}`)
}).catch(error => {
  console.log(error)
})
```

### subscribe(antDeviceNumber, events, isOnlyNewData)

Subscribe to receive events.
Returns a `Promise` object.

**Arguments**

- `antDeviceNumber` - `Integer` - the Ant device number.
- `events` - `Array of String` - supported device events.
- `isOnlyNewData` - `Boolean` - receive only if the values have changed.

**Example**

```js
const antDeviceNumber = 012345
const events = ['HeartRateData', 'Rssi']
const isOnlyNewData = true

AntPlus.subscribe(antDeviceNumber, events, isOnlyNewData).then(isSubscribed => {
  console.log(`Subscribed: ${isSubscribed}`)
}).catch(error => {
  console.log(error)
})
```

### unsubscribe(antDeviceNumber, events)

Unsubscribe to receive events.
Returns a `Promise` object.

**Arguments**

- `antDeviceNumber` - `Integer` - the Ant device number.
- `events` - `Array of String` - supported device events.

**Example**

```js
const antDeviceNumber = 012345
const events = ['HeartRateData', 'Rssi']

AntPlus.unsubscribe(antDeviceNumber, events).then(isUnsubscribed => {
  console.log(`Unsubscribed: ${isUnsubscribed}`)
}).catch(error => {
  console.log(error)
})
```

>### request(antDeviceNumber, requestName, args) [Experimental]
>
>Send a request to a plugin service.
>Returns a `Promise` object.
>
>**Arguments**
>
>- `antDeviceNumber` - `Integer` - the Ant device number.
>- `requestName` - `String` - Name of request.
>- `args` - `ReadableMap` - Arguments for the request.
>
>**Example**
>
>```js
>const antDeviceNumber = 012345
>const requestName = 'BasicMeasurement'
>const args = {}
>
>AntPlus.request(antDeviceNumber, requestName, args)
>```

>### setVariables(antDeviceNumber, variables) [Experimental]
>
>Change a variable in the plugin.
>Returns a `Promise` object.
>
>**Arguments**
>
>- `antDeviceNumber` - `Integer` - the Ant device number.
>- `variables` - `ReadableMap` - Variables you want to change.
>
>**Example**
>
>```js
>const antDeviceNumber = 012345
>const variables = {wheelCircumferenceInMeters: 2.06}
>
>AntPlus.setVariables(antDeviceNumber, variables)
>```

## Events

### searchStatus

Devices search status.

**Arguments**

- `isSearching` - `boolean` - is searching.
- `reason` - `string?` - reason for stopping search.

**Example**

```js
AntPlusEmitter.addListener('searchStatus', arguments => {})
```

### foundDevice

The searching find a new device.

**Arguments**

- `resultID` - `number` - result ID.
- `describeContents` - `string` - describe contents.
- `antDeviceNumber` - `number` - the Ant device number.
- `antPlusDeviceTypeName` - `string` - the name device type.
- `antPlusDeviceType` - `number` - the ANT+ Device Type.
- `deviceDisplayName` - `string` - the user's saved name for the device if it exists in the ANT+ Plugin Service database or a default generated name based on the device number.
- `isAlreadyConnected` - `boolean` - indicates if device is already connected to another application.
- `isPreferredDevice` - `boolean` - indicates if device is set as preferred in the user's ANT+ Plugin Service database.
- `isUserRecognizedDevice` - `boolean` - indicates if device exists in the user's ANT+ Plugin Service database.
- `rssi` - `number?` - rssi signal.

**Example**

```js
AntPlusEmitter.addListener('foundDevice', arguments => {})
```

### rssi

The rssi signal while the search is going on.

**Arguments**

- `resultID` - `number` - result ID.
- `rssi` - `number?` - rssi signal.

**Example**

```js
AntPlusEmitter.addListener('rssi', arguments => {})
```

### devicesStateChange

The rssi signal while the search is going on.

**Arguments**

- `event` - `string` - DeviceStateChangeReceiver
- `antDeviceNumber` - `number` - the Ant device number.
- `state` - `number` - DEAD | CLOSED | SEARCHING | TRACKING | PROCESSING_REQUEST | UNRECOGNIZED

**Example**

```js
AntPlusEmitter.addListener('devicesStateChange', arguments => {})
```

### bikeCadence

Event listener for the BikeCadence plugin service

**Arguments**

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

CalculatedCadence - event
- `calculatedCadence` - `number` - The cadence calculated from the raw values in the sensor broadcast. Units: rpm.

MotionAndCadence - event
- `isPedallingStopped` - `boolean` - False indicates the user is pedalling, true indicates the user has stopped pedalling.

RawCadence - event
- `timestampOfLastEvent` - `number` - Sensor reported time counter value of last event (up to 1/1024s accuracy). Units: s. Rollover: Every ~46 quadrillion s (~1.5 billion years).
- `cumulativeRevolutions` - `number` - Total number of revolutions since the sensor was connected. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: revolutions. Rollover: Every ~9 quintillion revolutions.

ManufacturerIdentification - event
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

ManufacturerSpecific - event
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

ProductInformation - event
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

Rssi - event
- `rssi` - `number` - rssi signal.

**Example**

```js
AntPlusEmitter.addListener('bikeCadence', arguments => {})
```

### bikePower

Event listener for the BikePower plugin service

**Arguments**

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

AutoZeroStatus - event
- `autoZeroStatus` - `string` - The [AutoZeroStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.AutoZeroStatus.html) currently known for the power meter, aggregated from multiple calibration page types.

CalculatedCrankCadence - event
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedCrankCadence` - `number` - The average crank cadence calculated from sensor data. Units: RPM.

CalculatedPower - event
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedPower` - `number` - The average power calculated from sensor data. Units: W.

CalculatedTorque - event
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedTorque` - `number` - The average torque calculated from sensor data. Units: Nm.

CalculatedWheelDistance - event
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedWheelDistance` - `string` - The accumulated distance calculated from sensor data. Units: m.

CalculatedWheelSpeed - event
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedWheelSpeed` - `number` - The average speed calculated from sensor data. Units: km/h.

CalibrationMessage - event
- `calibrationId` - `string` - The calibration ID sent by the power sensor
- `calibrationData` - `number` - The calibration data sent by the power sensor
- `ctfOffset` - `number` - The CTF Zero Offset sent by the CTF power sensor
- `manufacturerSpecificData` - `number[]` - The 6 manufacturer specific bytes sent by the power sensor

CrankParameters - event
- `crankParameters` - `object` - `fullCrankLength`, `crankLengthStatus`, `sensorSoftwareMismatchStatus`, `sensorAvailabilityStatus`, `customCalibrationStatus`, `isAutoCrankLengthSupported`
- `fullCrankLength` - `string` - The crank length value set in the power meter (up to 0.5mm resolution). Units: mm.
- `crankLengthStatus` - `string` - The [CrankLengthStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.CrankLengthStatus.html)  of the power meter.
- `sensorSoftwareMismatchStatus` - `string` - The [SensorSoftwareMismatchStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.SensorSoftwareMismatchStatus.html) of the power meter.
- `sensorAvailabilityStatus` - `string` - The AntPlusBikePowerPcc.SensorAvailabilityStatus of the power meter.
- `customCalibrationStatus` - `string` - The AntPlusBikePowerPcc.CustomCalibrationStatus of the power meter, indicating if custom calibration is required.
- `isAutoCrankLengthSupported` - `boolean` - Indicates if the power meter is capable of automatically determining crank length.

InstantaneousCadence - event
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which data page type this field was generated from.
- `instantaneousCadence` - `number` - Instantaneous cadence valid for display, computed by sensor (up to 1RPM resolution). '-1' = Invalid data. Units: RPM.

MeasurementOutputData - event
- `numOfDataTypes` - `number` - The total number of different data types the power meter intends to send.
- `dataType` - `number` - The data type and engineering units applicable to the measurement value.
- `timeStamp` - `number` - The timestamp corresponding to the instantaneous measurement value. Units: s. Rollover: Every ~4.4 quadrillion s.
- `measurementValue` - `number` - The signed data value sent by the power meter, the data type may be used to infer the type of data being received.

PedalPowerBalance - event
- `rightPedalIndicator` - `boolean` - Indicates if the power meter is reporting the right pedal power (true), or if it does not know which pedal it is reporting (false).
- `pedalPowerPercentage` - `number` - The percentage of the user's total power contribution to a single pedal (up to 1% resolution). Units: %.

PedalSmoothness - event
- `powerOnlyUpdateEventCount` - `number` - This field is incremented each time the sensor updates power-only data and is linked to the same field in the Power-Only Data Event. It can be used to help graph and associate data received in this event. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `separatePedalSmoothnessSupport` - `boolean` - Indicates if the power meter supports separate pedal smoothness or combined pedal smoothness reporting.
- `leftOrCombinedPedalSmoothness` - `number` - The left (or combined) pedal smoothness as determined by the sensor (up to 1/2% resolution. '-1' = Invalid or negative data. Units: %.
- `rightPedalSmoothness` - `number` - The right pedal smoothness as determined by the sensor (up to 1/2% resolution. '-1' = Invalid or negative data. Units: %.

RawCrankTorqueData - event
- `crankTorqueUpdateEventCount` - `number` - This field is incremented each time the sensor updates crank torque data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `accumulatedCrankTicks` - `number` - The crank ticks increment with each crank revolution and indicates a full rotation of the crank. For systems that update synchronously with crank events (event-synchronous), the crank ticks and update event count increment at the same rate. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: rotations. Rollover: Every ~9 quintillion rotations.
- `accumulatedCrankPeriod` - `number` - Used to indicate the average rotation period of the crank during the last update interval, in increments of 1/2048s. Each crank period tick represents a 488-microsecond interval. In event-synchronous systems, the accumulated crank period field rolls over in 32 seconds. In fixed update (time-synchronous) systems the time to rollover depends on wheel speed, but is greater than 32 seconds. Units: s. Rollover: Every ~4 quadrillion s.
- `accumulatedCrankTorque` - `number` - The cumulative sum of the average torque measured every crank rotation event (up to 1/32 Nm resolution). Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: Nm. Rollover: Every ~280 quadrillion Nm.

RawCtfData - event
- `ctfUpdateEventCount` - `number` - The update event count increments with each complete pedal stroke. The update event count is used to indicate the number of cadence events that have occurred between two consecutively received messages. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `instantaneousSlope` - `number` - The variation of the output frequency (up to 1/10 Nm/Hz resolution). Units: Nm/Hz.
- `accumulatedTimeStamp` - `number` - The crank torque-frequency message uses a 2000Hz clock to time cadence events. The time stamp field indicates the time of the most recent cadence event. Each time stamp tick represents a 500-microsecond interval. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: s. Rollover: Every ~5 quadrillion s.
- `accumulatedTorqueTicksStamp` - `number` - Represents the most recent value of torque ticks since the last registered revolution. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.

RawPowerOnlyData - event
- `powerOnlyUpdateEventCount` - `number` - This field is incremented each time the sensor updates power-only data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `instantaneousPower` - `number` - Instantaneous power computed by the sensor valid for display (up to 1W resolution). Units: W.
- `accumulatedPower` - `number` - Accumulated power is the running sum of the instantaneous power data and is incremented at each update of the update event count (up to 1W resolution). Can be used to help calculate power between RF outages. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: W. Rollover: Every ~9 quintillion W.

RawWheelTorqueData - event
- `wheelTorqueUpdateEventCount` - `number` - This field is incremented each time the sensor updates wheel torque data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `accumulatedWheelTicks` - `number` - The wheel ticks field increments with each wheel revolution and is used to calculate linear distance traveled (up to 1 rotation resolution). For event-synchronous systems, the wheel ticks and update event count increment at the same rate. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: rotations. Rollover: Every ~9 quintillion rotations.
- `accumulatedWheelPeriod` - `number` - The average rotation period of the wheel during the last update interval, in increments of 1/2048s. Each Wheel Period tick represents a 488-microsecond interval. In event-synchronous systems, the accumulated wheel period time stamp field rolls over in 32 seconds. In fixed time interval update systems, the time to rollover depends on wheel speed but is greater than 32 seconds. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: s. Rollover: Every ~4 quadrillion s.
- `accumulatedWheelTorque` - `number` - The cumulative sum of the average torque measured every update event count (up to 1/32 Nm resolution). Do NOT use wheel ticks to calculate linear speed. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: Nm. Rollover: Every ~280 quadrillion Nm.

TorqueEffectiveness - event
- `powerOnlyUpdateEventCount` - `number` - This field is incremented each time the sensor updates power-only data and is linked to the same field in the Power-Only Data Event. It can be used to help graph and associate data received in this event. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `leftTorqueEffectiveness` - `number` - The Torque Effectiveness is calculated for each crank arm based on the positive (clockwise) and negative (anti-clockwise) torque applied to the crank over each revolution (up to 1/2% resolution). This is the torque effectiveness calculated for the left leg by the sensor. '-1' = Invalid or negative data. Units: %.
- `rightTorqueEffectiveness` - `number` - The Torque Effectiveness is calculated for each crank arm based on the positive (clockwise) and negative (anti-clockwise) torque applied to the crank over each revolution (up to 1/2% resolution). This is the torque effectiveness calculated for the right leg by the sensor. '-1' = Invalid or negative data. Units: %.

ManufacturerIdentification - event
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (resolution indicated by cumulativeOperatingTimeResolution]). Rollover: Every 16777215s*resolution. ie:~1.1yrs at 2s resolution, ~8.5yrs at 16s resolution.
- `batteryVoltage` - `string` - Current battery voltage. Invalid = -1. Units: Volts (with 1/256V resolution).
- `batteryStatus` - `string` - The current reported BatteryStatus.
- `cumulativeOperatingTimeResolution` - `number` - The resolution accuracy of the cumulativeOperatingTime. Units: seconds.
- `numberOfBatteries` - `number` - Specifies how many batteries are available in the system. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+
- `batteryIdentifier` - `number` - Identifies the battery in system to which this battery status pertains. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+

ManufacturerIdentification - event
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

ManufacturerSpecific - event
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

ProductInformation - event
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

Rssi - event
- `rssi` - `number` - rssi signal.

**Example**

```js
AntPlusEmitter.addListener('bikePower', arguments => {})
```

### weightScale

Event listener for the WeightScale plugin service

**Arguments**

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

BodyWeightBroadcast - event
- `bodyWeightStatus` - `string` -  The [BodyWeightStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusWeightScalePcc.BodyWeightStatus.html) of the current broadcast. The bodyWeight parameter will only be non-null if this parameter is VALID.
- `bodyWeight` - `number` -  Body weight value of current broadcast. Units: Kg.

ManufacturerIdentification - event
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (resolution indicated by cumulativeOperatingTimeResolution]). Rollover: Every 16777215s*resolution. ie:~1.1yrs at 2s resolution, ~8.5yrs at 16s resolution.
- `batteryVoltage` - `string` - Current battery voltage. Invalid = -1. Units: Volts (with 1/256V resolution).
- `batteryStatus` - `string` - The current reported BatteryStatus.
- `cumulativeOperatingTimeResolution` - `number` - The resolution accuracy of the cumulativeOperatingTime. Units: seconds.
- `numberOfBatteries` - `number` - Specifies how many batteries are available in the system. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+
- `batteryIdentifier` - `number` - Identifies the battery in system to which this battery status pertains. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+

ManufacturerIdentification - event
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

ManufacturerSpecific - event
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

ProductInformation - event
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

Rssi - event
- `rssi` - `number` - rssi signal.

**Example**

```js
AntPlusEmitter.addListener('weightScale', arguments => {})
```

### heartRate

Event listener for the HeartRate plugin service.

**Arguments**

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

CalculatedRrInterval - event
- `rrInterval` - `number` - Current R-R interval, calculated by the plugin. Units: ms. Invalid if value is negative.
- `rrFlag` - `string` - Indicates how the RR interval was calculated.

HeartRateData - event
- `heartRate` - `number` - Current heart rate valid for display, computed by sensor. Units: BPM.
- `heartBeatCount` - `number` - Heart beat count. Units: beats. Rollover: Every ~9 quintillion beats.
- `heartBeatEventTime` - `number` - Sensor reported time counter value of last distance or speed computation (up to 1/1024s accuracy). Units: s. Rollover: Every ~9 quadrillion s.
- `dataState` - `number` - The state of the data. If stale, app should indicate to the user that the device is not active. @since 2.1.7; supported on Plugin Service 2.2.8+. Earlier versions of the service will only send LIVE_DATA flag.

Page4AddtData - event
- `manufacturerSpecificByte` - `number` - Defined by manufacturer. Receivers do not need to interpret this byte. Units: Defined by manufacturer.
- `previousHeartBeatEventTime` - `number` - The time of the previous valid heart beat event (up to 1/1024s resolution). Units: s. Rollover: Every ~9 quadrillion s.

CumulativeOperatingTime - event
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (w/ 2s resolution). Rollover: Every 33554430s seconds (w/ 2s resolution) (388 days).

ManufacturerAndSerial - event
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `serialNumber` - `number` - This is the upper 16 bits of a 32 bit serial number.

VersionAndModel - event
- `hardwareVersion` - `number` - Manufacturer defined.
- `softwareVersion` - `number` - Manufacturer defined.
- `modelNumber` - `number` - Manufacturer defined.

Rssi - event
- `rssi` - `number` - rssi signal.

**Example**

```js
AntPlusEmitter.addListener('heartRate', arguments => {})
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
