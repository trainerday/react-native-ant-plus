# react-native-ant-plus

[![npm downloads](https://img.shields.io/npm/dm/react-native-ant-plus?style=flat)](https://www.npmjs.com/package/react-native-ant-plus)
[![GitHub issues](https://img.shields.io/github/issues/trainerday/react-native-ant-plus.svg?style=flat)](https://github.com/trainerday/react-native-ant-plus/issues)

An Ant+ module for React Native

>### Important
>The library is in the early stages of development!


### At the moment devices supported:

WEIGHT_SCALE (119) events:
- BodyWeightBroadcast
- BatteryStatus
- ManufacturerIdentification
- ManufacturerSpecific
- ProductInformation
- Rssi

HEARTRATE (120) events:
- CalculatedRrInterval
- HeartRateData
- Page4AddtData
- CumulativeOperatingTime
- ManufacturerAndSerial
- VersionAndModel
- Rssi

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
AntPlusEmitter.addListener('rssi', arguments => {})
```

### weightScale

Event listener for the HeartRate device

**Arguments**

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

BodyWeightBroadcast - subscription
- `bodyWeightStatus` - `string` -  The AntPlusWeightScalePcc.BodyWeightStatus of the current broadcast. The bodyWeight parameter will only be non-null if this parameter is AntPlusWeightScalePcc.BodyWeightStatus.VALID.
- `bodyWeight` - `number` -  Body weight value of current broadcast. Units: Kg.

ManufacturerIdentification - subscription
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (resolution indicated by cumulativeOperatingTimeResolution]). Rollover: Every 16777215s*resolution. ie:~1.1yrs at 2s resolution, ~8.5yrs at 16s resolution.
- `batteryVoltage` - `string` - Current battery voltage. Invalid = -1. Units: Volts (with 1/256V resolution).
- `batteryStatus` - `string` - The current reported BatteryStatus.
- `cumulativeOperatingTimeResolution` - `number` - The resolution accuracy of the cumulativeOperatingTime. Units: seconds.
- `numberOfBatteries` - `number` - Specifies how many batteries are available in the system. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+
- `batteryIdentifier` - `number` - Identifies the battery in system to which this battery status pertains. Invalid = -1. Unsupported, requires upgrade to ANT+ Plugin Service Version 2.3.0 or newer = -2. @since 2.1.7; requires Plugin Service 2.2.8+

ManufacturerIdentification - subscription
- `hardwareRevision` - `number` - Manufacturer defined. -1 = 'Not available'.
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `modelNumber` - `number` - Manufacturer defined. -1 = 'Not available'.

ManufacturerSpecific - subscription
- `rawDataBytes` - `number[]` - The raw eight bytes which make up the manufacturer specific page.

ProductInformation - subscription
- `softwareRevision` - `number` - Manufacturer defined main software revision.
- `supplementaryRevision` - `number` - Manufacturer defined supplemental software revision. 0xFF = Invalid. -2 = Not supported by installed ANT+ Plugins Service version. @since 3.1.0; requires Plugin Service 3.1.0+
- `serialNumber` - `number` - Serial number of the device.

Rssi - subscription
- `rssi` - `number` - rssi signal.

**Example**

```js
AntPlusEmitter.addListener('weightScale', arguments => {})
```

### heartRate

Event listener for the HeartRate device

**Arguments**

- `event` - `string` - Name of the event to which the subscription
- `eventFlags` - `string` - Informational flags about the event.
- `estTimestamp` - `number` - The estimated timestamp of when this event was triggered. Useful for correlating multiple events and determining when data was sent for more accurate data records.

CalculatedRrInterval - subscription
- `rrInterval` - `number` - Current R-R interval, calculated by the plugin. Units: ms. Invalid if value is negative.
- `rrFlag` - `string` - Indicates how the RR interval was calculated.

HeartRateData - subscription
- `heartRate` - `number` - Current heart rate valid for display, computed by sensor. Units: BPM.
- `heartBeatCount` - `number` - Heart beat count. Units: beats. Rollover: Every ~9 quintillion beats.
- `heartBeatEventTime` - `number` - Sensor reported time counter value of last distance or speed computation (up to 1/1024s accuracy). Units: s. Rollover: Every ~9 quadrillion s.
- `dataState` - `number` - The state of the data. If stale, app should indicate to the user that the device is not active. @since 2.1.7; supported on Plugin Service 2.2.8+. Earlier versions of the service will only send LIVE_DATA flag.

Page4AddtData - subscription
- `manufacturerSpecificByte` - `number` - Defined by manufacturer. Receivers do not need to interpret this byte. Units: Defined by manufacturer.
- `previousHeartBeatEventTime` - `number` - The time of the previous valid heart beat event (up to 1/1024s resolution). Units: s. Rollover: Every ~9 quadrillion s.

CumulativeOperatingTime - subscription
- `cumulativeOperatingTime` - `number` - The cumulative operating time since the battery was inserted. Units: seconds (w/ 2s resolution). Rollover: Every 33554430s seconds (w/ 2s resolution) (388 days).

ManufacturerAndSerial - subscription
- `manufacturerID` - `number` - ANT+ Alliance managed manufacturer identifier.
- `serialNumber` - `number` - This is the upper 16 bits of a 32 bit serial number.

VersionAndModel - subscription
- `hardwareVersion` - `number` - Manufacturer defined.
- `softwareVersion` - `number` - Manufacturer defined.
- `modelNumber` - `number` - Manufacturer defined.

Rssi - subscription
- `rssi` - `number` - rssi signal.

**Example**

```js
AntPlusEmitter.addListener('heartRate', arguments => {})
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
