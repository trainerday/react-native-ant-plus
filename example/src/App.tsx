import React, { useEffect, useState } from 'react'

import { ScrollView, StyleSheet, Text, View } from 'react-native'
import AntPlus, {
  AntPlusBikeCadenceEvent,
  AntPlusBikePowerEvent,
  AntPlusDevice,
  AntPlusDeviceType,
  AntPlusEvent,
  AntPlusFitnessEquipmentEvent,
  AntPlusHeartRateEvent,
  BikeCadenceEventArguments,
  BikePowerEventArguments,
  DevicesStateChangeArguments,
  FitnessEquipmentEventArguments,
  HeartRateEventArguments,
  RssiArguments,
  SearchStatusArguments,
} from 'react-native-ant-plus'
import Device from './Device'

export default function App() {
  const [devices, setDevices] = useState<AntPlusDevice[]>(() => [])
  const [statusSearch, setStatusSearch] = useState(false)
  const [bpm, setBpm] = useState(0)
  const [rpm, setRpm] = useState(0)
  const [power, setPower] = useState(0)

  const startSearch = async () => {
    try {
      const searchSeconds = 60

      const result = await AntPlus?.startSearch(
        [
          AntPlusDeviceType.HEARTRATE,
          AntPlusDeviceType.BIKE_CADENCE,
          AntPlusDeviceType.BIKE_POWER,
          AntPlusDeviceType.FITNESS_EQUIPMENT,
        ],
        searchSeconds,
        true
      )
      console.log(result, 'result')
    } catch (error) {
      console.log(error, 'error')
    }
  }

  const searchStatus = (event: SearchStatusArguments) => {
    setStatusSearch(event.isSearching)
  }

  const foundDevice = (device: AntPlusDevice) => {
    setDevices((prev) => {
      if (prev.some((unit) => unit.resultID === device.resultID)) {
        return prev
      }
      return [...prev, device]
    })
  }

  const rssi = (event: RssiArguments) => {
    console.log('rssi', event.rssi)
  }

  const devicesStateChange = (event: DevicesStateChangeArguments) => {
    console.log('devicesStateChange', event)
  }

  const heartRateChange = (data: HeartRateEventArguments) => {
    switch (data.event) {
      case AntPlusHeartRateEvent.HeartRateData: {
        setBpm(data.heartRate)
      }
    }
  }

  const bikeCadenceChange = (data: BikeCadenceEventArguments) => {
    switch (data.event) {
      case AntPlusBikeCadenceEvent.CalculatedCadence: {
        data.calculatedCadence && setRpm(data.calculatedCadence)
      }
    }
  }

  const bikePowerChange = (data: BikePowerEventArguments) => {
    switch (data.event) {
      case AntPlusBikePowerEvent.CalculatedPower: {
        setPower(data.calculatedPower)
      }
    }
  }

  const fitnessEquipmentChange = (data: FitnessEquipmentEventArguments) => {
    switch (data.event) {
      case AntPlusFitnessEquipmentEvent.CalculatedTrainerPower: {
        setPower(data.calculatedPower)
      }
    }
  }

  useEffect(() => {
    if (AntPlus) {
      startSearch()
    }

    AntPlus.addListener(AntPlusEvent.searchStatus, searchStatus)
    AntPlus.addListener(AntPlusEvent.foundDevice, foundDevice)
    AntPlus.addListener(AntPlusEvent.rssi, rssi)
    AntPlus.addListener(AntPlusEvent.devicesStateChange, devicesStateChange)
    AntPlus.addListener(AntPlusEvent.heartRate, heartRateChange)
    AntPlus.addListener(AntPlusEvent.bikeCadence, bikeCadenceChange)
    AntPlus.addListener(AntPlusEvent.bikePower, bikePowerChange)
    AntPlus.addListener(AntPlusEvent.fitnessEquipment, fitnessEquipmentChange)

    return () => {
      AntPlus.removeListener(AntPlusEvent.searchStatus, searchStatus)
      AntPlus.removeListener(AntPlusEvent.foundDevice, foundDevice)
      AntPlus.removeListener(AntPlusEvent.rssi, rssi)
      AntPlus.removeListener(AntPlusEvent.devicesStateChange, devicesStateChange)
      AntPlus.removeListener(AntPlusEvent.heartRate, heartRateChange)
      AntPlus.removeListener(AntPlusEvent.bikeCadence, bikeCadenceChange)
      AntPlus.removeListener(AntPlusEvent.bikePower, bikePowerChange)
      AntPlus.removeListener(AntPlusEvent.fitnessEquipment, fitnessEquipmentChange)
    }
  }, [])

  return (
    <View style={styles.container}>
      <Text>BPM: {bpm}</Text>
      <Text>RPM: {rpm}</Text>
      <Text>POWER: {power}</Text>
      <Text>Searching: {String(statusSearch)}</Text>
      <Text>Devices</Text>
      <ScrollView style={styles.devices}>
        {devices.map((device) => (
          <Device {...device} key={device.antPlusDeviceTypeName + device.resultID} />
        ))}
      </ScrollView>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  devices: {
    width: '100%',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  device: {
    alignItems: 'center',
  },
  controls: {
    flexDirection: 'row',
  },
  button: {
    marginHorizontal: 2,
  },
})
