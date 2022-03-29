import AntPlus, {
  AntPlusBikeCadenceEvent,
  AntPlusBikePowerEvent,
  AntPlusDeviceType,
  AntPlusFitnessEquipmentEvent,
  AntPlusHeartRateEvent,
} from 'react-native-ant-plus'

export default async (device) => {
  try {
    const result = await AntPlus.connect(device.antDeviceNumber, device.antPlusDeviceType)
    if (!result.connected) return

    switch (device.antPlusDeviceType) {
    case AntPlusDeviceType.BIKE_CADENCE:
      AntPlus.subscribe(device.antDeviceNumber, [AntPlusBikeCadenceEvent.CalculatedCadence], true)
      break
    case AntPlusDeviceType.BIKE_POWER:
      AntPlus.subscribe(device.antDeviceNumber, [AntPlusBikePowerEvent.CalculatedPower], true)
      break
    case AntPlusDeviceType.FITNESS_EQUIPMENT:
      AntPlus.subscribe(device.antDeviceNumber, [AntPlusFitnessEquipmentEvent.CalculatedTrainerPower, AntPlusFitnessEquipmentEvent.Capabilities], true)
      break
    case AntPlusDeviceType.HEARTRATE:
      AntPlus.subscribe(device.antDeviceNumber, [AntPlusHeartRateEvent.HeartRateData], true)
      break
    }

    return result
  } catch (error) {
    console.log('connect error', error)
  }
}
