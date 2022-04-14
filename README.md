# react-native-ant-plus

[![npm downloads](https://img.shields.io/npm/dm/react-native-ant-plus?style=flat)](https://www.npmjs.com/package/react-native-ant-plus)
[![GitHub issues](https://img.shields.io/github/issues/trainerday/react-native-ant-plus.svg?style=flat)](https://github.com/trainerday/react-native-ant-plus/issues)

An Ant+ module for React Native

>### Important
>The library is in the early stages of development!


### At the moment devices supported:

- BIKE_POWER (11)
- FITNESS_EQUIPMENT (17)
- ENVIRONMENT (25)
- WEIGHT_SCALE (119)
- HEARTRATE (120)
- BIKE_SPDCAD (121)
- BIKE_CADENCE (122)
- BIKE_SPD (123)
- STRIDE_SDM (124)

## Supported Platforms

- Android

# Installation

```sh
npm install react-native-ant-plus
```

### Android - Update Manifest

```xml
<!-- file: android/app/src/main/AndroidManifest.xml -->

<manifest
  xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:tools="http://schemas.android.com/tools"
  package="YOUR_PACKAGE_NAME">
  ... your uses-permissions

<!--  for Android 11 or higher -->
  <queries>
    <package android:name="com.dsi.ant.plugins.antplus"/>
  </queries>

  ...

  <application>
    ...
  </application>
</manifest>
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
- `antPlusDeviceType` - `Integer` - the ANT+ Device Type.

**Example**

```js
const antDeviceNumber = 012345
const antPlusDeviceType = AntPlusDeviceType.HEARTRATE

AntPlus.disconnect(antDeviceNumber, antPlusDeviceType).then(isDisconnect => {
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
- `antPlusDeviceType` - `Integer` - the ANT+ Device Type.
- `events` - `Array of String` - supported device events.
- `isOnlyNewData` - `Boolean` - receive only if the values have changed.

**Example**

```js
const antDeviceNumber = 012345
const antPlusDeviceType = AntPlusDeviceType.HEARTRATE
const events = ['HeartRateData', 'Rssi']
const isOnlyNewData = true

AntPlus.subscribe(antDeviceNumber, antPlusDeviceType, events, isOnlyNewData).then(isSubscribed => {
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
- `antPlusDeviceType` - `Integer` - the ANT+ Device Type.
- `events` - `Array of String` - supported device events.

**Example**

```js
const antDeviceNumber = 012345
const antPlusDeviceType = AntPlusDeviceType.HEARTRATE
const events = ['HeartRateData', 'Rssi']

AntPlus.unsubscribe(antDeviceNumber, antPlusDeviceType, events).then(isUnsubscribed => {
  console.log(`Unsubscribed: ${isUnsubscribed}`)
}).catch(error => {
  console.log(error)
})
```

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

## Plugin services

You have the feature to interact with plugin services: change variables, make requests and subscribe to events

### setVariables(antDeviceNumber, variables) [Experimental]

Change a variable in the plugin.
Returns a `Promise` object.

**Arguments**

- `antDeviceNumber` - `Integer` - the Ant device number.
- `antPlusDeviceType` - `Integer` - the ANT+ Device Type.
- `variables` - `ReadableMap` - Variables you want to change.

**Example**

```js
const antDeviceNumber = 012345
const antPlusDeviceType = AntPlusDeviceType.HEARTRATE
const variables = {wheelCircumferenceInMeters: 2.06}

AntPlus.setVariables(antDeviceNumber, antPlusDeviceType, variables)
```

### request(antDeviceNumber, requestName, args)

Send a request to a plugin service.
Returns a `Promise` object.

**Arguments**

- `antDeviceNumber` - `Integer` - the Ant device number.
- `antPlusDeviceType` - `Integer` - the ANT+ Device Type.
- `requestName` - `String` - Name of request.
- `args` - `ReadableMap` - Arguments for the request.

**Example**

```js
const antDeviceNumber = 012345
const antPlusDeviceType = AntPlusDeviceType.HEARTRATE
const requestName = 'BasicMeasurement'
const args = {}

AntPlus.request(antDeviceNumber, antPlusDeviceType, requestName, args)
```

### request(antDeviceNumber, requestName, args)

Send a request to a plugin service.
Returns a `Promise` object.

**Arguments**

- `antDeviceNumber` - `Integer` - the Ant device number.
- `antPlusDeviceType` - `Integer` - the ANT+ Device Type.
- `requestName` - `String` - Name of request.
- `args` - `ReadableMap` - Arguments for the request.

**Example**

```js
const antDeviceNumber = 012345
const antPlusDeviceType = AntPlusDeviceType.HEARTRATE
const requestName = 'BasicMeasurement'
const args = {}

AntPlus.request(antDeviceNumber, antPlusDeviceType, requestName, args)
```


## bikePower

### Variables
`wheelCircumference` - `Double` - Default "2.07"

### Events Arguments

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

#### AutoZeroStatus
- `autoZeroStatus` - `string` - The [AutoZeroStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.AutoZeroStatus.html) currently known for the power meter, aggregated from multiple calibration page types.

#### CalculatedCrankCadence
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedCrankCadence` - `number` - The average crank cadence calculated from sensor data. Units: RPM.

#### CalculatedPower
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedPower` - `number` - The average power calculated from sensor data. Units: W.

#### CalculatedTorque
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedTorque` - `number` - The average torque calculated from sensor data. Units: Nm.

#### CalculatedWheelDistance
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedWheelDistance` - `string` - The accumulated distance calculated from sensor data. Units: m.

#### CalculatedWheelSpeed
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which type of data was used for calculation or if this is a new starting average value. A new starting value could be indicated when starting the plugin, or if reception is lost for too long to guarantee accurate data. If using a new starting value to record data, please use this event as a new starting point in time.
- `calculatedWheelSpeed` - `number` - The average speed calculated from sensor data. Units: km/h.

#### CalibrationMessage
- `calibrationId` - `string` - The calibration ID sent by the power sensor
- `calibrationData` - `number` - The calibration data sent by the power sensor
- `ctfOffset` - `number` - The CTF Zero Offset sent by the CTF power sensor
- `manufacturerSpecificData` - `number[]` - The 6 manufacturer specific bytes sent by the power sensor

#### CrankParameters
- `crankParameters` - `object` - `fullCrankLength`, `crankLengthStatus`, `sensorSoftwareMismatchStatus`, `sensorAvailabilityStatus`, `customCalibrationStatus`, `isAutoCrankLengthSupported`
- `fullCrankLength` - `string` - The crank length value set in the power meter (up to 0.5mm resolution). Units: mm.
- `crankLengthStatus` - `string` - The [CrankLengthStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.CrankLengthStatus.html)  of the power meter.
- `sensorSoftwareMismatchStatus` - `string` - The [SensorSoftwareMismatchStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.SensorSoftwareMismatchStatus.html) of the power meter.
- `sensorAvailabilityStatus` - `string` - The AntPlusBikePowerPcc.SensorAvailabilityStatus of the power meter.
- `customCalibrationStatus` - `string` - The AntPlusBikePowerPcc.CustomCalibrationStatus of the power meter, indicating if custom calibration is required.
- `isAutoCrankLengthSupported` - `boolean` - Indicates if the power meter is capable of automatically determining crank length.

#### InstantaneousCadence
- `dataSource` - `string` - The [DataSource](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusBikePowerPcc.DataSource.html) indicating which data page type this field was generated from.
- `instantaneousCadence` - `number` - Instantaneous cadence valid for display, computed by sensor (up to 1RPM resolution). '-1' = Invalid data. Units: RPM.

#### MeasurementOutputData
- `numOfDataTypes` - `number` - The total number of different data types the power meter intends to send.
- `dataType` - `number` - The data type and engineering units applicable to the measurement value.
- `timeStamp` - `number` - The timestamp corresponding to the instantaneous measurement value. Units: s. Rollover: Every ~4.4 quadrillion s.
- `measurementValue` - `number` - The signed data value sent by the power meter, the data type may be used to infer the type of data being received.

#### PedalPowerBalance
- `rightPedalIndicator` - `boolean` - Indicates if the power meter is reporting the right pedal power (true), or if it does not know which pedal it is reporting (false).
- `pedalPowerPercentage` - `number` - The percentage of the user's total power contribution to a single pedal (up to 1% resolution). Units: %.

#### PedalSmoothness
- `powerOnlyUpdateEventCount` - `number` - This field is incremented each time the sensor updates power-only data and is linked to the same field in the Power-Only Data Event. It can be used to help graph and associate data received in this event. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `separatePedalSmoothnessSupport` - `boolean` - Indicates if the power meter supports separate pedal smoothness or combined pedal smoothness reporting.
- `leftOrCombinedPedalSmoothness` - `number` - The left (or combined) pedal smoothness as determined by the sensor (up to 1/2% resolution. '-1' = Invalid or negative data. Units: %.
- `rightPedalSmoothness` - `number` - The right pedal smoothness as determined by the sensor (up to 1/2% resolution. '-1' = Invalid or negative data. Units: %.

#### RawCrankTorqueData
- `crankTorqueUpdateEventCount` - `number` - This field is incremented each time the sensor updates crank torque data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `accumulatedCrankTicks` - `number` - The crank ticks increment with each crank revolution and indicates a full rotation of the crank. For systems that update synchronously with crank events (event-synchronous), the crank ticks and update event count increment at the same rate. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: rotations. Rollover: Every ~9 quintillion rotations.
- `accumulatedCrankPeriod` - `number` - Used to indicate the average rotation period of the crank during the last update interval, in increments of 1/2048s. Each crank period tick represents a 488-microsecond interval. In event-synchronous systems, the accumulated crank period field rolls over in 32 seconds. In fixed update (time-synchronous) systems the time to rollover depends on wheel speed, but is greater than 32 seconds. Units: s. Rollover: Every ~4 quadrillion s.
- `accumulatedCrankTorque` - `number` - The cumulative sum of the average torque measured every crank rotation event (up to 1/32 Nm resolution). Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: Nm. Rollover: Every ~280 quadrillion Nm.

#### RawCtfData
- `ctfUpdateEventCount` - `number` - The update event count increments with each complete pedal stroke. The update event count is used to indicate the number of cadence events that have occurred between two consecutively received messages. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `instantaneousSlope` - `number` - The variation of the output frequency (up to 1/10 Nm/Hz resolution). Units: Nm/Hz.
- `accumulatedTimeStamp` - `number` - The crank torque-frequency message uses a 2000Hz clock to time cadence events. The time stamp field indicates the time of the most recent cadence event. Each time stamp tick represents a 500-microsecond interval. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: s. Rollover: Every ~5 quadrillion s.
- `accumulatedTorqueTicksStamp` - `number` - Represents the most recent value of torque ticks since the last registered revolution. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.

#### RawPowerOnlyData
- `powerOnlyUpdateEventCount` - `number` - This field is incremented each time the sensor updates power-only data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `instantaneousPower` - `number` - Instantaneous power computed by the sensor valid for display (up to 1W resolution). Units: W.
- `accumulatedPower` - `number` - Accumulated power is the running sum of the instantaneous power data and is incremented at each update of the update event count (up to 1W resolution). Can be used to help calculate power between RF outages. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: W. Rollover: Every ~9 quintillion W.

#### RawWheelTorqueData
- `wheelTorqueUpdateEventCount` - `number` - This field is incremented each time the sensor updates wheel torque data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `accumulatedWheelTicks` - `number` - The wheel ticks field increments with each wheel revolution and is used to calculate linear distance traveled (up to 1 rotation resolution). For event-synchronous systems, the wheel ticks and update event count increment at the same rate. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: rotations. Rollover: Every ~9 quintillion rotations.
- `accumulatedWheelPeriod` - `number` - The average rotation period of the wheel during the last update interval, in increments of 1/2048s. Each Wheel Period tick represents a 488-microsecond interval. In event-synchronous systems, the accumulated wheel period time stamp field rolls over in 32 seconds. In fixed time interval update systems, the time to rollover depends on wheel speed but is greater than 32 seconds. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: s. Rollover: Every ~4 quadrillion s.
- `accumulatedWheelTorque` - `number` - The cumulative sum of the average torque measured every update event count (up to 1/32 Nm resolution). Do NOT use wheel ticks to calculate linear speed. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: Nm. Rollover: Every ~280 quadrillion Nm.

#### TorqueEffectiveness
- `powerOnlyUpdateEventCount` - `number` - This field is incremented each time the sensor updates power-only data and is linked to the same field in the Power-Only Data Event. It can be used to help graph and associate data received in this event. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `leftTorqueEffectiveness` - `number` - The Torque Effectiveness is calculated for each crank arm based on the positive (clockwise) and negative (anti-clockwise) torque applied to the crank over each revolution (up to 1/2% resolution). This is the torque effectiveness calculated for the left leg by the sensor. '-1' = Invalid or negative data. Units: %.
- `rightTorqueEffectiveness` - `number` - The Torque Effectiveness is calculated for each crank arm based on the positive (clockwise) and negative (anti-clockwise) torque applied to the crank over each revolution (up to 1/2% resolution). This is the torque effectiveness calculated for the right leg by the sensor. '-1' = Invalid or negative data. Units: %.

#### ManufacturerIdentification
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (resolution indicated by cumulativeOperatingTimeResolution]). Rollover: Every 16777215s*resolution. ie:~1.1yrs at 2s resolution, ~8.5yrs at 16s resolution.
- `batteryVoltage` - `string` - Current battery voltage. Invalid = -1. Units: Volts (with 1/256V resolution).
- `batteryStatus` - `string` - The current reported BatteryStatus.
- `cumulativeOperatingTimeResolution` - `number` - The resolution accuracy of the cumulativeOperatingTime. Units: seconds.
- `numberOfBatteries` - `number` - Specifies how many batteries are available in the system. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+
- `batteryIdentifier` - `number` - Identifies the battery in system to which this battery status pertains. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+

#### ManufacturerIdentification
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

#### ManufacturerSpecific
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

#### ProductInformation
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

#### Rssi
- `rssi` - `number` - rssi signal.


## fitnessEquipment

### Variables
`wheelCircumference` - `Double` - Default "0.7"

### Requests

#### Capabilities
Send a request to the device to set the user configuration. This data is optional and may not be supported by all devices.
Returns a `Promise` object.

***Returns***
- `basicResistanceModeSupport` - `boolean` - Supports Basic Resistance mode.
- `maximumResistance` - `number` - The maximum applicable resistance of the trainer. Units: N. Valid range: 0 N - 65534 N. Resolution: 1 N.
- `simulationModeSupport` - `boolean` - Supports Simulation mode.
- `targetPowerModeSupport` - `boolean` - Supports Target Power mode.

#### SetUserConfiguration
Send a request to the device to set the user configuration. This data is optional and may not be supported by all devices.
Returns a `Promise` object.

***Arguments***
- `bicycleWeight` - `Double` - Bicycle weight.
- `gearRatio` - `Double` - Front to back gear ratio.
- `bicycleWheelDiameter` - `Double` - Bicycle wheel diameter.
- `userWeight` - `Double` - User weight.

***Returns***
- `requestStatus` - `string` - The RequestStatus for the requested operation. Note that even if a command is sent successfully, there is no guaranteed response from the sensor. Please contact the manufacturer for more information on how this request is handled.

#### SpinDownCalibration
Send a request to the device to start spin down calibration. The subscribeTrainerStatusEvent may request this be done by the user. The command may not be supported by all devices. If this command is supported you may begin to receive calibrationInProgress messages for a period of time before receiving the calibration response.
Returns a `Promise` object.

***Returns***
- `requestStatus` - `string` - The RequestStatus for the requested operation. Note that even if a command is sent successfully, there is no guaranteed response from the sensor. Please contact the manufacturer for more information on how this request is handled.

#### UserConfiguration
Send a request to the device to send the user configuration. This data is optional and may not be supported by all devices.
Returns a `Promise` object.

***Returns***
- `bicycleWeight` - `Double` - Bicycle weight.
- `gearRatio` - `Double` - Front to back gear ratio.
- `bicycleWheelDiameter` - `Double` - Bicycle wheel diameter.
- `userWeight` - `Double` - User weight.


#### ZeroOffsetCalibration
Send a request to the device to start zero offset calibration. The subscribeTrainerStatusEvent may request this be done by the user. The command may not be supported by all devices. If this command is supported you may begin to receive calibrationInProgress messages for a period of time before receiving the calibration response.
Returns a `Promise` object.

***Returns***
- `requestStatus` - `string` - The RequestStatus for the requested operation. Note that even if a command is sent successfully, there is no guaranteed response from the sensor. Please contact the manufacturer for more information on how this request is handled.


#### BasicResistance
Send a request to the device to send the basic resistance.
Returns a `Promise` object.

***Returns***
- `totalResistance` - `number` - Percentage of maximum resistance to be applied. Units: %. Valid range: 0% - 100%. Resolution: 0.5%.

#### CommandStatus
Send a request to the device to send the command status.
Returns a `Promise` object.

***Returns***
- `draftingFactor` - `number` - The drafting factor is used to set the resistance reduction due to travelling behind a virtual competitor.
- `grade` - `number` - Grade of simulated track.
- `lastReceivedCommandId` - `string` - Indicates data page number of the last control page received.
- `lastReceivedSequenceNumber` - `number` - 0 to 254: Sequence number used by Slave in last received command request.
- `rawResponseData` - `number[]` - Response data bytes specific to received command ID.
- `rollingResistanceCoefficient` - `number` - The coefficient of rolling resistance is a dimensionless factor used to quantify rolling resistance based on the friction between the bicycle tires and the track surface.
- `status - `string` - The command status of the last received command by the fitness equipment.
- `targetPower` - `number` - The target power for fitness equipment operating in target power mode.
- `totalResistance` - `number` - Percentage of maximum resistance to be applied.
- `windResistanceCoefficient` - `number` - Product of Frontal Surface Area, Drag Coefficient and Air Density.
- `windSpeed` - `number` - Speed of simulated wind acting on the cyclist.


#### SetBasicResistance
Send a request to the device to set the basic resistance. This data is optional and may not be supported by all devices. Use the [requestCapabilities](#Capabilities) method first to find if it is supported. The current total resistance setting can be gathered from the [subscribeGeneralSettingsEvent](#GeneralSettings) event.
Returns a `Promise` object.

***Arguments***
- `totalResistance` - `Double` - Percentage of maximum resistance to be applied. Units: %. Valid range: 0% - 100%. Resolution: 0.5%.

***Returns***
- `requestStatus` - `string` - The RequestStatus for the requested operation. Note that even if a command is sent successfully, there is no guaranteed response from the sensor. Please contact the manufacturer for more information on how this request is handled.


#### SetTargetPower
Send a request to the device to set the target power. This data is optional and may not be supported by all devices. Use the [requestCapabilities](#Capabilities) method first to find if it is supported.
Returns a `Promise` object.

***Arguments***
- `target` - `Double` - The target power for fitness equipment operating in target power mode. Units: W. Valid range: 0W - 1000W. Resolution: 0.25W

***Returns***
- `requestStatus` - `string` - The RequestStatus for the requested operation. Note that even if a command is sent successfully, there is no guaranteed response from the sensor. Please contact the manufacturer for more information on how this request is handled.


#### SetTrackResistance
Send a request to the device to set the track resistance. This data is optional and may not be supported by all devices. Use the [requestCapabilities](#Capabilities) method first to find if it is supported.
Returns a `Promise` object.

***Arguments***
- `grade` - `Double` - Grade of simulated track. Gravitational resistance is calculated using the grade of the simulated track and the combined mass of the user plus fitness equipment. A default value of 0% will be assumed if set to null. Units: %. Valid range: -200.00% - 200.00%. Resolution: 0.01.
- `rollingResistanceCoefficient` - `Double` - The coefficient of rolling resistance is a dimensionless factor used to quantify rolling resistance based on the friction between the bicycle tires and the track surface. A default value of 0.004 will be assumed if set to null.

***Returns***
- `requestStatus` - `string` - The RequestStatus for the requested operation. Note that even if a command is sent successfully, there is no guaranteed response from the sensor. Please contact the manufacturer for more information on how this request is handled.


#### SetWindResistance
Send a request to the device to set the wind resistance. This data is optional and may not be supported by all devices. Use the [requestCapabilities](#Capabilities) method first to find if it is supported.
Returns a `Promise` object.

***Arguments***
- `frontalSurfaceArea` - `Double` - The frontal surface area of the user plus virtual equipment. Default values are used if field is null. Units: m^2.
- `dragCoefficient` - `Double` - The drag coefficient is a dimensionless factor used to quantify air resistance based on how streamlined the user plus virtual equipment is. Default values are used if field is null. Units: none.
- `airDensity` - `Double` - The air density is set in units of kilograms per cubic meter. Default values are used if field is null. Air density is dependent on the temperature, elevation, and humidity of the simulated track. The standard density of air, 1.275kg/m3 (15C at sea level) may be used as the default value for the air density field.
- `windSpeed` - `Double` - Speed of simulated wind acting on the cyclist. (+) - Head Wind (-) - Tail Wind. Default value of 0 km/h is used if field is null. Units: km/h. Valid range: -127km/h - 127km/h. Resolution: 1km/h.
- `draftingFactor` - `Double` - The drafting factor is used to set the resistance reduction due to travelling behind a virtual competitor. The drafting factor scales the total wind resistance depending on the position of the user relative to other virtual competitors. The drafting scale factor ranges from 0.0 to 1.0, where 0.0 removes all air resistance from the simulation, and 1.0 indicates no drafting effects (e.g. cycling alone, or in the lead of a pack). Default value of 1.00 is used if field is null. Units: none. Valid range: 0.00 - 1.00. Resolution: 0.01.

***or***
- `windResistanceCoefficient` - `Double` - Product of Frontal Surface Area, Drag Coefficient and Air Density. Default value of 0.51 kg/m is used if field is null. Units: kg/m. Valid range: 0kg/m - 1.86kg/m. Resolution: 0.01kg/m.
- `windSpeed` - `Double` - Speed of simulated wind acting on the cyclist. (+) - Head Wind (-) - Tail Wind. Default value of 0 km/h is used if field is null. Units: km/h. Valid range: -127km/h - 127km/h. Resolution: 1km/h.
- `draftingFactor` - `Double` - The drafting factor is used to set the resistance reduction due to travelling behind a virtual competitor. The drafting factor scales the total wind resistance depending on the position of the user relative to other virtual competitors. The drafting scale factor ranges from 0.0 to 1.0, where 0.0 removes all air resistance from the simulation, and 1.0 indicates no drafting effects (e.g. cycling alone, or in the lead of a pack). Default value of 1.00 is used if field is null. Units: none. Valid range: 0.00 - 1.00. Resolution: 0.01.

***Returns***
- `requestStatus` - `string` - The RequestStatus for the requested operation. Note that even if a command is sent successfully, there is no guaranteed response from the sensor. Please contact the manufacturer for more information on how this request is handled.


#### TargetPower
Send a request to the device to send the target power. This data is optional and may not be supported by all devices. Use the [requestCapabilities](#Capabilities) method first to find if it is supported.
Returns a `Promise` object.

***Returns***
- `targetPower` - `number` - The target power for fitness equipment operating in target power mode. Units: W. Valid range: 0W - 1000W. Resolution: 0.25W.


#### TrackResistance
Send a request to the device to send the track resistance. This data is optional and may not be supported by all devices. Use the [requestCapabilities](#Capabilities) method first to find if it is supported.
Returns a `Promise` object.

***Returns***
- `grade` - `number` - Grade of simulated track. Gravitational resistance is calculated using the grade of the simulated track and the combined mass of the user plus fitness equipment. Units: %. Valid range: -200.00% - 200.00%. Resolution: 0.01.
- `rollingResistanceCoefficient` - `number` - The coefficient of rolling resistance is a dimensionless factor used to quantify rolling resistance based on the friction between the bicycle tires and the track surface. Units: none. Valid range: 0 - 0.0127. Resolution: 5x10^-5.


#### WindResistance
Send a request to the device to send the wind resistance. This data is optional and may not be supported by all devices. Use the [requestCapabilities](#Capabilities) method first to find if it is supported.
Returns a `Promise` object.

***Returns***
- `windResistanceCoefficient` - `number` - Product of Frontal Surface Area, Drag Coefficient and Air Density. Units: kg/m. Valid range: 0kg/m - 1.86kg/m. Resolution: 0.01kg/m.
- `windSpeed` - `number` - Speed of simulated wind acting on the cyclist. (+) - Head Wind (-) - Tail Wind. Units: km/h. Valid range: -127km/h - 127km/h. Resolution: 1km/h.
- `draftingFactor` - `number` - The drafting factor is used to set the resistance reduction due to travelling behind a virtual competitor. The drafting factor scales the total wind resistance depending on the position of the user relative to other virtual competitors. The drafting scale factor ranges from 0.0 to 1.0, where 0.0 removes all air resistance from the simulation, and 1.0 indicates no drafting effects (e.g. cycling alone, or in the lead of a pack). Units: none. Valid range: 0.00 - 1.00. Resolution: 0.01.


### Events

***Arguments***

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

#### CalibrationInProgress
- `calibrationInProgress` - `object` - `currentTemperature`, `speedCondition`, `spinDownCalibrationPending`, `targetSpeed`, `targetSpinDownTime`, `temperatureCondition`, `zeroOffsetCalibrationPending`
- `currentTemperature` - `number` - The fitness equipment may set this field to indicate its temperature or null. Units: C. Valid range: -25C - +100C. Resolution: 0.5C
- `speedCondition` - `string` - Indicates whether the speed conditions for successful calibration are currently met by the fitness equipment.
- `spinDownCalibrationPending` - `boolean` - Spin down calibration is pending.
- `targetSpeed` - `number` - The fitness equipment should set this field to indicate the speed that should be reached in order to perform a spin-down calibration. Units: m/s. Valid range: 0m/s - 65.534m/s. Resolution: 0.001m/s
- `targetSpinDownTime` - `number` - The fitness equipment should set this field to indicate the ideal spin-down time. Units: ms. Valid range: 0ms - 65534ms. Resolution: 1ms.
- `temperatureCondition` - `number` - Indicates whether the temperature conditions for successful calibration are currently met by the fitness equipment.
- `zeroOffsetCalibrationPending` - `boolean` - Zero offset calibration is pending.

#### CalibrationResponse
- `calibrationResponse` - `object` - `spinDownCalibrationSuccess`, `spinDownTime`, `temperature`, `zeroOffset`, `zeroOffsetCalibrationSuccess`
- `spinDownCalibrationSuccess` - `boolean` - Spin down calibration completed successfully if true. Failure or not attempted if false.
- `spinDownTime` - `number` - Some trainers use spin-down time to calibrate the resistance applied by the trainer. This is typically done by requesting the user to pedal at a known speed and then remove their feet from the pedals. The time required for the rear bike wheel and/or trainer roller to stop spinning is known as the spin-down time. The fitness equipment should set this field to indicate its spin-down calibration time if a spin-down calibration was requested, otherwise it shall be set to null. Units: ms. Valid range: 0ms - 65534ms. Resolution: 1ms
- `temperature` - `number` - The fitness equipment may set this field to indicate its temperature or null.
- `zeroOffset` - `number` - The fitness equipment should set this field to indicate its zero offset if a zero offset calibration was requested, otherwise it shall be set to null. Units: none. Valid range: 0 - 65534. Resolution: 1
- `zeroOffsetCalibrationSuccess` - `boolean` - Zero offset calibration completed successfully if true. Failure or not attempted if false.

#### Capabilities
- `capabilities` - `object` - `maximumResistance`, `simulationModeSupport`, `targetPowerModeSupport`.
- `maximumResistance` - `number` - The maximum applicable resistance of the trainer.
- `simulationModeSupport` - `boolean` - Supports Simulation mode.
- `targetPowerModeSupport` - `boolean` - Supports Target Power mode.

#### GeneralFitnessEquipmentData
- `elapsedTime` - `number` - Total elapsed duration of the workout from the time the plugin connected to the fitness equipment (up to 1/4s resolution). Units: s. Rollover: Every ~2.3 quintillion s.
- `cumulativeDistance` - `number` - Total distance from the time the plugin connected to this device (up to 1m resolution). Units: m. Rollover: Every ~9 quintillion m.
- `instantaneousSpeed` - `number` - Instantaneous speed computed by sensor valid for display (up to 0.001 m/s resolution). Units: m/s.
- `virtualInstantaneousSpeed` - `boolean` - Flag indicating if the instantaneous speed field represents virtual speed or real speed. (since pluginLib 2.1.8; requires Plugin Service 2.2.9+)
- `instantaneousHeartRate` - `number` - Instantaneous heart rate data valid for display. Units: bpm.
- `heartRateDataSource` - `string` - The AntPlusFitnessEquipmentPcc.HeartRateDataSource of the connected fitness equipment.

#### GeneralMetabolicData
- `instantaneousMetabolicEquivalents` - `number` - Instantaneous measure of the rate of energy expenditure (up to 0.01 MET resolution). Units: METs.
- `instantaneousCaloricBurn` - `number` - Instantaneous value of the caloric burn rate with 0.1 Cal/hr resolution. Units: kcal/hr.
- `cumulativeCalories` - `number` - Total number of calories consumed from the time the plugin connected to the fitness equipment. Units: kcal. Rollover: Every ~9 quintillion kcal.

#### GeneralSettings
- `cycleLength` - `number` - The cycle length field provides information on the length of a single complete "cycle" on the FE. For a treadmill or elliptical machine, this would be the stride length. It could also be used to indicate step height on a climber, or stroke length on a rower (up to 0.01m resolution). Units: m.
- `inclinePercentage` - `number` - The incline percentage field provides the treadmill's percentage incline with 0.01% resolution and a valid range from -100.00% to +100.00%. Units: %.
- `resistanceLevel` - `number` - The resistance level setting of the FE or FE-C device. This is sent as a positive integer value between 1 and 254 as an FE device, or as a positive percentage with 0.5% units up to 100%.

#### LapOccured
- `lapCount` - `number` - The total number of laps completed by the user recorded from the time the plugin connected to the fitness equipment. Units: laps.

#### UserConfiguration
- `userConfiguration` - `object` - `bicycleWeight`, `bicycleWheelDiameter`, `gearRatio`, `userWeight`
- `bicycleWeight` - `number` -Bicycle weight.
- `bicycleWheelDiameter` - `number` - Bicycle wheel diameter.
- `gearRatio` - `number` - Front to back gear ratio.
- `userWeight` - `number` - User weight.

#### Treadmill
- `instantaneousCadence` - `number` - Instantaneous cadence valid for display. Units: strides/min.
- `cumulativeNegVertDistance` - `number` - Total vertical distance traveled down, or the total distance descended from the time the plugin connected to the fitness equipment (up to 0.1m resolution). Units: m. Rollover: Every ~900 quadrillion m.
- `cumulativePosVertDistance` - `number` - Total vertical distance traveled up, or the total distance ascended from the time the plugin connected to the fitness equipment (up to 0.1m resolution). Units: m. Rollover: Every ~900 quadrillion m.

#### ClimberData
- `cumulativeStrideCycles` - `number` - Total number of stride cycles (i.e. number of steps climbed/2) taken from the time the plugin connected to the fitness equipment. Units: strokes. Rollover: Every ~9 quintillion strokes.
- `instantaneousCadence` - `number` - Instantaneous cadence valid for display. Units: cycles/min or RPM.
- `instantaneousPower` - `number` - Instantaneous power valid for display. Units: Watts.

#### EllipticalData
- `cumulativePosVertDistance` -`number` - Total vertical distance traveled up, or the total distance ascended from the time the plugin connected to the fitness equipment (up to 0.1m resolution). Units: m. Rollover: Every ~900 quadrillion m.
- `cumulativeStrides` -`number` - Total number of strides taken during the session from the time the plugin connected to the fitness equipment. Units: strides. Rollover: Every ~9 quintillion strides.
- `instantaneousCadence` -`number` - Instantaneous cadence valid for display. Units: strides/min.
- `instantaneousPower` -`number` - Instantaneous power valid for display. Units: Watts.

#### NordicSkierData
- `cumulativeStrides` - `number` - Total number of strides taken from the time the plugin connected to the fitness equipment. Units: strides. Rollover: Every ~9 quintillion strides.
- `instantaneousCadence` - `number` - Instantaneous cadence valid for display. Units: strides/min.
- `instantaneousPower` - `number` - Instantaneous power valid for display. Units: Watts.

#### RowerData
- `cumulativeStrokes` - `number` - Total number of strokes taken from the time the plugin connected to the fitness equipment. Units: strokes. Rollover: Every ~9 quintillion strokes.
- `instantaneousCadence` - `number` - Instantaneous cadence valid for display. Units: strokes/min.
- `instantaneousPower` - `number` - Instantaneous power valid for display. Units: Watts.

#### BikeData
- `instantaneousCadence` - `number` - Instantaneous cadence valid for display. Units: RPM.
- `instantaneousPower` - `number` - Instantaneous power valid for display. Units: Watts.

#### BasicResistance
- `totalResistance` -`number` - Percentage of maximum resistance to be applied. Units: %. Valid range: 0% - 100%. Resolution: 0.5%.

#### CalculatedTrainerDistance
- `calculatedDistance` - `number` - The accumulated distance calculated from sensor data. Units: m.

#### CalculatedTrainerPower
- `calculatedPower` - `number` - The average power calculated from sensor data. Units: W.

#### CalculatedTrainerSpeed
- `calculatedSpeed` - `number` - The average speed calculated from sensor data. Units: km/h.

#### CommandStatus
- `draftingFactor` - `number` - The drafting factor is used to set the resistance reduction due to travelling behind a virtual competitor.
- `grade` - `number` - Grade of simulated track.
- `lastReceivedCommandId` - `string` - Indicates data page number of the last control page received.
- `lastReceivedSequenceNumber` - `number` - 0 to 254: Sequence number used by Slave in last received command request.
- `rawResponseData` - `number[]` - Response data bytes specific to received command ID.
- `rollingResistanceCoefficient` - `number` - The coefficient of rolling resistance is a dimensionless factor used to quantify rolling resistance based on the friction between the bicycle tires and the track surface.
- `status - `string` - The command status of the last received command by the fitness equipment.
- `targetPower` - `number` - The target power for fitness equipment operating in target power mode.
- `totalResistance` - `number` - Percentage of maximum resistance to be applied.
- `windResistanceCoefficient` - `number` - Product of Frontal Surface Area, Drag Coefficient and Air Density.
- `windSpeed` - `number` - Speed of simulated wind acting on the cyclist.

#### RawTrainerData
- `updateEventCount` - `number` - This field is incremented each time the sensor updates trainer data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `instantaneousCadence` - `number` - Instantaneous cadence valid for display, computed by sensor (up to 1RPM resolution). '-1' = Invalid data. Units: RPM.
- `instantaneousPower` - `number` - Instantaneous power computed by the sensor valid for display (up to 1W resolution). Units: W.
- `accumulatedPower` - `number` - Accumulated power is the running sum of the instantaneous power data and is incremented at each update of the update event count (up to 1W resolution). Can be used to help calculate power between RF outages. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: W. Rollover: Every ~9 quintillion W.

#### RawTrainerTorqueData
- `updateEventCount` - `number` - This field is incremented each time the sensor updates trainer torque data. Can be used to help calculate power between RF outages and to determine if the data from the sensor is new. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Rollover: Every ~9 quintillion N/A.
- `accumulatedWheelTicks` - `number` - The wheel ticks field increments with each wheel revolution and is used to calculate linear distance traveled (up to 1 rotation resolution). For event-synchronous systems, the wheel ticks and update event count increment at the same rate. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: rotations. Rollover: Every ~9 quintillion rotations.
- `accumulatedWheelPeriod` - `number` - The average rotation period of the wheel during the last update interval, in increments of 1/2048s. Each Wheel Period tick represents a 488-microsecond interval. In event-synchronous systems, the accumulated wheel period time stamp field rolls over in 32 seconds. In fixed time interval update systems, the time to rollover depends on wheel speed but is greater than 32 seconds. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: s. Rollover: Every ~4 quadrillion s.
- `accumulatedTorque` - `number` - The cumulative sum of the average torque measured every update event count (up to 1/32 Nm resolution). Do NOT use wheel ticks to calculate linear speed. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: Nm. Rollover: Every ~280 quadrillion Nm.

#### TargetPower
- `targetPower` - `number` - The target power for fitness equipment operating in target power mode. Units: W. Valid range: 0W - 1000W. Resolution: 0.25W.

#### TrackResistance
- `grade` - `number` - Grade of simulated track. Gravitational resistance is calculated using the grade of the simulated track and the combined mass of the user plus fitness equipment. Units: %. Valid range: -200.00% - 200.00%. Resolution: 0.01.
- `rollingResistanceCoefficient` - `number` - The coefficient of rolling resistance is a dimensionless factor used to quantify rolling resistance based on the friction between the bicycle tires and the track surface. Units: none. Valid range: 0 - 0.0127. Resolution: 5x10^-5.

#### TrainerStatus
- `trainerStatusFlags` - `string` - Status flags for trainers which indicate possibly required user intervention.

#### WindResistance
- `windResistanceCoefficient` - `number` - Product of Frontal Surface Area, Drag Coefficient and Air Density. Units: kg/m. Valid range: 0kg/m - 1.86kg/m. Resolution: 0.01kg/m.
- `windSpeed` - `number` - Speed of simulated wind acting on the cyclist. (+) - Head Wind (-) - Tail Wind. Units: km/h. Valid range: -127km/h - 127km/h. Resolution: 1km/h.
- `draftingFactor` - `number` - The drafting factor is used to set the resistance reduction due to travelling behind a virtual competitor. The drafting factor scales the total wind resistance depending on the position of the user relative to other virtual competitors. The drafting scale factor ranges from 0.0 to 1.0, where 0.0 removes all air resistance from the simulation, and 1.0 indicates no drafting effects (e.g. cycling alone, or in the lead of a pack). Units: none. Valid range: 0.00 - 1.00. Resolution: 0.01.


## environment

### Events

***Arguments***

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

#### TemperatureData
- `currentTemperature` - `number` - The most recent temperature reading of the sensor (up to 0.01*C accuracy). Units: Degrees Celsius.
- `eventCount` - `number` - Incremented every measurement. Rollover: Every ~9 quintillion N/A.
- `lowLast24Hours` - `number` - Lowest temperature recorded over the last 24 hours (up to 0.1*C accuracy). Units: Degrees Celsius.
- `highLast24Hours` - `number` - Highest temperature recorded over the last 24 hours (up to 0.1*C accuracy). Units: Degrees Celsius.

#### ManufacturerIdentification
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (resolution indicated by cumulativeOperatingTimeResolution]). Rollover: Every 16777215s*resolution. ie:~1.1yrs at 2s resolution, ~8.5yrs at 16s resolution.
- `batteryVoltage` - `string` - Current battery voltage. Invalid = -1. Units: Volts (with 1/256V resolution).
- `batteryStatus` - `string` - The current reported BatteryStatus.
- `cumulativeOperatingTimeResolution` - `number` - The resolution accuracy of the cumulativeOperatingTime. Units: seconds.
- `numberOfBatteries` - `number` - Specifies how many batteries are available in the system. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+
- `batteryIdentifier` - `number` - Identifies the battery in system to which this battery status pertains. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+

#### ManufacturerIdentification
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

#### ManufacturerSpecific
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

#### ProductInformation
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

#### Rssi
- `rssi` - `number` - rssi signal.


## weightScale


### Requests

### AdvancedMeasurement
Requests advanced weight measurements for the given user from the scale. The advanced measurements will only be calculated when a valid user profile is available to the weight scale; this requires a scale that supports device profiles, and for a user profile to be provided in this function or for the scale to have a user profile already 'selected'.
Returns a `Promise` object.

***Arguments***
- `gender` - `Integer` - User gender, 0 = 'FEMALE' | 1 = 'MALE' | -1 = 'UNASSIGNED'.
- `age` - `Integer` - User age, -1 = 'Unassigned'.
- `height` - `Double` - User height, -1 = 'Unassigned'.
- `activityLevel` - `Integer` - User activity level, ranging from 0 (sedentary) to 6 (regular), -1 = 'Unassigned'.
- `lifetimeAthlete` - `Boolean` - Indicates whether the user is a lifetime athlete (true) or not (false).

***Returns***
`bodyWeight` - `number` -  Body weight value, -1 = 'Invalid'. Units: Kg.
`hydrationPercentage` - `number` - Hydration percentage, -1 = 'Invalid'. Units: %.
`bodyFatPercentage` - `number` - Body fat percentage, -1 = 'Invalid'. Units: %.
`muscleMass` - `number` - Muscle mass, -1 = 'Invalid'. Units: Kg.
`boneMass` - `number` - Bone mass, -1 = 'Invalid'. Units: Kg.
`activeMetabolicRate` - `number` - Active metabolic rate: total amount of energy required daily by the body to maintain the user's current weight at the current activity level, -1 = 'Invalid'. Units: kcal.
`basalMetabolicRate` - `number` - Basal metabolic rate: daily amount of energy needed by the body in its resting state, -1 = 'Invalid'. Units: kcal.


### BasicMeasurement
Requests a basic weight measurement from the scale.
Returns a `Promise` object.

***Returns***
- `bodyWeight` - `number` - Body weight value, -1 = 'Invalid', null if request was unsuccessful. Units: Kg.
- `status` - `string` - The [WeightScaleRequestStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusWeightScalePcc.WeightScaleRequestStatus.html) defining the result of the measurement task.

### Capabilities
Requests the capabilities of weight scale and the identifier of the currently 'selected' user profile, if any.
Returns a `Promise` object.

***Returns***
`status` - `number` - The [WeightScaleRequestStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusWeightScalePcc.WeightScaleRequestStatus.html) defining the result of the request.
`userProfileID` - `number` - the user identifier of the scale's currently 'selected' user profile, -1 = 'Unassigned'.
`historySupport` - `number` - Indicates if scale supports storage and download of measurement history.
`userProfileExchangeSupport` - `number` - Indicates if scale supports receiving a user profile.
`userProfileSelected` - `number` - Indicates if a user profile has been 'selected' on the scale. The 'selected' profile is used to calculate advanced body measurements in the absence of an application-provided profile.


### Events

***Arguments***

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

#### BodyWeightBroadcast
- `bodyWeightStatus` - `string` -  The [BodyWeightStatus](https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/AntPlusWeightScalePcc.BodyWeightStatus.html) of the current broadcast. The bodyWeight parameter will only be non-null if this parameter is VALID.
- `bodyWeight` - `number` -  Body weight value of current broadcast. Units: Kg.

#### ManufacturerIdentification
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (resolution indicated by cumulativeOperatingTimeResolution]). Rollover: Every 16777215s*resolution. ie:~1.1yrs at 2s resolution, ~8.5yrs at 16s resolution.
- `batteryVoltage` - `string` - Current battery voltage. Invalid = -1. Units: Volts (with 1/256V resolution).
- `batteryStatus` - `string` - The current reported BatteryStatus.
- `cumulativeOperatingTimeResolution` - `number` - The resolution accuracy of the cumulativeOperatingTime. Units: seconds.
- `numberOfBatteries` - `number` - Specifies how many batteries are available in the system. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+
- `batteryIdentifier` - `number` - Identifies the battery in system to which this battery status pertains. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+

#### ManufacturerIdentification
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

#### ManufacturerSpecific
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

#### ProductInformation
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

#### Rssi
- `rssi` - `number` - rssi signal.


## heartRate

### Events

**Arguments**

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

#### CalculatedRrInterval
- `rrInterval` - `number` - Current R-R interval, calculated by the plugin. Units: ms. Invalid if value is negative.
- `rrFlag` - `string` - Indicates how the RR interval was calculated.

#### HeartRateData
- `heartRate` - `number` - Current heart rate valid for display, computed by sensor. Units: BPM.
- `heartBeatCount` - `number` - Heart beat count. Units: beats. Rollover: Every ~9 quintillion beats.
- `heartBeatEventTime` - `number` - Sensor reported time counter value of last distance or speed computation (up to 1/1024s accuracy). Units: s. Rollover: Every ~9 quadrillion s.
- `dataState` - `number` - The state of the data. If stale, app should indicate to the user that the device is not active. @since 2.1.7; supported on Plugin Service 2.2.8+. Earlier versions of the service will only send LIVE_DATA flag.

#### Page4AddtData
- `manufacturerSpecificByte` - `number` - Defined by manufacturer. Receivers do not need to interpret this byte. Units: Defined by manufacturer.
- `previousHeartBeatEventTime` - `number` - The time of the previous valid heart beat event (up to 1/1024s resolution). Units: s. Rollover: Every ~9 quadrillion s.

#### CumulativeOperatingTime
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (w/ 2s resolution). Rollover: Every 33554430s seconds (w/ 2s resolution) (388 days).

#### ManufacturerAndSerial
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `serialNumber` - `number` - This is the upper 16 bits of a 32 bit serial number.

#### VersionAndModel
- `hardwareVersion` - `number` - Manufacturer defined.
- `softwareVersion` - `number` - Manufacturer defined.
- `modelNumber` - `number` - Manufacturer defined.

#### Rssi
- `rssi` - `number` - rssi signal.


## bikeCadence

### Events

**Arguments**

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

#### CalculatedCadence
- `calculatedCadence` - `number` - The cadence calculated from the raw values in the sensor broadcast. Units: rpm.

#### MotionAndCadence
- `isPedallingStopped` - `boolean` - False indicates the user is pedalling, true indicates the user has stopped pedalling.

#### RawCadence
- `timestampOfLastEvent` - `number` - Sensor reported time counter value of last event (up to 1/1024s accuracy). Units: s. Rollover: Every ~46 quadrillion s (~1.5 billion years).
- `cumulativeRevolutions` - `number` - Total number of revolutions since the sensor was connected. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: revolutions. Rollover: Every ~9 quintillion revolutions.

#### ManufacturerIdentification
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

#### ManufacturerSpecific
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

#### ProductInformation
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

#### Rssi
- `rssi` - `number` - rssi signal.

#### BatteryStatus
- `batteryVoltage` - `number` - Current battery voltage. Units: Volts (with 1/256V resolution).
- `batteryStatus` - `string` - The current reported BatteryStatus.


### bikeSpeedDistance

### Variables
- `wheelCircumference` - `Double` - The wheel circumference used to calculate the distance and speed

### Events

**Arguments**

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

#### CalculatedAccumulatedDistance
- `calculatedAccumulatedDistance` - 'number' - The accumulated distance calculated from the raw values in the sensor broadcast since the sensor was first connected, based on this classes' set wheel circumference passed to the constructor.

#### CalculatedSpeed
- `calculatedSpeed` - `number` - The speed calculated from the raw values in the sensor broadcast, based on this classes' set wheel circumference passed to the constructor. Units: m/s.

#### MotionAndSpeedData
- `isBikeStopped` - `boolean` - False indicates the bike is moving, true indicates the bike has stopped.

#### RawSpeedAndDistanceData
- `timestampOfLastEvent` - `number` - Sensor reported time counter value of last distance or speed computation (up to 1/200s accuracy). Units: s. Rollover: Every ~46 quadrillion s (~1.5 billion years).
- `cumulativeRevolutions` - `number` - Total number of revolutions since the sensor was first connected. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: revolutions. Rollover: Every ~9 quintillion revolutions.

#### ManufacturerIdentification
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

#### ManufacturerSpecific
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

#### ProductInformation
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

#### Rssi
- `rssi` - `number` - rssi signal.

#### BatteryStatus
- `batteryVoltage` - `number` - Current battery voltage. Units: Volts (with 1/256V resolution).
- `batteryStatus` - `string` - The current reported BatteryStatus.


## bikeSpeedAndCadence

### Variables, Request and Events

- Inherits all [bikeCadence](#bikeCadence) and [bikeSpeedDistance](#bikeSpeedDistance)


## strideSdm

### Events

***Arguments***

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

#### CalorieData
- `cumulativeCalories` - `number` - The total number of calories consumed from the start of the session. Units: kcal. Rollover: Every ~9 quintillion kcal.

#### ComputationTimestamp
- `timestampOfLastComputation` - `number` - Sensor reported time counter value of last distance or speed computation (up to 1/200s accuracy). Units: s. Rollover: Every ~46 quadrillion s (~1.5 billion years).

#### DataLatency
- `updateLatency` - `number` - Sensor reported time elapsed between the last speed or distance computation and the message transmission (up to 1/32s accuracy). Note: This latency time is a sensor reported value and does not include the time between the message being transmitted by the sensor to the plugin receiver and then to the plugin triggering this event. Units: s.

#### Distance
- `cumulativeDistance` - `number` - Total distance since the plugin connected to this device (up to 1/16m accuracy). Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: m. Rollover: Every ~576 quadrillion m.

#### InstantaneousCadence
- `instantaneousCadence` - `number` - Instantaneous cadence valid for display, computed by sensor (up to 1/16 strides/min accuracy). Units: strides/min.

#### InstantaneousSpeed
- `instantaneousSpeed` - `number` - Instantaneous speed computed by sensor valid for display (up to 1/256 m/s accuracy). Units: m/s.

#### SensorStatus
- `sensorLocation` - `string` - The AntPlusStrideSdmPcc.SensorLocation of the current sensor location.
- `batteryStatus` - `string` - The BatteryStatus of the current sensor battery status. Note: This value may also be received in the AntPlusCommonPcc.IBatteryStatusReceiver.onNewBatteryStatus(long, java.util.EnumSet<com.dsi.ant.plugins.antplus.pcc.defines.EventFlag>, long, java.math.BigDecimal, com.dsi.ant.plugins.antplus.pcc.defines.BatteryStatus, int, int, int) event if the sensor supports the battery status common page.
- `sensorHealth` - `string` - The AntPlusStrideSdmPcc.SensorHealth of the current sensor health.
- `useState` - `string` - The AntPlusStrideSdmPcc.SensorUseState of the current sensor use state.

#### StrideCount
- `cumulativeStrides` - `number` - Total number of strides taken during the session. This value is incremented once for every two footfalls. Note: If the subscriber is not the first PCC connected to the device the accumulation will probably already be at a value greater than 0 and the subscriber should save the first received value as a relative zero for itself. Units: strides. Rollover: Every ~9 quintillion strides.

#### BatteryStatus
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (resolution indicated by cumulativeOperatingTimeResolution]). Rollover: Every 16777215s*resolution. ie:~1.1yrs at 2s resolution, ~8.5yrs at 16s resolution.
- `batteryVoltage` - `number` - Current battery voltage. Invalid = -1. Units: Volts (with 1/256V resolution).
- `batteryStatus` - `string` - The current reported BatteryStatus.
- `cumulativeOperatingTimeResolution` - `number` - The resolution accuracy of the cumulativeOperatingTime. Units: seconds.
- `numberOfBatteries` - `number` - Specifies how many batteries are available in the system. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+
- `batteryIdentifier` - `number` - Identifies the battery in system to which this battery status pertains. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+

#### ManufacturerIdentification
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

#### ManufacturerSpecific
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

#### ProductInformation
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

#### Rssi
- `rssi` - `number` - rssi signal.


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
