import {NativeEventEmitter, NativeModules} from 'react-native'

const AntPlusModule = NativeModules.AntPlusModule ? NativeModules.AntPlusModule : null
export const AntPlusEmitter = new NativeEventEmitter(AntPlusModule)

export enum AntPlusDevicesType {
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

export interface AntPlusDevice {
  antDeviceNumber: number
  antDeviceType: AntPlusDevicesType
  antDeviceTypeName: AntPlusDevicesTypeName
  describeContents: number
  deviceDisplayName: string
  isAlreadyConnected: boolean
  isPreferredDevice: boolean
  isUserRecognizedDevice: boolean
  resultID: number
  rssi?: number
}


interface AntPlusInterface {
  startSearch(devices: number[], scanSearch: number): Promise<string>

  stopSearch(): Promise<void>

  connect(antDeviceNumber: number, antDeviceType: AntPlusDevicesType): Promise<any>

  disconnect(antDeviceNumber: number, antDeviceType: AntPlusDevicesType): Promise<void>

  subscribe(antDeviceNumber: number, antDeviceType: AntPlusDevicesType): Promise<void>
}

class AntPlus {
  static startSearch(devices: AntPlusDevicesType[], scanSearch: number = 30, rssi: boolean = false) {
    AntPlusModule.startSearch(devices, scanSearch, rssi)
  }
  static stopSearch() {
    AntPlusModule.stopSearch()
  }
}

export default AntPlus
