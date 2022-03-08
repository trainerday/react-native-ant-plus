import {NativeEventEmitter, NativeModules} from 'react-native'

const AntPlusModule = NativeModules.AntPlusModule ? NativeModules.AntPlusModule : null
export const AntPlusEmitter = new NativeEventEmitter(AntPlusModule)

export enum AntPlusDeviceType {
  BIKE_POWER = 11,
  CONTROLLABLE_DEVICE = 16,
  FITNESS_EQUIPMENT = 17,
  BLOOD_PRESSURE = 18,
  GEOCACHE = 19,
  ENVIRONMENT = 25,
  WEIGHT_SCALE = 119,
  HEARTRATE = 120,
  BIKE_SPDCAD = 121,
  BIKE_CADENCE = 122,
  BIKE_SPD = 123,
  STRIDE_SDM = 124,
  UNKNOWN = -1,
}

export enum AntPlusDeviceState {
  DEAD = -100,
  CLOSED = 1,
  SEARCHING = 2,
  TRACKING = 3,
  PROCESSING_REQUEST = 300,
  UNRECOGNIZED = -1,
}

export enum AntPlusDevicesTypeName {
  BIKE_POWER = 'Bike Power Sensors',
  CONTROLLABLE_DEVICE = 'Controls',
  FITNESS_EQUIPMENT = 'Fitness Equipment Devices',
  BLOOD_PRESSURE = 'Blood Pressure Monitors',
  GEOCACHE = 'Geocache Transmitters',
  ENVIRONMENT = 'Environment Sensors',
  WEIGHT_SCALE = 'Weight Sensors',
  HEARTRATE = 'Heart Rate Sensors',
  BIKE_SPDCAD = 'Bike Speed and Cadence Sensors',
  BIKE_CADENCE = 'Bike Cadence Sensors',
  BIKE_SPD = 'Bike Speed Sensors',
  STRIDE_SDM = 'Stride-Based Speed and Distance Sensors',
  UNKNOWN = 'Unknown',
}

export enum AntPlusEvent {
  rssi = 'rssi',
  searchStatus = 'searchStatus',
  foundDevice = 'foundDevice',
  heartRate = 'heartRate',
  bikeCadence = 'bikeCadence',
  bikeSpeed = 'bikeSpeed',
  bikePower = 'bikePower',
  error = 'error',
}

export interface RssiEvent {
  rssi: number
  resultID: AntPlusDevice['resultID']
}

export const RssiSignal = {
  perfect: -65,
  good: -75,
  satisfactory: -85,
}

enum AntPlusHeartRateEvents {
  CalculatedRrInterval = 'CalculatedRrInterval',
  HeartRateData = 'HeartRateData',
  Page4AddtData = 'Page4AddtData',
  CumulativeOperatingTime = 'CumulativeOperatingTime',
  ManufacturerAndSerial = 'ManufacturerAndSerial',
  VersionAndModel = 'VersionAndModel',
  Rssi = 'Rssi',
}

export interface AntPlusDevice {
  antDeviceNumber: number
  antDeviceType: AntPlusDeviceType
  antDeviceTypeName: AntPlusDevicesTypeName
  describeContents: number
  deviceDisplayName: string
  isAlreadyConnected: boolean
  isPreferredDevice: boolean
  isUserRecognizedDevice: boolean
  resultID: number
  rssi?: number
}

// class AntPlus {
//   static async startSearch(antPlusDeviceTypes: AntPlusDeviceType[], seconds: number = 30, allowRssi: boolean = false): Promise<boolean> {
//     return await AntPlusModule.startSearch(antPlusDeviceTypes, seconds, allowRssi)
//   }
//   static async stopSearch(): Promise<boolean> {
//     return await AntPlusModule.stopSearch()
//   }
//   static addListener(eventType: string, listener: (...args: any[]) => any) {
//     AntPlusEmitter.addListener(eventType, listener)
//   }
//   static removeListener(eventType: string, listener: (...args: any[]) => any) {
//     AntPlusEmitter.removeListener(eventType, listener)
//   }
// }

enum AntPlusRequestAccessResult {
  SUCCESS = 'SUCCESS',
  USER_CANCELLED = 'USER_CANCELLED',
  CHANNEL_NOT_AVAILABLE = 'CHANNEL_NOT_AVAILABLE',
  OTHER_FAILURE = 'OTHER_FAILURE',
  DEPENDENCY_NOT_INSTALLED = 'DEPENDENCY_NOT_INSTALLED',
  DEVICE_ALREADY_IN_USE = 'DEVICE_ALREADY_IN_USE',
  SEARCH_TIMEOUT = 'SEARCH_TIMEOUT',
  ALREADY_SUBSCRIBED = 'ALREADY_SUBSCRIBED',
  BAD_PARAMS = 'BAD_PARAMS',
  ADAPTER_NOT_DETECTED = 'ADAPTER_NOT_DETECTED',
  UNRECOGNIZED = 'UNRECOGNIZED',
}

export interface AntPlusSearchStatus {
  isSearching: boolean
  reason: AntPlusRequestAccessResult
}

export interface AntPlusConnect {
  name: string
  state: AntPlusDeviceState
  connected: boolean
  code: AntPlusRequestAccessResult
}

interface AntPlus {
  startSearch: (antPlusDeviceTypes: AntPlusDeviceType[], seconds: number, allowRssi: boolean) => Promise<boolean>
  stopSearch: () => Promise<boolean>
  connect: (antPlusDeviceNumber: number, antPlusDeviceType: AntPlusDeviceType) => Promise<AntPlusConnect>
  disconnect: (antPlusDeviceNumber: number) => Promise<boolean>
}

export default AntPlusModule as AntPlus
