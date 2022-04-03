'use strict'
import { NativeEventEmitter, NativeModules } from 'react-native'

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
  searchStatus = 'searchStatus',
  rssi = 'rssi',
  foundDevice = 'foundDevice',
  devicesStateChange = 'devicesStateChange',

  bikeCadence = 'bikeCadence',
  bikePower = 'bikePower',
  bikeSpeedDistance = 'bikeSpeedDistance',
  bikeSpeedAndCadence = 'bikeSpeedAndCadence',
  environment = 'environment',
  fitnessEquipment = 'fitnessEquipment',
  weightScale = 'weightScale',
  heartRate = 'heartRate',
}

export interface AntPlusArguments {
  antDeviceNumber: number
  estTimestamp: number
  eventFlags: string
}

export const AntPlusRssiSignal = {
  perfect: -65,
  good: -75,
  satisfactory: -85,
}

export enum AntPlusLegacyCommonEvent {
  CumulativeOperatingTime = 'CumulativeOperatingTime',
  ManufacturerAndSerial = 'ManufacturerAndSerial',
  VersionAndModel = 'VersionAndModel',
  Rssi = 'Rssi',
}

interface CumulativeOperatingTime {
  event: AntPlusLegacyCommonEvent.CumulativeOperatingTime
  cumulativeOperatingTime: number
}

interface ManufacturerAndSerial {
  event: AntPlusLegacyCommonEvent.ManufacturerAndSerial
  manufacturerID: number
  serialNumber: number
}

interface VersionAndModel {
  event: AntPlusLegacyCommonEvent.VersionAndModel
  hardwareVersion: number
  softwareVersion: number
  modelNumber: number
}

interface Rssi {
  event: AntPlusLegacyCommonEvent.Rssi | AntPlusCommonEvent.Rssi
  rssi: number
}

export type LegacyCommonEventArguments = AntPlusArguments &
  (CumulativeOperatingTime | ManufacturerAndSerial | VersionAndModel | Rssi)

export enum AntPlusCommonEvent {
  BatteryStatus = 'BatteryStatus',
  ManufacturerIdentification = 'ManufacturerIdentification',
  ManufacturerSpecific = 'ManufacturerSpecific',
  ProductInformation = 'ProductInformation',
  Rssi = 'Rssi',
}

interface BatteryStatus {
  event: AntPlusCommonEvent.BatteryStatus
  cumulativeOperatingTime: number
  batteryVoltage: number
  batteryStatus: string
  cumulativeOperatingTimeResolution: number
  numberOfBatteries: number
  batteryIdentifier: number
}

interface ManufacturerIdentification {
  event: AntPlusCommonEvent.ManufacturerIdentification
  hardwareRevision: number
  manufacturerID: number
  modelNumber: number
}

interface ManufacturerSpecific {
  event: AntPlusCommonEvent.ManufacturerSpecific
  rawDataBytes: number[]
}

interface ProductInformation {
  event: AntPlusCommonEvent.ProductInformation
  softwareRevision: number
  supplementaryRevision: number
  serialNumber: number
}

export type CommonEventArguments = AntPlusArguments &
  (BatteryStatus | ManufacturerIdentification | ManufacturerSpecific | ProductInformation | Rssi)

export enum AntPlusBikePowerEvent {
  AutoZeroStatus = 'AutoZeroStatus',
  CalculatedCrankCadence = 'CalculatedCrankCadence',
  CalculatedPower = 'CalculatedPower',
  CalculatedTorque = 'CalculatedTorque',
  CalculatedWheelDistance = 'CalculatedWheelDistance',
  CalculatedWheelSpeed = 'CalculatedWheelSpeed',
  CalibrationMessage = 'CalibrationMessage',
  CrankParameters = 'CrankParameters',
  InstantaneousCadence = 'InstantaneousCadence',
  MeasurementOutputData = 'MeasurementOutputData',
  PedalPowerBalance = 'PedalPowerBalance',
  PedalSmoothness = 'PedalSmoothness',
  RawCrankTorqueData = 'RawCrankTorqueData',
  RawCtfData = 'RawCtfData',
  RawPowerOnlyData = 'RawPowerOnlyData',
  RawWheelTorqueData = 'RawWheelTorqueData',
  TorqueEffectiveness = 'TorqueEffectiveness',
}

interface AutoZeroStatus {
  event: AntPlusBikePowerEvent.AutoZeroStatus
  autoZeroStatus: string
}

interface CalculatedCrankCadence {
  event: AntPlusBikePowerEvent.CalculatedCrankCadence
  dataSource: string
  calculatedCrankCadence: number
}

interface CalculatedPower {
  event: AntPlusBikePowerEvent.CalculatedPower
  dataSource: string
  calculatedPower: number
}

interface CalculatedTorque {
  event: AntPlusBikePowerEvent.CalculatedTorque
  dataSource: string
  calculatedTorque: number
}

interface CalculatedWheelDistance {
  event: AntPlusBikePowerEvent.CalculatedWheelDistance
  dataSource: string
  calculatedWheelDistance: number
}

interface CalculatedWheelSpeed {
  event: AntPlusBikePowerEvent.CalculatedWheelSpeed
  dataSource: string
  calculatedWheelSpeed: number
}

interface CalibrationMessage {
  event: AntPlusBikePowerEvent.CalibrationMessage
  calibrationMessage: {
    calibrationId: string
    calibrationData: number
    ctfOffset: number
    manufacturerSpecificData: number[]
  }
}

interface CrankParameters {
  event: AntPlusBikePowerEvent.CrankParameters
  crankParameters: {
    fullCrankLength: number
    crankLengthStatus: string
    sensorSoftwareMismatchStatus: string
    sensorAvailabilityStatus: string
    customCalibrationStatus: string
    isAutoCrankLengthSupported: boolean
  }
}

interface InstantaneousCadence {
  event: AntPlusBikePowerEvent.InstantaneousCadence
  dataSource: string
  instantaneousCadence: number
}

interface MeasurementOutputData {
  event: AntPlusBikePowerEvent.MeasurementOutputData
  numOfDataTypes: number
  dataType: number
  timeStamp: number
  measurementValue: number
}

interface PedalPowerBalance {
  event: AntPlusBikePowerEvent.PedalPowerBalance
  rightPedalIndicator: boolean
  pedalPowerPercentage: number
}

interface PedalSmoothness {
  event: AntPlusBikePowerEvent.PedalSmoothness
  powerOnlyUpdateEventCount: number
  separatePedalSmoothnessSupport: number
  leftOrCombinedPedalSmoothness: number
  rightPedalSmoothness: number
}

interface RawCrankTorqueData {
  event: AntPlusBikePowerEvent.RawCrankTorqueData
  crankTorqueUpdateEventCount: number
  accumulatedCrankTicks: number
  accumulatedCrankPeriod: number
  accumulatedCrankTorque: number
}

interface RawCtfData {
  event: AntPlusBikePowerEvent.RawCtfData
  ctfUpdateEventCount: number
  instantaneousSlope: number
  accumulatedTimeStamp: number
  accumulatedTorqueTicksStamp: number
}

interface RawPowerOnlyData {
  event: AntPlusBikePowerEvent.RawPowerOnlyData
  powerOnlyUpdateEventCount: number
  instantaneousPower: number
  accumulatedPower: number
}

interface RawWheelTorqueData {
  event: AntPlusBikePowerEvent.RawWheelTorqueData
  wheelTorqueUpdateEventCount: number
  accumulatedWheelTicks: number
  accumulatedWheelPeriod: number
  accumulatedWheelTorque: number
}

interface TorqueEffectiveness {
  event: AntPlusBikePowerEvent.TorqueEffectiveness
  powerOnlyUpdateEventCount: number
  leftTorqueEffectiveness: number
  rightTorqueEffectiveness: number
}

export type BikePowerEventArguments = AntPlusArguments &
  (
    | AutoZeroStatus
    | CalculatedCrankCadence
    | CalculatedPower
    | CalculatedTorque
    | CalculatedWheelDistance
    | CalculatedWheelSpeed
    | CalibrationMessage
    | CrankParameters
    | InstantaneousCadence
    | MeasurementOutputData
    | PedalPowerBalance
    | PedalSmoothness
    | RawCrankTorqueData
    | RawCtfData
    | RawPowerOnlyData
    | RawWheelTorqueData
    | TorqueEffectiveness
    | CommonEventArguments
  )

export enum AntPlusBikePowerRequest {
  CommandBurst = 'CommandBurst',
  CrankParameters = 'CrankParameters',
  CustomCalibrationParameters = 'CustomCalibrationParameters',
  ManualCalibration = 'ManualCalibration',
  SetAutoZero = 'SetAutoZero',
  SetCrankParameters = 'SetCrankParameters',
  SetCtfSlope = 'SetCtfSlope',
  SetCustomCalibrationParameters = 'SetCustomCalibrationParameters',
}

export interface AntPlusBikePowerRequestCommandBurstArguments {
  requestedCommandId: number
  commandData: number[]
}

export interface AntPlusBikePowerRequestCustomCalibrationParametersArguments {
  manufacturerSpecificParameters: number[]
}

export interface AntPlusBikePowerRequestSetAutoZeroArguments {
  autoZeroEnable: boolean
}

export interface AntPlusBikePowerRequestSetCrankParametersArguments {
  crankLengthSetting: string
  fullCrankLength: number
}

export interface AntPlusBikePowerRequestSetCtfSlopeArguments {
  slope: number
}

export interface AntPlusBikePowerRequestSetCustomCalibrationParametersArguments {
  manufacturerSpecificParameters: number[]
}

export interface AntPlusBikePowerRequestSetCustomCalibrationParametersArguments {
  manufacturerSpecificParameters: number[]
}

export enum AntPlusBikeCadenceEvent {
  CalculatedCadence = 'CalculatedCadence',
  MotionAndCadence = 'MotionAndCadence',
  RawCadence = 'RawCadence',
}

interface CalculatedCadence {
  event: AntPlusBikeCadenceEvent.CalculatedCadence
  calculatedCadence: number
}

interface MotionAndCadence {
  event: AntPlusBikeCadenceEvent.MotionAndCadence
  isPedallingStopped: boolean
}

interface RawCadence {
  event: AntPlusBikeCadenceEvent.RawCadence
  timestampOfLastEvent: number
  cumulativeRevolutions: number
}

export type BikeCadenceEventArguments = AntPlusArguments &
  (CalculatedCadence | MotionAndCadence | RawCadence | LegacyCommonEventArguments)

export enum AntPlusSpeedDistanceEvent {
  CalculatedAccumulatedDistance = 'CalculatedAccumulatedDistance',
  CalculatedSpeed = 'CalculatedSpeed',
  MotionAndSpeedData = 'MotionAndSpeedData',
  RawSpeedAndDistanceData = 'RawSpeedAndDistanceData',
}

interface CalculatedAccumulatedDistance {
  event: AntPlusSpeedDistanceEvent.CalculatedAccumulatedDistance
  calculatedAccumulatedDistance: number
}

interface CalculatedSpeed {
  event: AntPlusSpeedDistanceEvent.CalculatedSpeed
  calculatedSpeed: number
}

interface MotionAndSpeedData {
  event: AntPlusSpeedDistanceEvent.MotionAndSpeedData
  isBikeStopped: boolean
}

interface RawSpeedAndDistanceData {
  event: AntPlusSpeedDistanceEvent.RawSpeedAndDistanceData
  timestampOfLastEvent: number
  cumulativeRevolutions: number
}

export type SpeedDistanceEventArguments = AntPlusArguments &
  (
    | CalculatedAccumulatedDistance
    | CalculatedSpeed
    | MotionAndSpeedData
    | RawSpeedAndDistanceData
    | LegacyCommonEventArguments
  )

export enum AntPlusSpeedAndCadenceEvent {
  BatteryStatus = 'BatteryStatus',
}

interface SpeedAndCadenceBatteryStatus {
  event: AntPlusSpeedAndCadenceEvent.BatteryStatus
  batteryVoltage: number
  batteryStatus: string
}

export type SpeedAndCadenceEventArguments = AntPlusArguments &
  (SpeedAndCadenceBatteryStatus | BikeCadenceEventArguments | SpeedDistanceEventArguments)

export enum AntPlusEnvironmentEvent {
  TemperatureData = 'TemperatureData',
}

interface TemperatureData {
  event: AntPlusEnvironmentEvent.TemperatureData
  eventCount: number
  currentTemperature: number
  lowLast24Hours: number
  highLast24Hours: number
}

export type EnvironmentEventArguments = AntPlusArguments & (TemperatureData | CommonEventArguments)

export enum AntPlusFitnessEquipmentEvent {
  CalibrationInProgress = 'CalibrationInProgress',
  CalibrationResponse = 'CalibrationResponse',
  Capabilities = 'Capabilities',
  GeneralFitnessEquipmentData = 'GeneralFitnessEquipmentData',
  GeneralMetabolicData = 'GeneralMetabolicData',
  GeneralSettings = 'GeneralSettings',
  LapOccured = 'LapOccured',
  UserConfiguration = 'UserConfiguration',
  Treadmill = 'Treadmill',
  ClimberData = 'ClimberData',
  EllipticalData = 'EllipticalData',
  NordicSkierData = 'NordicSkierData',
  RowerData = 'RowerData',
  BikeData = 'BikeData',
  BasicResistance = 'BasicResistance',
  CalculatedTrainerDistance = 'CalculatedTrainerDistance',
  CalculatedTrainerPower = 'CalculatedTrainerPower',
  CalculatedTrainerSpeed = 'CalculatedTrainerSpeed',
  CommandStatus = 'CommandStatus',
  RawTrainerData = 'RawTrainerData',
  RawTrainerTorqueData = 'RawTrainerTorqueData',
  TargetPower = 'TargetPower',
  TrackResistance = 'TrackResistance',
  TrainerStatus = 'TrainerStatus',
  WindResistance = 'WindResistance',

  BatteryStatus = 'BatteryStatus',
  ManufacturerIdentification = 'ManufacturerIdentification',
  ManufacturerSpecific = 'ManufacturerSpecific',
  ProductInformation = 'ProductInformation',
  Rssi = 'Rssi',
}

interface CalibrationInProgress {
  event: AntPlusFitnessEquipmentEvent.CalibrationInProgress
  calibrationInProgress: {
    currentTemperature: number
    speedCondition: string
    spinDownCalibrationPending: boolean
    targetSpeed: number
    targetSpinDownTime: number
    temperatureCondition: string
    zeroOffsetCalibrationPending: boolean
  }
}

interface CalibrationResponse {
  event: AntPlusFitnessEquipmentEvent.CalibrationResponse
  calibrationResponse: {
    spinDownCalibrationSuccess: boolean
    spinDownTime: number
    temperature: number
    zeroOffset: number
    zeroOffsetCalibrationSuccess: boolean
  }
}

interface Capabilities {
  event: AntPlusFitnessEquipmentEvent.Capabilities
  capabilities: {
    maximumResistance?: number
    simulationModeSupport: boolean
    targetPowerModeSupport: boolean
    basicResistanceModeSupport: boolean
  }
}

interface GeneralFitnessEquipmentData {
  event: AntPlusFitnessEquipmentEvent.GeneralFitnessEquipmentData
  elapsedTime: number
  cumulativeDistance: number
  instantaneousSpeed: number
  virtualInstantaneousSpeed: boolean
  instantaneousHeartRate: number
  heartRateDataSource: string
}

interface GeneralMetabolicData {
  event: AntPlusFitnessEquipmentEvent.GeneralMetabolicData
  instantaneousMetabolicEquivalents: number
  instantaneousCaloricBurn: number
  cumulativeCalories: number
}

interface GeneralSettings {
  event: AntPlusFitnessEquipmentEvent.GeneralSettings
  cycleLength: number
  inclinePercentage: number
  resistanceLevel: number
}

interface LapOccured {
  event: AntPlusFitnessEquipmentEvent.LapOccured
  lapCount: number
}

interface UserConfiguration {
  event: AntPlusFitnessEquipmentEvent.UserConfiguration
  userConfiguration: {
    bicycleWeight: number
    bicycleWheelDiameter: number
    gearRatio: number
    userWeight: number
  }
}

interface Treadmill {
  event: AntPlusFitnessEquipmentEvent.Treadmill
  instantaneousCadence: number
  cumulativeNegVertDistance: number
  cumulativePosVertDistance: number
}

interface ClimberData {
  event: AntPlusFitnessEquipmentEvent.ClimberData
  cumulativeStrideCycles: number
  instantaneousCadence: number
  instantaneousPower: number
}

interface EllipticalData {
  event: AntPlusFitnessEquipmentEvent.EllipticalData
  cumulativePosVertDistance: number
  cumulativeStrides: number
  instantaneousCadence: number
  instantaneousPower: number
}

interface NordicSkierData {
  event: AntPlusFitnessEquipmentEvent.NordicSkierData
  cumulativeStrides: number
  instantaneousCadence: number
  instantaneousPower: number
}

interface RowerData {
  event: AntPlusFitnessEquipmentEvent.RowerData
  cumulativeStrokes: number
  instantaneousCadence: number
  instantaneousPower: number
}

interface BikeData {
  event: AntPlusFitnessEquipmentEvent.BikeData
  instantaneousCadence: number
  instantaneousPower: number
}

interface BasicResistance {
  event: AntPlusFitnessEquipmentEvent.BasicResistance
  totalResistance: number
}

interface CalculatedTrainerDistance {
  event: AntPlusFitnessEquipmentEvent.CalculatedTrainerDistance
  calculatedDistance: number
}

interface CalculatedTrainerPower {
  event: AntPlusFitnessEquipmentEvent.CalculatedTrainerPower
  calculatedPower: number
  dataSource: string
}

interface CalculatedTrainerSpeed {
  event: AntPlusFitnessEquipmentEvent.CalculatedTrainerSpeed
  calculatedSpeed: number
  dataSource: string
}

interface CommandStatus {
  event: AntPlusFitnessEquipmentEvent.CommandStatus
  commandStatus: {
    lastReceivedSequenceNumber: number
    status: string
    rawResponseData: number[]
    lastReceivedCommandId: string
    totalResistance: number
    targetPower: number
    windResistanceCoefficient: number
    windSpeed: number
    draftingFactor: number
    grade: number
    rollingResistanceCoefficient: number
  }
}

interface RawTrainerData {
  event: AntPlusFitnessEquipmentEvent.RawTrainerData
  updateEventCount: number
  instantaneousCadence: number
  instantaneousPower: number
  accumulatedPower: number
}

interface RawTrainerTorqueData {
  event: AntPlusFitnessEquipmentEvent.RawTrainerTorqueData
  updateEventCount: number
  accumulatedWheelTicks: number
  accumulatedWheelPeriod: number
  accumulatedTorque: number
}

interface TargetPower {
  event: AntPlusFitnessEquipmentEvent.TargetPower
  targetPower: number
}

interface TrackResistance {
  event: AntPlusFitnessEquipmentEvent.TrackResistance
  grade: number
  rollingResistanceCoefficient: number
}

interface TrainerStatus {
  event: AntPlusFitnessEquipmentEvent.TrainerStatus
  trainerStatusFlags: string
}

interface WindResistance {
  event: AntPlusFitnessEquipmentEvent.WindResistance
  windResistanceCoefficient: number
  windSpeed: number
  draftingFactor: number
}

export type FitnessEquipmentEventArguments = AntPlusArguments &
  (
    | CalibrationInProgress
    | CalibrationResponse
    | Capabilities
    | GeneralFitnessEquipmentData
    | GeneralMetabolicData
    | GeneralSettings
    | LapOccured
    | UserConfiguration
    | Treadmill
    | ClimberData
    | EllipticalData
    | NordicSkierData
    | RowerData
    | BikeData
    | BasicResistance
    | CalculatedTrainerDistance
    | CalculatedTrainerPower
    | CalculatedTrainerSpeed
    | CommandStatus
    | RawTrainerData
    | RawTrainerTorqueData
    | TargetPower
    | TrackResistance
    | TrainerStatus
    | WindResistance
    | CommonEventArguments
  )

export enum AntPlusFitnessEquipmentRequest {
  Capabilities = 'Capabilities',
  SetUserConfiguration = 'SetUserConfiguration',
  SpinDownCalibration = 'SpinDownCalibration',
  UserConfiguration = 'UserConfiguration',
  ZeroOffsetCalibration = 'ZeroOffsetCalibration',
  SetTargetPower = 'SetTargetPower',
  BasicResistance = 'BasicResistance',
  CommandStatus = 'CommandStatus',
  SetBasicResistance = 'SetBasicResistance',
  SetTrackResistance = 'SetTrackResistance',
  SetWindResistance = 'SetWindResistance',
  TargetPower = 'TargetPower',
  TrackResistance = 'TrackResistance',
  WindResistance = 'WindResistance',
}

export interface AntPlusFitnessEquipmentRequestSetUserConfiguration {
  bicycleWeight: number
  gearRatio: number
  bicycleWheelDiameter: number
  userWeight: number
}

export interface AntPlusFitnessEquipmentRequestSetTargetPower {
  target: number
}

export interface AntPlusFitnessEquipmentRequestSetBasicResistance {
  totalResistance: number
}

export interface AntPlusFitnessEquipmentRequestSetTrackResistance {
  grade: number
  rollingResistanceCoefficient: number
}

export type AntPlusFitnessEquipmentRequestSetWindResistance =
  | {
      windSpeed: number
      draftingFactor: number
      windResistanceCoefficient: number
    }
  | {
      windSpeed: number
      draftingFactor: number
      frontalSurfaceArea: number
      dragCoefficient: number
      airDensity: number
    }

export enum AntPlusHeartRateEvent {
  CalculatedRrInterval = 'CalculatedRrInterval',
  HeartRateData = 'HeartRateData',
  Page4AddtData = 'Page4AddtData',
}

interface HeartRateData {
  event: AntPlusHeartRateEvent.HeartRateData
  heartRate: number
  heartBeatCount: number
  heartBeatEventTime: number
  dataState: number
}

interface CalculatedRrInterval {
  event: AntPlusHeartRateEvent.CalculatedRrInterval
  rrInterval: number
  rrFlag: string
}

interface Page4AddtData {
  event: AntPlusHeartRateEvent.Page4AddtData
  manufacturerSpecificByte: number
  previousHeartBeatEventTime: number
}

export type HeartRateEventArguments = AntPlusArguments &
  (HeartRateData | CalculatedRrInterval | Page4AddtData | LegacyCommonEventArguments)

export enum AntPlusWeightScaleEvent {
  BodyWeightBroadcast = 'BodyWeightBroadcast',

  BatteryStatus = 'BatteryStatus',
  ManufacturerIdentification = 'ManufacturerIdentification',
  ManufacturerSpecific = 'ManufacturerSpecific',
  ProductInformation = 'ProductInformation',
  Rssi = 'Rssi',
}

interface BodyWeightBroadcast {
  event: AntPlusWeightScaleEvent.BodyWeightBroadcast
  bodyWeightStatus: string
  bodyWeight: number
}

export type WeightScaleEventArguments = AntPlusArguments & (BodyWeightBroadcast | CommonEventArguments)

export enum AntPlusWeightScaleRequest {
  BasicMeasurement = 'BasicMeasurement',
  AdvancedMeasurement = 'AdvancedMeasurement',
}

interface AntPlusWeightScaleRequestAdvancedMeasurement {
  gender: number
  age: number
  height: number
  activityLevel: number
  lifetimeAthlete: boolean
}

export interface AntPlusDevice {
  antDeviceNumber: number
  antPlusDeviceType: AntPlusDeviceType
  antPlusDeviceTypeName: AntPlusDevicesTypeName
  describeContents: number
  deviceDisplayName: string
  isAlreadyConnected: boolean
  isPreferredDevice: boolean
  isUserRecognizedDevice: boolean
  resultID: number
  rssi?: number
}

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

export enum AntPlusFitnessEquipmentType {
  BIKE = 'BIKE',
  CLIMBER = 'CLIMBER',
  ELLIPTICAL = 'ELLIPTICAL',
  GENERAL = 'GENERAL',
  NORDICSKIER = 'NORDICSKIER',
  ROWER = 'ROWER',
  TRAINER = 'TRAINER',
  TREADMILL = 'TREADMILL',
  UNKNOWN = 'UNKNOWN',
  UNRECOGNIZED = 'UNRECOGNIZED',
}

export interface AntPlusConnect {
  name: string
  state: AntPlusDeviceState
  connected: boolean
  code: AntPlusRequestAccessResult
  type?: AntPlusFitnessEquipmentType
}

type AntPlusSubscribeEvent =
  | AntPlusLegacyCommonEvent[]
  | AntPlusBikeCadenceEvent[]
  | AntPlusBikePowerEvent[]
  | AntPlusSpeedDistanceEvent[]
  | AntPlusSpeedAndCadenceEvent[]
  | AntPlusEnvironmentEvent[]
  | AntPlusFitnessEquipmentEvent[]
  | AntPlusHeartRateEvent[]
  | AntPlusWeightScaleEvent[]

type AntPlusRequest =
  | {
      requestName: AntPlusBikePowerRequest.CommandBurst
      args: AntPlusBikePowerRequestCommandBurstArguments
    }
  | {
      requestName: AntPlusBikePowerRequest.CustomCalibrationParameters
      args: AntPlusBikePowerRequestCustomCalibrationParametersArguments
    }
  | {
      requestName: AntPlusBikePowerRequest.SetAutoZero
      args: AntPlusBikePowerRequestSetAutoZeroArguments
    }
  | {
      requestName: AntPlusBikePowerRequest.SetCrankParameters
      args: AntPlusBikePowerRequestSetCrankParametersArguments
    }
  | {
      requestName: AntPlusBikePowerRequest.SetCtfSlope
      args: AntPlusBikePowerRequestSetCtfSlopeArguments
    }
  | {
      requestName: AntPlusBikePowerRequest.SetCustomCalibrationParameters
      args: AntPlusBikePowerRequestSetCustomCalibrationParametersArguments
    }
  | {
      requestName: AntPlusFitnessEquipmentRequest.SetUserConfiguration
      args: AntPlusFitnessEquipmentRequestSetUserConfiguration
    }
  | {
      requestName: AntPlusFitnessEquipmentRequest.SetTargetPower
      args: AntPlusFitnessEquipmentRequestSetTargetPower
    }
  | {
      requestName: AntPlusFitnessEquipmentRequest.SetBasicResistance
      args: AntPlusFitnessEquipmentRequestSetBasicResistance
    }
  | {
      requestName: AntPlusFitnessEquipmentRequest.SetTrackResistance
      args: AntPlusFitnessEquipmentRequestSetTrackResistance
    }
  | {
      requestName: AntPlusFitnessEquipmentRequest.SetWindResistance
      args: AntPlusFitnessEquipmentRequestSetWindResistance
    }
  | {
      requestName:
        | AntPlusBikePowerRequest.CrankParameters
        | AntPlusBikePowerRequest.ManualCalibration
        | AntPlusFitnessEquipmentRequest.Capabilities
        | AntPlusFitnessEquipmentRequest.SpinDownCalibration
        | AntPlusFitnessEquipmentRequest.UserConfiguration
        | AntPlusFitnessEquipmentRequest.ZeroOffsetCalibration
        | AntPlusFitnessEquipmentRequest.BasicResistance
        | AntPlusFitnessEquipmentRequest.CommandStatus
        | AntPlusFitnessEquipmentRequest.TargetPower
        | AntPlusFitnessEquipmentRequest.TrackResistance
        | AntPlusFitnessEquipmentRequest.WindResistance
        | AntPlusWeightScaleRequest.BasicMeasurement
      args: {}
    }
  | {
      requestName: AntPlusWeightScaleRequest.AdvancedMeasurement
      args: AntPlusWeightScaleRequestAdvancedMeasurement
    }

export interface RssiArguments {
  rssi: number
  resultID: AntPlusDevice['resultID']
}

export interface SearchStatusArguments {
  isSearching: boolean
  reason: AntPlusRequestAccessResult
}

export interface DevicesStateChangeArguments {
  antDeviceNumber: number
  state: string
}

type AntPlusEvents =
  | {
      event: AntPlusEvent.searchStatus
      listener: (data: SearchStatusArguments) => void
    }
  | {
      event: AntPlusEvent.rssi
      listener: (data: RssiArguments) => void
    }
  | {
      event: AntPlusEvent.foundDevice
      listener: (data: AntPlusDevice) => void
    }
  | {
      event: AntPlusEvent.devicesStateChange
      listener: (data: DevicesStateChangeArguments) => void
    }
  | {
      event: AntPlusEvent.bikePower
      listener: (data: BikePowerEventArguments) => void
    }
  | {
      event: AntPlusEvent.bikeCadence
      listener: (data: BikeCadenceEventArguments) => void
    }
  | {
      event: AntPlusEvent.bikeSpeedDistance
      listener: (data: SpeedDistanceEventArguments) => void
    }
  | {
      event: AntPlusEvent.bikeSpeedAndCadence
      listener: (data: SpeedAndCadenceEventArguments) => void
    }
  | {
      event: AntPlusEvent.environment
      listener: (data: EnvironmentEventArguments) => void
    }
  | {
      event: AntPlusEvent.fitnessEquipment
      listener: (data: FitnessEquipmentEventArguments) => void
    }
  | {
      event: AntPlusEvent.weightScale
      listener: (data: WeightScaleEventArguments) => void
    }
  | {
      event: AntPlusEvent.heartRate
      listener: (data: HeartRateEventArguments) => void
    }

class AntPlus {
  static async startSearch(
    antPlusDeviceTypes: AntPlusDeviceType[],
    seconds: number,
    allowRssi: boolean = false
  ): Promise<boolean> {
    return await AntPlusModule.startSearch(antPlusDeviceTypes, seconds, allowRssi)
  }

  static async stopSearch(): Promise<boolean> {
    return await AntPlusModule.stopSearch()
  }

  static async isConnected(antDeviceNumber: number): Promise<boolean | undefined> {
    return await AntPlusModule.isConnected(antDeviceNumber)
  }

  static async connect(antDeviceNumber: number, antPlusDeviceType: AntPlusDeviceType): Promise<AntPlusConnect> {
    return await AntPlusModule.connect(antDeviceNumber, antPlusDeviceType)
  }

  static async disconnect(antDeviceNumber: number): Promise<boolean> {
    return await AntPlusModule.disconnect(antDeviceNumber)
  }

  static async subscribe(
    antDeviceNumber: number,
    events: AntPlusSubscribeEvent,
    isOnlyNewData: boolean = true
  ): Promise<boolean> {
    return await AntPlusModule.subscribe(antDeviceNumber, events, isOnlyNewData)
  }

  static async unsubscribe(antDeviceNumber: number, events: AntPlusSubscribeEvent): Promise<boolean> {
    return await AntPlusModule.unsubscribe(antDeviceNumber, events)
  }

  static async setVariables(antDeviceNumber: number, variables: { [key: string]: any }): Promise<boolean> {
    return await AntPlusModule.setVariables(antDeviceNumber, variables)
  }

  static async getVariables(antDeviceNumber: number, variables: { [key: string]: any }): Promise<any> {
    return await AntPlusModule.getVariables(antDeviceNumber, variables)
  }

  static async request<T extends AntPlusRequest>(
    antDeviceNumber: number,
    requestName: T['requestName'],
    args: T['args'] = {}
  ): Promise<any> {
    return await AntPlusModule.request(antDeviceNumber, requestName, args)
  }

  static addListener<T extends AntPlusEvents>(event: T['event'], listener: T['listener']) {
    AntPlusEmitter.addListener(event, listener)
  }

  static removeListener<T extends AntPlusEvents>(event: T['event'], listener: T['listener']) {
    AntPlusEmitter.removeListener(event, listener)
  }
}

export default AntPlus
