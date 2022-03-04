import {NativeEventEmitter, NativeModules} from 'react-native'
const AntPlusModule = NativeModules.AntPlusModule ? NativeModules.AntPlusModule : null
export const AntPlusEmitter = new NativeEventEmitter(AntPlusModule)

interface AntPlusInterface {
  startSearch(devices: number[], scanSearch: number): Promise<string>
  stopSearch(): Promise<void>
  connect(antDeviceNumber: number, antDeviceType: DevicesType): Promise<any>
  disconnect(antDeviceNumber: number, antDeviceType: DevicesType): Promise<void>
  subscribe(antDeviceNumber: number, antDeviceType: DevicesType): Promise<void>
}

export enum DevicesType {
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

export enum DevicesTypeName {
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

export interface DeviceInterface {
  resultID: number
  rssi: number
  describeContents: number
  antDeviceNumber: number
  antDeviceTypeName: DevicesTypeName
  antDeviceType: DevicesType
  deviceDisplayName: string
  isAlreadyConnected: boolean
  isPreferredDevice: boolean
  isUserRecognizedDevice: boolean
}

export interface RssiEvent {
  rssi: number
  resultID: DeviceInterface['resultID']
}

export const RssiSignal = {
  perfect: -65,
  good: -75,
  satisfactory: -85,
}

export default AntPlusModule as AntPlusInterface
