import React, {useEffect, useState} from 'react'

import {Button, ScrollView, StyleSheet, Text, View} from 'react-native'
import AntPlus, {
  AntPlusDevice,
  AntPlusDeviceStateChange,
  AntPlusDeviceType,
  AntPlusEmitter,
  AntPlusEvent,
  AntPlusHeartRateEvent,
  AntPlusRssiEvent,
  AntPlusSearchStatus,
} from 'react-native-ant-plus'

export default function App() {
  const [devices, setDevices] = useState<AntPlusDevice[]>(() => ([]));
  const [statusSearch, setStatusSearch] = useState(false);
  const [bpm, setBpm] = useState(0);

  const startSearch = async () => {
    try {
      const result = await AntPlus?.startSearch([AntPlusDeviceType.HEARTRATE], 20, true)
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
    setDevices(prev => {
      if (prev.some(unit => unit.antDeviceNumber === device.antDeviceNumber)) {
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

  useEffect(() => {
    if (AntPlus) {
      startSearch()
    }

    AntPlusEmitter.addListener(AntPlusEvent.searchStatus, searchStatus)
    AntPlusEmitter.addListener(AntPlusEvent.foundDevice, foundDevice)
    AntPlusEmitter.addListener(AntPlusEvent.rssi, rssi)
    AntPlusEmitter.addListener(AntPlusEvent.devicesStateChange, devicesStateChange)
    AntPlusEmitter.addListener(AntPlusEvent.heartRate, heartRateChange)

    return () => {
      AntPlusEmitter.removeListener(AntPlusEvent.searchStatus, searchStatus)
      AntPlusEmitter.removeListener(AntPlusEvent.foundDevice, foundDevice)
      AntPlusEmitter.removeListener(AntPlusEvent.rssi, rssi)
      AntPlusEmitter.removeListener(AntPlusEvent.devicesStateChange, devicesStateChange)
      AntPlusEmitter.removeListener(AntPlusEvent.heartRate, heartRateChange)
    }
  }, []);

  return (
    <View style={styles.container}>
      <Text>BPM: {bpm}</Text>
      <Text>Searching: {String(statusSearch)}</Text>
      <Text>Devices</Text>
      <ScrollView>
        {devices.map(device => {
          const handlePressConnect = async () => {
            try {
              const result = await AntPlus.connect(device.antDeviceNumber, device.antPlusDeviceType)
              console.log('connect', result)

              if (!result.connected) return

              AntPlus.subscribe(device.antDeviceNumber, [AntPlusHeartRateEvent.HeartRateData], true)
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
              <Button onPress={handlePressConnect} title="Connect" />
              <Text>{' '}</Text>
              <Button onPress={handlePressDisconnect} title="Disconnect" />
            </View>
          )
        })}
      </ScrollView>
    </View>
  );
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
    flexDirection: 'row',
    alignItems: 'center'
  }
});

