package com.reactnativeantplus

import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch
import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch.*
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import java.util.*

class AntPlusSearch(context: ReactApplicationContext, antPlus: AntPlusModule) {
  private var context: ReactApplicationContext = context
  private var antPlus: AntPlusModule = antPlus
  private var search: MultiDeviceSearch? = null
  private var isSearching = false

  private val searchCallback: SearchCallbacks = object : SearchCallbacks {
    override fun onDeviceFound(deviceFound: MultiDeviceSearchResult) {
      val device = Arguments.createMap()
      device.putInt("resultID", deviceFound.resultID)
      device.putInt("describeContents", deviceFound.describeContents())
      device.putInt("antDeviceNumber", deviceFound.antDeviceNumber)
      device.putString("antDeviceTypeName", deviceFound.antDeviceType.toString())
      device.putInt("antDeviceType", deviceFound.antDeviceType.intValue)
      device.putString("deviceDisplayName", deviceFound.deviceDisplayName)
      device.putBoolean("isAlreadyConnected", deviceFound.isAlreadyConnected)
      device.putBoolean("isPreferredDevice", deviceFound.isPreferredDevice)
      device.putBoolean("isUserRecognizedDevice", deviceFound.isUserRecognizedDevice)

      antPlus.sendEvent(AntPlusEvent.foundDevice, device)
    }

    override fun onSearchStarted(p0: RssiSupport?) {
      isSearching = true

      val map = Arguments.createMap()
      map.putBoolean("isSearching", true)
      antPlus.sendEvent(AntPlusEvent.searchStatus, map)
    }

    override fun onSearchStopped(reason: RequestAccessResult) {
      isSearching = false

      val map = Arguments.createMap()
      map.putBoolean("isSearching", false)
      map.putString("reason", reason.toString())
      antPlus.sendEvent(AntPlusEvent.searchStatus, map)
    }
  }

  private val rssiCallback = RssiCallback { resultId, rssi ->
    runOnUiThread(Runnable {
      val map = Arguments.createMap()
      map.putInt("rssi", rssi)
      map.putInt("resultID", resultId)
      antPlus.sendEvent(AntPlusEvent.rssi, map)
    })
  }

  fun startSearch(deviceTypes: ReadableArray, searchSeconds: Int, promise: Promise) {
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

      search = MultiDeviceSearch(context, devices, searchCallback, rssiCallback)

      if (searchSeconds > 0) {
        val thread: Thread = object : Thread() {
          override fun run() {
            try {
              sleep((searchSeconds * 1000).toLong())
            } catch (ignored: InterruptedException) {
            }
            runOnUiThread {
              search?.close()
              isSearching = false
            }
          }
        }
        thread.start()
      }
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }

  fun stopSearch(promise: Promise) {
    try {
      isSearching = false
      search?.close()
      promise.resolve(true)
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }
}
