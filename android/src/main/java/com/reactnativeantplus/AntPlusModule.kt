package com.reactnativeantplus

import androidx.annotation.Nullable
import com.reactnativeantplus.events.AntPlusEvent
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.RCTNativeAppEventEmitter
import kotlin.experimental.and

class AntPlusModule(val context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
  private val antPlusSearch = AntPlusSearch(context, this)
  private val devices = mutableMapOf<Int, AntPlusDevice>()

  override fun getName(): String {
    return "AntPlusModule"
  }

  fun sendEvent(eventName: AntPlusEvent, @Nullable params: WritableMap?) {
    reactApplicationContext.getJSModule(RCTNativeAppEventEmitter::class.java)
      .emit(eventName.toString(), params)
  }

  fun sendEvent(eventName: AntPlusEvent, bool: Boolean) {
    reactApplicationContext.getJSModule(RCTNativeAppEventEmitter::class.java)
      .emit(eventName.toString(), bool)
  }

  fun sendEvent(eventName: AntPlusEvent, error: RequestAccessResult) {
    reactApplicationContext.getJSModule(RCTNativeAppEventEmitter::class.java)
      .emit(eventName.toString(), error.toString())
  }

  @ReactMethod
  fun startSearch(deviceTypes: ReadableArray, scanSeconds: Int, allowRssi: Boolean, promise: Promise) {
    antPlusSearch.startSearch(deviceTypes, scanSeconds, allowRssi, promise)
  }

  @ReactMethod
  fun stopSearch(promise: Promise) {
    antPlusSearch.stopSearch(promise)
  }

  @ReactMethod
  fun connect(antDeviceNumber: Int, deviceTypeNumber: Int, promise: Promise) {
    try {
      if (devices[antDeviceNumber] != null && devices[antDeviceNumber]!!.isConnected) {
        throw Error("The device is already connected")
      }

      devices[antDeviceNumber] = AntPlusDevice(context, this, antDeviceNumber, deviceTypeNumber)
      devices[antDeviceNumber]?.connect(promise)
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }

  @ReactMethod
  fun disconnect(antDeviceNumber: Int, promise: Promise) {
    try {
      if (devices[antDeviceNumber] == null) {
        throw Error("Device not found")
      }

      devices[antDeviceNumber]?.disconnect(promise)
      devices.remove(antDeviceNumber)
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }

  @ReactMethod
  fun subscribe(antDeviceNumber: Int, events: ReadableArray, isOnlyNewData: Boolean, promise: Promise) {
    try {
      if (devices[antDeviceNumber] == null || !devices[antDeviceNumber]?.isConnected!!) {
        throw Error("Device not connected")
      }
      devices[antDeviceNumber]?.subscribe(events, isOnlyNewData)
      promise.resolve(true)
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }

  @ReactMethod
  fun unsubscribe(antDeviceNumber: Int, events: ReadableArray, promise: Promise) {
    try {
      if (devices[antDeviceNumber] == null || !devices[antDeviceNumber]?.isConnected!!) {
        throw Error("Device not connected")
      }
      devices[antDeviceNumber]?.unsubscribe(events)
      promise.resolve(true)
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }

  @ReactMethod
  fun setVariables(antDeviceNumber: Int, variables: ReadableMap, promise: Promise) {
    try {
      if (devices[antDeviceNumber] == null || !devices[antDeviceNumber]!!.isConnected) {
        throw Error("Device not connected")
      }
      devices[antDeviceNumber]?.setVariables(variables, promise)
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }

  @ReactMethod
  fun getVariables(antDeviceNumber: Int, variables: ReadableMap, promise: Promise) {
    try {
      if (devices[antDeviceNumber] == null || !devices[antDeviceNumber]!!.isConnected) {
        throw Error("Device not connected")
      }
      devices[antDeviceNumber]?.getVariables(variables, promise)
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }

  @ReactMethod
  fun request(antDeviceNumber: Int, requestName: String, args: ReadableMap, promise: Promise) {
    try {
      if (devices[antDeviceNumber] == null || !devices[antDeviceNumber]!!.isConnected) {
        throw Error("Device not connected")
      }
      devices[antDeviceNumber]?.request(requestName, args, promise)
    } catch (throwable: Throwable) {
      promise.reject(throwable)
    }
  }

  @ReactMethod
  fun addListener(eventName: String?) {
    // Set up any upstream listeners or background tasks as necessary
  }

  @ReactMethod
  fun removeListeners(count: Int?) {
    // Remove upstream listeners, stop unnecessary background tasks
  }

  companion object {
    fun bytesToWritableArray(bytes: ByteArray): WritableArray? {
      val data = Arguments.createArray()
      for (i in bytes.indices) {
        data.pushInt((bytes[i] and 0xFF.toByte()).toInt())
      }
      return data
    }

    fun writableArrayToBytes(array: ReadableArray): ByteArray {
      val bytes = ByteArray(array.size())
      for (i in 0 until array.size()) {
        bytes[i] = array.getInt(i).toByte()
      }
      return bytes
    }
  }
}
