import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import AntPlus, {DevicesType, AntPlusEmitter, AntPlusEvent} from 'react-native-ant-plus'

export default function App() {
  const [result, setResult] = React.useState(() => ([]));

  const startSearch = async () => {
    try {
      const result = await AntPlus?.startSearch([DevicesType.HEARTRATE], 10)
      console.log(result, 'result')
    } catch (error) {
      console.log(error, 'error')
    }
  }

  const searchStatus = (event) => {
    console.log(event, 'searchStatus')
  }

  const foundDevice = (device) => {
    setResult(prev => {
      if (prev.includes(device.antDeviceTypeName)) {
        return prev
      }
      return [...prev, device.antDeviceTypeName]
    })
    console.log(device, 'foundDevice')
  }

  React.useEffect(() => {
    console.log(AntPlus)

    if (AntPlus) {
      startSearch()
    }

    AntPlusEmitter.addListener(AntPlusEvent.searchStatus, searchStatus)
    AntPlusEmitter.addListener(AntPlusEvent.foundDevice, foundDevice)

    return () => {
      AntPlusEmitter.removeListener(AntPlusEvent.searchStatus, searchStatus)
      AntPlusEmitter.removeListener(AntPlusEvent.foundDevice, foundDevice)
    }
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result.join(', ')}</Text>
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
});
