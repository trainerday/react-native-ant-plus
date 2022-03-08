package com.reactnativeantplus

import androidx.annotation.Nullable
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.RCTNativeAppEventEmitter

class AntPlusModule(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
  private var context: ReactApplicationContext = context
  private var antPlusSearch = AntPlusSearch(context, this)
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
  fun startSearch(deviceTypes: ReadableArray, searchSeconds: Int, allowRssi: Boolean, promise: Promise) {
    antPlusSearch.startSearch(deviceTypes, searchSeconds, allowRssi, promise)
  }

  @ReactMethod
  fun stopSearch(promise: Promise) {
    antPlusSearch.stopSearch(promise)
  }

  @ReactMethod
  fun connect(antDeviceNumber: Int, deviceTypeNumber: Int, promise: Promise) {
    devices[antDeviceNumber] = AntPlusDevice(context, this, antDeviceNumber, deviceTypeNumber)
    devices[antDeviceNumber]?.connect(promise)
  }

  @ReactMethod
  fun disconnect(antDeviceNumber: Int, promise: Promise) {
    devices[antDeviceNumber]?.disconnect(promise)
    devices.remove(antDeviceNumber)
  }

  @ReactMethod
  fun subscribe(antDeviceNumber: Int, events: ReadableArray, isOnlyNewData: Boolean) {
    devices[antDeviceNumber]?.subscribe(events, isOnlyNewData)
  }

  @ReactMethod
  fun addListener(eventName: String?) {
    // Set up any upstream listeners or background tasks as necessary
  }

  @ReactMethod
  fun removeListeners(count: Int?) {
    // Remove upstream listeners, stop unnecessary background tasks
  }
}
