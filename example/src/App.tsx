import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import AntPlus, {
  AntPlusDevice,
  AntPlusDeviceType,
  AntPlusEmitter,
  AntPlusEvent,
  AntPlusSearchStatus,
} from 'react-native-ant-plus'

export default function App() {
  const [result, setResult] = React.useState<string[]>(() => ([]));
  const [statusSearch, setStatusSearch] = React.useState(false);

  const startSearch = async () => {
    try {
      const result = await AntPlus?.startSearch([AntPlusDeviceType.HEARTRATE], 10, false)
      console.log(result, 'result')
    } catch (error) {
      console.log(error, 'error')
    }
  }

  const searchStatus = (status: AntPlusSearchStatus) => {
    console.log('status', status)
    setStatusSearch(status.isSearching)
  }

  const foundDevice = (device: AntPlusDevice) => {
    setResult(prev => {
      if (prev.includes(device.antDeviceTypeName)) {
        return prev
      }
      return [...prev, device.antDeviceTypeName]
    })
    console.log(device, 'foundDevice')
  }

  React.useEffect(() => {
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
      <Text>Searching: {String(statusSearch)}</Text>
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
