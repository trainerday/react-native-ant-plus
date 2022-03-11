package com.reactnativeantplus

import com.facebook.react.bridge.Arguments
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver

class AntPlusPlugin {
    companion object {
        fun stateReceiver(
            antPlus: AntPlusModule,
            antDeviceNumber: Int
        ): IDeviceStateChangeReceiver {
            return IDeviceStateChangeReceiver { deviceState ->
                val state = Arguments.createMap()
                state.putString("event", "DeviceStateChangeReceiver")
                state.putInt("antDeviceNumber", antDeviceNumber)
                state.putString("state", deviceState.toString())
                antPlus.sendEvent(AntPlusEvent.devicesStateChange, state)
            }
        }
    }
}
