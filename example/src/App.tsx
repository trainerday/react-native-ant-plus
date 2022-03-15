import React, {useEffect, useState} from 'react'

import {Alert, Button, ScrollView, StyleSheet, Text, View} from 'react-native'
import AntPlus, {
  AntPlusBikeCadenceEvent,
  AntPlusBikePowerEvent,
  AntPlusDevice,
  AntPlusDeviceStateChange,
  AntPlusDeviceType,
  AntPlusEmitter,
  AntPlusEvent,
  AntPlusFitnessEquipmentEvent,
  AntPlusFitnessEquipmentRequest,
  AntPlusHeartRateEvent,
  AntPlusRssiEvent,
  AntPlusSearchStatus,
} from 'react-native-ant-plus'

export default function App() {
  const [devices, setDevices] = useState<AntPlusDevice[]>(() => [])
  const [statusSearch, setStatusSearch] = useState(false)
  const [bpm, setBpm] = useState(0)
  const [rpm, setRpm] = useState(0)
  const [power, setPower] = useState(0)

  const startSearch = async () => {
    try {
      const result = await AntPlus?.startSearch([
        AntPlusDeviceType.HEARTRATE,
        AntPlusDeviceType.BIKE_CADENCE,
        AntPlusDeviceType.BIKE_POWER,
        AntPlusDeviceType.FITNESS_EQUIPMENT,
      ], 20, true)
      console.log(result, 'result')
    } catch (error) {
      console.log(error, 'error')
    }
  }

  const searchStatus = (status: AntPlusSearchStatus) => {
    console.log('searchStatus', status)
    setStatusSearch(status.isSearching)
  }

  const foundDevice = (device: AntPlusDevice) => {
    console.log(device)
    setDevices((prev) => {
      if (prev.some((unit) => unit.antDeviceNumber === device.antDeviceNumber)) {
        return prev
      }
      return [...prev, device]
    })
  }

  const rssi = (event: AntPlusRssiEvent) => {
    console.log('rssi', event)
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

    AntPlusEmitter.addListener(AntPlusEvent.searchStatus, searchStatus)
    AntPlusEmitter.addListener(AntPlusEvent.foundDevice, foundDevice)
    AntPlusEmitter.addListener(AntPlusEvent.rssi, rssi)
    AntPlusEmitter.addListener(AntPlusEvent.devicesStateChange, devicesStateChange)
    AntPlusEmitter.addListener(AntPlusEvent.heartRate, heartRateChange)
    AntPlusEmitter.addListener(AntPlusEvent.bikeCadence, bikeCadenceChange)
    AntPlusEmitter.addListener(AntPlusEvent.bikePower, bikePowerChange)
    AntPlusEmitter.addListener(AntPlusEvent.fitnessEquipment, fitnessEquipmentChange)

    return () => {
      AntPlusEmitter.removeListener(AntPlusEvent.searchStatus, searchStatus)
      AntPlusEmitter.removeListener(AntPlusEvent.foundDevice, foundDevice)
      AntPlusEmitter.removeListener(AntPlusEvent.rssi, rssi)
      AntPlusEmitter.removeListener(AntPlusEvent.devicesStateChange, devicesStateChange)
      AntPlusEmitter.removeListener(AntPlusEvent.heartRate, heartRateChange)
      AntPlusEmitter.removeListener(AntPlusEvent.bikeCadence, bikeCadenceChange)
      AntPlusEmitter.removeListener(AntPlusEvent.bikePower, bikePowerChange)
      AntPlusEmitter.removeListener(AntPlusEvent.fitnessEquipment, fitnessEquipmentChange)
    }
  }, [])

  const [antDeviceNumber, setAntDeviceNumber] = useState(-1)
  const [target, setTarget] = useState(50)

  const handlePressTargetUp = () => {
    setTarget(target => target + 10)
  }

  const handlePressTargetDown = () => {
    if (target < 9) {
      return
    }
    setTarget(target => target - 10)
  }

  useEffect(() => {
    if (antDeviceNumber === -1) {
      return
    }

    AntPlus.request(antDeviceNumber, AntPlusFitnessEquipmentRequest.SetTargetPower, {target}).then(result => {
      console.log('target', result)
    }).catch(error => {
      console.log('target error', error)
    })
  }, [target, antDeviceNumber])

  return (
    <View style={styles.container}>
      <View style={styles.controls}>
        <Button onPress={handlePressTargetUp} title="TargetUp"/>
        <Button onPress={handlePressTargetDown} title="TargetDown"/>
      </View>
      <Text>BPM: {bpm}</Text>
      <Text>RPM: {rpm}</Text>
      <Text>POWER: {power}</Text>
      <Text>Target: {target}</Text>
      <Text>Searching: {String(statusSearch)}</Text>
      <Text>Devices</Text>
      <ScrollView>
        {devices.map((device) => {
          const handlePressConnect = async () => {
            try {
              const result = await AntPlus.connect(device.antDeviceNumber, device.antPlusDeviceType)
              console.log('connect', result)

              if (!result.connected) return

              switch (device.antPlusDeviceType) {
              case AntPlusDeviceType.BIKE_CADENCE:
                AntPlus.subscribe(device.antDeviceNumber, [AntPlusBikeCadenceEvent.CalculatedCadence], true)
                break
              case AntPlusDeviceType.BIKE_POWER:
                AntPlus.subscribe(device.antDeviceNumber, [AntPlusBikePowerEvent.CalculatedPower], true)
                break
              case AntPlusDeviceType.FITNESS_EQUIPMENT:
                setAntDeviceNumber(device.antDeviceNumber)
                AntPlus.subscribe(device.antDeviceNumber, [AntPlusFitnessEquipmentEvent.CalculatedTrainerPower], true)
                break
              case AntPlusDeviceType.HEARTRATE:
                AntPlus.subscribe(device.antDeviceNumber, [AntPlusHeartRateEvent.HeartRateData], true)
                break
              }
            } catch (error) {
              console.log('connect error', error)
            }
          }

          const handlePressDisconnect = async () => {
            try {
              const result = await AntPlus.disconnect(device.antDeviceNumber)
              console.log('disconnect', result)
            } catch (error) {
              console.log('disconnect error', error)
            }
          }

          return (
            <View key={device.antDeviceNumber} style={styles.device}>
              <Text>{device.antPlusDeviceTypeName} </Text>
              <View style={styles.controls}>
                <Button onPress={handlePressConnect} title="Connect"/>
                <Button onPress={handlePressDisconnect} title="Disconnect"/>
              </View>
            </View>
          )
        })}
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
