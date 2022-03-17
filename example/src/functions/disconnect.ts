import AntPlus from 'react-native-ant-plus'

export default async (antPlusDeviceNumber: number) => {
  return await AntPlus.disconnect(antPlusDeviceNumber)
}
