package com.reactnativeantplus

import com.reactnativeantplus.events.AntPlusEvent
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import java.util.*

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

    fun createEventDataMap(
      event: String,
      estTimestamp: Long,
      eventFlags: EnumSet<EventFlag>
    ): WritableMap {
      val eventData = Arguments.createMap()

      eventData.putString("event", event)
      eventData.putInt("estTimestamp", estTimestamp.toInt())
      eventData.putString("eventFlags", eventFlags.toString())

      return eventData
    }
  }
}
