import React, {useEffect, useState} from 'react'

import {Alert, ScrollView, StyleSheet, Text, View} from 'react-native'
import AntPlus, {
  AntPlusDevice,
  AntPlusDeviceStateChange,
  AntPlusDeviceType,
  AntPlusEvent,
  AntPlusFitnessEquipmentEvent,
  AntPlusSearchStatus,
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

      const result = await AntPlus?.startSearch([
        AntPlusDeviceType.HEARTRATE,
        AntPlusDeviceType.BIKE_CADENCE,
        AntPlusDeviceType.BIKE_POWER,
        AntPlusDeviceType.FITNESS_EQUIPMENT,
      ], searchSeconds, true)
      console.log(result, 'result')
    } catch (error) {
      console.log(error, 'error')
    }
  }

  const searchStatus = (status: AntPlusSearchStatus): void => {
    console.log('searchStatus', status)
    setStatusSearch(status.isSearching)
  }

  const foundDevice = (device: AntPlusDevice) => {
    console.log(device)
    setDevices((prev) => {
      if (prev.some((unit) => unit.resultID === device.resultID)) {
        return prev
      }
      return [...prev, device]
    })
  }

  const rssi = (event: {rssi: number, resultID: number}): void => {
    console.log('rssi', event.rssi)
  }

  const devicesStateChange = (event: AntPlusDeviceStateChange) => {
    console.log('devicesStateChange', event)
  }

  const heartRateChange = (data: any) => {
    data.heartRate && setBpm(data.heartRate)
  }

  const bikeCadenceChange = (data: any) => {
    data.calculatedCadence && setRpm(data.calculatedCadence)
  }

  const bikePowerChange = (data: any) => {
    data.calculatedPower && setPower(data.calculatedPower)
  }

  const fitnessEquipmentChange = (data: any) => {
    if (data.event === 'Testing') {
      Alert.alert(data.type)
    }
    if (data.event === AntPlusFitnessEquipmentEvent.CalculatedTrainerPower) {
      setPower(data.calculatedPower)
    }
    console.log(data)
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
        {devices.map((device) => <Device {...device} key={device.antPlusDeviceTypeName + device.resultID} />)}
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
