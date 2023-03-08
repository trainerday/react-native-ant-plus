package com.reactnativeantplus

/**
 * Implementing the MultiDeviceSearch
 * https://www.thisisant.com/APIassets/Android_ANT_plus_plugins_API/com/dsi/ant/plugins/antplus/pcc/MultiDeviceSearch.html
 */

import com.reactnativeantplus.events.AntPlusEvent
import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch
import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch.*
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import java.util.*

class AntPlusSearch(val context: ReactApplicationContext, val antPlus: AntPlusModule) {
  private var search: MultiDeviceSearch? = null
  private var isSearching = false
  private val devicesRssi = mutableMapOf<Int, Int>()

  private val searchCallback: SearchCallbacks = object : SearchCallbacks {
    override fun onDeviceFound(deviceFound: MultiDeviceSearchResult) {
      val device = Arguments.createMap()
      device.putInt("resultID", deviceFound.resultID)
      device.putInt("describeContents", deviceFound.describeContents())
      device.putInt("antDeviceNumber", deviceFound.antDeviceNumber)
      device.putString("antPlusDeviceTypeName", deviceFound.antDeviceType.toString())
      device.putInt("antPlusDeviceType", deviceFound.antDeviceType.intValue)
      device.putString("deviceDisplayName", deviceFound.deviceDisplayName)
      device.putBoolean("isAlreadyConnected", deviceFound.isAlreadyConnected)
      device.putBoolean("isPreferredDevice", deviceFound.isPreferredDevice)
      device.putBoolean("isUserRecognizedDevice", deviceFound.isUserRecognizedDevice)

      devicesRssi[deviceFound.resultID]?.let { device.putInt("rssi", it) }

      antPlus.sendEvent(AntPlusEvent.foundDevice, device)
    }

    override fun onSearchStarted(p0: RssiSupport?) {
      isSearching = true

      val map = Arguments.createMap()
      map.putBoolean("isSearching", true)
      antPlus.sendEvent(AntPlusEvent.searchStatus, map)
    }

    override fun onSearchStopped(reason: RequestAccessResult) {
      val map = Arguments.createMap()

      if (reason == RequestAccessResult.USER_CANCELLED) {
        map.putString("reason", RequestAccessResult.SEARCH_TIMEOUT.toString())
      } else {
        map.putString("reason", reason.toString())
      }

      isSearching = false

      map.putBoolean("isSearching", isSearching)
      antPlus.sendEvent(AntPlusEvent.searchStatus, map)
    }
  }

  private val rssiCallback = RssiCallback { resultId, rssi ->
    runOnUiThread {
      val map = Arguments.createMap()
      map.putInt("rssi", rssi)
      map.putInt("resultID", resultId)
      antPlus.sendEvent(AntPlusEvent.rssi, map)
      devicesRssi[resultId] = rssi
    }
  }

  fun startSearch(deviceTypes: ReadableArray, searchSeconds: Int, allowRssi: Boolean, promise: Promise) {
    try {
      if (isSearching) {
        throw Error("The search is on")
      }

      val devices: EnumSet<DeviceType> =
        EnumSet.of(DeviceType.getValueFromInt(deviceTypes.getInt(0)))

      val size: Int = deviceTypes.size()
      if (size > 1) {
        for (index in 1 until size) {
          devices.add(DeviceType.getValueFromInt(deviceTypes.getInt(index)))
        }
      }

      search = if (allowRssi) {
        MultiDeviceSearch(context, devices, searchCallback, rssiCallback)
      } else {
        MultiDeviceSearch(context, devices, searchCallback)
      }

      if (searchSeconds > 0) {
        val thread: Thread = object : Thread() {
          override fun run() {
            try {
              sleep((searchSeconds * 1000).toLong())
            } catch (ignored: InterruptedException) {
            }
            runOnUiThread {
              isSearching = false
              search?.close()
            }
          }
        }
        thread.start()
      }
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
    promise.resolve(true)
  }

  fun stopSearch(promise: Promise) {
    try {
      search?.close()
      promise.resolve(true)
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }
}
